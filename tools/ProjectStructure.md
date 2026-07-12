# Shape Shifter Curse Fabric 项目框架文档

> 本文档用于之后快速读取项目实现。更新时间：2026-06-21。  
> 统计基于当前工作区文件树与 `fabric.mod.json`、`gradle.properties`、主要注册类。

## 1. 项目基本信息

| 项目 | 当前值 |
| --- | --- |
| 模组名称 | Shape Shifter Curse |
| Mod ID | `shape-shifter-curse` |
| 主包名 | `net.onixary.shapeShifterCurseFabric` |
| Minecraft | `1.20.1` |
| Yarn mappings | `1.20.1+build.10` |
| Fabric Loader | 构建使用 `0.16.10`，`fabric.mod.json` 要求 `>=0.12.3` |
| Fabric API | `0.92.3+1.20.1` |
| Java | 17 |
| Gradle 插件 | Fabric Loom `1.9-SNAPSHOT` |
| 模组版本 | `1.9.2` |
| License | MIT |
| 作者 | onixary, XuHaoNan |

## 2. 模组定位

Shape Shifter Curse 是一个以“玩家逐渐变形成不同生物形态”为核心的 Fabric 模组。当前实现同时包含：

- 玩家形态系统：内置阶段形态、特殊形态、数据包动态形态。
- Apoli 扩展能力：自定义 Power、Condition、Action、Item Condition。
- 内置 Origins 分支：项目内 fork/集成 Origins 的起源层、起源选择、Badge、网络与组件系统。
- 变形触发链：诅咒之月、变形实体、药水/状态效果、催化剂/治疗流程。
- 渲染与动画：AzureLib 模型、Player Animation Lib、形态渲染、毛发渐变 shader。
- 饰品与兼容：Trinkets/Curios 优先级接口、compileOnly 兼容若干第三方模组。

## 3. 根目录概览

```text
shape-shifter-curse-fabric/
├─ build.gradle                         # Gradle 构建脚本与依赖声明
├─ gradle.properties                    # MC、Loader、Mod、依赖版本
├─ settings.gradle
├─ README.md
├─ LICENSE.txt / LICENSE-zh_CN.txt
├─ src/main/java/net/onixary/shapeShifterCurseFabric/
│  ├─ ShapeShifterCurseFabric.java       # 服务端/通用主入口 ModInitializer
│  ├─ client/ShapeShifterCurseFabricClient.java
│  ├─ additional_power/                  # Apoli 扩展能力、条件、动作
│  ├─ player_form/                       # 玩家形态核心系统
│  ├─ integration/origins/               # 内置 Origins 系统
│  ├─ mixin/                             # 主模组 Mixin
│  ├─ items/, blocks/, entity/           # 自定义内容注册
│  ├─ form_giving_custom_entity/         # 给予形态用的特殊实体
│  ├─ player_animation/                  # 玩家动画系统
│  ├─ render/                            # 形态渲染、渲染层、shader 技术层
│  ├─ mana/, minion/, status_effects/    # 法力、召唤物、状态效果系统
│  └─ util/, networking/, config/        # 工具、网络、配置
├─ src/main/resources/
│  ├─ fabric.mod.json
│  ├─ shape-shifter-curse.accesswidener
│  ├─ shape-shifter-curse.mixins.json
│  ├─ origins.mixins.json
│  ├─ ssc-misc.mixins.json
│  ├─ assets/                            # 客户端资源
│  └─ data/                              # 数据包内容
├─ custom_form_pack_example/             # 外部自定义形态示例包
├─ 3d_models/                            # Blockbench、Aseprite 等源素材
├─ tools/                                # 开发辅助脚本与文档
├─ dev/                                  # 设计草案、移植/重构计划
├─ libs/                                 # 本地 jar，例如 mixinextras
└─ PatronServer/                         # Patron 数据/资源服务原型
```

## 4. Fabric 元数据与入口点

