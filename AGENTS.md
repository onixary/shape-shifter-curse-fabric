# 注册规范

- Perk 和 PerkTree 在 `perk/RegPerks.java` 中注册，便于统一调整图标等 API。不要放回 `studio/StudioGenerated.java`。
- 按现有写法声明 `public static final Identifier P_Xxx = registerPerkCommon(new NormalPerk(...).setIcon(...).cost(...).addPower(...).removePower(...));`；树使用 `T_Xxx = registerPerkTree(new PerkTree(...).addNode(P_Xxx, tier, y, P_Previous));`。Perk ID 只在其注册构造器中定义，节点及前置直接引用常量，禁止重复拼写 Perk ID。先声明 Perk，再声明引用它们的树；费用使用 `BaseCost`/`ItemCost`。
- 静态形态的技能树在 `registerPlayerForm(new ... .perkTree(RegPerks.XXX))` 的构建链中挂载。注册会调用 `onRegister()`，之后不得再修改形态的技能树等构建数据。
- 物品、实体、召唤物和客户端渲染器分别放到对应的现有注册类中；不要建立聚合多个注册域的形态专属 Content 类。
- Studio 管理的内容定义保存在 `.ssc-studio/content.json`。生成器在 `RegPerks.java` 的明确标记区域中维护 Perk/树字段，并修改形态声明中的树绑定；区域外的手写代码需要保留。
- Studio 字段保留 `SSC_P_`/`SSC_T_` 前缀以区分所有权，树节点同样引用常量（包括区域外已有的 `P_Xxx`）。转为手写注册的定义应从 Studio 管理清单中移出，避免重复注册；不得自动覆盖同 ID 手写代码。旧生成区域仅在与旧配置完全匹配时迁移。
- 调整注册生成规范时，同时修改相邻 `SSC_studio` 仓库的生成器、迁移保护和测试；不能只修一次生成结果。
