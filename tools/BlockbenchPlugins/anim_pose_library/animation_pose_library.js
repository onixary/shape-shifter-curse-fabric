(function() {
    const PLUGIN_ID = 'animation_pose_library';
    const DATA_KEY = 'blockbench_animation_pose_lib';
    const DATA_VERSION = 1;
    const EPSILON = 1e-6;

    let defineDefaultAction;
    let redefineDefaultAction;
    let applyPoseAction;
    let restoreDefaultAction;
    let poseLibraryMenuAction;

    function cloneVector(vector) {
        return [Number(vector[0]) || 0, Number(vector[1]) || 0, Number(vector[2]) || 0];
    }

    function addVector(target, delta) {
        target[0] += delta[0];
        target[1] += delta[1];
        target[2] += delta[2];
    }

    function subtractVectors(a, b) {
        return [a[0] - b[0], a[1] - b[1], a[2] - b[2]];
    }

    function hasVectorDelta(vector) {
        return vector.some(value => Math.abs(value) > EPSILON);
    }

    function roundVector(vector) {
        return vector.map(value => Math.abs(value) < EPSILON ? 0 : Math.round(value * 1e6) / 1e6);
    }

    function getPluginData(create = false) {
        if (!Project) return null;

        if (!Project.unhandled_root_fields || typeof Project.unhandled_root_fields !== 'object') {
            if (!create) return null;
            Project.unhandled_root_fields = {};
        }

        let data = Project.unhandled_root_fields[DATA_KEY];
        if ((!data || typeof data !== 'object') && create) {
            data = Project.unhandled_root_fields[DATA_KEY] = {
                version: DATA_VERSION,
                default_pose: null
            };
        }
        return data || null;
    }

    function getDefaultPose() {
        return getPluginData(false)?.default_pose || null;
    }

    function hasDefaultPose() {
        const pose = getDefaultPose();
        return !!(pose && pose.bones && Object.keys(pose.bones).length);
    }

    function captureCurrentPose() {
        const bones = {};
        Group.all.forEach(group => {
            bones[group.uuid] = {
                name: group.name,
                origin: cloneVector(group.origin),
                rotation: cloneVector(group.rotation)
            };
        });
        return {
            version: DATA_VERSION,
            defined_at: new Date().toISOString(),
            bones
        };
    }

    function saveCurrentAsDefault() {
        if (!Project || !Group.all.length) {
            Blockbench.showQuickMessage('当前项目中没有可保存的骨骼', 2400);
            return;
        }
        const data = getPluginData(true);
        data.version = DATA_VERSION;
        data.default_pose = captureCurrentPose();
        Project.saved = false;
        BARS.updateConditions();
        Blockbench.showQuickMessage(`已定义默认姿势（${Group.all.length} 根骨骼）`, 2400);
    }

    function findDefaultBone(group, defaultPose) {
        if (defaultPose.bones[group.uuid]) return defaultPose.bones[group.uuid];

        const lowerName = group.name.toLowerCase();
        const matches = Object.values(defaultPose.bones).filter(bone => {
            return typeof bone.name === 'string' && bone.name.toLowerCase() === lowerName;
        });
        return matches.length === 1 ? matches[0] : null;
    }

    function buildDefaultTargets(defaultPose) {
        const targets = new Map();
        const missing = [];

        Group.all.forEach(group => {
            const stored = findDefaultBone(group, defaultPose);
            if (stored && Array.isArray(stored.origin) && Array.isArray(stored.rotation)) {
                targets.set(group.uuid, {
                    origin: cloneVector(stored.origin),
                    rotation: cloneVector(stored.rotation)
                });
            } else {
                targets.set(group.uuid, {
                    origin: cloneVector(group.origin),
                    rotation: cloneVector(group.rotation)
                });
                missing.push(group);
            }
        });
        return {targets, missing};
    }

    function translateElementData(element, delta) {
        if (!hasVectorDelta(delta)) return;

        if (typeof Cube !== 'undefined' && element instanceof Cube) {
            addVector(element.from, delta);
            addVector(element.to, delta);
            addVector(element.origin, delta);
            return;
        }

        // Mesh, TextureMesh, Locator and NullObject all expose their model-space
        // placement through origin (position is aliased as origin where applicable).
        if (Array.isArray(element.origin)) {
            addVector(element.origin, delta);
            return;
        }
        if (Array.isArray(element.position)) {
            addVector(element.position, delta);
        }
    }

    function refreshModel() {
        Group.all.forEach(group => group.preview_controller?.updateTransform(group));
        Outliner.elements.forEach(element => element.preview_controller?.updateAll(element));
        Canvas.updateAllBones();
        Canvas.updateAllPositions();
        Canvas.scene.updateMatrixWorld(true);
        updateSelection();
    }

    /**
     * Changes the persisted bone origins while retaining each directly-owned
     * element's local coordinates relative to its bone.
     */
    function writePoseTargets(targets) {
        const oldOrigins = new Map(Group.all.map(group => [group.uuid, cloneVector(group.origin)]));

        Outliner.elements.forEach(element => {
            if (!(element.parent instanceof Group)) return;
            const target = targets.get(element.parent.uuid);
            const oldOrigin = oldOrigins.get(element.parent.uuid);
            if (!target || !oldOrigin) return;
            translateElementData(element, subtractVectors(target.origin, oldOrigin));
        });

        Group.all.forEach(group => {
            const target = targets.get(group.uuid);
            if (!target) return;
            group.origin.V3_set(roundVector(target.origin));
            group.rotation.V3_set(roundVector(target.rotation));
        });

        refreshModel();
    }

    function restoreDefaultInternal() {
        const defaultPose = getDefaultPose();
        if (!defaultPose) throw new Error('尚未定义默认姿势');
        const result = buildDefaultTargets(defaultPose);
        writePoseTargets(result.targets);
        return result;
    }

    function runUndoable(message, callback) {
        const aspects = {
            elements: Outliner.elements.slice(),
            groups: Group.all.slice(),
            outliner: true
        };
        Undo.initEdit(aspects);
        try {
            const result = callback();
            Undo.finishEdit(message, aspects);
            return result;
        } catch (error) {
            Undo.cancelEdit(true);
            console.error(`[${PLUGIN_ID}]`, error);
            Blockbench.showMessageBox({
                title: 'Pose Library',
                icon: 'error',
                message: `操作失败：${error.message || error}`
            });
            return null;
        }
    }

    function restoreDefaultPose() {
        const result = runUndoable('Restore default pose', restoreDefaultInternal);
        if (!result) return;
        const suffix = result.missing.length ? `；${result.missing.length} 根新增/未匹配骨骼保持不变` : '';
        Blockbench.showQuickMessage(`已恢复默认姿势${suffix}`, 2800);
    }

    function evaluateAnimation(animation, time) {
        const previousTime = Timeline.time;
        const captures = new Map();

        try {
            Timeline.time = time;
            Animator.showDefaultPose(true);
            Animator.resetLastValues();
            Animator.MolangParser.resetVariables();
            Animator.stackAnimations([animation], false);
            Canvas.scene.updateMatrixWorld(true);

            Group.all.forEach(group => {
                const mesh = group.mesh;
                if (!mesh) return;
                captures.set(group.uuid, {
                    position: [mesh.position.x, mesh.position.y, mesh.position.z],
                    rotation: [
                        Math.radToDeg(mesh.rotation.x),
                        Math.radToDeg(mesh.rotation.y),
                        Math.radToDeg(mesh.rotation.z)
                    ]
                });
            });
        } finally {
            Timeline.time = previousTime;
            Animator.showDefaultPose(true);
            Animator.resetLastValues();
            Canvas.scene.updateMatrixWorld(true);
        }
        return captures;
    }

    function buildAnimationTargets(captures) {
        const targets = new Map();

        function visit(nodes, parentGroup) {
            nodes.forEach(node => {
                if (!(node instanceof Group)) return;
                const capture = captures.get(node.uuid);
                if (!capture) return;

                let origin;
                if (parentGroup) {
                    const parentTarget = targets.get(parentGroup.uuid);
                    origin = [
                        parentTarget.origin[0] + capture.position[0],
                        parentTarget.origin[1] + capture.position[1],
                        parentTarget.origin[2] + capture.position[2]
                    ];
                } else {
                    origin = cloneVector(capture.position);
                }

                targets.set(node.uuid, {
                    origin,
                    rotation: cloneVector(capture.rotation)
                });
                visit(node.children, node);
            });
        }

        visit(Outliner.root, null);
        return targets;
    }

    function countUnmatchedAnimators(animation) {
        let count = 0;
        Object.values(animation.animators || {}).forEach(animator => {
            if (animator.type !== 'bone' && !(animator instanceof BoneAnimator)) return;
            if (!animator.keyframes?.length) return;
            if (!animator.getGroup()) count++;
        });
        return count;
    }

    function applyAnimationPose(animation, time) {
        let restoreResult;
        const result = runUndoable('Apply animation pose', () => {
            restoreResult = restoreDefaultInternal();
            const captures = evaluateAnimation(animation, time);
            const targets = buildAnimationTargets(captures);
            writePoseTargets(targets);
            return targets;
        });
        if (!result) return;

        const unmatched = countUnmatchedAnimators(animation);
        const warnings = [];
        if (restoreResult?.missing.length) warnings.push(`${restoreResult.missing.length} 根骨骼不在默认姿势中`);
        if (unmatched) warnings.push(`${unmatched} 个动画骨骼无法匹配`);
        const suffix = warnings.length ? `；${warnings.join('，')}` : '';
        Blockbench.showQuickMessage(`已应用 ${animation.name} @ ${time}s${suffix}`, 3200);
    }

    function openApplyDialog() {
        const animations = Animation.all.filter(animation => animation.type === 'animation');
        if (!animations.length) {
            Blockbench.showQuickMessage('当前项目中没有动画', 2400);
            return;
        }

        const options = {};
        animations.forEach(animation => {
            options[animation.uuid] = animation.name;
        });

        new Dialog({
            id: 'animation_pose_library_apply',
            title: '从动画应用姿势',
            form: {
                animation: {
                    label: '动画',
                    type: 'select',
                    options,
                    value: Animation.selected?.uuid || animations[0].uuid
                },
                time: {
                    label: '取样时间（秒）',
                    type: 'number',
                    value: 0,
                    min: 0,
                    step: 0.05
                },
                information: {
                    type: 'info',
                    text: '应用前会自动恢复已定义的默认姿势。只写入骨骼位移与旋转，不写入缩放。'
                }
            },
            onConfirm(form) {
                const animation = animations.find(item => item.uuid === form.animation);
                if (!animation) return;
                applyAnimationPose(animation, Math.max(0, Number(form.time) || 0));
            }
        }).show();
    }

    function confirmRedefineDefault() {
        Blockbench.showMessageBox({
            title: '重新定义默认姿势',
            icon: 'warning',
            message: '将以当前 Edit 模式中的骨骼枢轴和旋转覆盖原默认姿势。是否继续？',
            buttons: ['确认', '取消'],
            confirm: 0,
            cancel: 1
        }, result => {
            if (result === 0) saveCurrentAsDefault();
        });
    }

    function isAvailableInEditMode() {
        return !!Project && !!Modes.edit && Group.all.length > 0;
    }

    Plugin.register(PLUGIN_ID, {
        title: 'Animation Pose Library',
        author: 'onixary',
        description: 'Apply an existing animation pose to editable bone pivots and rotations, with a persistent restorable default pose.',
        icon: 'accessibility_new',
        version: '0.1.0',
        min_version: '5.1.4',
        variant: 'both',
        tags: ['Animation', 'Utility'],

        onload() {
            defineDefaultAction = new Action('pose_library_define_default', {
                name: '定义默认姿势',
                icon: 'bookmark_add',
                category: 'animation',
                condition: () => isAvailableInEditMode() && !hasDefaultPose(),
                click: saveCurrentAsDefault
            });

            redefineDefaultAction = new Action('pose_library_redefine_default', {
                name: '重新定义默认姿势',
                icon: 'bookmark_added',
                category: 'animation',
                condition: () => isAvailableInEditMode() && hasDefaultPose(),
                click: confirmRedefineDefault
            });

            applyPoseAction = new Action('pose_library_apply_animation', {
                name: '从动画应用姿势…',
                icon: 'movie',
                category: 'animation',
                condition: () => isAvailableInEditMode() && hasDefaultPose() && Animation.all.length > 0,
                click: openApplyDialog
            });

            restoreDefaultAction = new Action('pose_library_restore_default', {
                name: '恢复默认姿势',
                icon: 'restore',
                category: 'animation',
                condition: () => isAvailableInEditMode() && hasDefaultPose(),
                click: restoreDefaultPose
            });

            poseLibraryMenuAction = new Action('pose_library_menu', {
                name: 'Pose Library',
                icon: 'accessibility_new',
                category: 'animation',
                children: [
                    defineDefaultAction,
                    redefineDefaultAction,
                    applyPoseAction,
                    restoreDefaultAction
                ],
                click() {}
            });
            MenuBar.menus.tools.addAction(poseLibraryMenuAction);
        },

        onunload() {
            MenuBar.menus.tools.removeAction(poseLibraryMenuAction);
            poseLibraryMenuAction?.delete();
            restoreDefaultAction?.delete();
            applyPoseAction?.delete();
            redefineDefaultAction?.delete();
            defineDefaultAction?.delete();
        }
    });
})();
