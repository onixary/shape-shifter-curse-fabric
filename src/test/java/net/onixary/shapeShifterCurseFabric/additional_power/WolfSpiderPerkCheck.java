package net.onixary.shapeShifterCurseFabric.additional_power;

import com.google.gson.JsonParser;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.entity.RegCustomEntity;
import net.onixary.shapeShifterCurseFabric.entity.projectile.WebBullet;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity;
import net.onixary.shapeShifterCurseFabric.perk.NormalPerk;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;

public class WolfSpiderPerkCheck {
    private static Identifier id(String name) { return new Identifier("shape-shifter-curse", name); }
    private static ServerPlayerEntity player(TestContext c) {
        var p = new ServerPlayerEntity(c.getWorld().getServer(), c.getWorld(),
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "perk-test-player"));
        c.getWorld().getServer().getPlayerManager().onPlayerConnect(
                new net.minecraft.network.ClientConnection(net.minecraft.network.NetworkSide.SERVERBOUND), p);
        p.changeGameMode(net.minecraft.world.GameMode.CREATIVE);
        for (int i = 0; i < 61; i++) p.tick();
        p.getAbilities().invulnerable = false; p.getAbilities().flying = false; p.setInvulnerable(false);
        p.setPosition(c.getAbsolute(new Vec3d(2.5, 2, 2.5)));
        return p;
    }
    private static Power add(ServerPlayerEntity p, String name) {
        var type = PowerTypeRegistry.get(id("perks/"+name));
        PowerHolderComponent.KEY.get(p).addPower(type, id("wolf_spider_test"));
        return PowerHolderComponent.KEY.get(p).getPower(type);
    }
    private static void action(ServerPlayerEntity player, String json) {
        ApoliDataTypes.ENTITY_ACTION.read(JsonParser.parseString(json)).accept(player);
    }
    private static void webMana(ServerPlayerEntity p) {
        ManaUtils.gainManaTypeID(p, id("web_resource"), id("wolf_spider_test"));
        ManaUtils.setPlayerMana(p, 50);
    }
    private static void clearSpace(TestContext c) {
        for (int x=0;x<8;x++) for (int z=0;z<8;z++) for (int y=0;y<9;y++)
            c.setBlockState(new BlockPos(x,y,z), y == 0 ? Blocks.STONE : Blocks.AIR);
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void registeredTreesAndPowers(TestContext c) {
        String[] names = {"anubis_wolf_pack_response_1", "anubis_wolf_soul_excitation", "anubis_wolf_pack_response_2", "anubis_wolf_pack_amplification", "anubis_wolf_wither_tolerance_1", "anubis_wolf_wither_tolerance_2", "anubis_wolf_soul_recall", "anubis_wolf_undead_discernment", "spider_four_legged_adaptation", "spider_cocoon_digestion_1", "spider_cocoon_digestion_2", "spider_bridge_weaver", "spider_perceptual_web", "spider_rapid_spinner", "spider_stabilized_projectile", "spider_silk_secretion_1", "spider_silk_secretion_2", "spider_silk_grapple"};
        for (String name : names) {
            var perk = (NormalPerk) RegPerks.getPerk(id(name));
            c.assertTrue(perk != null, "Registered perk: " + name);
            for (var power : perk.powerAdd) c.assertTrue(PowerTypeRegistry.contains(power), "Loaded power: " + power);
        }
        c.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void wolfSummonsAndRecall(TestContext c) {
        var p=player(c); var owner=(IPlayerEntityMinion)p;
        add(p,"anubis_wolf_pack_response_1"); add(p,"anubis_wolf_pack_amplification");
        action(p,"{\"type\":\"shape-shifter-curse:summon_anubis_wolf_minion\",\"count\":2,\"max_minion_count\":2,\"cooldown\":420,\"minion_level\":3}");
        c.assertTrue(owner.shape_shifter_curse$getMinionsCount(AnubisWolfMinionEntity.MinionID)==2,"Normal cap");
        long normalCooldown=owner.shape_shifter_curse$getCooldownTime(AnubisWolfMinionEntity.MinionID);
        var extra=(ActiveCooldownPower)add(p,"anubis_wolf_soul_excitation"); extra.fromTag(NbtLong.of(-10000)); extra.onUse();
        c.assertTrue(owner.shape_shifter_curse$getMinionsCount(AnubisWolfMinionEntity.MinionID)==3,"Extra summon bypasses cap but occupies slot");
        c.assertTrue(normalCooldown==owner.shape_shifter_curse$getCooldownTime(AnubisWolfMinionEntity.MinionID),"Extra summon preserves normal cooldown");
        c.assertTrue(p.getStatusEffect(StatusEffects.WITHER).getDuration()==200,"Self wither lasts 10 seconds");
        for (var uuid : owner.shape_shifter_curse$getMinionsByMinionID(AnubisWolfMinionEntity.MinionID)) {
            var wolf=(AnubisWolfMinionEntity)c.getWorld().getEntity(uuid);
            c.assertTrue(wolf.getStatusEffect(StatusEffects.STRENGTH).getDuration()==600,"Both summon paths gain Strength I");
        }
        p.setHealth(10);
        var recall=(ActiveCooldownPower)add(p,"anubis_wolf_soul_recall");recall.fromTag(NbtLong.of(-10000));recall.onUse();
        c.assertTrue(!p.hasStatusEffect(StatusEffects.WITHER) && p.getHealth()==16,"Recall clears wither and heals 2 per death: "+p.getHealth());
        c.assertTrue(owner.shape_shifter_curse$getMinionsCount(AnubisWolfMinionEntity.MinionID)==0,"Recall frees all occupied slots");
        p.age+=421;
        action(p,"{\"type\":\"shape-shifter-curse:summon_anubis_wolf_minion\",\"count\":3,\"max_minion_count\":2,\"cooldown\":420}");
        c.assertTrue(owner.shape_shifter_curse$getCooldownTime(AnubisWolfMinionEntity.MinionID)==p.age,"Partial summon still starts cooldown");
        action(p,"{\"type\":\"shape-shifter-curse:recall_wolf_minions\"}");p.discard();c.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void witherIntervals(TestContext c) {
        var p=player(c);add(p,"anubis_wolf_wither_tolerance_1");
        var effect=new StatusEffectInstance(StatusEffects.WITHER,120,0);
        effect.update(p,()->{});c.assertTrue(p.getHealth()==19,"Tier I damage every 60 ticks");
        p.timeUntilRegen=0; p.setHealth(20);
        new StatusEffectInstance(StatusEffects.WITHER,80,0).update(p,()->{});
        c.assertTrue(p.getHealth()==20,"Original 40-tick cadence suppressed");
        add(p,"anubis_wolf_wither_tolerance_2");
        new StatusEffectInstance(StatusEffects.WITHER,80,0).update(p,()->{});
        c.assertTrue(p.getHealth()==19,"Tier II uses 80 ticks and does not stack with I");
        p.timeUntilRegen=0;
        new StatusEffectInstance(StatusEffects.WITHER,40,1).update(p,()->{});
        c.assertTrue(p.getHealth()==18,"Wither II uses 40 ticks");
        var zombie=c.spawnEntity(EntityType.ZOMBIE,new BlockPos(4,1,2));
        new StatusEffectInstance(StatusEffects.WITHER,40,0).update(zombie,()->{});
        c.assertTrue(zombie.getHealth()<20,"Other entities keep vanilla cadence");
        p.discard();zombie.discard();c.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void craftingAndThrowable(TestContext c) {
        var p=player(c);webMana(p);var craft=(ItemOnItemPower)add(p,"spider_stabilized_projectile");
        var using=new ItemStack(Items.COBWEB,1);p.getInventory().setStack(0,new ItemStack(Items.COBWEB,1));
        var slot=new Slot(p.getInventory(),0,0,0);
        c.assertTrue(craft.isActive() && craft.doesApply(using,slot.getStack()),"Cobweb combination enabled");
        craft.execute(using,slot.getStack(),slot);
        c.assertTrue(using.isEmpty() && slot.getStack().isOf(RegCustomItem.WEB_PROJECTILE) && slot.getStack().getCount()==1,"Exactly two cobwebs produce one projectile");
        c.assertTrue(ManaUtils.getPlayerMana(p)==40,"Craft costs 10 web resource");
        ManaUtils.setPlayerMana(p,9);c.assertTrue(!craft.isActive(),"Insufficient mana disables crafting");
        PowerHolderComponent.KEY.get(p).removePower(craft.getType(),id("wolf_spider_test"));
        p.changeGameMode(net.minecraft.world.GameMode.SURVIVAL); p.setStackInHand(Hand.MAIN_HAND,new ItemStack(RegCustomItem.WEB_PROJECTILE));
        RegCustomItem.WEB_PROJECTILE.use(c.getWorld(),p,Hand.MAIN_HAND);
        var bullet=c.getWorld().getEntitiesByType(RegCustomEntity.WEB_BULLET,p.getBoundingBox().expand(4),e->e.getOwner()==p).get(0);
        c.assertTrue(bullet.Tier==3 && !bullet.EnableVenomSpindle && p.getMainHandStack().isEmpty(),"Non-perk player can fire and consumes the item");
        var tag=new NbtCompound();bullet.writeNbt(tag);
        var restored=new WebBullet(RegCustomEntity.WEB_BULLET,c.getWorld());restored.readNbt(tag);
        c.assertTrue(restored.Tier==3 && !restored.EnableVenomSpindle,"Projectile configuration survives reload");
        var target=c.spawnEntity(EntityType.ZOMBIE,new BlockPos(4,1,2));
        restored.onEntityHit(new EntityHitResult(target));
        c.assertTrue(target.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier()==3 && target.getStatusEffect(StatusEffects.WEAKNESS).getDuration()==160,"Fully charged projectile effects survive reload");
        bullet.discard();target.discard();p.discard();c.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void spiderPassiveAndChargeValues(TestContext c) {
        clearSpace(c);var p=player(c);webMana(p);
        c.assertTrue(CocoonLootChancePower.getChance(p)==40,"Base cocoon chance");
        add(p,"spider_cocoon_digestion_1");c.assertTrue(CocoonLootChancePower.getChance(p)==55,"First upgrade chance");
        add(p,"spider_cocoon_digestion_2");c.assertTrue(CocoonLootChancePower.getChance(p)==65,"Second upgrade replaces chance");
        for (String name : new String[]{"spider_bridge_weaver","spider_rapid_spinner"}) {
            var charge=(ChargePower)add(p,name);
            for(int i=1;i<=3;i++) c.assertTrue(charge.ChargeTierList.get(i).chargeTime==14*i,"Charge thresholds");
            ManaUtils.setPlayerMana(p,50);
            for(int i=0;i<42;i++){charge.onUse();charge.tick();}
            c.assertTrue(charge.nowTier==3 && Math.abs(ManaUtils.getPlayerMana(p)-35)<1,"Full charge still costs approximately 15");
            charge.onRemoved();
        }
        var glow=(EntityGlowPower)add(p,"spider_perceptual_web");
        c.assertTrue(!glow.isActive(),"No outline off web bridge");
        c.setBlockState(new BlockPos(2,2,2),RegCustomBlock.TEMP_WEB_BRIDGE);
        c.assertTrue(glow.isActive(),"Standing inside bridge enables outline");
        var sheep=c.spawnEntity(EntityType.SHEEP,new BlockPos(4,2,2));
        c.assertTrue(glow.doesApply(sheep) && !glow.usesTeams() && glow.getRed()==1,"Passive animals receive white outline too");
        sheep.setPosition(p.getPos().add(17,0,0));c.assertTrue(!glow.doesApply(sheep),"Outline range is 16 blocks");
        sheep.discard();p.discard();c.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void grappleRefundAndFlight(TestContext c) {
        clearSpace(c);var p=player(c);webMana(p);p.setYaw(0);p.setPitch(0);
        var grapple=(SurfaceGrapplePower)add(p,"spider_silk_grapple");
        p.setPitch(-90);grapple.onUse();grapple.onUse();
        c.assertTrue(ManaUtils.getPlayerMana(p)==40,"Hold reserves cost exactly once");
        grapple.release();c.assertTrue(ManaUtils.getPlayerMana(p)==50,"No surface refunds full reservation");
        p.setPitch(0);
        for(int x=1;x<=4;x++)for(int y=1;y<=5;y++)c.setBlockState(new BlockPos(x,y,6),Blocks.STONE);
        var target=grapple.findDestination();
        c.assertTrue(target!=null && Math.abs(target.z-c.getAbsolute(new Vec3d(0,0,5)).z)<0.01,"Destination is one block outside hit face");
        grapple.onUse();grapple.release();
        for(int i=0;i<12;i++){grapple.tick();p.move(MovementType.SELF,p.getVelocity());}
        c.assertTrue(p.getPos().distanceTo(target)<0.21 && !p.hasNoGravity(),"Flight reaches target and restores gravity");
        grapple.onUse();c.assertTrue(ManaUtils.getPlayerMana(p)==40,"Successful cast starts cooldown");
        p.discard();c.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void grappleCancellationAndObstacle(TestContext c) {
        clearSpace(c);var p=player(c);webMana(p);p.setYaw(0);p.setPitch(0);
        var grapple=(SurfaceGrapplePower)add(p,"spider_silk_grapple");
        grapple.onUse();grapple.onRemoved();
        c.assertTrue(ManaUtils.getPlayerMana(p)==50,"Removing held power refunds reservation");
        for(int y=1;y<6;y++)c.setBlockState(new BlockPos(2,y,6),Blocks.STONE);
        grapple.onUse();grapple.release();
        for(int y=1;y<6;y++)c.setBlockState(new BlockPos(2,y,3),Blocks.STONE);
        Vec3d before=p.getPos();grapple.tick();p.move(MovementType.SELF,p.getVelocity());
        c.assertTrue(p.getPos().equals(before) && !p.hasNoGravity(),"New obstruction stops flight without phasing");
        p.discard();c.complete();
    }
}
