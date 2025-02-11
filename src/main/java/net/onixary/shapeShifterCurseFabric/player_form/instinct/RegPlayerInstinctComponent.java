package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegPlayerInstinctComponent implements EntityComponentInitializer {
    public static final ComponentKey<PlayerInstinctComponent> PLAYER_INSTINCT_COMP =
            ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "player_instinct_component"), PlayerInstinctComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
                PLAYER_INSTINCT_COMP,
                player -> new PlayerInstinctComponent(),
                RespawnCopyStrategy.ALWAYS_COPY// 玩家重生时复制数据
        );
    }
}
