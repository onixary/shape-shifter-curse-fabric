package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.Power;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class DisableHurtCameraPowerCheck {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void conditionFollowsWither(TestContext context) {
        checkCondition(context, "form_anubis_wolf_disable_hurt_camera_when_withered", DisableHurtCameraPower.class);
        checkCondition(context, "form_anubis_wolf_disable_wither_hearts", DisableWitherHeartsPower.class);
        context.complete();
    }

    private static void checkCondition(TestContext context, String powerId, Class<? extends Power> powerClass) {
        var entity = EntityType.COW.create(context.getWorld());
        var type = PowerTypeRegistry.get(new Identifier("shape-shifter-curse", powerId));
        var source = new Identifier("test", "hurt_camera");
        var holder = PowerHolderComponent.KEY.get(entity);
        holder.addPower(type, source);
        context.assertTrue(!PowerHolderComponent.hasPower(entity, powerClass), "Inactive without Wither: " + powerId);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200));
        context.assertTrue(PowerHolderComponent.hasPower(entity, powerClass), "Active with Wither: " + powerId);
        context.assertTrue(entity.hasStatusEffect(StatusEffects.WITHER), "Visual power must not remove Wither");
        entity.removeStatusEffect(StatusEffects.WITHER);
        context.assertTrue(!PowerHolderComponent.hasPower(entity, powerClass), "Inactive when Wither ends: " + powerId);
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200));
        holder.removePower(type, source);
        context.assertTrue(!PowerHolderComponent.hasPower(entity, powerClass), "Wither alone does not suppress visuals: " + powerId);
    }
}
