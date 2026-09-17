#!/usr/bin/env node
/**
 * Independent verifier for the "Model BB -> Anim BB" Blockbench plugin.
 *
 * It recomputes the expected conversion from the ORIGINAL model bbmodel with
 * plain matrix math (no Blockbench involved) and diffs it against the
 * bbmodel exported after running the plugin.
 *
 * Verified conventions (empirically pinned against Blockbench 5.1.6):
 *   - stored euler triple (x, y, z)  <->  THREE.Euler order 'ZYX', values direct
 *   - world chain composition: qWorld = qParent * qLocal  (premultiply)
 *   - the conversion is a conjugation of the whole scene by D = Ry(180)
 *     (rotation around the world origin, pivot (0,0,0))
 *   - expected group:  rotation [0,0,0], pivot = D * W(old pivot)
 *   - expected cube :  from/to/origin = D * W(old coords),
 *                      rotation matrix = D * W_rot(old chain * cube rot)
 *
 * Usage: node verify_model2anim.mjs <model.bbmodel> <converted.bbmodel>
 */
import fs from 'fs';

const D2R = Math.PI / 180;

/* ---------------------------------------------------------------- quaternions */

function qFromEulerZYX(x, y, z) {
	const hx = (x * D2R) / 2, hy = (y * D2R) / 2, hz = (z * D2R) / 2;
	const s1 = Math.sin(hx), c1 = Math.cos(hx);
	const s2 = Math.sin(hy), c2 = Math.cos(hy);
	const s3 = Math.sin(hz), c3 = Math.cos(hz);
	return [
		s1 * c2 * c3 - c1 * s2 * s3,
		c1 * s2 * c3 + s1 * c2 * s3,
		c1 * c2 * s3 - s1 * s2 * c3,
		c1 * c2 * c3 + s1 * s2 * s3
	];
}

/* THREE.Matrix4.makeRotationFromQuaternion, row-major [r][c] */
function mFromQuat(q) {
	const [x, y, z, w] = q;
	return [
		[1 - 2 * (y * y + z * z), 2 * (x * y - w * z), 2 * (x * z + w * y)],
		[2 * (x * y + w * z), 1 - 2 * (x * x + z * z), 2 * (y * z - w * x)],
		[2 * (x * z - w * y), 2 * (y * z + w * x), 1 - 2 * (x * x + y * y)]
	];
}

function mMul(A, B) {
	const R = [[0, 0, 0], [0, 0, 0], [0, 0, 0]];
	for (let i = 0; i < 3; i++)
		for (let j = 0; j < 3; j++)
			R[i][j] = A[i][0] * B[0][j] + A[i][1] * B[1][j] + A[i][2] * B[2][j];
	return R;
}

const D = mFromQuat(qFromEulerZYX(0, 180, 0)); /* diag(-1, 1, -1) */

function applyM(M, p) {
	return [
		M[0][0] * p[0] + M[0][1] * p[1] + M[0][2] * p[2],
		M[1][0] * p[0] + M[1][1] * p[1] + M[1][2] * p[2],
		M[2][0] * p[0] + M[2][1] * p[1] + M[2][2] * p[2]
	];
}

function quatOf(t) {
	return qFromEulerZYX(t[0] || 0, t[1] || 0, t[2] || 0);
}

/* -------------------------------------------------------------- self-test pin */

function selfTest() {
	const pin = qFromEulerZYX(-142.3827, 29.6168, -144.5237);
	const expected = [-0.200333, 0.896758, -0.223171, 0.325402];
	for (let i = 0; i < 4; i++) {
		if (Math.abs(pin[i] - expected[i]) > 1e-5) {
			throw new Error('ZYX quaternion formula failed the pinned test vector');
		}
	}
}

/* ---------------------------------------------------------------- bbmodel load */

function loadBbmodel(path) {
	const j = JSON.parse(fs.readFileSync(path, 'utf8'));
	const groups = new Map();
	for (const g of j.groups || []) {
		groups.set(g.uuid, {
			name: g.name,
			uuid: g.uuid,
			origin: g.origin || [0, 0, 0],
			rotation: g.rotation || [0, 0, 0],
			parent: null
		});
	}
	const elements = new Map();
	for (const e of j.elements || []) elements.set(e.uuid, e);
	const rootChildren = [];
	function link(nodes, parentNode) {
		for (const n of nodes) {
			if (typeof n === 'string') {
				const el = elements.get(n);
				if (el) {
					el._parent = parentNode ? parentNode.uuid : null;
					(parentNode ? parentNode.children : rootChildren).push({ type: 'element', uuid: n });
				}
			} else {
				const g = groups.get(n.uuid);
				if (g) {
					g.parent = parentNode ? parentNode.uuid : null;
					(parentNode ? parentNode.children : rootChildren).push({ type: 'group', uuid: n.uuid });
					link(n.children || [], g);
				}
			}
		}
	}
	link(j.outliner || [], null);
	return { raw: j, groups, elements, rootChildren };
}

/* world transform of a node's frame in the ORIGINAL model */
function frameWorld(model, groupUuid) {
	const chain = [];
	let g = groupUuid ? model.groups.get(groupUuid) : null;
	while (g) { chain.push(g); g = g.parent ? model.groups.get(g.parent) : null; }
	return {
		apply(p) {
			let x = p.slice();
			for (const c of chain) {
				const rel = [x[0] - c.origin[0], x[1] - c.origin[1], x[2] - c.origin[2]];
				const r = applyM(mFromQuat(quatOf(c.rotation)), rel);
				x = [c.origin[0] + r[0], c.origin[1] + r[1], c.origin[2] + r[2]];
			}
			return x;
		},
		rot: chain.reduce((M, c) => mMul(mFromQuat(quatOf(c.rotation)), M), [[1, 0, 0], [0, 1, 0], [0, 0, 1]])
	};
}

