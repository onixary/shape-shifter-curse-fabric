(XuHaoNan)(https://github.com/xu233333)

重构形态变形代码(先设计 后开工)
1. 实现网状进化功能(由Form类决定下一个阶段形态 包含退化)
2. 诅咒之月变形代码重写(现在的代码成了屎山了 加补丁难度太高 重写一份)

大约需要的功能(未设计完 等我先设计几天再和 Onixary 讨论一下还需要什么功能)
- `@NotNull PlayerFormBase PlayerFormBase._getNextForm(PlayerEntity player, Reason reason)` | `@NotNull PlayerFormBase PlayerFormBase._getPrevForm(PlayerEntity, Reason reason)` 由PlayerFormBase实现 一般不用改
- `@Nullable PlayerFormBase PlayerFormBase.getNextForm(PlayerEntity player, Reason reason)` | `@Nullable PlayerFormBase PlayerFormBase.getPrevForm(PlayerEntity, Reason reason)`
- `@NotNull PlayerFormBase PlayerFormBase.getDefaultNextForm(PlayerEntity player)` | `@NotNull PlayerFormBase PlayerFormBase.getDefaultPrevForm(PlayerEntity)`
- Reason有ReasonType(Identifier) SSC自带(NameSpace在此文档中用SSC简写 否则太长了) `SSC:Default` `SSC:Instinct` `SSC:CurseMoon` `SSC:Inhibitor(Reason带ItemStack)` `SSC:Force(Reason带PlayerFormBase)`
- ~~Reason有FallBack机制 当getNextForm/getPrevForm返回null时使用 类型为@Nullable Reason 如果为null 自动调用DefaultXXXX函数或由_XXXX函数内部处理 拥有boolean OverrideReason变量 如果为True 则在失败时覆写Reason~~
- ~~_XXXX 函数先调用不带_的函数 如果返回null 处理FallBack 如果FallBack处理还是null 先用_XXXX内置处理 如果没有对应的内置处理 直接调用getDefaultXXXX函数~~
- _getXXXX流程 先调用getXXXX函数 如果为null 则调用Reason里的getFallBackForm 如果还为null 自动调用getDefaultXXXX 再为null就返回this 并写一个Error日志

```java
public interface Reason {
    @NotNull Identifier getType();
    default @Nullable PlayerFormBase getFallBackForm(PlayerEntity player, PlayerFormBase nowForm) {
        // 可以链式调用 比如[SSC:Inhibitor]为nowForm._getNextForm(player, INSTINCT) [SSC:Instinct]为nowForm._getNextForm(player, DEFAULT)
        return null;
    }
}
```

```java
public class PlayerFormBase {
    // 最后一层了 必须是NotNull 否则直接throw 所以不推荐覆写
    public @NotNull PlayerFormBase _getNextForm(PlayerEntity player, Reason reason) {
        PlayerFormBase nextForm = getNextForm(player, reason);
        if (nextForm == null) {
            nextForm = reason.getFallBackForm(player, this);
        }
        if (nextForm == null) {
            nextForm = getDefaultNextForm(player, reason);
        }
        if (nextForm == null) {
            nextForm = this;
            // 错误日志
        }
        return nextForm;
    }

    public @NotNull PlayerFormBase _getPrevForm(PlayerEntity player, Reason reason) {
        PlayerFormBase prevForm = getPrevForm(player, reason);
        if (prevForm == null) {
            prevForm = reason.getFallBackForm(player, this);
        }
        if (prevForm == null) {
            prevForm = getDefaultPrevForm(player, reason);
        }
        if (nextForm == null) {
            nextForm = this;
            // 错误日志
        }
        return prevForm;
    }

    // 选择性处理 如果不匹配则必须返回null
    public @Nullable PlayerFormBase getNextForm(PlayerEntity player, Reason reason) {
        return null;
    }

    public @Nullable PlayerFormBase getPrevForm(PlayerEntity player, Reason reason) {
        return null;
    }

    // 说是NotNull 但是返回null不会崩溃 只会弹错误日志 不过极度不推荐返回null
    public @NotNull PlayerFormBase getDefaultNextForm(PlayerEntity player, Reason reason) {
        // reason 用处不大 但或许可以做些特殊功能
        // 省略从Group里自动获取下一级形态
        return this;
    }

    public @NotNull PlayerFormBase getDefaultPrevForm(PlayerEntity player, Reason reason) {
        // reason 用处不大 但或许可以做些特殊功能
        // 省略从Group里自动获取上一级形态
        return this;
    }
}
```