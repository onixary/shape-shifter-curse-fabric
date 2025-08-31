package net.onixary.shapeShifterCurseFabric.mixin.test;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 测试披风显示渲染，在打包版本移除！
// Test cape rendering display, remove in packaged version!
@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class DevCapeTestMixin {
    @Inject(method = "getCapeTexture", at = @At("HEAD"), cancellable = true)
    private void forceGetCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        // 只在开发环境中生效
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            // 返回一个默认的披风纹理
            cir.setReturnValue(new Identifier("textures/entity/cape/cape.png"));
        }
    }

    @Inject(method = "canRenderCapeTexture", at = @At("HEAD"), cancellable = true)
    private void forceCanRenderCape(CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            cir.setReturnValue(true);
        }
    }
}
