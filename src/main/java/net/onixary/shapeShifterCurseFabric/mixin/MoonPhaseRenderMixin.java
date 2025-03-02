package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.cursed_moon.MoonIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Mixin(WorldRenderer.class)
public class MoonPhaseRenderMixin {
    @Mutable
    @Final
    @Shadow
    private static final Identifier MOON_PHASES;

    static {
        MOON_PHASES = new MoonIdentifier("textures/environment/moon_phases.png");
    }

}