入口点来自 `src/main/resources/fabric.mod.json`：

| 类型 | 类 |
| --- | --- |
| `main` | `ShapeShifterCurseFabric` |
| `main` | `integration.origins.Origins` |
| `client` | `client.ShapeShifterCurseFabricClient` |
| `client` | `integration.origins.OriginsClient` |
| `modmenu` | `config.ConfigIntegration` |
| `cardinal-components-entity` | Origins 组件、玩家形态、玩家本能、皮肤设置、召唤物、法力组件 |
| `calio:ordered-resource-listener` | `integration.origins.Origins` |

当前 Mixin 配置：

- `shape-shifter-curse.mixins.json`
- `origins.mixins.json`
- `ssc-misc.mixins.json`

当前 access widener：

- `shape-shifter-curse.accesswidener`

注意：旧文档中的 `OriginalFur` / `OriginalFurClient` 独立入口点当前不在 `fabric.mod.json` 中；相关渲染能力现在位于 `render/`、`render/form_render/`、`render/render_layer/` 等包内，并由主客户端入口初始化。

## 5. Java 主包目录统计

| 包目录 | Java 文件数 | 主要职责 |
| --- | ---: | --- |
| `additional_power` | 94 | Apoli 自定义 Power、Condition、Action、Item Condition |
| `advancement` | 17 | 自定义进度触发器 |
| `blocks` | 4 | 自定义方块注册与行为 |
| `client` | 2 | 客户端入口、粒子/按键/渲染初始化 |
| `command` | 4 | `/shape-shifter-curse` 命令与参数类型 |
| `config` | 5 | AutoConfig/Cloth Config 与 ModMenu 集成 |
| `cursed_moon` | 1 | 诅咒之月状态/逻辑 |
| `custom_ui` | 15 | 变形者之书、启动书等 UI，含 `ui_part/` |
| `data` | 2 | 静态参数/数据定义 |
| `entity` | 3 | 自定义实体与弹射物渲染注册，含 `projectile/` |
| `features` | 3 | 世界特征 |
| `form_giving_custom_entity` | 13 | 给予形态用实体，含 `axolotl/bat/ocelot/spider/wolf` |
| `integration` | 71 | 当前主要是内置 Origins 系统 |
| `items` | 48 | 自定义物品、护甲、工具、饰品 |
| `mana` | 10 | 法力组件、属性、tick、客户端注册 |
| `minion` | 12 | 召唤物系统，含 `mobs/` |
| `mixin` | 95 | 主模组与兼容 Mixin |
| `networking` | 4 | C2S/S2C 数据包 |
| `player_animation` | 28 | 动画注册、v2、v3 FSM/控制器 |
| `player_form` | 52 | 玩家形态核心、动态形态、能力、本能、皮肤、变形 |
| `recipes` | 8 | 自定义配方与动态酿造，含 `alter/` |
| `render` | 16 | 形态渲染、渲染层、技术渲染 |
| `screen_effect` | 2 | 屏幕叠加/变形特效 |
| `status_effects` | 17 | 变形状态效果与其他效果 |
| `team` | 2 | 队伍/阵营相关逻辑 |
| `util` | 28 | 通用工具、饰品工具、接口 |

空壳/占位目录：

- `accessors/` 当前无 Java 文件。
- `misc/` 当前无 Java 文件。

## 6. 初始化链路

`ShapeShifterCurseFabric.onInitialize()` 负责通用侧初始化，主要顺序如下：

1. 注册物品、方块、变形实体刷怪蛋、状态效果、事件处理。
2. 注册形态给予实体、实体生成、附着事件、自定义实体。
3. 注册变形动画、Apoli 扩展 Condition/Action/Power。
4. 注册召唤物、攻击数据追踪、配置文件。
5. 注册 C2S 网络包、屏幕特效、药水、配方、法力、默认饰品。
6. 在服务器启动时初始化形态能力世界引用、Patron、变形管理器、饰品系统。
7. 注册数据包 reload listener：
   - `FormDataPackReloadListener`
   - `TrinketDataPackReloadListener`
   - `BrewingRecipeReloadListener`
