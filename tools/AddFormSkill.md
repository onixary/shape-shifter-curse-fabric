# Shape Shifter Curse - 添加新形态开发指南

## 概述
本Skill指导如何在Shape Shifter Curse模组中以Java硬编码方式添加新的玩家形态。

## 前置条件
- 了解Minecraft Fabric模组开发基础
- 熟悉Java编程
- 已配置好开发环境

## 形态系统架构
- **PlayerFormBase**: 形态基类
- **PlayerFormPhase**: PHASE_0 → PHASE_1 → PHASE_2 → PHASE_3 → PHASE_SP
- **PlayerFormBodyType**: NORMAL(两足) / FERAL(四足)
- **动画系统**: PlayerAnimator v3 (AbstractAnimStateController)

---

## 形态类型说明

### 按阶段分类
- **渐进式形态 (Progressive)**: PHASE_0 → PHASE_1 → PHASE_2 → PHASE_3
- **特殊形态 (Special)**: PHASE_SP（独立形态，不进阶）

### 按身体类型分类
- **普通形态 (Normal)**: PlayerFormBodyType.NORMAL - 保持两足直立姿态
- **野性/四足形态 (Feral)**: PlayerFormBodyType.FERAL - 四足着地姿态

### 组合可能性
| 类型 | Phase 0-2 | Phase 3 | Phase SP |
|------|-----------|---------|----------|
| 渐进式+普通 | 两足 | 两足 | - |
| 渐进式+野性 | 两足 | 四足 | - |
| 特殊+普通 | - | - | 两足 |
| 特殊+野性 | - | - | 四足 |

---

## 步骤1: 创建形态Java类

### 1.1 文件位置
```
src/main/java/net/onixary/shapeShifterCurseFabric/player_form/forms/Form_<Name>.java
```

### 1.2 渐进式普通形态（Phase 0-3均为两足）

```java
package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_<Name> extends PlayerFormBase {
    
    public Form_<Name>(Identifier formID) {
        super(formID);
    }

    // ========== v3动画系统 ==========
    public static final AnimUtils.AnimationHolderData ANIM_IDLE = 
        new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("<name>_idle"));

    public static final AbstractAnimStateController IDLE_CONTROLLER = 
        new WithSneakAnimController(ANIM_IDLE, null);

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(
            PlayerEntity player, 
            AnimSystem.AnimSystemData animSystemData, 
            @NotNull Identifier animStateID) {
        
        AnimStateEnum state = AnimStateEnum.getStateEnum(animStateID);
        if (state != null) {
            switch (state) {
                case ANIM_STATE_IDLE: return IDLE_CONTROLLER;
                default: return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
```

### 1.3 渐进式野性形态（Phase 3变为四足）

**Phase 0-2（两足阶段）:**
```java
package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_<Name>1 extends PlayerFormBase {
    // Phase 1 使用普通两足动画
    public Form_<Name>1(Identifier formID) {
        super(formID);
    }
    // ... 实现动画控制器
}
```

**Phase 3（四足最终形态）:**
```java
package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_<Name>3 extends Form_FeralBase {
    
    public Form_<Name>3(Identifier formID) {
        super(formID);
        this.setBodyType(PlayerFormBodyType.FERAL);
    }

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(
            PlayerEntity player, 
            AnimSystem.AnimSystemData animSystemData, 
            @NotNull Identifier animStateID) {
        
        AnimStateEnum state = AnimStateEnum.getStateEnum(animStateID);
        if (state != null) {
            switch (state) {
                case ANIM_STATE_WALK:
                    // 如果有潜行冲刺能力
                    return this.getCanSneakRush() ? WALK_CONTROLLER_SNEAK_RUSH : WALK_CONTROLLER;
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return this.getCanSneakRush() ? SPRINT_CONTROLLER_SNEAK_RUSH : SPRINT_CONTROLLER;
                // ... 复用Form_FeralBase的其他控制器
                default:
                    return Form_FeralBase.IDLE_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
```

### 1.4 特殊普通形态（Phase SP，两足）

