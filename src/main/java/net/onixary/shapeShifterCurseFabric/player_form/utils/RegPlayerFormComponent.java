package net.onixary.shapeShifterCurseFabric.player_form.utils;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class RegPlayerFormComponent implements EntityComponentInitializer {
    public static final ComponentKey<PlayerFormComponent> PLAYER_FORM = PlayerFormComponent.COMPONENT;

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                PLAYER_FORM,
                PlayerFormComponent::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
