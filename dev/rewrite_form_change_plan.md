(XuHaoNan)(https://github.com/xu233333)

重构形态变形代码(先设计 后开工)
- 重构 TransformManager
- 重构 PlayerFormBase 提取出接口
- 重构诅咒之月逻辑
- 重构本能系统(可选)
- 修改催化剂和抑制剂逻辑(改到Item类里处理 金苹果成就改为使用数据包实现)

- 将"form_giving_custom_entity"的Buff逻辑改为攻击后给予 比较符合知觉(不属于这个计划 等什么时候有空就整一下)

大约需要的功能(未设计完 等我先设计几天再和 Onixary 讨论一下还需要什么功能)
- `@NotNull PlayerFormBase PlayerFormBase._getNextForm(PlayerEntity player, Reason reason)` | `@NotNull PlayerFormBase PlayerFormBase._getPrevForm(PlayerEntity, Reason reason)` 由PlayerFormBase实现 一般不用改
- `@Nullable PlayerFormBase PlayerFormBase.getNextForm(PlayerEntity player, Reason reason)` | `@Nullable PlayerFormBase PlayerFormBase.getPrevForm(PlayerEntity, Reason reason)`
- `@NotNull PlayerFormBase PlayerFormBase.getDefaultNextForm(PlayerEntity player)` | `@NotNull PlayerFormBase PlayerFormBase.getDefaultPrevForm(PlayerEntity)`
- Reason有ReasonType(Identifier) SSC自带(NameSpace在此文档中用SSC简写 否则太长了) `SSC:Default` `SSC:Instinct` `SSC:CurseMoon` `SSC:Inhibitor(Reason带ItemStack)` `SSC:Force(Reason带PlayerFormBase)`
- ~~Reason有FallBack机制 当getNextForm/getPrevForm返回null时使用 类型为@Nullable Reason 如果为null 自动调用DefaultXXXX函数或由_XXXX函数内部处理 拥有boolean OverrideReason变量 如果为True 则在失败时覆写Reason~~
- ~~_XXXX 函数先调用不带_的函数 如果返回null 处理FallBack 如果FallBack处理还是null 先用_XXXX内置处理 如果没有对应的内置处理 直接调用getDefaultXXXX函数~~
- _getXXXX流程 先调用getXXXX函数 如果为null 则调用Reason里的getFallBackForm 如果还为null 自动调用getDefaultXXXX 再为null就返回this 并写一个Error日志
- 移除Phase Enum 改为Int(类Index) 未开启使用-1 开启后使用0 形态_0使用1 以此类推 拥有一个IsFinalForm的Flag用于默认升降级(仅最终形态为True SP形态不为True)
- 每个组的不同Level可以有多个形态 自动处理升降级时会随机选一个(如果要指定 可以覆写get\[Next/Prev\]Form) 需要加一个Function权重系统
- 诅咒之月改成在触发时记录触发前形态F1和触发后形态F2 变形结束时如果当前形态和F2相同则变为F1
- 维护一个LinkedList记录玩家的变形路线 当玩家使用命令/返回开始前/后的形态时清空 用于定向返回逻辑
- Layer系统在完成移除Origins后需要保留 改为1个主Layer(Form) 多个副Layer 具体启用什么Layer由PlayerFormBase的函数决定(输出不可变) 可以实现一个形态带几个拼接能力包这种功能

- getNextForm 和 getPrevForm 在数据包上使用Power处理 仅提供一个默认升降级配置项

- 诅咒之月使用ServerTick事件挂载 取消客户端dayTime之类变量的存储(可以从World里拿) 同步逻辑改为玩家进入时同步一次 修改诅咒之月状态时一次 当处于诅咒之月时检查玩家诅咒之月变形flag 如果为false则触发变形 非诅咒之月时如果flag为true则触发还原 (旧版经过多次补丁 已经成屎山了 再补几个估计我也快没法补了)
- 动画变量只保留"HasSlowFall"(因为由外部渲染调用) 其他可以用动画控制器代替
- CustomForm改为DynamicForm(动态加载形态) 原先的CustomForm是给空位形态留的 现在支持数据包加载 可以移除CustomForm了
- getCapeIdleLoc/getCapeBaseRotateAngle/NeedModifyXRotationAngle 这三在PlayerFormBase里不太好 改为外部注册表 HashMap<PlayerFormBase, CapeProcessor>
- 新增3个Hook 用于实现一些特殊逻辑 比如给形态初始化一些数据
- Flag系统 每个形态有`Set<String>`的Flag列表 可以压缩部分变量到flag系统里
- Scale系统由Power改到Form处理 毕竟每个形态都得设置尺寸 不如放进形态类里省的漏

```java
public interface Reason {
    @NotNull Identifier getType();
    default @Nullable PlayerFormBase getFallBackNextForm(PlayerEntity player, PlayerFormBase nowForm) {
        // 可以链式调用 比如[SSC:Inhibitor]为nowForm._getNextForm(player, INSTINCT) [SSC:Instinct]为nowForm._getNextForm(player, DEFAULT)
        return null;
    }
    default @Nullable PlayerFormBase getFallBackPrevForm(PlayerEntity player, PlayerFormBase nowForm) {
        // 可以链式调用 比如[SSC:Inhibitor]为nowForm._getNextForm(player, INSTINCT) [SSC:Instinct]为nowForm._getNextForm(player, DEFAULT)
        return null;
    }
}
```

```java
import java.util.Set;

public class PlayerFormBase {
    // 最后一层了 必须是NotNull 否则直接throw 所以不推荐覆写
    public @NotNull PlayerFormBase _getNextForm(PlayerEntity player, Reason reason) {
        PlayerFormBase nextForm = getNextForm(player, reason);
        if (nextForm == null) {
            nextForm = reason.getFallBackNextForm(player, this);
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
            prevForm = reason.getFallBackPrevForm(player, this);
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

    public void onTransform_From(PlayerEntity player, PlayerFormBase prevForm) {
    }

    public void onTransformFinish(PlayerEntity player) {
    }

    public void onTransform_To(PlayerEntity player, PlayerFormBase nextForm) {
    }

    // 不可变Set 优化一下性能 听AI说<100个时比HashSet快(话说Hash消耗这么高吗) 但应该没人用超过10个Flag吧
    public Set<String> getFlags() {
        return Set.of();
    }

    public PlayerFormBodyType getBodyType() {
        return PlayerFormBodyType.NORMAL;
    }

    // Phase和Index合并在一起了
    public int getPhase() {
        return -1;
    }
    
    public Identifier getFormID() {
        return FormID;
    }
    
    public Text getFormName() {
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + ".name");
    }
    
    public Text getContentText(CodexData.ContentType type) {
        return Text.translatable("codex.form." + FormID.getNamespace() + "." + FormID.getPath() + "." + type.toString().toLowerCase());
    }

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        return null;
    }

    public void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        this.IsRegisteredPowerAnim = true;
    }

    public boolean isPowerAnimRegistered(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        return IsRegisteredPowerAnim;
    }

    public @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        return new Pair<>(false, null);
    }

    public boolean getIsDynamicForm() {
        return false;
    }

    public PlayerFormGroup getGroup() {
        return Group;
    }
    
    // 临时先用这套方案 等移除Origins完工了后再改 变动位置小 影响不大
    public Pair<Identifier, Identifier> getFormLayer() {
        
    }
    
    // 取消power设置scale的操作 改为由form指定 填null为使用外部power ScaleData为record 记录scale的数据
    public @Nullable ScaleData getScale() {
        
    }
}
```