package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.AdvancementUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(AdvancementManager.class)
public class AdvancementFixMixin {
    @Unique
    private void onAdvancementAdded(Advancement advancement) {
        AdvancementUtils.onAdvancementAdded(advancement);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void load(Map<Identifier, Advancement.Builder> advancements, CallbackInfo ci, @Local Advancement advancement) {
        onAdvancementAdded(advancement);
    }
}
