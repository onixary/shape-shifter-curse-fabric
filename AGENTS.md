# 注册规范

- Perk 和 PerkTree 在 `perk/RegPerks.java` 中注册，便于统一调整图标等 API。不要放回 `studio/StudioGenerated.java`。
- 静态形态的技能树在 `registerPlayerForm(new ... .perkTree(RegPerks.XXX))` 的构建链中挂载。注册会调用 `onRegister()`，之后不得再修改形态的技能树等构建数据。
- 物品、实体、召唤物和客户端渲染器分别放到对应的现有注册类中；不要建立聚合多个注册域的形态专属 Content 类。
- Studio 管理的内容定义保存在 `.ssc-studio/content.json`。生成器在 `RegPerks.java` 的明确标记区域中维护 Perk/树字段，并修改形态声明中的树绑定；区域外的手写代码需要保留。
- 调整注册生成规范时，同时修改相邻 `SSC_studio` 仓库的生成器、迁移保护和测试；不能只修一次生成结果。