8. 数据包 reload 结束后同步动态形态，并修复不存在的当前形态。
9. 注册命令和参数类型。
10. 玩家断开时清理召唤物。
11. 注册服务端 tick、睡眠结束、允许带变形效果睡觉等事件。

`ShapeShifterCurseFabricClient.onInitializeClient()` 负责客户端侧初始化：

1. 注册变形实体和召唤物渲染器。
2. 注册 S2C 网络包。
3. 注册 `fur_gradient_remap` shader、毛发渐变渲染层。
4. 注册 AzureLib 护甲渲染器。
5. 注册法力客户端、弹射物渲染、形态渲染工具。
6. 读取形态颜色配置。
7. 注册客户端 tick：法力 tick、ItemStore、Levitate、CustomEdible 等。
8. 注册按键：
   - `make_sound`
   - `toggle_clip_at_ledge`
9. 初始化方块客户端资源与 Patron 客户端资源。

## 7. 玩家形态系统

核心包：`player_form/`

关键类：

- `RegPlayerForms`：注册内置形态和形态组，并维护动态形态 registry。
- `PlayerFormBase`：形态基础数据与能力开关。
- `PlayerFormGroup`：形态路线/阶段分组。
- `PlayerFormDynamic`：从数据包构建的动态形态。
- `FormDataPackReloadListener`：从数据包 reload 动态形态。
- `PlayerFormPhase`：阶段枚举，包含 `PHASE_0` 到 `PHASE_3` 与 `PHASE_SP`。

子系统：

- `ability/`：玩家形态组件与能力管理，核心为 `FormAbilityManager`、`RegPlayerFormComponent`。
- `effect/`：变形过程效果。
- `forms/`：内置特殊实现类，例如蝙蝠高阶段、蜘蛛、雪狐、Allay、野猫。
- `instinct/`：本能系统 tick 与组件。
- `new_form_system/`：新形态系统接口/占位演进。
- `skin/`：皮肤设置组件。
- `transform/`：变形管理器，处理服务器 tick 中的变形状态更新。

当前内置形态组：

| 组 ID | 内含形态 |
| --- | --- |
| `original_form` | `original_before_enable` |
| `base_form` | `original_shifter` |
| `bat_form` | `bat_0` ~ `bat_3` |
| `axolotl_form` | `axolotl_0` ~ `axolotl_3` |
| `ocelot_form` | `ocelot_0` ~ `ocelot_3` |
| `familiar_fox_form` | `familiar_fox_0` ~ `familiar_fox_3` |
| `snow_fox_form` | `snow_fox_0` ~ `snow_fox_3` |
| `anubis_wolf_form` | `anubis_wolf_0` ~ `anubis_wolf_3` |
| `spider_form` | `spider_0` ~ `spider_3` |
| `allay_form` | `allay_sp` |
| `feral_cat_form` | `feral_cat_sp` |

动态形态相关约定：

- 动态形态 ID 记录在 `RegPlayerForms.dynamicPlayerForms`。
- 动态形态组 ID 记录在 `RegPlayerForms.dynamicPlayerFormGroups`。
- `ssc-patron` 命名空间受 `PatronNameSpace` 保护，reload 清理时保留。
- `custom_form_pack_example/` 提供外部数据包/资源包样例。

## 8. Apoli 扩展能力系统

核心包：`additional_power/`

注册入口：

- `AdditionalPowers.register()`
- `AdditionalEntityConditions.register()`
- `AdditionalEntityActions.register()`
- `AdditionalItemCondition.register()`

能力类别示例：

