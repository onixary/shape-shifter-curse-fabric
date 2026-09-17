(function () {
	const NAME_MAP = {
		bipedHead: 'head',
		bipedBody: 'torso',
		bipedLeftArm: 'leftArm',
		bipedRightArm: 'rightArm',
		bipedLeftLeg: 'leftLeg',
		bipedRightLeg: 'rightLeg'
	};
	const EPSILON = 1e-4;

	function hasRotation(rot) {
		return !!rot && (
			Math.abs(rot[0]) >= EPSILON ||
			Math.abs(rot[1]) >= EPSILON ||
			Math.abs(rot[2]) >= EPSILON
		);
	}

	function refreshTransforms() {
		Canvas.updateAllBones();
		Canvas.updateAllPositions();
	}

	function bakeGroupRotation(group) {
		const kids = group.children.slice();
		const temp = new Group({
			name: '_m2a_bake_temp',
			origin: group.origin.slice()
		});
		temp.init();
		temp.addTo(group);
		kids.forEach(kid => kid.addTo(temp));
		temp.extend({ rotation: group.rotation.slice() });
		group.extend({ rotation: [0, 0, 0] });
		refreshTransforms();
		temp.resolve(false);
	}

	function collectGroupsPreorder(rootGroup) {
		const out = [];
		(function rec(parent) {
			parent.children.forEach(child => {
				if (child instanceof Group) {
					out.push(child);
					rec(child);
				}
			});
		})(rootGroup);
		return out;
	}

	function convert(showReport) {
		if (!Format || !Format.bone_rig) {
			Blockbench.showMessageBox({
				title: 'Model BB to Anim BB',
				message: '当前格式不含骨骼（bone rig），无法转换。'
			});
			return;
		}
		if (Outliner.root.length === 0) {
			Blockbench.showMessageBox({
				title: 'Model BB to Anim BB',
				message: '当前项目为空。'
			});
			return;
		}

		Undo.initEdit({
			outliner: true,
			groups: Group.all.slice(),
			elements: Outliner.elements.slice(),
			selection: true
		});

		const tempRoot = new Group({
			name: '_m2a_flip_temp',
			origin: [0, 0, 0]
		});
		tempRoot.init();
		tempRoot.addTo();

		Outliner.root.slice().forEach(node => {
			if (node !== tempRoot) node.addTo(tempRoot);
		});

		const body = new Group({
			name: 'body',
			origin: [0, 16, 0]
		});
		body.init();
		body.addTo();
		tempRoot.addTo(body);

		tempRoot.extend({ rotation: [0, 180, 0] });
		refreshTransforms();
		tempRoot.resolve(false);

		const order = collectGroupsPreorder(body);
		let renamed = 0;
		order.forEach(group => {
			const mapped = NAME_MAP[group.name];
			if (mapped && mapped !== group.name) {
				group.name = mapped;
				renamed++;
			}
		});

		let baked = 0;
		order.forEach(group => {
			if (hasRotation(group.rotation)) {
				bakeGroupRotation(group);
				baked++;
			}
		});

		const residual = Group.all.filter(group => hasRotation(group.rotation));

		Undo.finishEdit('Convert model BB to anim BB');

		Project.save_path = null;
		Project.export_path = null;
		Project.saved = false;

		if (showReport !== false) {
			Blockbench.showMessageBox({
				title: 'Model BB to Anim BB',
				message: [
					'转换完成。',
					'重命名骨骼：' + renamed,
					'旋转归零骨骼：' + baked,
					'残留非零旋转骨骼：' + residual.length,
					'cube 数量：' + Outliner.elements.length,
					'',
					'已清除原保存路径，请使用「文件 → 另存为」保存为动画BB，避免覆盖模型BB。'
				].join('\n')
			});
		}
	}

	let convertAction = null;

	Plugin.register('ssc_model_to_anim', {
		title: 'Model BB to Anim BB',
		author: 'onixary',
		description: '将模型导出用BB项目一键转换为动画制作用BB项目：朝向 +Z 翻转为 -Z，biped 骨骼重命名为动画规范，最外层套上 pivot (0,16,0) 的 body 根骨骼，并把所有骨骼旋转下沉烘焙进 cube 自身旋转。',
		icon: 'sync_alt',
		version: '1.0.0',
		variant: 'both',
		min_version: '5.0.0',
		tags: ['Utility'],
		onload() {
			convertAction = new Action('ssc_model_to_anim_convert', {
				name: 'Convert Model BB to Anim BB',
				description: '模型BB → 动画BB 一键转换（翻转朝向 / 重命名骨骼 / body根骨骼 / 旋转烘焙进cube）',
				icon: 'sync_alt',
				category: 'edit',
				condition: () => Format && Format.bone_rig,
				click() {
					convert(true);
				}
			});
			MenuBar.addAction(convertAction, 'tools');
		},
		onunload() {
			if (convertAction) {
				convertAction.delete();
				convertAction = null;
			}
		}
	});
})();
