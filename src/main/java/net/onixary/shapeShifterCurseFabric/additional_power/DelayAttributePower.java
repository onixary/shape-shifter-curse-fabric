package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.LinkedList;
import java.util.List;

public class DelayAttributePower extends Power {
    private final int TargetDelay;
    private int NowDelay;
    private final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();;
    private final boolean updateHealth;
    private final int tickRate;
    private boolean IsModActive = false;

    public DelayAttributePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data)  {
        // 延迟生效的Attribute
        super(type, entity);
        this.tickRate = data.getInt("tick_rate");
        this.updateHealth = data.getBoolean("updateHealth");
        this.TargetDelay = data.getInt("delay");
        this.NowDelay = this.TargetDelay;  // 确保玩家进入游戏时的属性会立即生效
        if (data.isPresent("modifier")) {
            this.addModifier((AttributedEntityAttributeModifier)data.get("modifier"));
        }
        if (data.isPresent("modifiers")) {
            List<AttributedEntityAttributeModifier> modifierList = (List)data.get("modifiers");
            modifierList.forEach(this::addModifier);
        }
        this.setTicking(true);
    }

    public void tick() {
        if (this.entity.age % this.tickRate == 0) {
            if (this.isActive()) {
                if (!this.IsModActive) {
                    if (this.NowDelay >= this.TargetDelay) {
                        this.addMods();
                        this.NowDelay = 0;
                        return;
                    }
                    this.NowDelay++;
                    return;
                }
                else {
                    this.addMods();
                }
            } else {
                if (this.IsModActive) {
                    if (this.NowDelay >= this.TargetDelay) {
                        this.removeMods();
                        this.NowDelay = 0;
                        return;
                    }
                    this.NowDelay++;
                    return;
                }
                else {
                    this.removeMods();
                }
            }
            this.NowDelay = 0;
        }
    }

    public void onRemoved() {
        this.removeMods();
    }

    public void addMods() {
        this.IsModActive = true;
        float previousMaxHealth = this.entity.getMaxHealth();
        float previousHealthPercent = this.entity.getHealth() / previousMaxHealth;
        this.modifiers.forEach((mod) -> {
            if (this.entity.getAttributes().hasAttribute(mod.getAttribute())) {
                EntityAttributeInstance instance = this.entity.getAttributeInstance(mod.getAttribute());
                if (instance != null && !instance.hasModifier(mod.getModifier())) {
                    instance.addTemporaryModifier(mod.getModifier());
                }
            }

        });
        float afterMaxHealth = this.entity.getMaxHealth();
        if (this.updateHealth && afterMaxHealth != previousMaxHealth) {
            this.entity.setHealth(afterMaxHealth * previousHealthPercent);
        }
    }

    public void removeMods() {
        this.IsModActive = false;
        float previousMaxHealth = this.entity.getMaxHealth();
        float previousHealthPercent = this.entity.getHealth() / previousMaxHealth;
        this.modifiers.forEach((mod) -> {
            if (this.entity.getAttributes().hasAttribute(mod.getAttribute())) {
                EntityAttributeInstance instance = this.entity.getAttributeInstance(mod.getAttribute());
                if (instance != null && instance.hasModifier(mod.getModifier())) {
                    instance.removeModifier(mod.getModifier());
                }
            }

        });
        float afterMaxHealth = this.entity.getMaxHealth();
        if (this.updateHealth && afterMaxHealth != previousMaxHealth) {
            this.entity.setHealth(afterMaxHealth * previousHealthPercent);
        }
    }

    public DelayAttributePower addModifier(EntityAttribute attribute, EntityAttributeModifier modifier) {
        AttributedEntityAttributeModifier mod = new AttributedEntityAttributeModifier(attribute, modifier);
        this.modifiers.add(mod);
        return this;
    }

    public DelayAttributePower addModifier(AttributedEntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("delay_attribute"),
                new SerializableData()
                    .add("tick_rate", SerializableDataTypes.INT, 1)
                    .add("updateHealth", SerializableDataTypes.BOOLEAN, true)
                    .add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
                    .add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
                    .add("delay", SerializableDataTypes.INT, 0),
                data -> (powerType, livingEntity) -> new DelayAttributePower(
                    powerType,
                    livingEntity,
                    data
                )
        ).allowCondition();
    }
}
