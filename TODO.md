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
- 需要给赞助者离线验证系统生成一对根密钥 现在是测试密钥

### ?.?.? (不是必须项(炸不了拓展)或优先级不高的修复逻辑异常(但是目前还能通过一些操作来正常运行) 什么时候都可以加):
- 再添加1~2个主动按键(用于不常用功能 比如我拓展的打开UI类操作 或者可以让潜行边缘停止抑制不再为全形态共享 直接仅豹猫使用) 不过常用按键就4个 多了也操作不过来(甚至4还得拆成2(鼠标的2侧键)+2(键盘) 或者用4+侧键鼠标(额外侧键绑F13~F24(我G502就是这么绑的))) [√]
- 尝试让动画和模型合成为同一个文件 我初步怀疑可能通左右腿反转的OriginFurs遗留问题 我可能得让游戏读取由动画形态导出的模型 如果不是OriginFurs的问题 我再想其他方法
- 修复美西螈无限水爆破的Bug 准备改为需要冲一会或威力随冲刺时间变化(最大值为原先的值) 感觉湿润度消耗有点烦人 感觉可以砍一下战斗力上限 加一下水瓶堆叠上限
