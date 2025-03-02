package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LightmapTextureManager.class)
public abstract class CursedMoonLightmapMixin implements AutoCloseable{
    @Inject(
            method = {"update"},
            at = {@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/LightmapTextureManager;flickerIntensity:F"
            )},
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    //获取常量flickerIntensity之前的局部变量，并修改目标变量
    public void update(float delta, CallbackInfo ci, ClientWorld clientWorld, float f, float g, float h, float i, float j, float l, float k, Vector3f vector3f){
        if(CursedMoon.isCursedMoon()){
            Vector3f modifiedColor = new Vector3f(1.0F, 0.24F, 0.82F);
            float skyBlend = 1.0F - f - clientWorld.getRainGradient(1.0F);
            vector3f.lerp(modifiedColor, skyBlend);
        }
    }
}
