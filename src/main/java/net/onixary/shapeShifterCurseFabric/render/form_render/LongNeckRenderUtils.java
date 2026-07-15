package net.onixary.shapeShifterCurseFabric.render.form_render;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;


public class LongNeckRenderUtils {
    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");

    public static boolean isFirstPersonModelActiveForSelf(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        return IS_FIRST_PERSON_MOD_LOADED && player.isMainPlayer() && client.options.getPerspective().isFirstPerson() && FirstPersonModelCore.instance.isEnabled();
    }

    public static float lerpAngle(float delta, float start, float end) {
        return start + MathHelper.wrapDegrees(end - start) * delta;
    }

    public static float lerpAngleAwayFrom(float delta, float start, float end, float avoidAngle) {
        if (Math.abs(MathHelper.wrapDegrees(avoidAngle - end)) < 0.0001F) {
            return lerpAngle(delta, start, end);
        }
        start = MathHelper.wrapDegrees(start);
        end = MathHelper.wrapDegrees(end);
        float diff = MathHelper.wrapDegrees(end - start);
        float avoidDiff = MathHelper.wrapDegrees(avoidAngle - start);
        boolean flipDir = Math.signum(diff) == Math.signum(avoidDiff) && Math.abs(diff) > Math.abs(avoidDiff);
        if (flipDir) {
            diff = Math.copySign(360.0F - Math.abs(diff), -diff);
        }
        return MathHelper.wrapDegrees(start + diff * delta);
    }
}