```java
package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

public class Form_<Name>SP extends PlayerFormBase {
    
    public Form_<Name>SP(Identifier formID) {
        super(formID);
    }
    
    // 实现自定义动画控制器...
}
```

### 1.5 特殊野性形态（Phase SP，四足）

```java
package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_<Name>SPFeral extends Form_FeralBase {
    
    public Form_<Name>SPFeral(Identifier formID) {
        super(formID);
        this.setBodyType(PlayerFormBodyType.FERAL);
    }

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(
            PlayerEntity player,
            AnimSystem.AnimSystemData animSystemData,
            @NotNull Identifier animStateID) {

        AnimStateEnum state = AnimStateEnum.getStateEnum(animStateID);
        if (state != null) {
            switch (state) {
                case ANIM_STATE_WALK:
                    // 如果有潜行冲刺能力
                    return this.getCanSneakRush() ? WALK_CONTROLLER_SNEAK_RUSH : WALK_CONTROLLER;
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return this.getCanSneakRush() ? SPRINT_CONTROLLER_SNEAK_RUSH : SPRINT_CONTROLLER;
                // ... 复用Form_FeralBase的其他控制器
                default:
                    return Form_FeralBase.IDLE_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
```

---

## 步骤2: 注册形态

### 2.1 编辑文件
```
src/main/java/net/onixary/shapeShifterCurseFabric/player_form/RegPlayerForms.java
```

### 2.2 渐进式普通形态注册

```java
public class RegPlayerForms {
    
    // ========== 渐进式+普通形态（例：蝙蝠） ==========
    public static PlayerFormBase <NAME>_0 = registerPlayerForm(
        new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_0"))
            .setPhase(PlayerFormPhase.PHASE_0)
    );

    public static PlayerFormBase <NAME>_1 = registerPlayerForm(
        new Form_<Name>1(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_1"))
            .setPhase(PlayerFormPhase.PHASE_1)
    );

    public static PlayerFormBase <NAME>_2 = registerPlayerForm(
        new Form_<Name>2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_2"))
            .setPhase(PlayerFormPhase.PHASE_2)
            .setHasSlowFall(true)
            .setOverrideHandAnim(true)
    );

    public static PlayerFormBase <NAME>_3 = registerPlayerForm(
        new Form_<Name>3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_3"))
            .setPhase(PlayerFormPhase.PHASE_3)
            .setHasSlowFall(true)
            .setOverrideHandAnim(true)
    );

    public static PlayerFormGroup <NAME>_FORM = registerPlayerFormGroup(
        new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_form"))
            .addForm(<NAME>_0, 0)
            .addForm(<NAME>_1, 1)
            .addForm(<NAME>_2, 2)
            .addForm(<NAME>_3, 3)
    );
}
```

### 2.3 渐进式野性形态注册

```java
public class RegPlayerForms {
    
    // ========== 渐进式+野性形态（例：豹猫） ==========
    public static PlayerFormBase <NAME>_0 = registerPlayerForm(
        new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_0"))
            .setPhase(PlayerFormPhase.PHASE_0)
    );

    public static PlayerFormBase <NAME>_1 = registerPlayerForm(
        new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_1"))
            .setPhase(PlayerFormPhase.PHASE_1)
    );

    // Phase 2开始有四足特征
    public static PlayerFormBase <NAME>_2 = registerPlayerForm(
        new Form_<Name>2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_2"))
            .setPhase(PlayerFormPhase.PHASE_2)
            .setCanSneakRush(true)
            .setCanRushJump(true)
    );

    // Phase 3完全四足
    public static PlayerFormBase <NAME>_3 = registerPlayerForm(
        new Form_<Name>3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_3"))
            .setPhase(PlayerFormPhase.PHASE_3)
            .setBodyType(PlayerFormBodyType.FERAL)
            .setCanSneakRush(true)
    );

    public static PlayerFormGroup <NAME>_FORM = registerPlayerFormGroup(
        new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_form"))
            .addForm(<NAME>_0, 0)
            .addForm(<NAME>_1, 1)
            .addForm(<NAME>_2, 2)
            .addForm(<NAME>_3, 3)
    );
}
```

