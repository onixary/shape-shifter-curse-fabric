package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 为什么是Pro版本呢 由于四足形态没有四足站立的动画 而且我(XuHaoNan)不太会K动画 只能用IDLE和WALK动画来代替 添加动画后使用WithSneakAnimController就行
public class UseItemAnimControllerPro extends AbstractAnimStateControllerDP {
    private @NotNull AnimUtils.AnimationHolderData animationHolderData1 = AnimUtils.EMPTY_ANIM;
    private AnimationHolder animationHolder1 = null;
    private @NotNull AnimUtils.AnimationHolderData animationHolderData2 = AnimUtils.EMPTY_ANIM;
    private AnimationHolder animationHolder2 = null;
    private @NotNull AnimUtils.AnimationHolderData animationHolderData3 = AnimUtils.EMPTY_ANIM;
    private AnimationHolder animationHolder3 = null;
    private @NotNull AnimUtils.AnimationHolderData animationHolderData4 = AnimUtils.EMPTY_ANIM;
    private AnimationHolder animationHolder4 = null;

    public UseItemAnimControllerPro(@Nullable JsonObject jsonData) {
        super(jsonData);
    }

    public UseItemAnimControllerPro(@Nullable AnimUtils.AnimationHolderData animationHolderDataIDLE, @Nullable AnimUtils.AnimationHolderData animationHolderDataWALK, @Nullable AnimUtils.AnimationHolderData animationHolderDataSneakIDLE, @Nullable AnimUtils.AnimationHolderData animationHolderDataSneakWALK) {
        super(null);
        this.animationHolderData1 = AnimUtils.ensureAnimHolderDataNotNull(animationHolderDataIDLE);
        this.animationHolderData2 = AnimUtils.ensureAnimHolderDataNotNull(animationHolderDataWALK);
        this.animationHolderData3 = AnimUtils.ensureAnimHolderDataNotNull(animationHolderDataSneakIDLE);
        this.animationHolderData4 = AnimUtils.ensureAnimHolderDataNotNull(animationHolderDataSneakWALK);
    }

    @Override
    public @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data) {
        if (player.isSneaking()) {
            return data.IsWalking ? animationHolder4 : animationHolder3;
        } else {
            return data.IsWalking ? animationHolder2 : animationHolder1;
        }

        // 可读性太差
        // return player.isSneaking() ? (data.IsWalking ? animationHolder4 : animationHolder3) : (data.IsWalking ? animationHolder2 : animationHolder1);
    }

    @Override
    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        this.animationHolder1 = this.animationHolderData1.build();
        this.animationHolder2 = this.animationHolderData2.build();
        this.animationHolder3 = this.animationHolderData3.build();
        this.animationHolder4 = this.animationHolderData4.build();
        super.registerAnim(player, data);
    }

    @Override
    public AbstractAnimStateController loadFormJson(JsonObject jsonObject) {
        this.animationHolderData1 = AnimUtils.readAnimInJson(jsonObject, "idleAnim", null);
        this.animationHolderData2 = AnimUtils.readAnimInJson(jsonObject, "walkAnim", null);
        this.animationHolderData3 = AnimUtils.readAnimInJson(jsonObject, "sneakIdleAnim", null);
        this.animationHolderData4 = AnimUtils.readAnimInJson(jsonObject, "sneakWalkAnim", null);
        return this;
    }
}