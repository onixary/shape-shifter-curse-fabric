package net.onixary.shapeShifterCurseFabric.mixin;

import com.google.gson.JsonParser;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.PlayerOriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form_render.AbstractClientPlayerEntityCompatMixins;
import net.onixary.shapeShifterCurseFabric.player_form_render.IPlayerEntityMixins;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginalFurClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityMixins {
    @Mixin(ServerPlayerEntity.class)
    public static class ServerPlayerEntityMixin implements IPlayerEntityMixins{

        @Override
        public ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {
            // Not supported on server, this shouldn't ever be run!
            return new ArrayList<>();
        }
        @Override
        public ArrayList<Origin> originalFur$currentOrigins() {
            PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(this);
            var v = c.getOrigins().values();
            return new ArrayList<>(v);
        }
    }
    @Pseudo
    @Mixin(OtherClientPlayerEntity.class)
    public static class OtherClientPlayerEntityMixin implements IPlayerEntityMixins{

        @Unique
        boolean betterCombat$isSwinging = false;
        @Override
        public void betterCombat$setSwinging(boolean value) {
            betterCombat$isSwinging = value;
        }
        @Override
        public boolean betterCombat$isSwinging() {
            return betterCombat$isSwinging;
        }

    }

    @Mixin(AbstractClientPlayerEntity.class)
    public static class ChangeElytraTextureMixin implements IPlayerEntityMixins, AbstractClientPlayerEntityCompatMixins {
        @Unique
        Origin[] orif$origins = new Origin[]{};

        @Override
        public ArrayList<Origin> originfurs$getOrigins() {
            return new ArrayList<>(List.of(orif$origins));
        }

        @Override
        public void originfurs$setOrigins(ArrayList<Identifier> origins) {
            AbstractClientPlayerEntityCompatMixins.super.originfurs$setOrigins(origins);
            ArrayList<Origin> oA = new ArrayList<>();
            for (Identifier oID : origins) {
                var orig = OriginRegistry.get(oID);
                if (orig != null){
                    oA.add(orig);
                }
            }
        }

        /*@Unique
        private static Unsafe unsafe;

        static{
            try{
                final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe) unsafeField.get(null);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        @Unique
        private static void setFinalStatic(Field field, Object value) throws Exception{
            Object fieldBase = unsafe.staticFieldBase(field);
            long fieldOffset = unsafe.staticFieldOffset(field);

            unsafe.putObject(fieldBase, fieldOffset, value);
        }*/
        /*@ModifyReturnValue(method="getSkinTextures", at=@At("RETURN"))
        private SkinTextures getElytraTextureMixin(SkinTextures original) throws Exception {
            boolean hE = false;
            Identifier id = original.elytraTexture();
            for (var fur : originalFur$getCurrentModels()) {
                if (fur == null || !fur.hasCustomElytraTexture()) {
                    continue;
                }
                id = fur.getElytraTexture();
                break;
            }
            //        this.texture = identifier;
            //        this.textureUrl = string;
            //        this.capeTexture = identifier2;
            //        this.elytraTexture = identifier3;
            //        this.model = model;
            //        this.secure = bl;
            return new SkinTextures(original.texture(), original.textureUrl(), original.capeTexture(), id, original.model(), original.secure());
        }
    }*/
    @Override
    public ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {
        var cO = originalFur$currentOrigins();
        ArrayList<OriginalFurClient.OriginFur> furs = new ArrayList<>();
        if (cO.isEmpty()) {return furs;}
        for (var origin : cO){
            try {
                Identifier id = origin.getIdentifier();
                id = Identifier.of(id.getNamespace(), id.getPath().replace('/', '.').replace('\\', '.'));
                var opt = OriginalFurClient.FUR_REGISTRY.get(id);
                if (opt == null) {
                    opt = OriginalFurClient.FUR_REGISTRY.get(Identifier.of("origins", id.getPath()));
                    if (opt == null) {
                        System.out.println("[Origin Furs] Fur was null in entity mixin: " + id + ". This should NEVER happen! Report this to the devs!");
                        System.out.println(OriginalFurClient.FUR_REGISTRY.keySet());
                        System.out.println("[Origin Furs] Listed all registered furs. Please include the previous line!");
                        System.out.println("[Origin Furs] Please copy all mods, and this log file and create an issue:");
                        System.out.println("[Origin Furs] https://github.com/avetharun/OriginalFur/issues/new");
                        // 防止重复报错
                        OriginalFurClient.FUR_REGISTRY.put(id, new OriginalFurClient.OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
                        continue;
                    }
                }
                opt.currentAssociatedOrigin = origin;
                furs.add(opt);
            } catch (IndexOutOfBoundsException IOBE) {
                System.err.println("[Origin Furs] Something very wrong happened!");
                System.err.println(OriginalFurClient.FUR_REGISTRY.keySet());
                cO.forEach(System.out::println);
                System.err.println("[Origin Furs] Listed all registered furs. Please include the previous line!");
                System.err.println("[Origin Furs] Please copy all mods, and this log file and create an issue:");
                System.err.println("[Origin Furs] https://github.com/avetharun/OriginalFur/issues/new");
                throw new RuntimeException(IOBE.fillInStackTrace().toString());
            }
        }
        return furs;
    }
    @Override
    public ArrayList<Origin> originalFur$currentOrigins() {
        PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(this);
        var v = c.getOrigins().values();
        return new ArrayList<>(v);
    }

}
}