### 2.4 特殊形态注册

```java
public class RegPlayerForms {
    
    // ========== 特殊+普通形态（例：悦灵） ==========
    public static PlayerFormBase <NAME>_SP = registerPlayerForm(
        new Form_<Name>SP(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_sp"))
            .setPhase(PlayerFormPhase.PHASE_SP)
            .setHasSlowFall(true)
    );

    public static PlayerFormGroup <NAME>_FORM = registerPlayerFormGroup(
        new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_form"))
            .addForm(<NAME>_SP, 5)  // SP形态索引通常为5
    );

    // ========== 特殊+野性形态（例：野性猫） ==========
    public static PlayerFormBase <NAME>_SP = registerPlayerForm(
        new Form_<Name>SPFeral(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_sp"))
            .setPhase(PlayerFormPhase.PHASE_SP)
            .setBodyType(PlayerFormBodyType.FERAL)
    );

    public static PlayerFormGroup <NAME>_FORM = registerPlayerFormGroup(
        new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "<name>_form"))
            .addForm(<NAME>_SP, 5)
    );
}
```

### 2.5 属性设置链

```java
.setPhase(PlayerFormPhase.PHASE_0~3/SP)     // 形态阶段 [必需]
.setBodyType(PlayerFormBodyType.FERAL)      // 四足形态
.setHasSlowFall(true)                       // 缓降能力
.setOverrideHandAnim(true)                  // 覆盖手部动画
.setCanSneakRush(true)                      // 潜行冲刺（四足形态）
.setCanRushJump(true)                       // 冲刺跳跃
```

---

## 步骤3: 创建Origins配置

### 3.1 形态定义文件
```
src/main/resources/data/shape-shifter-curse/origins/form_<name>_<phase>.json
```

**模板说明**: 
- **非Feral形态**: 以 `form_ocelot_0.json` 为模板，powers默认**留空**
- **Feral形态**: 以 `form_ocelot_3.json` 为模板，powers默认**留空**

**非Feral形态模板 (form_<name>_<phase>.json):**
```json
{
    "powers": [],
    "icon": {
        "item": "minecraft:player_head"
    },
    "order": 0,
    "unchoosable": true,
    "impact": 0
}
```

**Feral形态模板 (form_<name>_3.json):**
```json
{
    "powers": [],
    "icon": {
        "item": "minecraft:player_head"
    },
    "order": 0,
    "unchoosable": true,
    "impact": 0
}
```

**参考模板位置:**
- 非Feral: `src/main/resources/data/shape-shifter-curse/origins/form_ocelot_0.json`
- Feral: `src/main/resources/data/shape-shifter-curse/origins/form_ocelot_3.json`

**可选powers（按需添加）:**
```json
{
    "powers": [
        "shape-shifter-curse:no_render_arm",
        "shape-shifter-curse:form_<name>_<phase>_scale",
        "shape-shifter-curse:slow_falling",
        "shape-shifter-curse:form_disable_chest_armor",
        "shape-shifter-curse:form_disable_leg_armor",
        "shape-shifter-curse:form_disable_feet_armor",
        "shape-shifter-curse:prevent_ranged_weapon_use",
        "shape-shifter-curse:no_shield"
    ],
    "icon": {
        "item": "minecraft:player_head"
    },
    "order": 0,
    "unchoosable": true,
    "impact": 0
}
```

**渐进式普通Phase 3示例:**
```json
{
    "powers": [
        "shape-shifter-curse:no_render_arm",
        "shape-shifter-curse:form_<name>_3_scale",
        "shape-shifter-curse:slow_falling",
        "shape-shifter-curse:form_vegetarian"
    ],
    "icon": {
        "item": "minecraft:player_head"
    },
    "order": 0,
    "unchoosable": true,
    "impact": 0
}
```

