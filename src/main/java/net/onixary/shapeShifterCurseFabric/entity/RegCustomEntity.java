package net.onixary.shapeShifterCurseFabric.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.entity.projectile.WebBullet;

public class RegCustomEntity {
    public static final EntityType<WebBullet> WEB_BULLET = Registry.register(
            Registries.ENTITY_TYPE,
            ShapeShifterCurseFabric.identifier("web_bullet"),
            FabricEntityTypeBuilder.<WebBullet>create(SpawnGroup.MISC, WebBullet::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(10).trackedUpdateRate(1).build()
    );

    public static void init() {
    }
}
