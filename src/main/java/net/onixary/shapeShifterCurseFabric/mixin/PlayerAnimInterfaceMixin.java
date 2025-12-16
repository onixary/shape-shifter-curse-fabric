package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
    private boolean isAnimationLoop = false;  // 在服务器端上处理同步时使用 用于区分Count Loop还是真Loop CountLoop在服务器上不会处理 仅会发送到客户端

    @Unique
    private final int updateRate = 100;  // 每多少tick更新一次(5s)

    @Unique
    private final int updateAdditionalTime = 20;  // 用于无限时长的动画

    @Unique
    private void setAnimation(@Nullable Identifier id, int count, int time, Boolean isAnimationLoop)  {
        this.powerAnimationID = id;
        this.powerAnimationCount = count;
        this.powerAnimationTime = time;
        this.isAnimationLoop = isAnimationLoop;
    }

    @Unique
    private void stopAnimation() {
        this.setAnimation(null, -1, -1, false);
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

    // 为带同步的设置动画 其他和setAnimation一样
    @Unique
    private void setAnimationOnServer(@Nullable Identifier id, int count, int time, Boolean isAnimationLoop) {
        this.setAnimation(id, count, time, isAnimationLoop);
        this.syncToOtherPlayers();
    }

    @Unique
    private void stopAnimationOnServer() {
        this.setAnimationOnServer(null, -1, -1, false);
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
    public int shape_shifter_curse$getPowerAnimationTime() {
        return this.powerAnimationTime;
    }

    @Override
    public void shape_shifter_curse$playAnimationWithCount(@NotNull Identifier id, int PlayCount) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(id, PlayCount, -1);
            return;
        }
        this.setAnimationOnServer(id, PlayCount, -1, false);
        // 服务器端不处理次数动画 发送到客户端后在服务器端上清除
        this.stopAnimation();  // 使用客户端清除 不用同步
    }

    @Override
    public void shape_shifter_curse$playAnimationWithTime(@NotNull Identifier id, int Time) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(id, -1, Time);
            return;
        }
        this.setAnimationOnServer(id, -1, Time, false);
    }

    @Override
    public void shape_shifter_curse$playAnimationLoop(@NotNull Identifier id) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(id, -1, -1);
            return;
        }
        this.setAnimationOnServer(id, -1, -1, true);
    }

    @Override
    public void shape_shifter_curse$stopAnimation() {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            ModPacketsS2C.sendPowerAnimationDataToServer(null, -1, -1);
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

    // 仅在客户端调用 服务器不应该调用
    @Override
    public void shape_shifter_curse$setAnimationData(@Nullable Identifier id, int count, int time) {
        this.setAnimation(id, count, time, false);
    }

    @Unique
    private Boolean isLoadedAnim = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (realThis.getWorld().isClient) {
            this.isLoadedAnim = false;
        } else {
            this.isLoadedAnim = true;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity realThis = (PlayerEntity) (Object) this;
        if (this.powerAnimationTime >= 0) {
            if (this.powerAnimationTime == 0) {
                this.stopAnimation();
            } else {
                this.powerAnimationTime--;
            }
        }
        // 为什么在这里加载而不是在init中加载呢? 在init加载会导致游戏崩溃 而且崩溃的信息也查不到这个Mixin
        if (!this.isLoadedAnim) {
            if (realThis.getWorld().isClient) {
                ModPacketsS2C.sendRequestPlayerAnimationData(realThis.getUuid());
                this.isLoadedAnim = true;
            }
        }
        if (!realThis.getWorld().isClient) {
            if (this.shouldAutoSyncToOtherPlayers()) {
                this.syncToOtherPlayers();
            }
        }
    }
}