**渐进式野性Phase 3示例:**
```json
{
    "powers": [
        "shape-shifter-curse:no_render_arm",
        "shape-shifter-curse:form_<name>_3_scale",
        "shape-shifter-curse:form_disable_leg_armor",
        "shape-shifter-curse:form_disable_feet_armor",
        "shape-shifter-curse:prevent_ranged_weapon_use"
    ],
    "icon": {
        "item": "minecraft:player_head"
    },
    "order": 0,
    "unchoosable": true,
    "impact": 0
}
```

**特殊形态SP示例:**
```json
{
    "powers": [
        "shape-shifter-curse:no_render_arm",
        "shape-shifter-curse:form_<name>_sp_scale",
        "shape-shifter-curse:slow_falling"
    ],
    "icon": {
        "item": "minecraft:player_head"
    },
    "order": 0,
    "unchoosable": true,
    "impact": 0
}
```

### 3.2 Power能力文件
```
src/main/resources/data/shape-shifter-curse/powers/
```

**缩放**（form_<name>_3_scale.json）：
```json
{
    "type": "shape-shifter-curse:scale",
    "scale": 0.6,
    "eye_scale": 0.7
}
```

**缓降**（slow_falling.json）：
```json
{
    "type": "apoli:modify_falling",
    "velocity": 0.01,
    "take_fall_damage": false
}
```

---

## 步骤4: 创建动画文件

### 4.1 文件位置
```
src/main/resources/assets/shape-shifter-curse/player_animation/<name>_<state>.json
```

### 4.2 渐进式普通形态动画

需要为每个Phase创建独立的动画文件（如果外观有变化）：
- `<name>_0_idle.json` / `<name>_0_walk.json` - Phase 0
- `<name>_1_idle.json` / `<name>_1_walk.json` - Phase 1
- `<name>_2_idle.json` / `<name>_2_walk.json` - Phase 2
- `<name>_3_idle.json` / `<name>_3_walk.json` - Phase 3

如果外观相似，可以复用动画。

### 4.3 渐进式野性形态动画

Phase 0-2使用普通动画：
- `<name>_0_idle.json` / `<name>_0_walk.json`
- `<name>_1_idle.json` / `<name>_1_walk.json`
- `<name>_2_idle.json` / `<name>_2_walk.json`

Phase 3使用四足动画（可以复用form_feral_common_*或自定义）：
- `<name>_3_idle.json` - 四足闲置
- `<name>_3_walk.json` - 四足行走
- `<name>_3_sneak_idle.json` - 四足坐下
- `<name>_3_sneak_walk.json` - 四足潜行走动
- `<name>_3_run.json` - 四足奔跑

### 4.4 特殊形态动画

SP形态独立一套动画：
- `<name>_sp_idle.json`
- `<name>_sp_walk.json`
- ...

### 4.5 动画文件模板

```json
{
    "format_version": "1.8.0",
    "animations": {
        "<name>_<phase>_<state>": {
            "loop": true,
            "bones": {
                "head": {
                    "position": { "vector": [0, -9, 1] }
                },
                "torso": {
                    "rotation": { "vector": [45, 0, 0] },
                    "position": { "vector": [0, -8, 0] }
                },
                "rightArm": {
                    "rotation": { "vector": [0, 0, 0] },
                    "position": { "vector": [3, -10, 0] }
                },
                "leftArm": {
                    "rotation": { "vector": [0, 0, 0] },
                    "position": { "vector": [-3, -10, 0] }
                },
                "rightLeg": {
                    "position": { "vector": [0, 0, 8] }
                },
                "leftLeg": {
                    "position": { "vector": [0, 0, 8] }
                }
            }
        }
    }
}
```

### 4.6 推荐动画文件列表

**普通形态（两足）:**
| 状态 | 文件名 |
|------|--------|
| idle | `<name>_<phase>_idle.json` |
| walk | `<name>_<phase>_walk.json` |
| sneak_idle | `<name>_<phase>_sneak_idle.json` |
| sneak_walk | `<name>_<phase>_sneak_walk.json` |
| jump | `<name>_<phase>_jump.json` |
| attack | `<name>_<phase>_attack.json` |
| digging | `<name>_<phase>_digging.json` |

