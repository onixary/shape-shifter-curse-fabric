package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;

public class SnowFoxPerkCheck {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void configurableFallingAttackCap(TestContext c) {
        var p = player(c, 3);
        var factory = EnhancedFallingAttackPower.createFactory();
        var defaults = (EnhancedFallingAttackPower) factory.read(new com.google.gson.JsonObject()).apply(null, p);
        var json = new com.google.gson.JsonObject(); json.addProperty("max_multiplier", 4.0f);
        var configured = (EnhancedFallingAttackPower) factory.read(json).apply(null, p);
        c.assertTrue(defaults.getFallMultiplier(20) == 2.0f, "Omitted cap keeps old behavior");
        c.assertTrue(configured.getFallMultiplier(1) == 1.0f, "No bonus below one block");
        c.assertTrue(configured.getFallMultiplier(1.5f) == 2.5f, "Interpolate toward configured cap");
        c.assertTrue(configured.getFallMultiplier(2) == 4.0f && configured.getFallMultiplier(20) == 4.0f, "Cap at two blocks and beyond");
        json.addProperty("max_multiplier", 0.5f);
        var lower = (EnhancedFallingAttackPower) factory.read(json).apply(null, p);
        c.assertTrue(lower.getFallMultiplier(20) == 1.0f, "Cap cannot invert the bonus");
        p.discard(); c.complete();
    }
    private static Identifier id(String s) { return new Identifier("shape-shifter-curse", s); }
    private static ServerPlayerEntity player(TestContext c, double height) {
        var p = c.createMockCreativeServerPlayerInWorld();
        for (int i = 0; i < 61; i++) p.tick();
        p.getAbilities().invulnerable = false; p.getAbilities().flying = false; p.setInvulnerable(false);
        p.setPosition(c.getAbsolute(new Vec3d(2.5, height, 2.5))); p.setOnGround(false);
        p.getHungerManager().setFoodLevel(20); return p;
    }
    private static Power add(ServerPlayerEntity p, String suffix) {
        var type = PowerTypeRegistry.get(id("perks/snow_fox_" + suffix));
        PowerHolderComponent.KEY.get(p).addPower(type, id("snow_test"));
        return PowerHolderComponent.KEY.get(p).getPower(type);
    }
    private static FrostDivePower dive(ServerPlayerEntity p) {
        var power = (FrostDivePower) add(p, "frost_dive");
        var tag = new NbtCompound(); tag.put("cooldown", NbtLong.of(-10000)); power.fromTag(tag); return power;
    }
    private static void floor(TestContext c, boolean water) {
        for (int x = 0; x < 6; x++) for (int z = 0; z < 6; z++) {
            c.setBlockState(new BlockPos(x, 0, z), water ? Blocks.WATER : Blocks.STONE);
            for (int y = 1; y < 10; y++) c.setBlockState(new BlockPos(x, y, z), Blocks.AIR);
        }
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void registrations(TestContext c) {
        for (String key : new String[]{"revenge", "elusive_paws", "air_jump", "frost_dive", "cold_whirlwind_1", "cold_whirlwind_2", "cold_recovery_1", "cold_recovery_2"}) {
            c.assertTrue(PowerTypeRegistry.contains(id("perks/snow_fox_" + key)), "Power loaded: " + key);
            c.assertTrue(RegPerks.getPerk(id("snow_fox_" + key)) != null, "Perk registered: " + key);
        }
        c.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void frostClawIndependentFastFall(TestContext c) {
        floor(c, true); var p = player(c, 6);
        p.addStatusEffect(new StatusEffectInstance(RegOtherStatusEffects.FROST_CLAW, 200));
        c.assertTrue(EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, p) == 1, "Virtual Frost Walker I");
        p.move(MovementType.SELF, new Vec3d(0, -6, 0));
        c.assertTrue(c.getBlockState(new BlockPos(2, 0, 2)).isOf(Blocks.FROSTED_ICE), "Independent buff freezes before fast collision");
        c.assertTrue(p.isOnGround() && Math.abs(p.getY() - c.getAbsolutePos(new BlockPos(0, 1, 0)).getY()) < 0.01, "Lands on ice, not inside water");
        p.removeStatusEffect(RegOtherStatusEffects.FROST_CLAW);
        c.assertTrue(!EnchantmentHelper.hasFrostWalker(p), "Virtual enchantment removed with effect");
        p.discard(); c.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void diveImpactAndLock(TestContext c) {
        floor(c, true); var p = player(c, 8); var d = dive(p);
        var zombie = c.spawnEntity(EntityType.ZOMBIE, new BlockPos(4, 1, 2)); zombie.setAiDisabled(true);
        zombie.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(0);
        p.getHungerManager().setFoodLevel(2); p.setOnFireFor(10); d.onUse();
        c.assertTrue(FrostDivePower.isDiving(p) && p.getHungerManager().getFoodLevel() == 0 && !p.isOnFire(), "Start costs exactly 2 food and extinguishes");
        var up = (ActiveCooldownPower) add(p, "cold_whirlwind_2"); up.fromTag(NbtLong.of(-10000));
        p.getHungerManager().setFoodLevel(20); up.onUse();
        c.assertTrue(p.getHungerManager().getFoodLevel() == 20 && p.getVelocity().y < 0, "Whirlwind cannot interrupt or charge food");
        double x = p.getX(), y = p.getY(); p.move(MovementType.SELF, new Vec3d(2, 2, 0));
        c.assertTrue(p.getX() == x && p.getY() == y, "Movement lock blocks upward and horizontal impulses");
        p.handleFallDamage(30, 1, p.getDamageSources().fall()); c.assertTrue(p.getHealth() == 20, "Dive fall immunity");
        for (int i = 0; i < 15 && FrostDivePower.isDiving(p); i++) { d.tick(); p.move(MovementType.SELF, p.getVelocity()); }
        c.assertTrue(!FrostDivePower.isDiving(p), "Dive ends on actual ground");
        c.assertTrue(zombie.getHealth() == 14, "One six-point impact: " + zombie.getHealth());
        c.assertTrue(zombie.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() == 2, "Slowness III");
        zombie.timeUntilRegen = 0; d.tick(); c.assertTrue(zombie.getHealth() == 14, "No repeated impact");
        p.setOnGround(false); d.onUse(); c.assertTrue(!FrostDivePower.isDiving(p), "Eight-second cooldown enforced");
        p.discard(); zombie.discard(); c.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void shortDiveAndAirJump(TestContext c) {
        floor(c, false); var p = player(c, 5); var air = (AirJumpPower) add(p, "air_jump");
        air.tick(); air.tick(); air.onUse(); c.assertTrue(Math.abs(p.getVelocity().y - 0.6) < 0.001, "Fixed air jump impulse");
        p.setVelocity(Vec3d.ZERO); air.onUse(); c.assertTrue(p.getVelocity().y == 0, "Only one jump per flight");
        p.setOnGround(true); air.tick(); p.setOnGround(false); air.tick(); air.tick(); air.onUse();
        c.assertTrue(p.getVelocity().y > 0, "Landing replenishes air jump");
        var zombie = c.spawnEntity(EntityType.ZOMBIE, new BlockPos(4, 1, 2)); var d = dive(p); d.onUse();
        for (int i = 0; i < 8 && FrostDivePower.isDiving(p); i++) { d.tick(); p.move(MovementType.SELF, p.getVelocity()); }
        c.assertTrue(zombie.getHealth() == 20 && !zombie.hasStatusEffect(StatusEffects.SLOWNESS), "Exactly four blocks has no shockwave");
        p.discard(); zombie.discard(); c.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void revengeAndRecovery(TestContext c) {
        var p = player(c, 1); add(p, "revenge_melee"); add(p, "revenge_projectile");
        var witch = c.spawnEntity(EntityType.WITCH, new BlockPos(4, 1, 2));
        float before = witch.getHealth(); witch.damage(p.getDamageSources().playerAttack(p), 10);
        c.assertTrue(Math.abs(before - witch.getHealth() - 13) < 0.01, "Witch attack bonus");
        var arrow = new net.minecraft.entity.projectile.ArrowEntity(c.getWorld(), p); witch.timeUntilRegen = 0;
        before = witch.getHealth(); witch.damage(p.getDamageSources().arrow(arrow, p), 2);
        c.assertTrue(Math.abs(before - witch.getHealth() - 2.6) < 0.01, "Projectile bonus");
        add(p, "cold_recovery_1_immunity"); p.damage(p.getDamageSources().freeze(), 3);
        c.assertTrue(p.getHealth() == 20, "Freeze immunity");
        var heal = add(p, "cold_recovery_1_healing"); p.setHealth(10); c.setBlockState(new BlockPos(2, 1, 2), Blocks.POWDER_SNOW);
        heal.tick();
        for (int i = 0; i < 60; i++) { p.age++; heal.tick(); }
        c.assertTrue(p.getHealth() == 12, "Powder snow heals two per three seconds: " + p.getHealth());
        p.discard(); witch.discard(); c.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void whirlwindAndFrostRules(TestContext c) {
        floor(c, true); var p = player(c, 4);
        var whirlwind = (ActiveCooldownPower) add(p, "cold_whirlwind_2"); whirlwind.fromTag(NbtLong.of(-10000));
        p.setVelocity(0, -0.5, 0); p.setOnFireFor(4); whirlwind.onUse();
        c.assertTrue(p.getHungerManager().getFoodLevel() == 17 && !p.isOnFire(), "Whirlwind payment and extinguish");
        c.assertTrue(Math.abs(p.getVelocity().y - 1.1) < 0.001 && p.getStatusEffect(StatusEffects.ABSORPTION).getAmplifier() == 1, "Stronger lift and absorption II");
        whirlwind.onUse(); c.assertTrue(p.getHungerManager().getFoodLevel() == 17 && whirlwind.getRemainingTicks() == 100, "Five-second cooldown");
        p.setPosition(c.getAbsolute(new Vec3d(2.5, 1.6, 2.5)));
        c.setBlockState(new BlockPos(1, 0, 2), Blocks.WATER.getDefaultState().with(net.minecraft.block.FluidBlock.LEVEL, 1));
        c.setBlockState(new BlockPos(3, 1, 2), Blocks.STONE);
        RegOtherStatusEffects.FROST_CLAW.applyUpdateEffect(p, 0);
        c.assertTrue(c.getBlockState(new BlockPos(1, 0, 2)).isOf(Blocks.WATER), "Flowing water remains water");
        c.assertTrue(c.getBlockState(new BlockPos(3, 0, 2)).isOf(Blocks.WATER), "Covered source water remains water");
        c.assertTrue(c.getBlockState(new BlockPos(2, 0, 2)).isOf(Blocks.FROSTED_ICE), "Airborne source water freezes");
        p.discard(); c.complete();
    }
}
