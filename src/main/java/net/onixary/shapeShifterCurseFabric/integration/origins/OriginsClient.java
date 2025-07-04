package net.onixary.shapeShifterCurseFabric.integration.origins;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.integration.PowerClearCallback;
import net.onixary.shapeShifterCurseFabric.integration.origins.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModBlocks;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModEntities;
import net.onixary.shapeShifterCurseFabric.integration.origins.screen.ViewOriginScreen;
import net.onixary.shapeShifterCurseFabric.integration.origins.util.PowerKeyManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class OriginsClient implements ClientModInitializer {

    public static KeyBinding usePrimaryActivePowerKeybind;
    public static KeyBinding useSecondaryActivePowerKeybind;
    public static KeyBinding viewCurrentOriginKeybind;

    public static boolean isServerRunningOrigins = false;

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TEMPORARY_COBWEB, RenderLayer.getCutout());

        EntityRendererRegistry.register(ModEntities.ENDERIAN_PEARL, FlyingItemEntityRenderer::new);

        ModPacketsS2C.register();

        // 暂时不需要主动技能按键绑定
        // 添加额外的主动技能按键并不是一个很好的设计
        /*usePrimaryActivePowerKeybind = new KeyBinding("key.origins.primary_active", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category." + Origins.MODID);
        useSecondaryActivePowerKeybind = new KeyBinding("key.origins.secondary_active", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + Origins.MODID);
        viewCurrentOriginKeybind = new KeyBinding("key.origins.view_origin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category." + Origins.MODID);

        ApoliClient.registerPowerKeybinding("key.origins.primary_active", usePrimaryActivePowerKeybind);
        ApoliClient.registerPowerKeybinding("key.origins.secondary_active", useSecondaryActivePowerKeybind);
        ApoliClient.registerPowerKeybinding("primary", usePrimaryActivePowerKeybind);
        ApoliClient.registerPowerKeybinding("secondary", useSecondaryActivePowerKeybind);

        // "none" is the default key used when none is specified.
        ApoliClient.registerPowerKeybinding("none", usePrimaryActivePowerKeybind);

        KeyBindingHelper.registerKeyBinding(usePrimaryActivePowerKeybind);
        KeyBindingHelper.registerKeyBinding(useSecondaryActivePowerKeybind);
        KeyBindingHelper.registerKeyBinding(viewCurrentOriginKeybind);*/

        ClientTickEvents.START_CLIENT_TICK.register(tick -> {
            // 用于显示当前的起源信息，不需要，将其注释掉
            /*while(viewCurrentOriginKeybind.wasPressed()) {
                if(!(MinecraftClient.getInstance().currentScreen instanceof ViewOriginScreen)) {
                    MinecraftClient.getInstance().setScreen(new ViewOriginScreen());
                }
            }*/
        });

        PowerClearCallback.EVENT.register(PowerKeyManager::clearCache);
    }
}