**野性形态（四足）:**
| 状态 | 文件名 | 说明 |
|------|--------|------|
| idle | `<name>_3_idle.json` | 站立闲置 |
| sneak_idle | `<name>_3_sneak_idle.json` | 坐下 |
| walk | `<name>_3_walk.json` | 行走 |
| sneak_walk | `<name>_3_sneak_walk.json` | 潜行走 |
| run | `<name>_3_run.json` | 奔跑 |
| sneak_rush | `<name>_3_sneak_rush.json` | 潜行冲刺 |
| jump | `<name>_3_jump.json` | 跳跃 |
| attack | `<name>_3_attack.json` | 攻击 |
| dig | `<name>_3_dig.json` | 挖掘 |

---

## 步骤4.5: 模型与动画模板（推荐做法）

### 4.5.1 模型文件模板

**非Feral形态（普通两足）模型模板:**
- **复制来源**: `assets/orif-defaults/geo/form_ocelot_0.geo.json`
- **新文件路径**: `assets/orif-defaults/geo/form_<name>_<phase>.geo.json`
- **修改内容**: 
  - 重命名骨骼（如 ear_a_0 → ear_a_<phase>）
  - 调整立方体位置、大小以符合新形态外观
  - 保持 `bipedHead`, `bipedBody`, `bipedLeftArm`, `bipedRightArm`, `bipedLeftLeg`, `bipedRightLeg` 等基础骨骼名不变

**Feral形态（四足）模型模板:**
- **复制来源**: `assets/orif-defaults/geo/form_ocelot_3.geo.json`
- **新文件路径**: `assets/orif-defaults/geo/form_<name>_3.geo.json`
- **修改内容**:
  - 保持四足结构（前肢和后肢分开）
  - 调整尾巴、耳朵等特征部位
  - 确保骨骼层级关系正确

### 4.5.2 动画文件模板

**非Feral形态动画模板:**
- **复制来源**: `assets/shape-shifter-curse/player_animation/ocelot_2_sneak_idle.json` 或其他ocelot_0/1/2动画
- **新文件路径**: `assets/shape-shifter-curse/player_animation/<name>_<phase>_<state>.json`
- **修改内容**:
  ```json
  {
      "format_version": "1.8.0",
      "animations": {
          "<name>_<phase>_<state>": {
              "loop": true,
              "bones": {
                  "head": { "position": { "vector": [0, -10, 1] } },
                  "torso": { 
                      "rotation": { "vector": [32.5, 0, 0] },
                      "position": { "vector": [0, -10, 3] }
                  },
                  "rightArm": { 
                      "rotation": { "vector": [0, 0, 0] },
                      "position": { "vector": [2, -10, 0] }
                  },
                  "leftArm": { 
                      "rotation": { "vector": [0, 0, 0] },
                      "position": { "vector": [-2, -10, 0] }
                  },
                  "rightLeg": { 
                      "rotation": { "vector": [0, 0, 0] },
                      "position": { "vector": [-2, -6, 12] }
                  },
                  "leftLeg": { 
                      "rotation": { "vector": [0, 0, 0] },
                      "position": { "vector": [2, -6, 12] }
                  }
              }
          }
      }
  }
  ```

**Feral形态动画模板:**
- **复制来源**: `assets/shape-shifter-curse/player_animation/form_feral_common_*.json`
- **新文件路径**: `assets/shape-shifter-curse/player_animation/<name>_3_<state>.json`
- **建议**: 直接复用 `form_feral_common_*` 系列动画作为起点，然后根据需要调整

### 4.5.3 快速创建清单

**非Feral形态:**
1. 复制 `form_ocelot_0.geo.json` → `form_<name>_<phase>.geo.json`
2. 复制 `ocelot_2_sneak_idle.json` 等动画文件 → `<name>_<phase>_*.json`
3. 修改骨骼名称和位置以匹配新形态
4. 调整动画关键帧数值

**Feral形态:**
1. 复制 `form_ocelot_3.geo.json` → `form_<name>_3.geo.json`
2. 复制 `form_feral_common_*.json` → `<name>_3_*.json`（或直接引用）
3. 在Java类中使用Form_FeralBase提供的控制器
4. 如需自定义动画，覆写getAnimStateController方法

