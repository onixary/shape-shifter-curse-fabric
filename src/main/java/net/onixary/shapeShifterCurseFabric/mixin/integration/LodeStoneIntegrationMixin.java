package net.onixary.shapeShifterCurseFabric.mixin.integration;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils.getPowerFoodComponent;

@Mixin(LivingEntity.class)
public abstract class LodeStoneIntegrationMixin {
    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    protected int itemUseTimeLeft;

    @Shadow
    protected abstract void setLivingFlag(int mask, boolean value);

    // 其实应该是Lodestone的ci.cancel的问题 不过我这边还是能修的(也就是今天心情好一些 按理说直接在Fabric.mod.json写一个不兼容就行 Lodestone的直接ci.cancel会和大量Mod冲突)
    // 调了半天 现在心情不好了 感觉Lodestone就没考虑别人的Mixin 直接ci.cancel还把客户端操作给屏蔽了 还有大量位置直接覆盖原始值 还不用mixinExtra读取原始值

    @Inject(method = "setCurrentHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I"), order = 900)
    private void setCurrentHand$getMaxUseTimeA(Hand hand, CallbackInfo ci) {
        if ((Object)this instanceof PlayerEntity playerEntity) {
            FoodComponent fc = getPowerFoodComponent(playerEntity, activeItemStack);
            if (fc == null) {
                return;
            }
            this.itemUseTimeLeft = fc.isSnack() ? 16 : 32;
            if (!playerEntity.getWorld().isClient) {
                this.setLivingFlag(1, true);
                this.setLivingFlag(2, hand == Hand.OFF_HAND);
                playerEntity.emitGameEvent(GameEvent.ITEM_INTERACT_START);
            }
        }
        return;
    }
}