| 类别 | 示例类 |
| --- | --- |
| 移动/姿态 | `CrawlingPower`、`AlwaysSprintSwimmingPower`、`LevitatePower`、`TripleJumpPower`、`KeepSneakingPower` |
| 战斗 | `ChargePower`、`CriticalDamageModifierPower`、`VirtualShieldPower`、`VirtualTotemPower`、`AlwaysSweepingPower` |
| 环境/生存 | `BreathingUnderWaterPower`、`HoldBreathPower`、`WaterFlexibilityPower`、`PowderSnowWalkerPower` |
| 食物/物品 | `CustomEdiblePower`、`ItemStorePower`、`ModifyFoodHealPower`、`ModifyPotionStackPower` |
| 属性/缩放 | `ManaAttributePower`、`ConditionedManaAttributePower`、`ScalePower`、`ConditionScalePower`、`DelayAttributePower` |
| 渲染/动画 | `NoRenderArmPower`、`RenderTrinketsSlotPower`、`PlayPowerAnimationAction`、`FormCameraBobbingPower` |
| 生物关系 | `FoxFriendlyPower`、`TWolfFriendlyPower`、`PillagerFriendlyPower`、`WitchFriendlyPower`、`ScareVillagerPower` |
| 方块/掉落 | `ModifyBlockDropPower`、`ModifyEntityLootPower`、`WebBridgeAction`、`SnowballBlockTransformPower` |

这些类型通常通过 `data/*/powers/*.json` 使用。

## 9. 内置 Origins 系统

核心包：`integration/origins/`

当前项目没有依赖外部 Origins 模组，而是内置一套 Origins 分支实现。`fabric.mod.json` 中也声明 `breaks.origins`，避免同时加载外部 Origins。

主要模块：

- `Origins` / `OriginsClient`：通用与客户端入口。
- `origin/`：`Origin`、`OriginLayer`、`OriginLayers`、`OriginManager`、`OriginRegistry`、`OriginUpgrade`。
- `component/`：`OriginComponent`、`PlayerOriginComponent`。
- `screen/`：`ChooseOriginScreen`、`ViewOriginScreen`、`OriginDisplayScreen`、`WaitForNextLayerScreen`。
- `badge/`：Sprite、Keybind、Tooltip、CraftingRecipe Badge。
- `power/`：Origins 侧 Power 与 Entity Condition 注册。
- `registry/`：Origins 内容注册，包括方块、物品、实体、附魔、组件、Tag、Loot、DamageSource。
- `networking/`：Origins 侧 C2S/S2C 包。
- `mixin/`：Origins 行为注入。
- `data/`、`util/`、`content/`、`enchantment/`、`entity/`、`command/`。

## 10. 变形实体、召唤物与实体系统

形态给予实体位于 `form_giving_custom_entity/`，当前注册：

| EntityType ID | 类 | SpawnGroup |
| --- | --- | --- |
| `t_bat` | `TransformativeBatEntity` | `AMBIENT` |
| `t_axolotl` | `TransformativeAxolotlEntity` | `AXOLOTLS` |
| `t_ocelot` | `TransformativeOcelotEntity` | `MONSTER` |
| `t_wolf` | `TransformativeWolfEntity` | `CREATURE` |
| `t_spider` | `TransformativeSpiderEntity` | `MONSTER` |

相关注册：

- `RegTransformativeEntity`
- `RegTransformativeEntitySpawnEgg`
- `TransformativeEntitySpawning`
- 客户端渲染器在 `ShapeShifterCurseFabricClient.registerEntityModels()` 注册。

召唤物系统位于 `minion/`：

- `MinionRegister`：通用注册与玩家断线清理。
- `MinionRegisterClient`：客户端渲染注册。
- `mobs/`：召唤物实体实现。

## 11. 渲染与动画

动画包：`player_animation/`

- `form_animation/`：早期变形动画注册，`AnimationTransform.registerAnims()` 当前仍由主入口调用。
- `v2/`：第二版动画系统。
- `v3/`：较新的动画 FSM/控制器结构。
  - `AnimFSM/`
  - `AnimStateController/`
  - `AnimStateControllerDP/`

