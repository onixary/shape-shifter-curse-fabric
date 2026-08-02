package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.Nullable;

// 组合控制器:静止Idle持续达到阈值tick后切换动画 潜行等其他行为由内嵌控制器(委托)自行处理
// baseController传原有控制器(如WithSneakAnimController)即可保留潜行等功能
public class IdleStayAnimController extends AbstractAnimStateController {
    private final AbstractAnimStateController baseController;  // 未达到阈值时使用 如WithSneakAnimController(含潜行分支)
    private final AbstractAnimStateController stayController;  // 达到阈值后使用
    private final int stayTickThreshold;  // 静止Idle多少tick后切换 20tick=1秒

    public IdleStayAnimController(AbstractAnimStateController baseController, AbstractAnimStateController stayController, int stayTickThreshold) {
        this.baseController = baseController;
        this.stayController = stayController;
        this.stayTickThreshold = stayTickThreshold;
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (data.ContinueIdleStayTickCounter >= stayTickThreshold) {
            return stayController.getAnimation(player, data);
        }
        return baseController.getAnimation(player, data);
    }

    @Override
    public boolean isRegistered(PlayerEntity player, AnimSystem.AnimSystemData data) {
        return super.isRegistered(player, data)
                && baseController.isRegistered(player, data)
                && stayController.isRegistered(player, data);
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (!baseController.isRegistered(player, data)) {
            baseController.registerAnim(player, data);
        }
        if (!stayController.isRegistered(player, data)) {
            stayController.registerAnim(player, data);
        }
        super.registerAnim(player, data);
    }

    public int getStayTickThreshold() {
        return stayTickThreshold;
    }
}