---

## 步骤4.6: Geo模型文件与Furs配置

### 4.6.1 Geo模型文件

**普通形态模型:**
- **来源**: `src/main/resources/assets/orif-defaults/geo/form_ocelot_0.geo.json`
- **目标**: `src/main/resources/assets/orif-defaults/geo/form_<name>_<phase>.geo.json`
- **修改**: 复制后重命名骨骼（如 `ear_a_0` → `ear_a_<phase>`），调整模型细节

**野性形态模型:**
- **来源**: `src/main/resources/assets/orif-defaults/geo/form_ocelot_3.geo.json`
- **目标**: `src/main/resources/assets/orif-defaults/geo/form_<name>_3.geo.json`
- **修改**: 复制后调整四足结构、尾巴、耳朵等部位

### 4.6.2 Furs配置文件

**普通形态Furs:**
- **来源**: `src/main/resources/assets/originalfur/furs/shape-shifter-curse.form_ocelot_0.json`
- **目标**: `src/main/resources/assets/originalfur/furs/shape-shifter-curse.form_<name>_<phase>.json`
- **内容模板**:
```json
{
  "id": "shape-shifter-curse:form_<name>_<phase>",
  "display": "<Name> Form Phase <Phase>",
  "model": "orif-defaults:geo/form_<name>_<phase>.geo.json",
  "texture": "orif-defaults:textures/form_<name>_<phase>.png",
  "animation": "shape-shifter-curse:player_animation/<name>_<phase>_idle.json",
  "parts": [
    {"id": "head", "display": "Head"},
    {"id": "body", "display": "Body"},
    {"id": "leftArm", "display": "Left Arm"},
    {"id": "rightArm", "display": "Right Arm"},
    {"id": "leftLeg", "display": "Left Leg"},
    {"id": "rightLeg", "display": "Right Leg"}
  ]
}
```

**野性形态Furs:**
- **来源**: `src/main/resources/assets/originalfur/furs/shape-shifter-curse.form_ocelot_3.json`
- **目标**: `src/main/resources/assets/originalfur/furs/shape-shifter-curse.form_<name>_3.json`
- **内容模板**:
```json
{
  "id": "shape-shifter-curse:form_<name>_3",
  "display": "<Name> Form Phase 3",
  "model": "orif-defaults:geo/form_<name>_3.geo.json",
  "texture": "orif-defaults:textures/form_<name>_3.png",
  "animation": "shape-shifter-curse:player_animation/<name>_3_idle.json",
  "parts": [
    {"id": "head", "display": "Head"},
    {"id": "body", "display": "Body"},
    {"id": "leftFrontLeg", "display": "Left Front Leg"},
    {"id": "rightFrontLeg", "display": "Right Front Leg"},
    {"id": "leftHindLeg", "display": "Left Hind Leg"},
    {"id": "rightHindLeg", "display": "Right Hind Leg"},
    {"id": "tail", "display": "Tail"}
  ]
}
```

---

## 步骤4.7: Origin Layer注册（重要）

### 4.7.1 修改Origin Layer文件

**文件位置**: `src/main/resources/data/origins/origin_layers/origin.json`

**操作**: 在每个新形态的origins配置完成后，必须在origin layer中注册对应的origin ID，否则形态能力无法生效。

**添加内容示例**:
```json
{
  "replace": false,
  "origins": [
    "origins:human",
    "shape-shifter-curse:original_shifter",
    "shape-shifter-curse:bat_0",
    "shape-shifter-curse:bat_1",
    "shape-shifter-curse:bat_2",
    "shape-shifter-curse:bat_3",
    "shape-shifter-curse:ocelot_0",
    "shape-shifter-curse:ocelot_1",
    "shape-shifter-curse:ocelot_2",
    "shape-shifter-curse:ocelot_3",
    "shape-shifter-curse:<name>_0",
    "shape-shifter-curse:<name>_1",
    "shape-shifter-curse:<name>_2",
    "shape-shifter-curse:<name>_3"
  ]
}
```

