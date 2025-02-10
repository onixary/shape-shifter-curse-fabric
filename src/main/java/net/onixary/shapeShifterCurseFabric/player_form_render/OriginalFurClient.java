package net.onixary.shapeShifterCurseFabric.player_form_render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.LinkedHashMap;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class OriginalFurClient implements ClientModInitializer {

    public static class ItemRendererFeatureAnim extends dev.kosmx.playerAnim.api.layered.PlayerAnimationFrame {
        PlayerEntity player;
        ItemRendererFeatureAnim(PlayerEntity player) {
            super();
            this.player = player;
        }
        private int time = 0;
        @Override
        public void tick(){
            time++;
        }

        @Override
        public void setupAnim(float v) {
            if (player instanceof ClientPlayerEntity cPE && player instanceof IPlayerEntityMixins iPE) {
                for (var m : iPE.originalFur$getCurrentModels()) {
                    if (m == null) {
                        return;
                    }
                    var lP = m.getLeftOffset();
                    var rP = m.getRightOffset();
                }

            }
        }

    }
    public static class OriginFur extends GeoObjectRenderer<OriginFurAnimatable> {
        public Origin currentAssociatedOrigin = Origin.EMPTY;
        public static final OriginFur NULL_OR_DEFAULT_FUR = new OriginFur(JsonParser.parseString("{}").getAsJsonObject());
        public void renderBone(String name, MatrixStack poseStack, @Nullable VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer, int packedLight) {
            poseStack.push();
            var b = this.getGeoModel().getBone(name).orElse(null);
            if (b == null) {return;}
            if (buffer == null) {buffer = bufferSource.getBuffer(renderType);}
            var cubes = b.getCubes();
            int packedOverlay = this.getPackedOverlay(animatable, 0.0F, MinecraftClient.getInstance().getTickDelta());
            for (var child_bones : b.getChildBones()) {
                cubes.addAll(child_bones.getCubes());
            }
            @Nullable VertexConsumer finalBuffer = buffer;
            cubes.forEach(geoCube -> {
                renderRecursively(poseStack, this.animatable, b, renderType, bufferSource, finalBuffer, false, MinecraftClient.getInstance().getTickDelta(), packedLight, packedOverlay, 1, 1, 1, 1);
            });
            poseStack.pop();
        }

        public void setPlayer(PlayerEntity e) {
            this.animatable.setPlayer(e);
        }

        public OriginFur(JsonObject json) {
            super(new OriginFurModel(json));
            this.animatable = new OriginFurAnimatable();
        }


    }
    public static boolean isRenderingInWorld = false;

    public static LinkedHashMap<Identifier, OriginFur> FUR_REGISTRY = new LinkedHashMap<>();
    public static LinkedHashMap<Identifier, Resource> FUR_RESOURCES = new LinkedHashMap<>();
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("player-animator") || FabricLoader.getInstance().isModLoaded("playeranimator")) {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new Identifier("originfurs", "item_renderer"), 9999, ItemRendererFeatureAnim::new);
        }
        WorldRenderEvents.END.register(context -> isRenderingInWorld = false);
        WorldRenderEvents.START.register(context -> isRenderingInWorld = true);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("originalfur", "furs");
            }

            final String r_M = "\\/([A-Za-z0-9_.-]+)\\.json";
            @Override
            public void reload(ResourceManager manager) {
                var resources = manager.findResources("furs", identifier -> identifier.getPath().endsWith(".json"));
                for (var res : resources.keySet()) {
                    String itemName = res.getPath().substring(res.getPath().indexOf('/')+1, res.getPath().lastIndexOf('.'));
                    //System.out.println(itemName);
                    Identifier id = new Identifier("origins", itemName);
                    var p = itemName.split("\\.", 2);
                    if (p.length > 1) {
                        id = Identifier.of(p[0], p[1]);
                    }
                    //System.out.println(id);
                    assert id != null;
                    id = new Identifier(id.getNamespace(), id.getPath().replace('/', '.').replace('\\', '.'));
                    if (!res.getNamespace().contentEquals("orif-defaults")) {
                        FUR_REGISTRY.remove(id);
                        FUR_RESOURCES.remove(id);
                    }
                    if (FUR_REGISTRY.containsKey(id)) {
                        OriginFurModel m = (OriginFurModel) FUR_REGISTRY.get(id).getGeoModel();
                        try {
                            //System.out.println(id);
                            m.recompile(JsonParser.parseString(new String(resources.get(res).getInputStream().readAllBytes())).getAsJsonObject());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //System.out.println(id);
                        FUR_RESOURCES.put(id, resources.get(res));
                    }
                }
                // 手动处理各形态的映射关系：
                if(FabricLoader.getInstance().isModLoaded(MOD_ID)){
                    RegFormConfig.CONFIGS.forEach((FORM, formConfig) -> {
                        Identifier id = new Identifier("origins", formConfig.getFormOriginID());
                        var fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
                        if(fur == null){
                            fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
                        }
                        if (fur == null) {
                            OriginalFurClient.FUR_REGISTRY.put(id, new OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
                        }
                        else{
                            try{
                                OriginalFurClient.FUR_REGISTRY.put(id, new OriginFur(JsonParser.parseString(new String(fur.getInputStream().readAllBytes())).getAsJsonObject()));
                                System.out.println(FUR_REGISTRY.get(id));
                                System.out.println(id.getPath());
                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                            }
                        }

                    });
                }


                /*assert FabricLoader.getInstance().isModLoaded("origins");
                try {
                    OriginRegistry.entries().forEach(identifierOriginEntry -> {
                        var oID = identifierOriginEntry.getKey();
                        var o = identifierOriginEntry.getValue();
                        Identifier id = oID;
                        var fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
                        if (fur == null) {
                            //id = Identifier.of("origins", oID.getPath());
                            // 在这里手动注册各形态的ID
                            id = new Identifier("origins", "phantom");
                            fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
                        }
                        if (fur == null) {
                            OriginalFurClient.FUR_REGISTRY.put(id, new OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
                        } else {
                            try {
                                OriginalFurClient.FUR_REGISTRY.put(id, new OriginFur(JsonParser.parseString(new String(fur.getInputStream().readAllBytes())).getAsJsonObject()));
                                if(FUR_REGISTRY.get(id) == null){
                                    System.out.println("null");
                                }else{
                                    System.out.println(FUR_REGISTRY.get(id));
                                }
                                //System.out.println(new String(fur.getInputStream().readAllBytes()));
                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    });
                } catch(Exception e) {
                    System.out.println("[ORIF] Failed to load origins registry! Ensure the Origins mod is loaded! Some models may not work, and crashes may occur!");
                    e.printStackTrace();
                }*/
            }
        });
    }
}

