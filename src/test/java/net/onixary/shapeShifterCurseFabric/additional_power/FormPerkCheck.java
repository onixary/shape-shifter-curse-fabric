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
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void collectWaterReleaseAndLandPropulsion(TestContext c) {
        var p=c.createMockSurvivalPlayer();
        p.setPosition(c.getAbsolute(new net.minecraft.util.math.Vec3d(.5,1,.5)));p.setYaw(0);
        var holder=PowerHolderComponent.KEY.get(p);
        var chargeType=PowerTypeRegistry.get(id("perks/axolotl_collect_water_charge"));
        var glowType=PowerTypeRegistry.get(id("perks/axolotl_collect_water_glow"));
        holder.addPower(chargeType,SOURCE);holder.addPower(glowType,SOURCE);
        var charge=(ChargePower)holder.getPower(chargeType);
        var glow=(io.github.apace100.apoli.power.EntityGlowPower)holder.getPower(glowType);
        var a=c.spawnEntity(EntityType.ZOMBIE,new net.minecraft.util.math.BlockPos(2,1,0));
        var b=c.spawnEntity(EntityType.ZOMBIE,new net.minecraft.util.math.BlockPos(-2,1,0));
        var far=c.spawnEntity(EntityType.ZOMBIE,new net.minecraft.util.math.BlockPos(5,1,0));
        var friend=c.spawnEntity(EntityType.COW,new net.minecraft.util.math.BlockPos(0,1,2));
        p.setAir(100);charge.onUse();charge.onUse();
        c.assertTrue(glow.isActive() && glow.doesApply(a) && glow.doesApply(b),"Hold previews enemies on both sides");
        c.assertTrue(!glow.doesApply(far) && !glow.doesApply(friend),"Preview excludes distant and friendly targets");
        c.assertTrue(a.getHealth()==20 && p.getAir()==100,"Holding neither attacks nor refunds");
        charge.fire(false);
        c.assertTrue(a.getHealth()<20 && b.getHealth()<20 && far.getHealth()==20 && friend.getHealth()==10,"Release matches preview targets");
        c.assertTrue(a.getVelocity().x>0 && b.getVelocity().x<0 && a.getVelocity().y>0,"Launches outward and upward");
        c.assertTrue(p.getAir()==160 && charge.nowCooldown==600 && !glow.isActive(),"Thirty moisture per hit and thirty second cooldown");
        charge.onUse();c.assertTrue(!charge.isCharging(),"Cooldown blocks restart");
        var jumpType=PowerTypeRegistry.get(id("perks/axolotl_propulsion_efficiency"));holder.addPower(jumpType,SOURCE);
        var jump=(ActionOnJumpPower)holder.getPower(jumpType);
        c.setBlockState(new net.minecraft.util.math.BlockPos(0,0,0),net.minecraft.block.Blocks.STONE);
        p.setOnGround(true);p.setSprinting(true);p.setVelocity(0,0,0);jump.executeAction();
        c.assertTrue(p.getAir()==159 && p.getVelocity().z>.29,"Land sprint jump costs one moisture and pushes forward");
        p.setSprinting(false);jump.executeAction();c.assertTrue(p.getAir()==159,"Ordinary jump does not spend moisture");
        var perk=(NormalPerk)RegPerks.getPerk(id("axolotl_propulsion_efficiency"));
        c.assertTrue(perk.powerRemove.contains(id("form_axolotl_3_sprinting_jump")) && !perk.powerRemove.contains(id("form_axolotl_2_water_spurt")),"Replaces land propulsion only");
        a.discard();b.discard();far.discard();friend.discard();c.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void independentSubformPerkTree(TestContext c) {
        var parent = new net.onixary.shapeShifterCurseFabric.player_form.NormalForm(id("test_parent"))
                .perkTree(id("parent_tree"));
        var child = new net.onixary.shapeShifterCurseFabric.player_form.NormalSubForm(id("test_child"), parent);
        c.assertTrue(child.getPerkTreeID().equals(RegPerks.EMPTY_PERK_TREE), "Unconfigured subform does not inherit parent tree");
        child.perkTree(id("child_tree"));
        c.assertTrue(child.getPerkTreeID().equals(id("child_tree")) && parent.getPerkTreeID().equals(id("parent_tree")), "Subform owns its configured tree");

        var player = c.createMockCreativeServerPlayerInWorld();
        var tree = RegPlayerForms.SNOW_FOX_3.getPerkTreeID();
        net.onixary.shapeShifterCurseFabric.perk.PerkUtils.__addPerk(player, tree, id("snow_fox_revenge"));
        net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils.setForm(player, RegPlayerForms.SNOW_FOX_3);
        var power = PowerTypeRegistry.get(id("perks/snow_fox_revenge_melee"));
        c.assertTrue(PowerHolderComponent.KEY.get(player).hasPower(power), "Master perk applies on master form");
        net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils.setForm(player, RegPlayerForms.SNOW_FOX_3_SUB_MARBLED_POLECAT);
        c.assertTrue(net.onixary.shapeShifterCurseFabric.perk.PerkUtils.getPlayerNowPerkTreeID(player).equals(id("marbled_polecat_perk_tree")), "Subform selects its own tree");
        c.assertTrue(!PowerHolderComponent.KEY.get(player).hasPower(power), "Master perk power is removed on subform transition");
        net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils.setForm(player, RegPlayerForms.SNOW_FOX_3);
        c.assertTrue(PowerHolderComponent.KEY.get(player).hasPower(power), "Master unlocks are preserved when returning");
        player.discard(); c.complete();
    }
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
        context.assertTrue(Math.abs(near.getVelocity().z + 1.5) < 0.001 && Math.abs(near.getVelocity().y - 0.9) < 0.001, "Apoli release uses configured pull and upward velocity");
        context.assertTrue(near.hasStatusEffect(StatusEffects.SLOW_FALLING) && near.getStatusEffect(StatusEffects.SLOW_FALLING).getDuration() == 100, "Pulled target receives five seconds of slow falling");
        context.assertTrue(!far.hasStatusEffect(StatusEffects.SLOW_FALLING) && !player.hasStatusEffect(StatusEffects.SLOW_FALLING), "Slow falling only applies to the selected target");
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
        context.assertTrue(RegPlayerForms.BAT_3_SUB_AVALI.getPerkTreeID().equals(id("avali_perk_tree")), "Avali has its independent tree");
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