**注意**: 
- 使用 `"replace": false` 来追加而非替换原有配置
- 每个形态的origin ID格式为 `<namespace>:form_<name>_<phase>`
- 必须在此处注册，否则Origins模组无法识别该形态，powers将无法生效

---

## 步骤5: 添加语言文件

### 5.1 文件位置
```
src/main/resources/assets/shape-shifter-curse/lang/en_us.json
src/main/resources/assets/shape-shifter-curse/lang/zh_cn.json
```

### 5.2 渐进式形态语言条目

```json
{
    "codex.form.shape-shifter-curse.<name>_0.title": "\nForm Title",
    "codex.form.shape-shifter-curse.<name>_0.appearance": "Appearance description...",
    "codex.form.shape-shifter-curse.<name>_0.pros": "Abilities...",
    "codex.form.shape-shifter-curse.<name>_0.cons": "Drawbacks...",
    "codex.form.shape-shifter-curse.<name>_0.instincts": "Instincts...",
    
    "codex.form.shape-shifter-curse.<name>_1.title": "\nForm Title",
    "codex.form.shape-shifter-curse.<name>_1.appearance": "...",
    
    "codex.form.shape-shifter-curse.<name>_2.title": "\nForm Title",
    "codex.form.shape-shifter-curse.<name>_2.appearance": "...",
    
    "codex.form.shape-shifter-curse.<name>_3.title": "\nForm Title",
    "codex.form.shape-shifter-curse.<name>_3.appearance": "...",
    
    "effect.shape-shifter-curse.to_<name>_0_effect": "...Strange creature?",
    "effect.shape-shifter-curse.to_<name>_0_effect.description": "Effect description..."
}
```

### 5.3 特殊形态语言条目

```json
{
    "codex.form.shape-shifter-curse.<name>_sp.title": "\nSpecial Form Title",
    "codex.form.shape-shifter-curse.<name>_sp.appearance": "Appearance...",
    "codex.form.shape-shifter-curse.<name>_sp.pros": "Abilities...",
    "codex.form.shape-shifter-curse.<name>_sp.cons": "Drawbacks...",
    "codex.form.shape-shifter-curse.<name>_sp.instincts": "Instincts...",
    
    "effect.shape-shifter-curse.to_<name>_sp_effect": "...Strange creature?",
    "effect.shape-shifter-curse.to_<name>_sp_effect.description": "Effect description..."
}
```

---

## 步骤6: （可选）状态效果

### 6.1 渐进式形态效果

```java
package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

public class <Name>StatusEffect extends BaseTransformativeStatusEffect {
    
    public <Name>StatusEffect() {
        super(RegPlayerForms.<NAME>_0, StatusEffectCategory.NEUTRAL, 0xRRGGBB, false);
    }

    @Override
    public void ActiveEffect(ServerPlayerEntity player) {
        // 效果激活时变身到Phase 0
    }
}
```

注册（RegTStatusEffect.java）：
```java
public static StatusEffect TO_<NAME>_0_EFFECT = register(
    "to_<name>_0_effect", 
    new <Name>StatusEffect()
);
```

### 6.2 特殊形态效果

```java
public class <Name>SPStatusEffect extends BaseTransformativeStatusEffect {
    
    public <Name>SPStatusEffect() {
        super(RegPlayerForms.<NAME>_SP, StatusEffectCategory.NEUTRAL, 0xRRGGBB, false);
    }
}
```

---

## 动画控制器参考

### 普通形态控制器

| 控制器 | 用途 | 参数 |
|--------|------|------|
| `OneAnimController` | 单一动画 | `anim` |
| `WithSneakAnimController` | 普通+潜行双动画 | `normalAnim`, `sneakAnim` |
| `RideAnimController` | 骑乘动画 | `rideAnim`, `idleAnim` |
| `SwimAnimController` | 游泳动画 | `floatAnim`, `swimAnim` |

### 野性形态控制器

