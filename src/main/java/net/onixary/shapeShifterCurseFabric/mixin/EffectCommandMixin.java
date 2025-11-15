package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.EffectCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

// TODO 之后需要重构状态效果的代码

@Mixin(EffectCommand.class)
public class EffectCommandMixin {
    @Inject(method = "executeClear(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;)I", at = @At("TAIL"))
    private static void executeClearMixinA(ServerCommandSource source, Collection<? extends Entity> targets, CallbackInfoReturnable<Integer> cir) {
        ClearAttachment(source, targets);
    }

    @Inject(method = "executeClear(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/registry/entry/RegistryEntry;)I", at = @At("TAIL"))
    private static void executeClearMixinB(ServerCommandSource source, Collection<? extends Entity> targets, RegistryEntry<StatusEffect> statusEffect, CallbackInfoReturnable<Integer> cir) {
        ClearAttachment(source, targets);
    }

    @Unique
    private static void ClearAttachment(ServerCommandSource source, Collection<? extends Entity> targets) {
        try {
            for (Entity entity : targets) {
                if (entity instanceof PlayerEntity player) {
                    EffectManager.cancelEffect(player);
                }
            }
        }
        catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to clear attachment for player: " + e.getMessage());
        }
    }
}