/* ------------------------------------------------------------------------- main */

function approx(a, b, tol) {
	return Math.abs(a - b) <= tol;
}

function v3Eq(a, b, tol) {
	return approx(a[0], b[0], tol) && approx(a[1], b[1], tol) && approx(a[2], b[2], tol);
}

function mEq(A, B, tol) {
	for (let r = 0; r < 3; r++) for (let c = 0; c < 3; c++) if (!approx(A[r][c], B[r][c], tol)) return false;
	return true;
}

const NAME_MAP = {
	bipedHead: 'head',
	bipedBody: 'torso',
	bipedLeftArm: 'leftArm',
	bipedRightArm: 'rightArm',
	bipedLeftLeg: 'leftLeg',
	bipedRightLeg: 'rightLeg'
};

function main() {
	selfTest();
	const [modelPath, convertedPath] = process.argv.slice(2);
	if (!modelPath || !convertedPath) {
		console.error('Usage: node verify_model2anim.mjs <model.bbmodel> <converted.bbmodel>');
		process.exit(2);
	}
	const model = loadBbmodel(modelPath);
	const conv = loadBbmodel(convertedPath);
	const errors = [];
	const notes = [];

	/* 1. single body root at (0,16,0), rotation 0 */
	const rootGroups = conv.rootChildren.filter(n => n.type === 'group');
	const rootElems = conv.rootChildren.filter(n => n.type === 'element');
	if (rootGroups.length !== 1 || rootElems.length !== 0) {
		errors.push('expected exactly one root group and no root elements, got ' + rootGroups.length + ' groups / ' + rootElems.length + ' elements');
	}
	const bodyUuid = rootGroups[0] && rootGroups[0].uuid;
	const body = bodyUuid ? conv.groups.get(bodyUuid) : null;
	if (!body || body.name !== 'body') errors.push('root group is not named body: ' + (body && body.name));
	if (body && !v3Eq(body.origin, [0, 16, 0], 1e-4)) errors.push('body pivot != (0,16,0): ' + JSON.stringify(body.origin));
	if (body && !v3Eq(body.rotation, [0, 0, 0], 1e-4)) errors.push('body rotation != 0: ' + JSON.stringify(body.rotation));

	/* 2. group set identical by uuid, names mapped, rotations zero, pivots expected */
	for (const [uuid, cg] of conv.groups) {
		if (uuid === bodyUuid) continue;
		const mg = model.groups.get(uuid);
		if (!mg) { errors.push('converted group ' + cg.name + ' (' + uuid + ') not present in model'); continue; }
		if (!v3Eq(cg.rotation, [0, 0, 0], 1e-4)) errors.push('group ' + cg.name + ' rotation not zero: ' + JSON.stringify(cg.rotation));
		const expName = NAME_MAP[mg.name] || mg.name;
		if (cg.name !== expName) errors.push('group name mismatch: model ' + mg.name + ' -> expected ' + expName + ', converted ' + cg.name);
		const world = frameWorld(model, uuid);
		const expPivot = applyM(D, world.apply(mg.origin));
		if (!v3Eq(cg.origin, expPivot, 1e-3)) errors.push('group ' + cg.name + ' pivot: expected ' + expPivot.map(v => v.toFixed(4)) + ', got ' + cg.origin.map(v => v.toFixed(4)));
		const expParent = mg.parent ? mg.parent : bodyUuid;
		if (cg.parent !== expParent) errors.push('group ' + cg.name + ' parent uuid mismatch');
	}

	/* 3. cubes identical by uuid: coords = D * W(stored), rot matrix = D * Wrot * local */
	for (const [uuid, ce] of conv.elements) {
		const me = model.elements.get(uuid);
		if (!me) { errors.push('converted element ' + ce.name + ' (' + uuid + ') not present in model'); continue; }
		const world = frameWorld(model, me._parent);
		for (const key of ['from', 'to', 'origin']) {
			const got = ce[key], want = applyM(D, world.apply(me[key]));
			if (!got || !v3Eq(got, want, 1e-3)) {
				errors.push('cube ' + me.name + ' (' + uuid.slice(0, 8) + ') ' + key + ': expected ' + want.map(v => v.toFixed(4)) + ', got ' + (got ? got.map(v => v.toFixed(4)) : 'missing'));
			}
		}
		const Mexp = mMul(D, mMul(world.rot, mFromQuat(quatOf(me.rotation))));
		const Mact = mFromQuat(quatOf(ce.rotation));
		if (!mEq(Mexp, Mact, 2e-3)) {
			errors.push('cube ' + me.name + ' (' + uuid.slice(0, 8) + ') rotation matrix mismatch (stored ' + JSON.stringify(ce.rotation) + ')');
		}
		if (JSON.stringify(me.faces) !== JSON.stringify(ce.faces)) {
			errors.push('cube ' + me.name + ' (' + uuid.slice(0, 8) + ') UV faces changed');
		}
	}

	if (model.elements.size !== conv.elements.size) {
		errors.push('element count mismatch: model ' + model.elements.size + ' vs converted ' + conv.elements.size);
	}
	if (model.groups.size !== conv.groups.size - 1) {
		notes.push('group count: model ' + model.groups.size + ' vs converted ' + (conv.groups.size - 1) + ' (excluding body)');
	}

	if (errors.length) {
		console.error('FAIL: ' + errors.length + ' problem(s)');
		for (const e of errors.slice(0, 40)) console.error('  - ' + e);
		process.exit(1);
	} else {
		console.log('PASS: ' + conv.elements.size + ' cubes and ' + (conv.groups.size - 1) + ' bones verified against independent math');
		for (const n of notes) console.log('note: ' + n);
	}
}

main();
