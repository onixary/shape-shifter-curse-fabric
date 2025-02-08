package net.onixary.shapeShifterCurseFabric.player_form.ability.form_abilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.Objects;

public class BatAbilities0 {

    public void onAdded() {
        // 触发狂暴效果（例如攻击力提升）
        //entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
        //        .addTemporaryModifier(new EntityAttributeModifier("BerserkBoost", 5.0, EntityAttributeModifier.Operation.ADDITION));
    }


    public void onRemoved() {
        // 移除攻击力提升
        //entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
        //        .removeModifier(Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).getModifiers());
    }
}
