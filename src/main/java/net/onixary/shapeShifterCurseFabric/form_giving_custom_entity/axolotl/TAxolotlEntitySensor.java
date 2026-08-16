package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.AxolotlAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

public class TAxolotlEntitySensor extends AxolotlAttackablesSensor {
    public static final SensorType<TAxolotlEntitySensor> T_AXOLOTL_ENTITY_SENSOR;

    static {
        T_AXOLOTL_ENTITY_SENSOR = (SensorType<TAxolotlEntitySensor>) Registry.register(Registries.SENSOR_TYPE, ShapeShifterCurseFabric.identifier("t_axolotl_attackables"), new SensorType(TAxolotlEntitySensor::new));
    }

    public static void init() {}

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        return this.isInRange(entity, target) && target.isInsideWaterOrBubbleColumn() && (this.isAlwaysHostileTo(target) || this.canHunt(entity, target)) && Sensor.testAttackableTargetPredicate(entity, target);
    }

    private boolean isInRange(LivingEntity axolotl, LivingEntity target) {
        return target.squaredDistanceTo(axolotl) <= (double)64.0F;  // 8.0f x 8.0f
    }

    private boolean canHunt(LivingEntity axolotl, LivingEntity target) {
        return !axolotl.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN) && target.getType().isIn(EntityTypeTags.AXOLOTL_HUNT_TARGETS);
    }

    private boolean isAlwaysHostileTo(LivingEntity axolotl) {
        return axolotl.getType().isIn(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES) || (axolotl instanceof PlayerEntity player && RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player));
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