渲染包：`render/`

- `form_render/`：形态模型/颜色/资源加载与客户端初始化。
- `render_layer/`：`FurGradientRenderLayer` 等渲染层。
- `tech/`：较底层的模型/渲染技术支持。

客户端 shader：

- 注册 ID：`shape-shifter-curse:fur_gradient_remap`
- 资源目录：`assets/shape-shifter-curse/shaders/core|post|program`
- 当前用于毛发渐变/颜色重映射相关渲染。

AzureLib：

- 用于护甲、实体、形态模型资源。
- 客户端入口注册 `MorphscaleArmorRenderer` 与 `NetheriteMorphscaleArmorRenderer`。

## 12. 物品、方块、药水、配方

物品包：`items/`

- `RegCustomItem`：自定义物品注册入口。
- `RegCustomPotions`：药水与药水配方注册。
- `armors/`：Morphscale 与 Netherite Morphscale 护甲及渲染。
- `tools/`：辅助工具、武器、采掘爪等。
- `trinkets/`：Trinkets 饰品实现。
- `accessory/`：通用饰品物品抽象。

方块包：`blocks/`

- `RegCustomBlock`：方块注册与客户端初始化。
- 资源侧当前有 `dew_covered_cobweb`、`moondust_crystal_grit`、`temp_web_bridge`、`web_composter` 等 blockstate。

配方包：`recipes/`

- `RecipeSerializerRegister`
- `BrewingRecipeReloadListener`
- `alter/` 子包用于额外/替换类配方逻辑。

## 13. 状态效果、法力、本能与屏幕特效

状态效果包：`status_effects/`

- `RegTStatusEffect`
- `RegTStatusPotionEffect`
- `RegOtherStatusEffects`
- `attachment/EffectManager`：变形效果附着/激活管理。
- `transformative_effects/`：变形相关状态效果。
- `other_effects/`：其他效果。

法力包：`mana/`

- `RegManaComponent`：CCA 法力组件。
- `ManaRegistries` / `ManaRegistriesClient`：通用与客户端注册。
- `ManaUtils`：服务端/客户端 tick。
- Apoli 侧相关能力在 `additional_power` 中：`ManaTypePower`、`ManaAttributePower`、`ConditionedManaAttributePower`。

本能系统：

- `player_form/instinct/InstinctTicker` 在服务端玩家 tick 中更新。
- `RegPlayerInstinctComponent` 注册玩家本能 CCA 组件。

屏幕特效：

- `screen_effect/TransformOverlay` 在主入口初始化。

## 14. 网络、命令、配置

网络包：`networking/`

- `ModPackets`：包 ID 常量。
- `ModPacketsC2S`：客户端到服务端。
- `ModPacketsS2C`：服务端到客户端。
- `ModPacketsS2CServer`：服务端广播/同步工具。

命令包：`command/`

- `ShapeShifterCurseCommand`
- `FormArgumentType`
- `CustomFormArgumentType`
- `MiscArgumentType.Enum_ArgumentType`

配置包：`config/`

- `PlayerCustomConfig`
- `ClientConfig`
- `CommonConfig`
- `ConfigIntegration`：ModMenu 入口。

配置序列化使用 AutoConfig + `Toml4jConfigSerializer`。

## 15. Mixin 分布

主 Mixin 包：`mixin/`，当前约 95 个 Java 文件。

子包：

- `accessor/`：访问器 Mixin。
- `accessory/`：Trinkets/饰品相关注入。
- `block/`：方块行为注入。
- `compatibility/`：兼容性注入。
- `integration/`：第三方集成注入，例如 AppleSkin、AzureLib、TaCZ。
- `mob/`：生物关系/行为注入。
- `plugin/`：`MixinConfigPlugin`。
- `projectile/`：弹射物相关注入。
- `render/`：渲染相关注入。
- `test/`：开发测试注入。

