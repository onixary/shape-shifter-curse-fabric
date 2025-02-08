package net.onixary.shapeShifterCurseFabric.integration.origins.mixin;

import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.OriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginUpgrade;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerAdvancementTracker.class)
public class OriginUpgradeMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;endTrackingCompleted(Lnet/minecraft/advancement/Advancement;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkOriginUpgrade(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> info, boolean bl, AdvancementProgress advancementProgress, boolean bl2) {
        if(advancementProgress.isDone()) {
            Origin.get(owner).forEach((layer, o) -> {
                Optional<OriginUpgrade> upgrade = o.getUpgrade(advancement);
                if(upgrade.isPresent()) {
                    try {
                        Origin upgradeTo = OriginRegistry.get(upgrade.get().getUpgradeToOrigin());
                        if(upgradeTo != null) {
                            OriginComponent component = ModComponents.ORIGIN.get(owner);
                            component.setOrigin(layer, upgradeTo);
                            component.sync();
                            String announcement = upgrade.get().getAnnouncement();
                            if (!announcement.isEmpty()) {
                                owner.sendMessage(Text.translatable(announcement).formatted(Formatting.GOLD), false);
                            }
                        }
                    } catch(IllegalArgumentException e) {
                        Origins.LOGGER.error("Could not perform Origins upgrade from " + o.getIdentifier().toString() + " to " + upgrade.get().getUpgradeToOrigin().toString() + ", as the upgrade origin did not exist!");
                    }
                }
            });
        }
    }
}
