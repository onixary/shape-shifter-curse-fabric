# 定向粒子双实体动作

`shape-shifter-curse:directed_particles` 从一方身体中心（包围盒中心）加偏移的位置，朝另一方身体中心加偏移的位置发射指定粒子。可放在支持 `bientity_action` 的位置，例如射线命中动作。

```json
{
  "type": "shape-shifter-curse:directed_particles",
  "particle": "minecraft:flame",
  "direction": "target_to_actor",
  "actor_offset": { "x": 0, "y": 0.5, "z": 0 },
  "target_offset": { "x": 0, "y": 0, "z": 0 },
  "speed": 0.3,
  "count": 8,
  "force": true
}
```

| 参数 | 默认值 | 含义 |
| --- | --- | --- |
| `particle` | 必填 | 粒子 ID，或包含 `type`、`params` 的粒子对象，沿用现有粒子动作格式 |
| `direction` | `actor_to_target` | 只接受 `actor_to_target` / `target_to_actor` |
| `actor_offset` | `{x:0,y:0,z:0}` | actor 身体中心的世界坐标轴偏移，单位为格 |
| `target_offset` | `{x:0,y:0,z:0}` | target 身体中心的世界坐标轴偏移，单位为格 |
| `speed` | `0.3` | 传给粒子的初速度大小，通常以格/tick 理解；负数按 0 处理 |
| `count` | `1` | 实际生成颗数；0 或负数不生成 |
| `force` | `false` | 沿用原版远距离粒子发送：普通发送距离 32 格，开启后 512 格 |

反向发射时仍先分别计算 actor 和 target 的 offset，再交换起点与终点；offset 不会随观察方向旋转。两点重合时速度为零。跨维度、缺少任一实体或实体已移除时不执行。

内部用原版粒子包 `count=0` 的定向模式逐颗发送；这与本动作配置中的 `count=0`（不生成）不同。多颗粒子同时从同一点发出，没有额外位置散布。想要持续粒子流，可将动作放在周期动作中调用。

本动作只设置初速度，不控制粒子后续物理：原版粒子可能减速、下坠、加入随机运动，或特殊解释速度参数；不会追踪移动目标，也不保证飞到终点。不产生伤害或实体碰撞判定。

实现：`additional_power/DirectedParticlesAction.java`，在 `AdditionalEntityActions` 注册；Studio 内归类为双实体动作，支持两组 vector 偏移。
