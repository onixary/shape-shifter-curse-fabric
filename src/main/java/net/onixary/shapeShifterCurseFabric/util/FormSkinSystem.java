package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class FormSkinSystem {
    public static void init() {};

    public static class FormSkin {
        public static final String IdNameSpace = ShapeShifterCurseFabric.MOD_ID;
        public static final String IdPrefix = "dynamic_skin";
        public static long nowId = 0;

        // SkinModels系统没实现(什么时候要用时再写) 需要用Mixin(AW只能动原版代码)挂载到 mod.azure.azurelib.cache.AzureLibCache 上 用于向MODELS的变量添加模型数据

        public final boolean enableSkinModels;
        public final boolean enableSkinTextures;
        private boolean isSkinModelDynamic = false;
        private @Nullable Identifier skinModel = null;
        private boolean isSkinModelSlimDynamic = false;
        private @Nullable Identifier skinModelSlim = null;
        private boolean isSkinTextureDynamic = false;
        private @Nullable Identifier skinTexture = null;
        private boolean isSkinTextureSlimDynamic = false;
        private @Nullable Identifier skinTextureSlim = null;
        private boolean isSkinOverlayTextureDynamic = false;
        private @Nullable Identifier skinOverlayTexture = null;
        private boolean isSkinOverlayTextureSlimDynamic = false;
        private @Nullable Identifier skinOverlayTextureSlim = null;
        private boolean isSkinEmissiveTextureDynamic = false;
        private @Nullable Identifier skinEmissiveTexture = null;
        private boolean isSkinEmissiveTextureSlimDynamic = false;
        private @Nullable Identifier skinEmissiveTextureSlim = null;
        private boolean isSkinFullBrightTextureDynamic = false;
        private @Nullable Identifier skinFullBrightTexture = null;
        private boolean isSkinFullBrightTextureSlimDynamic = false;
        private @Nullable Identifier skinFullBrightTextureSlim = null;

        public FormSkin(boolean enableSkinModels, boolean enableSkinTextures) {
            this.enableSkinModels = enableSkinModels;
            this.enableSkinTextures = enableSkinTextures;
        }

        private void CleanSkinModel(boolean slim) {
            // Not Implemented
        }

        private void CleanSkinTexture(boolean slim) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            if (slim) {
                if (this.isSkinTextureSlimDynamic) {
                    Identifier id = this.skinTextureSlim;
                    this.skinTextureSlim = null;
                    this.isSkinTextureSlimDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            } else {
                if (this.isSkinTextureDynamic) {
                    Identifier id = this.skinTexture;
                    this.skinTexture = null;
                    this.isSkinTextureDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            }
        }

        private void CleanSkinOverlayTexture(boolean slim) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            if (slim) {
                if (this.isSkinOverlayTextureSlimDynamic) {
                    Identifier id = this.skinOverlayTextureSlim;
                    this.skinOverlayTextureSlim = null;
                    this.isSkinOverlayTextureSlimDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            } else {
                if (this.isSkinOverlayTextureDynamic) {
                    Identifier id = this.skinOverlayTexture;
                    this.skinOverlayTexture = null;
                    this.isSkinOverlayTextureDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            }
        }

        private void CleanSkinEmissiveTexture(boolean slim) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            if (slim) {
                if (this.isSkinEmissiveTextureSlimDynamic) {
                    Identifier id = this.skinEmissiveTextureSlim;
                    this.skinEmissiveTextureSlim = null;
                    this.isSkinEmissiveTextureSlimDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            } else {
                if (this.isSkinEmissiveTextureDynamic) {
                    Identifier id = this.skinEmissiveTexture;
                    this.skinEmissiveTexture = null;
                    this.isSkinEmissiveTextureDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            }
        }

        private void CleanSkinFullBrightTexture(boolean slim) {
            TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
            if (slim) {
                if (this.isSkinFullBrightTextureSlimDynamic) {
                    Identifier id = this.skinFullBrightTextureSlim;
                    this.skinFullBrightTextureSlim = null;
                    this.isSkinFullBrightTextureSlimDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            } else {
                if (this.isSkinFullBrightTextureDynamic) {
                    Identifier id = this.skinFullBrightTexture;
                    this.skinFullBrightTexture = null;
                    this.isSkinFullBrightTextureDynamic = false;
                    if (id != null) {
                        textureManager.destroyTexture(id);
                    }
                }
            }
        }

        public void Clean() {
            this.CleanSkinModel(true);
            this.CleanSkinModel(false);
            this.CleanSkinTexture(true);
            this.CleanSkinTexture(false);
            this.CleanSkinOverlayTexture(true);
            this.CleanSkinOverlayTexture(false);
            this.CleanSkinEmissiveTexture(true);
            this.CleanSkinEmissiveTexture(false);
            this.CleanSkinFullBrightTexture(true);
            this.CleanSkinFullBrightTexture(false);
        }

        public Identifier getNextID() {
            Identifier id = new Identifier(IdNameSpace, IdPrefix + nowId);
            nowId++;
            return id;
        }

        public Identifier setSkinModel(NativeImage image, boolean slim) {
            throw new NotImplementedException();
        }

        public Identifier getSkinModel(boolean slim) {
            if (slim) {
                return this.skinModelSlim;
            }
            return this.skinModel;
        }

        public Identifier setSkinTexture(NativeImage image, boolean slim) {
            this.CleanSkinTexture(slim);
            if (image != null) {
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                Identifier id = this.getNextID();
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
                if (slim) {
                    this.isSkinTextureSlimDynamic = true;
                    this.skinTextureSlim = id;
                } else {
                    this.isSkinTextureDynamic = true;
                    this.skinTexture = id;
                }
                return id;
            }
            return null;
        }

        public Identifier getSkinTexture(boolean slim) {
            if (slim) {
                return this.skinTextureSlim;
            }
            return this.skinTexture;
        }

        public Identifier setSkinOverlayTexture(NativeImage image, boolean slim) {
            this.CleanSkinOverlayTexture(slim);
            if (image != null) {
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                Identifier id = this.getNextID();
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
                if (slim) {
                    this.isSkinOverlayTextureSlimDynamic = true;
                    this.skinOverlayTextureSlim = id;
                } else {
                    this.isSkinOverlayTextureDynamic = true;
                    this.skinOverlayTexture = id;
                }
                return id;
            }
            return null;
        }

        public Identifier getSkinOverlayTexture(boolean slim) {
            if (slim) {
                return this.skinOverlayTextureSlim;
            }
            return this.skinOverlayTexture;
        }

        public Identifier setSkinEmissiveTexture(NativeImage image, boolean slim) {
            this.CleanSkinEmissiveTexture(slim);
            if (image != null) {
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                Identifier id = this.getNextID();
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
                if (slim) {
                    this.isSkinEmissiveTextureSlimDynamic = true;
                    this.skinEmissiveTextureSlim = id;
                } else {
                    this.isSkinEmissiveTextureDynamic = true;
                    this.skinEmissiveTexture = id;
                }
                return id;
            }
            return null;
        }

        public Identifier getSkinEmissiveTexture(boolean slim) {
            if (slim) {
                return this.skinEmissiveTextureSlim;
            }
            return this.skinEmissiveTexture;
        }

        public Identifier setSkinFullBrightTexture(NativeImage image, boolean slim) {
            this.CleanSkinFullBrightTexture(slim);
            if (image != null) {
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                Identifier id = this.getNextID();
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
                if (slim) {
                    this.isSkinFullBrightTextureSlimDynamic = true;
                    this.skinFullBrightTextureSlim = id;
                } else {
                    this.isSkinFullBrightTextureDynamic = true;
                    this.skinFullBrightTexture = id;
                }
                return id;
            }
            return null;
        }

        public Identifier getSkinFullBrightTexture(boolean slim) {
            if (slim) {
                return this.skinFullBrightTextureSlim;
            }
            return this.skinFullBrightTexture;
        }
    }

    public static boolean enablePlayerFormSkinSystem = false;

    private static final HashMap<UUID, HashMap<Identifier, FormSkin>> PlayerFormSkinRegister = new HashMap<>();

    public static @Nullable FormSkin getFormSkin(UUID uuid, Identifier FormID) {
        if (!enablePlayerFormSkinSystem) {
            return null;
        }
        return PlayerFormSkinRegister.getOrDefault(uuid, new HashMap<>()).get(FormID);
    }

    public static void registerFormSkin(UUID uuid, Identifier FormID, FormSkin formSkin) {
        if (!enablePlayerFormSkinSystem) {
            return;
        }
        HashMap<Identifier, FormSkin> map = PlayerFormSkinRegister.computeIfAbsent(uuid, k -> new HashMap<>());
        if (map.containsKey(FormID)) {
            FormSkin old = map.get(FormID);
            old.Clean();
        }
        map.put(FormID, formSkin);
    }

    // 测试代码 需要在OpenGL初始化(已经显示页面 不要在Client初始化时调用 100%会因为EXCEPTION_ACCESS_VIOLATION崩溃)后再调用 推荐挂在重载资源包处 那里OpenGL肯定已经初始化过了
    // 形态ID用模型json里的形态ID 现在是OriginID(后续改为形态ID 现在由于我想保留Layer系统 就还是用Origins的ID了 等剔除Origins后再改(我手动实现一套Layer系统))
    // static {
    //     Path image = FabricLoader.getInstance().getConfigDir().resolve("ssc_dev/texture.png");  // 我从我拓展里CV了一张蝙蝠变种的贴图 那个形态没动多少模型
    //     if (Files.exists(image)) {
    //         NativeImage texture = null;
    //         try (InputStream is = Files.newInputStream(image)) {
    //             texture = NativeImage.read(is);
    //         } catch (IOException ignored) {}
    //         if (texture != null) {
    //             FormSkin fs = new FormSkin(false, true);
    //             fs.setSkinTexture(texture, false);
    //             FormSkinSystem.registerFormSkin(UUID.fromString("12345678-1234-1234-1234-123456789012"), RegPlayerForms.BAT_3.getFormOriginID(), fs);
    //         }
    //     }
    // }
}
