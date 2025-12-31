package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.EntityGlowPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;

import java.util.function.Predicate;

// 从EntityGlowPower复制过来的 一点没动
public class EntityGlowWithoutF1Power extends EntityGlowPower {

    private final Predicate<Entity> entityCondition;
    private final Predicate<Pair<Entity, Entity>> bientityCondition;
    private final boolean useTeams;
    private final float red;
    private final float green;
    private final float blue;

    public EntityGlowWithoutF1Power(PowerType<?> type, LivingEntity entity, Predicate<Entity> entityCondition, Predicate<Pair<Entity, Entity>> bientityCondition, boolean useTeams, float red, float green, float blue) {
        super(type, entity, entityCondition, bientityCondition, useTeams, red, green, blue);
        this.entityCondition = entityCondition;
        this.bientityCondition = bientityCondition;
        this.useTeams = useTeams;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public boolean doesApply(Entity e) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (!ClientUtils.CanDisplayGUI()) {
                return false;
            }
        }
        return (entityCondition == null || entityCondition.test(e)) && (bientityCondition == null || bientityCondition.test(new Pair<>(entity, e)));
    }

    public boolean usesTeams() {
        return useTeams;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("entity_glow_without_f1"),
                new SerializableData()
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("use_teams", SerializableDataTypes.BOOLEAN, true)
                        .add("red", SerializableDataTypes.FLOAT, 1.0F)
                        .add("green", SerializableDataTypes.FLOAT, 1.0F)
                        .add("blue", SerializableDataTypes.FLOAT, 1.0F),
                data ->
                        (type, player) -> new EntityGlowWithoutF1Power(type, player,
                                data.get("entity_condition"),
                                data.get("bientity_condition"),
                                data.getBoolean("use_teams"),
                                data.getFloat("red"),
                                data.getFloat("green"),
                                data.getFloat("blue")))
                .allowCondition();
    }
}