Mixin 配置拆分为主模组、Origins、misc 三份，方便分域管理。

## 16. 资源目录

客户端资源根：`src/main/resources/assets/`

| 命名空间 | 文件数 | 说明 |
| --- | ---: | --- |
| `shape-shifter-curse` | 477 | 主模组资源 |
| `origins` | 44 | 内置 Origins UI/模型/贴图/语言资源 |

`assets/shape-shifter-curse/` 主要子目录：

| 目录 | 文件数 | 用途 |
| --- | ---: | --- |
| `animations` | 1 | AzureLib 动画 |
| `blockstates` | 4 | 方块状态 |
| `geo` | 36 | AzureLib/Blockbench 模型，含 `form/item/tech` |
| `lang` | 4 | 普通语言文件 |
| `models` | 69 | 原版模型 JSON，含 `block/entity/item` |
| `player_animation` | 80 | Player Animation Lib 动画 JSON |
| `rich_lang` | 3 | Rich Translatable Text |
| `shaders` | 8 | core/post/program shader |
| `ssc_form_model` | 32 | 形态模型映射配置 |
| `textures` | 239 | 方块、实体、形态、GUI、物品、overlay、slot、tech 等贴图 |

## 17. 数据包目录

数据包根：`src/main/resources/data/`

| 命名空间 | 文件数 | 说明 |
| --- | ---: | --- |
| `shape-shifter-curse` | 608 | 主模组数据 |
| `origins` | 24 | 内置 Origins 默认数据 |
| `trinkets` | 15 | Trinkets 槽位与 tag |
| `curios` | 10 | Curios 槽位/tag 兼容数据 |
| `minecraft` | 3 | 原版 tag 扩展 |

`data/shape-shifter-curse/` 主要子目录：

| 目录 | 文件数 | 用途 |
| --- | ---: | --- |
| `accessory_power` | 12 | 饰品能力定义 |
| `advancements` | 45 | 自定义进度 |
| `curios` | 2 | Curios entities/slots |
| `functions` | 2 | 数据包函数 |
| `loot_tables` | 5 | 方块/实体掉落 |
| `origins` | 32 | 起源/形态相关数据 |
| `powers` | 450 | Apoli 能力 JSON，是数据包最大部分 |
| `recipes` | 42 | 合成/锻造/其他配方 |
| `tags` | 18 | 方块、伤害类型、实体类型、物品 tag |

动态形态与外部扩展重点：

- 示例数据包：`custom_form_pack_example/example_form_datapack/`
- 示例资源包：`custom_form_pack_example/example_form_resourcepack/`
- 形态模型映射：`assets/*/ssc_form_model/*.json`
- 动画资源：`assets/*/player_animation/*.json`
- Rich 文本：`assets/*/rich_lang/*.json`

## 18. 依赖与兼容关系

主要运行/打包依赖：

| 依赖 | 版本 | 说明 |
| --- | --- | --- |
| Fabric API | `0.92.3+1.20.1` | Fabric 基础 API |
| Apoli | `2.9.2+mc.1.20.x` | Power/Condition/Action 框架，include |
| Pehkui | `3.7.8` | 实体缩放 |
| AzureLib | `3.0.19` | 模型与护甲渲染，include |
| Cardinal Components API | `5.1.0` | 实体组件 |
| Cloth Config | `10.0.96` | 配置界面 |
| ModMenu | `7.2.2` | 配置入口 |
| Satin | `1.14.0` | shader 支持 |
| Reach Entity Attributes | `2.4.0` | 触及距离属性，include |
| Player Animation Lib | `1.0.2-rc1+1.20` | 玩家动画，include |
| Trinkets | `3.7.2` | compileOnly，推荐安装 |
| Rich Translatable Text | Curse Maven `7440321` | local runtime |

compileOnly/兼容目标：

