package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.IPlayerAnimController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerAnimInterfaceMixin implements IPlayerAnimController {
    @Unique
    private Identifier powerAnimationID = null;

    @Unique
    private int powerAnimationCount = -1;  // 在客户端上处理 服务器上为初始值

    @Unique
    private int powerAnimationTime = -1;  // 在服务器端和客户端上处理

    @Unique
    private boolean isAnimationLoop = false;  // 在服务器端上处理同步时使用 用于区分Count Loop还是真Loop

    @Unique
    private final int updateRate = 100;  // 每多少tick更新一次(5s)

    @Unique
    private final int updateAdditionalTime = 20;  // 用于无限时长的动画

    @Unique
    private void setAnimation(@Nullable Identifier id, int count, int time) {
        this.powerAnimationID = id;
        this.powerAnimationCount = count;
        this.powerAnimationTime = time;
    }

    @Unique
    private void stopAnimation() {
        this.setAnimation(null, -1, -1);
    }

    @Unique
    private boolean shouldAutoSyncToOtherPlayers() {
        if (this.powerAnimationID == null) {
            return false;
        }
        if (((PlayerEntity) (Object) this).age % updateRate == 0) {
            return this.isAnimationLoop || this.powerAnimationTime > 0;
        }
        return false;
    }

    @Unique
    private void syncToOtherPlayers() {
        if ((Object)this instanceof ServerPlayerEntity serverPlayerEntity) {
            if (this.isAnimationLoop) {
                ModPacketsS2CServer.sendPowerAnimationDataToNearPlayer(serverPlayerEntity, this.powerAnimationID, -1, updateRate + updateAdditionalTime);
            } else {
                ModPacketsS2CServer.sendPowerAnimationDataToNearPlayer(serverPlayerEntity, this.powerAnimationID, this.powerAnimationCount, this.powerAnimationTime);
            }
        }
        else {
            if (ShapeShifterCurseFabric.IsDevelopmentEnvironment()) {
                ShapeShifterCurseFabric.LOGGER.error("syncToOtherPlayers: Not a ServerPlayerEntity");
            }
        }
    }

    @Unique
    private void setAnimationOnServer(@Nullable Identifier id, int count, int time) {
        this.powerAnimationID = id;
        this.powerAnimationCount = count;
        this.powerAnimationTime = time;
        this.syncToOtherPlayers();
    }

    @Unique
    private void stopAnimationOnServer() {
        this.setAnimationOnServer(null, -1, -1);
    }

    @Override
    public @Nullable Identifier shape_shifter_curse$getPowerAnimationID() {
        return this.powerAnimationID;
    }


    @Override
    public int shape_shifter_curse$getPowerAnimationCount() {
        return this.powerAnimationCount;
    }

    @Override
    public  int shape_shifter_curse$getPowerAnimationTime() {
        return this.powerAnimationTime;
    }

    @Override
    public void shape_shifter_curse$playAnimationWithCount(@NotNull Identifier id, int PlayCount) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(id, PlayCount, -1);
            return;
        }
        this.setAnimationOnServer(id, PlayCount, -1);
    }

    @Override
    public void shape_shifter_curse$playAnimationWithTime(@NotNull Identifier id, int Time) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(id, -1, Time);
            return;
        }
        this.setAnimationOnServer(id, -1, Time);
    }

    @Override
    public void shape_shifter_curse$playAnimationLoop(@NotNull Identifier id) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(id, -1, -1);
            return;
        }
        this.setAnimationOnServer(id, -1, -1);
        this.isAnimationLoop = true;
    }

    @Override
    public void shape_shifter_curse$stopAnimation() {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            // 或许可以补充一个向服务器发包的代码
            return;
        }
        this.stopAnimationOnServer();
    }

    @Override
    public void shape_shifter_curse$animationDoneCallBack(@NotNull Identifier id) {
        if (this.powerAnimationID != null && this.powerAnimationID.equals(id)) {
            if (this.powerAnimationCount != 0) {
                if (this.powerAnimationCount > 0) {
                    this.powerAnimationCount--;
                }
            }
            else {
                this.stopAnimation();
            }
        }
    }

    @Override
    public void shape_shifter_curse$setAnimationData(@Nullable Identifier id, int count, int time) {
        this.setAnimation(id, count, time);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendRequestPlayerAnimationData(realThis.getUuid());
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (this.powerAnimationTime >= 0) {
            if (this.powerAnimationTime == 0) {
                this.stopAnimation();
            }
            else {
                this.powerAnimationTime--;
            }
        }
        if (!realThis.getWorld().isClient) {
            if (this.shouldAutoSyncToOtherPlayers()) {
                this.syncToOtherPlayers();
            }
        }
    }
}
