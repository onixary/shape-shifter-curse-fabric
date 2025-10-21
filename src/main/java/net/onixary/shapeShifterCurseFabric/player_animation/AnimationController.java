package net.onixary.shapeShifterCurseFabric.player_animation;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Function;

// 由于动画数量太多 所以注册在AnimationControllerInstance里注册 本Class只负责管理注册
public class AnimationController {
    // 动画State注册
    public static LinkedList<Identifier> AnimationStateConditionList = new LinkedList<>();
    public static HashMap<Identifier, Function<PlayerEntity, Boolean>>  AnimationStateConditionRegistry = new LinkedHashMap<>();

    public static Identifier RegisterAnimationStateCondition(Identifier id, Function<PlayerEntity, Boolean> function) {
        AnimationStateConditionList.add(id);
        AnimationStateConditionRegistry.put(id, function);
        return id;
    }

    // 动画控制器Cell注册
    public HashMap<Identifier, Function<PlayerEntity, Pair<AnimationControllerCellResult, PlayerAnimState>>> AnimControllerCellRegistry = new HashMap<>();
    public Function<PlayerEntity, PlayerAnimState> DefaultAnimControllerCell;
    public PlayerAnimState DefaultAnimState = null;

    public void RegisterAnimControllerCell(Identifier id, Function<PlayerEntity, Pair<AnimationControllerCellResult, PlayerAnimState>> function) {
        AnimControllerCellRegistry.put(id, function);
    }

    private final static Function<PlayerEntity, Pair<AnimationControllerCellResult, PlayerAnimState>> EmptyAnimControllerCell = (player) -> new Pair<>(AnimationControllerCellResult.NOT_MATCH, null);
    public Function<PlayerEntity, Pair<AnimationControllerCellResult, PlayerAnimState>> getAnimControllerCell(Identifier conditionID) {
        return AnimControllerCellRegistry.getOrDefault(conditionID, EmptyAnimControllerCell);
    }

    public AnimationController(Function<PlayerEntity, PlayerAnimState> DefaultAnimControllerCell) {
        this.DefaultAnimControllerCell = DefaultAnimControllerCell;
    }

    public PlayerAnimState getDefaultAnimState(PlayerEntity player) {
        if (DefaultAnimState != null) {
            return DefaultAnimState;
        }
        return DefaultAnimControllerCell.apply(player);
    }

    public PlayerAnimState getAnim(PlayerEntity player) {
        this.DefaultAnimState = null;
        for (Identifier conditionID : AnimationStateConditionList) {
            boolean IsMatch = AnimationStateConditionRegistry.get(conditionID).apply(player);
            if (IsMatch) {
                Function<PlayerEntity, Pair<AnimationControllerCellResult, PlayerAnimState>> cell = getAnimControllerCell(conditionID);
                Pair<AnimationControllerCellResult, PlayerAnimState> result = cell.apply(player);
                switch (result.getFirst()) {
                    case MATCH:
                        return result.getSecond();
                    case NOT_MATCH:
                        continue;
                    case SET_DEFAULT:
                        DefaultAnimState = result.getSecond();
                        break;
                    case RETURN_DEFAULT:
                        return getDefaultAnimState(player);
                    default:
                        // 如果爆了这个错误 说明程序未完工就发布了
                        throw new RuntimeException("AnimationControllerCellResult 未在getAnim里定义 如果在已发布版本里出现 请联系开发者");
                }
            }
        }
        return getDefaultAnimState(player);
    }
}