| 控制器 | 用途 | 参数 |
|--------|------|------|
| `SneakRushAnimController` | 潜行冲刺 | `normalAnim`, `sneakWalkAnim`, `sneakRushAnim` |
| `UseItemAnimControllerPro` | 使用物品 | 多参数 |

### Form_FeralBase提供的控制器

可以复用的静态控制器：
- `IDLE_CONTROLLER` - 闲置（带潜行）
- `WALK_CONTROLLER` / `WALK_CONTROLLER_SNEAK_RUSH` - 行走
- `SPRINT_CONTROLLER` / `SPRINT_CONTROLLER_SNEAK_RUSH` - 疾跑
- `SWIM_CONTROLLER` - 游泳
- `MINING_CONTROLLER` - 挖掘
- `ATTACK_CONTROLLER` - 攻击
- `SLEEP_CONTROLLER` - 睡觉
- `FALL_FLYING_CONTROLLER` - 鞘翅飞行
- `CLIMB_CONTROLLER` - 攀爬
- `JUMP_CONTROLLER` - 跳跃
- `FALL_CONTROLLER` - 下落
- `USE_ITEM_CONTROLLER` - 使用物品
- `RIDE_CONTROLLER` - 骑乘

---

## 检查清单

### 渐进式普通形态
- [ ] 创建4个Phase的Java类（继承PlayerFormBase）
- [ ] 在RegPlayerForms注册所有4个形态和形态组
- [ ] 创建4个Origins配置文件（form_<name>_0~3.json）
- [ ] 创建Power能力文件
- [ ] 创建动画文件（可能各Phase不同）
- [ ] 添加语言文件条目（4个Phase + 效果）
- [ ] （可选）创建状态效果类

### 渐进式野性形态
- [ ] 创建Phase 0-2 Java类（继承PlayerFormBase）
- [ ] 创建Phase 3 Java类（继承Form_FeralBase，setBodyType(FERAL)）
- [ ] 在RegPlayerForms注册所有4个形态和形态组
- [ ] 创建4个Origins配置文件
- [ ] 创建Power能力文件（Phase 3包含disable armor等）
- [ ] 创建普通动画（Phase 0-2）和四足动画（Phase 3）
- [ ] 添加语言文件条目
- [ ] （可选）创建状态效果类

### 特殊普通形态
- [ ] 创建SP Java类（继承PlayerFormBase）
- [ ] 在RegPlayerForms注册SP形态（groupIndex=5）
- [ ] 创建SP Origins配置文件
- [ ] 创建Power能力文件
- [ ] 创建SP动画文件
- [ ] 添加语言文件条目
- [ ] （可选）创建状态效果类

### 特殊野性形态
- [ ] 创建SP Java类（继承Form_FeralBase，setBodyType(FERAL)）
- [ ] 在RegPlayerForms注册SP形态（groupIndex=5）
- [ ] 创建SP Origins配置文件
- [ ] 创建Power能力文件
- [ ] 创建SP四足动画文件
- [ ] 添加语言文件条目
- [ ] （可选）创建状态效果类

---

## 常用代码片段

### 形态ID生成
```java
ShapeShifterCurseFabric.identifier("<name>_idle")
// 结果: shape-shifter-curse:<name>_idle
```

### 动画数据构造
```java
new AnimUtils.AnimationHolderData(id, speed, priority)
// speed: 播放速度倍率
// priority: 优先级（高优先级覆盖低优先级）
```

### 获取当前形态
```java
PlayerFormBase form = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
```

### 切换形态
```java
FormAbilityManager.applyForm(player, RegPlayerForms.<NAME>_1);
```

### 检查是否为四足形态
```java
if (form.getBodyType() == PlayerFormBodyType.FERAL) {
    // 四足形态逻辑
}
```

---

**命名规范**: 
- Java类: `Form<Name><Phase>` 如 `FormBat3`, `FormOcelotSP`
- 形态ID: `<name>_<phase>` 如 `bat_3`, `ocelot_sp`
- 文件名: 小写下划线 如 `form_bat_3.json`, `bat_3_idle.json`
