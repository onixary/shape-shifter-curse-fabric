package net.onixary.shapeShifterCurseFabric.mixin;

import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.onixary.shapeShifterCurseFabric.integration.origins.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginEvents;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginalFurClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Pseudo
@Mixin(OriginRegistry.class)
public class OriginsRegistryMixin {

    @Mixin(value = ModPacketsS2C.class, remap = false)
    public static class OriginListMixin$ORIF{
        @ModifyReturnValue(method="lambda$receiveOriginList$2", at=@At(value="RETURN"))
        private static Origin onRecievedOriginsDefineMissingMixin(Origin original) throws IOException {
            var manager = MinecraftClient.getInstance().getResourceManager();
            String path = "furs";
            Identifier id = original.getIdentifier();
            var fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
            if (fur == null) {
                OriginalFurClient.FUR_REGISTRY.put(id, new OriginalFurClient.OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
            } else {
                OriginalFurClient.FUR_REGISTRY.put(id, new  OriginalFurClient.OriginFur(JsonParser.parseString(new String(fur.getInputStream().readAllBytes())).getAsJsonObject()));
            }
            return original;
        }
    }
    //@Inject(method="register(Lnet/minecraft/util/Identifier;Lio/github/apace100/origins/origin/Origin;)Lio/github/apace100/origins/origin/Origin;", at=@At("RETURN"))
    private static void registerMixin(Identifier id, Origin origin, CallbackInfoReturnable<Origin> cir){
        OriginEvents.ORIGIN_REGISTRY_ADDED_EVENT.invoker().onOriginAddedToRegistry(origin,id);
    }
}
