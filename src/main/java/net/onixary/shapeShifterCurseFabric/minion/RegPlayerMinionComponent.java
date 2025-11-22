package net.onixary.shapeShifterCurseFabric.minion;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

// 或许应该把这几个Component合并到同一个RegClass
public class RegPlayerMinionComponent implements EntityComponentInitializer {
    public static final ComponentKey<PlayerMinionComponent> PLAYER_MINION_DATA = ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "player_minion_data"), PlayerMinionComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        // 为 PlayerEntity 注册组件，并启用持久化
        entityComponentFactoryRegistry.registerForPlayers(
                PLAYER_MINION_DATA,
                player -> new PlayerMinionComponent(),
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
