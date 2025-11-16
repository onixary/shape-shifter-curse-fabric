package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerDataSaverMixin {

    // 注入到 NBT 写入方法中，在数据被实际写入前执行
    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        // 确保我们处理的是服务端玩家
        if ((Object) this instanceof ServerPlayerEntity player) {
            // 检查玩家身上是否还存在效果
            boolean hasTransformEffect = player.getStatusEffects().stream()
                    .anyMatch(effect -> effect.getEffectType() instanceof BaseTransformativeStatusEffect);

            // 如果玩家身上已经没有变形效果了，但内部数据还在
            if (!hasTransformEffect) {
                PlayerEffectAttachment attachment = EffectManager.getCurrentEffectAttachment(player);
                if (attachment != null && attachment.currentEffect != null) {
                    // 执行清理逻辑
                    EffectManager.resetAttachment(player);
                }
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            // 读取NBT后检查数据一致性
            player.getServer().execute(() -> EffectManager.verifyAndCleanAttachment(player));
        }
    }
}

