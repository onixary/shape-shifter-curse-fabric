#### 标记一些需要在后续版本更新中需要修改项 尽可能让拓展大约能跨小版本兼容(仅跨1个小版本)

### 1.9.x:
- (1) Power 中的 Origins Namespace 迁移至 Apoli Namespace [√]
- (2) 将 Origins 的 Tag 转成 SSC 的 Tag (复制) [√]
- 迁移 Origins 的 Damage Type [√]
- 移除 Origins 的 Lang
- 移除 Origins 的数据包和资源包(除了`origins_layers`)
- 移除 `net.onixary.shapeShifterCurseFabric.integration.origins.Origins` 里的 `NamespaceAlias.addAlias(MODID, "apoli");` 需要 (1) 完成后等1个小版本
- 移除 Origins 的 Tag 需要 (2) 完成后等1个小版本

### 1.10.0:
- Origins 的按键(`key.origins.primary_active` `key.origins.secondary_active`)转为SSC的按键(没法跨版本兼容 所以放在中版本里) [√]
- 添加一个客户端配置 用来让客户端和服务器端的UUID一致(仅客户端生效 需要到服务器里获取UUID) [√]
- 添加一个通用配置(服务器端) 可以关闭诅咒之月强制变形 [√]