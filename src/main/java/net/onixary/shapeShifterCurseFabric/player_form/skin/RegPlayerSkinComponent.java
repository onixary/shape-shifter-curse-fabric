package net.onixary.shapeShifterCurseFabric.player_form.skin;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;

public class RegPlayerSkinComponent  implements EntityComponentInitializer {
    public static final ComponentKey<PlayerSkinComponent> SKIN_SETTINGS =
            ComponentRegistry.getOrCreate(new Identifier(ShapeShifterCurseFabric.MOD_ID, "skin_settings"), PlayerSkinComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                SKIN_SETTINGS,
                player -> new PlayerSkinComponent(),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
