package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.Active;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.perk.NormalPerk;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.util.util.cost.ItemCost;

public class FormPerkCheck {
    private static Identifier id(String path) { return new Identifier("shape-shifter-curse", path); }
    private static final Identifier SOURCE = id("perk_check");

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void pounceMovementAndHit(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        player.setPosition(context.getAbsolute(new net.minecraft.util.math.Vec3d(0.5, 1, 0.5)));
        var target = context.spawnEntity(EntityType.ZOMBIE, new net.minecraft.util.math.BlockPos(0, 1, 5));
        var holder = PowerHolderComponent.KEY.get(player);
        var type = PowerTypeRegistry.get(id("perks/ocelot_long_pounce_flight"));
        holder.addPower(type, SOURCE);
        var pounce = (TargetPouncePower) holder.getPower(type);
        player.getHungerManager().setFoodLevel(10);
        pounce.start(target);
        for (int i = 0; i < 15; i++) {
            pounce.tick();
            player.move(net.minecraft.entity.MovementType.SELF, player.getVelocity());
        }
        context.assertTrue(target.getHealth() < 11, "Pounce reaches target and deals damage");
        context.assertTrue(player.getHungerManager().getFoodLevel() == 16, "Pounce rewards food once");
        player.setPosition(context.getAbsolute(new net.minecraft.util.math.Vec3d(0.5, 1, 0.5)));
        context.setBlockState(new net.minecraft.util.math.BlockPos(0, 1, 2), net.minecraft.block.Blocks.STONE);
        context.setBlockState(new net.minecraft.util.math.BlockPos(0, 2, 2), net.minecraft.block.Blocks.STONE);
        pounce.start(target); pounce.tick(); pounce.tick();
        context.assertTrue(player.getVelocity().lengthSquared() == 0, "Wall cancels pounce movement");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void raycastPreviewAndRelease(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        player.setPosition(context.getAbsolute(new net.minecraft.util.math.Vec3d(0.5, 1, 0.5)));
        player.setYaw(0); player.setPitch(0);
        var near = context.spawnEntity(EntityType.ZOMBIE, new net.minecraft.util.math.BlockPos(0, 1, 3));
        var far = context.spawnEntity(EntityType.ZOMBIE, new net.minecraft.util.math.BlockPos(0, 1, 6));
        var holder = PowerHolderComponent.KEY.get(player);
        var chargeType = PowerTypeRegistry.get(id("perks/axolotl_tidal_pull_charge"));
        var glowType = PowerTypeRegistry.get(id("perks/axolotl_tidal_pull_glow"));
        holder.addPower(chargeType, SOURCE); holder.addPower(glowType, SOURCE);
        ChargePower charge = (ChargePower) holder.getPower(chargeType);
        var glow = (io.github.apace100.apoli.power.EntityGlowPower) holder.getPower(glowType);
        player.setAir(100); charge.onUse();
        context.assertTrue(glow.isActive() && glow.doesApply(near), "Preview chooses nearest living target");
        context.assertTrue(!glow.doesApply(far), "Near target occludes far target");
        charge.fire(false);
        context.assertTrue(near.getVelocity().z < -1 && near.getVelocity().y > 0, "Apoli release pulls preview target upward");
        context.assertTrue(far.getVelocity().lengthSquared() == 0, "Release only affects first target");
        context.assertTrue(!glow.isActive(), "Release clears glow condition");
        context.setBlockState(new net.minecraft.util.math.BlockPos(0, 2, 2), net.minecraft.block.Blocks.STONE);
        context.assertTrue(!glow.doesApply(near), "Walls block target selection");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void arrowUseAndPrimaryAttack(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        var holder = PowerHolderComponent.KEY.get(player);
        holder.addPower(PowerTypeRegistry.get(id("perks/bat_arrow_throw")), SOURCE);
        var arrows = new net.minecraft.item.ItemStack(net.minecraft.item.Items.ARROW, 2);
        player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, arrows);
        arrows.use(context.getWorld(), player, net.minecraft.util.Hand.MAIN_HAND);
        context.assertTrue(arrows.getCount() == 1, "Right click fires and consumes one arrow");
        var spectral = new net.minecraft.item.ItemStack(net.minecraft.item.Items.SPECTRAL_ARROW, 2);
        player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, spectral);
        spectral.use(context.getWorld(), player, net.minecraft.util.Hand.MAIN_HAND);
        context.assertTrue(spectral.getCount() == 2, "Changing arrow type cannot bypass cooldown");
        holder.addPower(PowerTypeRegistry.get(id("perks/ocelot_ambush")), SOURCE);
        player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, net.minecraft.item.ItemStack.EMPTY);
        player.setSneaking(true);
        player.getHungerManager().setFoodLevel(10);
        var target = EntityType.ZOMBIE.create(context.getWorld());
        player.attack(target);
        context.assertTrue(target.getHealth() < 15, "Primary attack applies ambush bonus through mixin");
        context.assertTrue(player.getHungerManager().getFoodLevel() == 9, "Successful primary attack pays once");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void registrationsAndCosts(TestContext context) {
        int nodes = 0, prisms = 0;
        for (String form : new String[]{"bat_3", "axolotl_3", "ocelot_3"}) {
            var tree = RegPerks.getPerkTree(id(form + "_perk_tree"));
            context.assertTrue(tree != null, "Registered tree: " + form);
            context.assertTrue(RegPlayerForms.getPlayerFormOrThrow(id(form)).getPerkTreeID().equals(tree.getID()), "Bound form: " + form);
            for (var node : tree.getAllNodes()) {
                nodes++;
                var perk = (NormalPerk) RegPerks.getPerk(node.perkID);
                context.assertTrue(perk != null, "Registered perk: " + node.perkID);
                if (perk.getCost() instanceof ItemCost item) {
                    prisms++;
                    context.assertTrue(item.getAmount() >= 1 && item.getAmount() <= 2, "Prism amount");
                }
                for (var power : perk.powerAdd) context.assertTrue(PowerTypeRegistry.get(power) != null, "Parsed power: " + power);
                for (var power : perk.powerRemove) context.assertTrue(PowerTypeRegistry.get(power) != null, "Removed power exists: " + power);
                for (var parent : node.dependentPerkIDs) context.assertTrue(tree.getNode(parent) != null, "Parent exists");
            }
        }
        context.assertTrue(nodes == 26 && prisms == 8, "26 perks and 8 prism purchases");
        context.assertTrue(RegPlayerForms.BAT_3_SUB_AVALI.getPerkTreeID().equals(id("bat_3_perk_tree")), "Avali inherits bat tree");
        var buyer = context.createMockSurvivalPlayer();
        buyer.addExperience(200);
        var cost = new net.onixary.shapeShifterCurseFabric.util.util.cost.BaseCost(
                net.onixary.shapeShifterCurseFabric.util.util.cost.RegCostType.COST_XP, 100);
        cost.getType().pay(cost, buyer);
        context.assertTrue(buyer.totalExperience == 100, "Experience cost deducts experience");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void combatThresholdsAndFood(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        var target = EntityType.ZOMBIE.create(context.getWorld());
        var holder = PowerHolderComponent.KEY.get(player);
        holder.addPower(PowerTypeRegistry.get(id("perks/axolotl_moisture_return_2")), SOURCE);
        player.setAir(100);
        ActionOnCombatHitPower.fire(player, target, "critical", 7);
        context.assertTrue(player.getAir() == 100, "Exactly 7 must not refund");
        ActionOnCombatHitPower.fire(player, target, "melee", 9);
        context.assertTrue(player.getAir() == 100, "Non-critical hit must not refund");
        ActionOnCombatHitPower.fire(player, target, "critical", 8);
        context.assertTrue(player.getAir() == 130, "Below 150 refunds 30");
        player.setAir(150);
        ActionOnCombatHitPower.fire(player, target, "critical", 8);
        context.assertTrue(player.getAir() == 165, "At 150 refunds 15");
        holder.addPower(PowerTypeRegistry.get(id("perks/ocelot_ambush")), SOURCE);
        player.getHungerManager().setFoodLevel(10);
        player.setSneaking(true);
        context.assertTrue(ActionOnCombatHitPower.meleeBonus(player, target) == 6, "Ambush adds six");
        ActionOnCombatHitPower.fire(player, target, "melee", 6);
        context.assertTrue(player.getHungerManager().getFoodLevel() == 9, "Ambush charges one food, not exhaustion");
        player.setSneaking(false);
        context.assertTrue(ActionOnCombatHitPower.meleeBonus(player, target) == 0, "Standing must not ambush");
        holder.addPower(PowerTypeRegistry.get(id("perks/ocelot_hungry_pounce")), SOURCE);
        ActionOnCombatHitPower.fire(player, target, "pounce", 0);
        context.assertTrue(player.getHungerManager().getFoodLevel() == 9, "No damage must not refund");
        ActionOnCombatHitPower.fire(player, target, "pounce", 7);
        context.assertTrue(player.getHungerManager().getFoodLevel() == 11, "Pounce refunds two");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void overloadAndCharging(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        var holder = PowerHolderComponent.KEY.get(player);
        var overload = PowerTypeRegistry.get(id("perks/ocelot_metabolic_overload"));
        holder.addPower(overload, SOURCE);
        holder.getPower(overload).fromTag(net.minecraft.nbt.NbtLong.of(-1000));
        player.getHungerManager().setFoodLevel(12);
        ((Active) holder.getPower(overload)).onUse();
        context.assertTrue(player.getHungerManager().getFoodLevel() == 4, "Overload exact food cost");
        context.assertTrue(player.hasStatusEffect(StatusEffects.ABSORPTION) && player.getStatusEffect(StatusEffects.ABSORPTION).getAmplifier() == 1, "Absorption II");
        ((Active) holder.getPower(overload)).onUse();
        context.assertTrue(player.getHungerManager().getFoodLevel() == 4, "Cooldown prevents duplicate cost");
        var chargeType = PowerTypeRegistry.get(id("perks/axolotl_tidal_pull_charge"));
        holder.addPower(chargeType, SOURCE);
        ChargePower charge = (ChargePower) holder.getPower(chargeType);
        player.setAir(100);
        charge.onUse(); charge.onUse();
        context.assertTrue(player.getAir() == 80, "Holding pays once");
        context.assertTrue(charge.isCharging(), "Charging state for preview");
        charge.fire(false);
        context.assertTrue(!charge.isCharging(), "Release clears preview");
        charge.onUse();
        context.assertTrue(player.getAir() == 80, "Cooldown prevents recharging");
        context.complete();
    }
}