- Iris
- Extra Origins
- Origins Classes
- Bendy Lib
- Better Combat
- Player Animator
- First-person Model
- AppleSkin
- ViveCraft
- TaCZ: Refabricated
- Tough as Nails

`fabric.mod.json` 中显式冲突：

- 外部 `origins`
- `enchantedlib`
- `optifabric`
- 旧版 `identity`
- 高于 `3.0.19` 的 `azurelib`
- 低于 `2.0.12` 的 `azurelibarmor`
- 低于 `4.7.0` 的 `geckolib`

## 19. 开发辅助目录

`tools/`：

| 文件/目录 | 用途 |
| --- | --- |
| `AddFormSkill.md` | 添加新形态的详细指南 |
| `ProjectStructure.md` | 当前项目框架文档 |
| `AlignLang.py` | 语言文件 key 对齐 |
| `LangMissingCheck.py` | 语言缺失检查 |
| `RichLang2Lang.py` | Rich 文本转普通 lang |
| `GenerateRideAnim.py` | 骑乘动画生成 |
| `form_riding_animation.json` | 骑乘动画模板 |
| `MaskProcess.py` | 贴图遮罩处理 |
| `MaskProcessV2/` | 遮罩处理 V2 |
| `MaskProcessV2_C/` | C/C++ 插件版遮罩处理 |
| `rgb_colormask_maker.py` | RGB colormask 生成 |
| `Log.log` | 工具日志 |

`3d_models/`：

- Blockbench 源文件、Aseprite 源文件、预处理贴图。
- 目录按 `player_form/`、`entity/`、`block/`、`item/`、`effects/`、`gui/`、`tech/` 等分类。
- 构建不直接依赖这些源文件，但它们是资源再生成的重要来源。

`dev/`：

- `rewrite_form_change_plan.md`
- `remove_origins_plan.txt`
- `forge_port_plan.txt`
- `curio_integration/`
- `new_render_system/`

这些是设计/迁移/兼容计划，不一定代表当前可运行代码。

`PatronServer/`：

- `Server.py`
- `WebRoot/`
- `PackRes/`

用于 Patron 数据与资源服务原型，和主 Fabric 构建相对独立。

## 20. 后续读取建议

快速理解初始化：

1. 读 `fabric.mod.json`。
2. 读 `ShapeShifterCurseFabric.java`。
3. 读 `ShapeShifterCurseFabricClient.java`。

快速理解形态：

1. 读 `player_form/RegPlayerForms.java`。
2. 读 `player_form/PlayerFormBase.java`、`PlayerFormGroup.java`、`PlayerFormDynamic.java`。
3. 读 `player_form/FormDataPackReloadListener.java`。
4. 对照 `data/shape-shifter-curse/origins`、`powers`、`assets/shape-shifter-curse/ssc_form_model`。

添加新形态：

1. 先读 `tools/AddFormSkill.md`。
2. 参考 `custom_form_pack_example/`。
3. 同时准备数据包 JSON、资源包模型/贴图/动画、语言文本。

排查能力问题：

1. 从 `data/shape-shifter-curse/powers/*.json` 找到能力类型。
2. 到 `additional_power/` 找对应 Java 类。
3. 检查 `AdditionalPowers`、`AdditionalEntityConditions`、`AdditionalEntityActions`、`AdditionalItemCondition` 的注册。

排查渲染/动画问题：

1. 客户端入口 `ShapeShifterCurseFabricClient`。
2. `render/form_render/` 与 `render/render_layer/`。
3. `assets/shape-shifter-curse/ssc_form_model`、`geo`、`textures/form`、`player_animation`。
4. 对 shader 问题查看 `assets/shape-shifter-curse/shaders` 与 `FurGradientRenderLayer`。

排查数据包 reload：

1. `FormDataPackReloadListener`
2. `TrinketDataPackReloadListener`
3. `BrewingRecipeReloadListener`
4. `ServerLifecycleEvents.END_DATA_PACK_RELOAD` 中的动态形态同步逻辑。
