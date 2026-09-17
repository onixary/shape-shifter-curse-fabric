# Model BB → Anim BB 一键转换插件

Blockbench 插件（5.0+，桌面版/网页版通用）：把**模型导出用 BB 项目**一键转换为**动画制作用 BB 项目**。

## 文件

- `model_to_anim.js` — 插件本体（同时已复制到 `%APPDATA%/Blockbench/plugins/`）
- `verify_model2anim.mjs` — 独立数学验证器（Node ≥ 18），用纯矩阵运算从原始模型推算期望转换结果，与插件输出逐 cube/逐骨骼比对

## 转换内容

1. **朝向翻转**：+Z 正向 → -Z 正向（绕世界原点 Y 轴 180°）
2. **骨骼重命名**（其余骨骼名保持不变）：

   | 模型 BB | 动画 BB |
   |---|---|
   | bipedHead | head |
   | bipedBody | torso |
   | bipedLeftArm | leftArm |
   | bipedRightArm | rightArm |
   | bipedLeftLeg | leftLeg |
   | bipedRightLeg | rightLeg |

3. **body 根骨骼**：所有骨骼（含根级散 cube）收进最外层 `body`，pivot (0, 16, 0)，旋转 0
4. **旋转烘焙**：所有骨骼分组旋转归零，旋转逐层下沉到 cube 自身旋转（从外向内，等价于手动「套临时父骨骼 → 旋转 → 解析分组」流程，内部直接调用 Blockbench 原生 `Group.resolve()`，结果与手动操作一致）
5. UV / 面贴图 / 纹理 / 已有动画全部保留；转换后自动清除保存路径，防止 Ctrl+S 覆盖模型 BB，请用**另存为**保存

## 安装

插件已复制到 `C:\Users\Nie\AppData\Roaming\Blockbench\plugins\model_to_anim.js`。
重启 Blockbench 后在 **文件 → 偏好设置 → 插件** 列表中确认「Model BB to Anim BB」已启用。
更新仓库里的 `tools/BlockbenchPlugins/model_to_anim.js` 后，重新复制一份到上述目录并重启即可。

## 使用

1. 打开模型 BB（如 `bat_3_sub_avali.bbmodel`）
2. 菜单 **Tools → Convert Model BB to Anim BB**
3. 弹出报告（重命名数 / 旋转归零数 / 残留非零旋转数 / cube 数），确认残留为 0
4. **文件 → 另存为**动画 BB

## 验证

```bash
node tools/BlockbenchPlugins/verify_model2anim.mjs <原模型.bbmodel> <转换后另存的.bbmodel>
```

期望输出（以 avali 为例）：

```
PASS: 125 cubes and 20 bones verified against independent math
```

验证器检查：单一 body 根（pivot (0,16,0) 旋转 0）、全部骨骼旋转为 0、命名映射正确、
每个骨骼 pivot = 原始世界 pivot 绕 Y 轴翻 180°、每个 cube 的 from/to/origin/旋转矩阵
与独立矩阵推算一致、UV 面未改变、cube 数量一致。

## 注意事项

- 转换是**单向**操作，且不要对已转换项目重复运行（会再翻 180°回去）。误操作可用 Ctrl+Z 整体撤销
- 与手工维护的 `avali_anim.bbmodel` 对比时注意：参考文件里 head 区域枢轴被手工取整、
  `tail_0` 保留了 (180,0,180) 的手工痕迹——插件输出是纯机械结果（全骨骼旋转 0，
  tail 链 pivot 翻转为 (0,16,3) 等），两者存在这些预期差异
- 模型 BB 里若已有同名目标骨骼（如已存在 `head`），映射会产生重名，请自行检查
- 建议在 GeckoLib / Bedrock 等含骨骼的格式下使用（插件菜单仅在这些格式下显示）

## 实现备忘（约定均为实测钉死，Blockbench 5.1.6）

- cube/骨骼存储的欧拉三元组 ↔ THREE Euler **'ZYX'** 序，数值直通（无符号翻转）
- 世界合成：`qWorld = qParent ⊗ qLocal`（前乘）
- 转换整体 = 用 D = Ry(180)（绕原点）共轭整个场景
- 闭式期望值：骨骼 pivot = `D·W(原pivot)`、旋转 0；cube from/to/origin = `D·W(原坐标)`、
  旋转矩阵 = `D·W_rot(链·cube)`
