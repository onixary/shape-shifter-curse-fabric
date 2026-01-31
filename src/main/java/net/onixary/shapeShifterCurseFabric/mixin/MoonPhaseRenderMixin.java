package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Mixin(value = WorldRenderer.class, priority = 949)
public class MoonPhaseRenderMixin {
    @Unique
    private final Identifier Vanilla_MOON_PHASES = new Identifier("textures/environment/moon_phases.png");

    @Unique
    private final Identifier CURSED_MOON_PHASES = new Identifier(MOD_ID,"textures/environment/cursed_moon_phases.png");

    @Unique
    public Identifier getMoonIdentifier() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            return CursedMoon.isCursedMoon(client.world) ? CURSED_MOON_PHASES : Vanilla_MOON_PHASES;
        }
        // fallback to client state
        return CursedMoon.clientIsCursedMoon ? CURSED_MOON_PHASES : Vanilla_MOON_PHASES;
    }

    @ModifyArg(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 1))
    private Identifier getMoonPhaseTexture(Identifier identifier) {
        return getMoonIdentifier();
    }
}
