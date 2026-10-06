package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.Active;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.items.FamiliarFoxContent;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.perk.NormalPerk;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

public class FamiliarFoxPerkCheck {
    private static Identifier id(String path) { return new Identifier("shape-shifter-curse", path); }
    private static ServerPlayerEntity player(TestContext context) {
        var player = context.createMockCreativeServerPlayerInWorld();
        for (int i = 0; i < 61; i++) player.tick();
        player.getAbilities().invulnerable = false;
        player.setInvulnerable(false);
        player.setPosition(context.getAbsolute(new Vec3d(0.5, 1, 0.5)));
        ManaUtils.gainManaTypeID(player, id("familiar_fox_mana"), id("fox_test"));
        ManaUtils.setPlayerMana(player, 50);
        return player;
    }
    private static io.github.apace100.apoli.power.Power add(ServerPlayerEntity player, String perk) {
        var type = PowerTypeRegistry.get(id("perks/familiar_fox_" + perk));
        var holder = PowerHolderComponent.KEY.get(player);
        holder.addPower(type, id("fox_test"));
        return holder.getPower(type);
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void registrations(TestContext context) {
        var tree = RegPerks.getPerkTree(id("familiar_fox_3_perk_tree"));
        context.assertTrue(tree.getAllNodes().size() == 11, "Eleven fox perks");
        context.assertTrue(RegPlayerForms.FAMILIAR_FOX_3.getPerkTreeID().equals(tree.getID()), "Fox tree binding");
        for (var node : tree.getAllNodes()) {
            var perk = (NormalPerk) RegPerks.getPerk(node.perkID);
            for (var power : perk.powerAdd) context.assertTrue(PowerTypeRegistry.get(power) != null, "Power parsed: " + power);
        }
        context.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void capacityAndProjectileSiphon(TestContext context) {
        var player = player(context);
        var capacity = add(player, "mana_capacity_1");
        context.assertTrue(ManaUtils.getPlayerMaxMana(player) == 125, "Capacity I");
        PowerHolderComponent.KEY.get(player).removePower(capacity.getType(), id("fox_test"));
        add(player, "mana_capacity_2");
        context.assertTrue(ManaUtils.getPlayerMaxMana(player) == 150, "Capacity II replaces I");
        add(player, "siphon_2");
        var zombie = context.spawnEntity(EntityType.ZOMBIE, new BlockPos(0, 1, 3));
        var arrow = new ArrowEntity(context.getWorld(), player);
        zombie.damage(player.getDamageSources().arrow(arrow, player), 3);
        context.assertTrue(ManaUtils.getPlayerMana(player) == 54, "Projectile hits restore mana");
        zombie.damage(player.getDamageSources().arrow(arrow, player), 3);
        context.assertTrue(ManaUtils.getPlayerMana(player) == 54, "Rejected damage cannot farm mana");
        player.discard();
        context.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void deflectionPayment(TestContext context) {
        var player = player(context);
        add(player, "deflection_reduction"); add(player, "deflection_payment");
        var skeleton = context.spawnEntity(EntityType.SKELETON, new BlockPos(0, 1, 3));
        var arrow = new ArrowEntity(context.getWorld(), skeleton);
        var source = player.getDamageSources().arrow(arrow, skeleton);
        player.damage(source, 8);
        context.assertTrue(player.getHealth() == 16, "Projectile damage halved: health=" + player.getHealth() + ", mana=" + ManaUtils.getPlayerMana(player));
        context.assertTrue(ManaUtils.getPlayerMana(player) == 45, "Successful hit costs five");
        player.damage(source, 8);
        context.assertTrue(ManaUtils.getPlayerMana(player) == 45, "Invulnerability frame does not cost mana");
        player.timeUntilRegen = 0; ManaUtils.setPlayerMana(player, 5);
        player.damage(source, 8);
        context.assertTrue(player.getHealth() == 8 && ManaUtils.getPlayerMana(player) == 5, "Exactly five mana does not activate protection");
        player.discard();
        context.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void shieldRetaliation(TestContext context) {
        var player = player(context);
        add(player, "return_shield");
        player.setYaw(0); player.setPitch(0); player.setSneaking(true);
        player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, new ItemStack(Items.SHIELD));
        player.setCurrentHand(net.minecraft.util.Hand.MAIN_HAND);
        for (int i = 0; i < 6; i++) player.playerTick();
        context.assertTrue(player.isBlocking(), "Shield raised");
        var skeleton = context.spawnEntity(EntityType.SKELETON, new BlockPos(0, 1, 3));
        var arrow = new ArrowEntity(context.getWorld(), skeleton); arrow.setPosition(skeleton.getPos());
        float before = player.getHealth();
        player.damage(player.getDamageSources().arrow(arrow, skeleton), 6);
        context.assertTrue(player.getHealth() == before, "Shield blocks damage");
        context.assertTrue(ManaUtils.getPlayerMana(player) == 45, "Blocked projectile costs five mana");
        var fireballs = context.getWorld().getEntitiesByType(EntityType.SMALL_FIREBALL, player.getBoundingBox().expand(3), e -> e.getOwner() == player);
        context.assertTrue(fireballs.size() == 1 && fireballs.get(0).powerZ > 0, "One fireball aimed at attacker");
        player.discard();
        context.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void surfaceRingRewards(TestContext context) {
        var player = player(context);
        player.setYaw(0); player.setPitch(0);
        player.getHungerManager().setFoodLevel(8); player.getHungerManager().setSaturationLevel(0);
        add(player, "siphon_2");
        var charge = (ChargePower) add(player, "siphoning_ring");
        context.setBlockState(new BlockPos(0, 2, 5), net.minecraft.block.Blocks.STONE);
        var target = context.spawnEntity(EntityType.ZOMBIE, new BlockPos(0, 1, 4));
        charge.onUse(); charge.onUse();
        context.assertTrue(ManaUtils.getPlayerMana(player) == 30, "Holding costs twenty once");
        charge.fire(false);
        context.assertTrue(target.getHealth() < 13, "Surface ring damages enemy");
        context.assertTrue(ManaUtils.getPlayerMana(player) == 34, "Ring also triggers siphon");
        context.assertTrue(player.getHungerManager().getFoodLevel() == 14, "Hit restores six food");
        context.assertTrue(Math.abs(player.getHungerManager().getSaturationLevel() - 4.8f) < 0.001f, "Hit restores 4.8 saturation");
        charge.onUse();
        context.assertTrue(ManaUtils.getPlayerMana(player) == 34, "Cooldown prevents new charge");
        player.discard();
        context.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void potionCharmCraftingAndUse(TestContext context) {
        var player = player(context);
        var craft = (io.github.apace100.apoli.power.ItemOnItemPower) add(player, "potion_charms_healing");
        var paper = new ItemStack(Items.PAPER, 2);
        var ingredient = new ItemStack(Items.GLISTERING_MELON_SLICE, 2);
        player.getInventory().setStack(0, ingredient);
        context.assertTrue(craft.doesApply(paper, ingredient), "Paper accepts healing ingredient");
        var result = craft.execute(paper, ingredient, new net.minecraft.screen.slot.Slot(player.getInventory(), 0, 0, 0));
        context.assertTrue(player.getInventory().count(FamiliarFoxContent.HEALING_CHARM) == 1 && paper.getCount() == 1 && ingredient.getCount() == 1, "Craft consumes one of each ingredient");
        context.assertTrue(ManaUtils.getPlayerMana(player) == 45, "Craft costs five mana");
        result = new ItemStack(FamiliarFoxContent.HEALING_CHARM);
        player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, result);
        context.assertTrue(!result.use(context.getWorld(), player, net.minecraft.util.Hand.MAIN_HAND).getResult().isAccepted(), "Other forms cannot use charm");
        FormUtils.setForm(player, RegPlayerForms.FAMILIAR_FOX_3);
        result.use(context.getWorld(), player, net.minecraft.util.Hand.MAIN_HAND);
        var arrows = context.getWorld().getEntitiesByType(FamiliarFoxContent.POTION_CHARM_ARROW, player.getBoundingBox().expand(3), e -> e.getOwner() == player);
        context.assertTrue(arrows.size() == 1, "Fox launches one charm arrow");
        context.assertTrue(arrows.get(0).pickupType == net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission.DISALLOWED, "Charm cannot be recovered");
        var nbt = new net.minecraft.nbt.NbtCompound(); arrows.get(0).writeCustomDataToNbt(nbt);
        context.assertTrue(nbt.getString("Potion").equals("minecraft:strong_healing"), "Strong potion payload retained");
        player.discard();
        context.complete();
    }
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void reservoirLifetime(TestContext context) {
        var player = player(context);
        FormUtils.setForm(player, RegPlayerForms.FAMILIAR_FOX_3);
        ManaUtils.setPlayerMana(player, 50);
        player.setYaw(0); player.setPitch(0); player.getHungerManager().setFoodLevel(10);
        var summon = add(player, "reservoir"); summon.fromTag(NbtLong.of(-1000));
        ((Active) summon).onUse();
        var entities = context.getWorld().getEntitiesByType(FamiliarFoxContent.MANA_RESERVOIR, player.getBoundingBox().expand(4), e -> true);
        context.assertTrue(entities.size() == 1, "One reservoir spawned");
        context.assertTrue(player.getHungerManager().getFoodLevel() == 4, "Summon costs six food");
        var reservoir = entities.get(0);
        reservoir.age = 20; reservoir.tick();
        context.assertTrue(reservoir.getHealth() == 8 && ManaUtils.getPlayerMana(player) == 55, "Pulse gives mana and loses health: age=" + reservoir.age + ", hp=" + reservoir.getHealth() + ", mana=" + ManaUtils.getPlayerMana(player));
        player.setPosition(player.getPos().add(10, 0, 0));
        for (int i = 0; i < 4; i++) { reservoir.age = 20; reservoir.tick(); }
        context.assertTrue(!reservoir.isAlive(), "Empty pulses still expire after five ticks of service");
        context.assertTrue(ManaUtils.getPlayerMana(player) == 55, "Out of range players receive no mana");
        player.discard();
        context.complete();
    }
}
