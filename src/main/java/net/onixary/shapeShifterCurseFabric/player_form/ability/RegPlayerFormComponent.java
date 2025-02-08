package net.onixary.shapeShifterCurseFabric.player_form.ability;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegPlayerFormComponent implements EntityComponentInitializer {
    public static final ComponentKey<PlayerFormComponent> PLAYER_FORM=
            ComponentRegistry.getOrCreate(new Identifier(MOD_ID, "player_form"), PlayerFormComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // 为 PlayerEntity 注册组件，并启用持久化
        registry.registerForPlayers(
                PLAYER_FORM,
                player -> new PlayerFormComponent(),
                RespawnCopyStrategy.ALWAYS_COPY// 玩家重生时复制数据
        );
    }
}
