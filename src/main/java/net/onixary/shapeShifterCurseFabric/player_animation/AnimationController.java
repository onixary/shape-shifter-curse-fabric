package net.onixary.shapeShifterCurseFabric.player_animation;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.player_animation.form_animation.AnimationTransform;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.BiFunction;

// 由于动画数量太多 所以注册在AnimationControllerInstance里注册 本Class只负责管理注册
public class AnimationController {

    // 由PlayerAnimDataHolder存储全部动画数据
    public static class PlayerAnimDataHolder {
        // 跟随PlayerEntity Object
        PlayerFormBase playerForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
        PlayerAnimState prevAnimState = null;
        public Vec3d lastPos = new Vec3d(0, 0, 0);
        int continueSwingAnimCounter = 0;
        boolean lastOnGround = false;

        // 计算缓存数据 常用变量
        boolean IsWalk;  // 有必要缓存么? 缓存仅减少几个equal判断
    }

    // 动画State注册
    public static LinkedList<Identifier> AnimationStateConditionList = new LinkedList<>();
    public static HashMap<Identifier, BiFunction<PlayerEntity, PlayerAnimDataHolder, Boolean>>  AnimationStateConditionRegistry = new LinkedHashMap<>();

    public static Identifier RegisterAnimationStateCondition(Identifier id, BiFunction<PlayerEntity, PlayerAnimDataHolder, Boolean> function) {
        AnimationStateConditionList.add(id);
        AnimationStateConditionRegistry.put(id, function);
        return id;
    }

    // 动画控制器Cell注册
    public HashMap<Identifier, BiFunction<PlayerEntity, PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>>> AnimControllerCellRegistry = new HashMap<>();
    public BiFunction<PlayerEntity, PlayerAnimDataHolder, PlayerAnimState> DefaultAnimControllerCell;
    public PlayerAnimState DefaultAnimState = null;

    public void RegisterAnimControllerCell(Identifier id, BiFunction<PlayerEntity, PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>> function) {
        this.AnimControllerCellRegistry.put(id, function);
    }

    private final static BiFunction<PlayerEntity, PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>> EmptyAnimControllerCell = (player, animDataHolder) -> new Pair<>(AnimationControllerCellResult.NOT_MATCH, null);
    public BiFunction<PlayerEntity, PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>> getAnimControllerCell(Identifier conditionID) {
        return this.AnimControllerCellRegistry.getOrDefault(conditionID, EmptyAnimControllerCell);
    }

    public AnimationController(BiFunction<PlayerEntity, PlayerAnimDataHolder, PlayerAnimState> DefaultAnimControllerCell) {
        this.DefaultAnimControllerCell = DefaultAnimControllerCell;
    }

    public PlayerAnimState getDefaultAnimState(PlayerEntity player, PlayerAnimDataHolder animDataHolder) {
        if (this.DefaultAnimState != null) {
            return this.DefaultAnimState;
        }
        return this.DefaultAnimControllerCell.apply(player, animDataHolder);
    }

    public void DataHolderPreTick(PlayerEntity player, PlayerAnimDataHolder animDataHolder) {
        // 可复用变量
        animDataHolder.IsWalk = !player.getPos().equals(animDataHolder.lastPos);
        animDataHolder.playerForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
    }

    public void DataHolderTick(PlayerEntity player, PlayerAnimDataHolder animDataHolder) {
        if (player.handSwinging) {
            // 原先有限位10 没什么用 反正状态就有三个 0 1~10 >=10
            animDataHolder.continueSwingAnimCounter++;
        } else {
            animDataHolder.continueSwingAnimCounter = 0;
        }
        animDataHolder.lastOnGround = player.isOnGround();
        animDataHolder.lastPos = player.getPos();
    }

    public AnimationHolder getAnim(PlayerEntity player, PlayerAnimDataHolder animDataHolder) {
        this.DataHolderPreTick(player, animDataHolder);
        PlayerAnimState animState = this.getAnimState(player, animDataHolder);
        // 先判断后处理变量
        this.DataHolderTick(player, animDataHolder);
        if(animState == null){
            animState = PlayerAnimState.ANIM_IDLE;
        }
        animDataHolder.prevAnimState = animState;
        // 获取具体动画
        switch (animState) {
            // 特殊动画在这里修改
            case ANIM_ON_TRANSFORM:
                String fromFormName = ShapeShifterCurseFabricClient.getClientTransformFromForm(player.getUuid());
                String toFormName = ShapeShifterCurseFabricClient.getClientTransformToForm(player.getUuid());
                PlayerFormBase transformCurrentForm = null;
                PlayerFormBase transformToForm = null;
                try {
                    transformCurrentForm = fromFormName != null ? RegPlayerForms.getPlayerForm(fromFormName) : null;
                    transformToForm = toFormName != null ? RegPlayerForms.getPlayerForm(toFormName) : null;
                } catch (IllegalArgumentException e) {
                    // 如果解析失败，使用当前形态作为 fallback
                    transformCurrentForm = animDataHolder.playerForm;
                    transformToForm = animDataHolder.playerForm;
                }
                return AnimationTransform.getFormAnimToPlay(transformCurrentForm, transformToForm);
            default:
                return animDataHolder.playerForm.Anim_getFormAnimToPlay(animState);
        }
    }

    public PlayerAnimState getAnimState(PlayerEntity player, PlayerAnimDataHolder animDataHolder) {
        this.DefaultAnimState = null;
        for (Identifier conditionID : AnimationStateConditionList) {
            boolean IsMatch = AnimationStateConditionRegistry.get(conditionID).apply(player, animDataHolder);
            if (IsMatch) {
                BiFunction<PlayerEntity, PlayerAnimDataHolder, Pair<AnimationControllerCellResult, PlayerAnimState>> cell = this.getAnimControllerCell(conditionID);
                Pair<AnimationControllerCellResult, PlayerAnimState> result = cell.apply(player, animDataHolder);
                switch (result.getFirst()) {
                    case MATCH:
                        return result.getSecond();
                    case NOT_MATCH:
                        continue;
                    case SET_DEFAULT:
                        DefaultAnimState = result.getSecond();
                        break;
                    case RETURN_DEFAULT:
                        return this.getDefaultAnimState(player, animDataHolder);
                    default:
                        // 如果爆了这个错误 说明程序未完工就发布了
                        throw new RuntimeException("AnimationControllerCellResult 未在getAnim里定义 如果在已发布版本里出现 请联系开发者");
                }
            }
        }
        return this.getDefaultAnimState(player, animDataHolder);
    }
}
