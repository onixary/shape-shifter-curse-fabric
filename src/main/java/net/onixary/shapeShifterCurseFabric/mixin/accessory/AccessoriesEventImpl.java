package net.onixary.shapeShifterCurseFabric.mixin.accessory;

import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.SlotStateChange;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoryUtils;
import net.onixary.shapeShifterCurseFabric.util.Accessory.DefaultAccessory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultAccessory.class, remap = false)
public class AccessoriesEventImpl {
    @Unique
    private static boolean accessoriesEventRegistered = false;

    @Inject(method = "init", at = @At("HEAD"))
    private static void init(CallbackInfo ci) {
        if (accessoriesEventRegistered) return;
        accessoriesEventRegistered = true;

        // 终于有能挂上事件的饰品Mod了 Trinket得高版本才有
        AccessoryChangeCallback.EVENT.register((lastStack, currentStack, slotReference, stateChange) -> {
            LivingEntity entity = slotReference.entity();
            if (!(entity instanceof PlayerEntity player)) return;

            boolean replacement = stateChange == SlotStateChange.REPLACEMENT;

            if (!lastStack.isEmpty() && (currentStack.isEmpty() || replacement)) {
                AccessoryUtils.onPlayerUnEquip(player, Registries.ITEM.getId(lastStack.getItem()), "accessories");
            }
            if (!currentStack.isEmpty() && (lastStack.isEmpty() || replacement)) {
                AccessoryUtils.onPlayerEquip(player, Registries.ITEM.getId(currentStack.getItem()), "accessories");
            }
        });
    }
}
