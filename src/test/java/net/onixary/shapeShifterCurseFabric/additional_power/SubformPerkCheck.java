package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.*;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.screen.slot.Slot;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.perk.*;

public class SubformPerkCheck {
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void directedParticleOffsets(TestContext c) {
        var actor=c.createMockSurvivalPlayer();
        var target=net.minecraft.entity.EntityType.COW.create(c.getWorld());
        actor.setPosition(0,1,0);target.setPosition(3,2,4);
        var factory=DirectedParticlesAction.getFactory();
        var json=com.google.gson.JsonParser.parseString("""
                {"particle":"minecraft:flame", "speed":0.5, "count":3,
                 "actor_offset":{"x":1,"y":2,"z":3},
                 "target_offset":{"x":-1,"y":1,"z":0}}
                """).getAsJsonObject();
        var a=actor.getBoundingBox().getCenter().add(1,2,3);
        var b=target.getBoundingBox().getCenter().add(-1,1,0);
        var forward=DirectedParticlesAction.packet(factory.getSerializableData().read(json),actor,target);
        var velocity=new net.minecraft.util.math.Vec3d(forward.getOffsetX(),forward.getOffsetY(),forward.getOffsetZ()).multiply(forward.getSpeed());
        c.assertTrue(new net.minecraft.util.math.Vec3d(forward.getX(),forward.getY(),forward.getZ()).distanceTo(a)<1e-6,"Actor offset is relative to its body center");
        c.assertTrue(velocity.distanceTo(b.subtract(a).normalize().multiply(.5))<1e-6 && forward.getCount()==0,"Packet uses directed velocity mode, not random spread");
        json.addProperty("direction","target_to_actor");
        var reverse=DirectedParticlesAction.packet(factory.getSerializableData().read(json),actor,target);
        c.assertTrue(new net.minecraft.util.math.Vec3d(reverse.getX(),reverse.getY(),reverse.getZ()).distanceTo(b)<1e-6,"Reverse emission starts at target offset");
        c.assertTrue(Math.abs(reverse.getOffsetX()+forward.getOffsetX())<1e-6 && Math.abs(reverse.getOffsetY()+forward.getOffsetY())<1e-6 && Math.abs(reverse.getOffsetZ()+forward.getOffsetZ())<1e-6,"Reversing retains actor/target offset ownership");
        json.addProperty("type","shape-shifter-curse:directed_particles");
        io.github.apace100.apoli.data.ApoliDataTypes.BIENTITY_ACTION.read(json).accept(new net.minecraft.util.Pair<>(actor,target));
        json.remove("actor_offset");json.remove("target_offset");
        var zero=DirectedParticlesAction.packet(factory.getSerializableData().read(json),actor,actor);
        c.assertTrue(zero.getOffsetX()==0 && zero.getOffsetY()==0 && zero.getOffsetZ()==0,"Coincident endpoints have zero velocity");
        json.addProperty("direction","invalid");boolean rejected=false;
        try { factory.read(json); } catch (RuntimeException expected) { rejected=true; }
        c.assertTrue(rejected,"Unknown direction rejected during parsing");c.complete();
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void respawnRestoresUnlockedPerks(TestContext c) {
        var p=c.createMockCreativeServerPlayerInWorld();
        var form=net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms.BAT_3_SUB_AVALI;
        net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils.setForm(p,form);
        PerkUtils.__addPerk(p,form.getPerkTreeID(),id("avali_environmental_protection_1"));
        PerkUtils.__addPerk(p,form.getPerkTreeID(),id("avali_nano_coating_1"));
        var upgraded=PowerTypeRegistry.get(id("perks/avali_environmental_protection_1"));
        var base=PowerTypeRegistry.get(id("sub_form_avali_water_slowness"));
        c.assertTrue(PowerHolderComponent.KEY.get(p).hasPower(upgraded),"Perk applies before death");
        p.setHealth(0);
        var respawned=c.getWorld().getServer().getPlayerManager().respawnPlayer(p,false);
        try {
            c.assertTrue(PerkUtils.getPlayerPerks(respawned,form.getPerkTreeID()).contains(id("avali_environmental_protection_1")),"Unlock record survives real respawn");
            c.assertTrue(PowerHolderComponent.KEY.get(respawned).hasPower(upgraded),"Unlocked power restored after all respawn component copies");
            c.assertTrue(!PowerHolderComponent.KEY.get(respawned).hasPower(base),"Replaced base power remains removed after respawn");
            c.assertTrue(PowerHolderComponent.KEY.get(respawned).hasPower(PowerTypeRegistry.get(id("perks/avali_nano_coating_1"))),"Additional perk restored after respawn");
        } finally { respawned.discard(); }
        c.complete();
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void perkCommands(TestContext c) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        var p=c.createMockCreativeServerPlayerInWorld();
        var forms=net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent.COMPONENT.get(p);
        var avali=net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms.BAT_3_SUB_AVALI;
        net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils.setForm(p,avali);
        var dispatcher=new com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource>();
        net.onixary.shapeShifterCurseFabric.command.ShapeShifterCurseCommand.register(dispatcher);
        var source=p.getCommandSource().withLevel(2);
        String prefix="shape_shifter_curse perk ";
        c.assertTrue(!dispatcher.getRoot().getChild("shape_shifter_curse").getChild("perk").canUse(source.withLevel(0)),"Non-OP cannot use debug commands");
        c.assertTrue(dispatcher.execute(prefix+"list",source)==7,"Lists current form tree");
        dispatcher.execute(prefix+"toggle_free true",source);
        p.getAbilities().creativeMode=false;
        c.assertTrue(PerkUtils.isFreeUnlock(p),"Free unlock independent of creative mode");
        var nbt=new NbtCompound();forms.writeToNbt(nbt);forms.freePerkForms.clear();forms.readFromNbt(nbt);
        c.assertTrue(PerkUtils.isFreeUnlock(p),"Free setting survives NBT sync and reload");
        forms.nowPerkTree=id("snow_fox_3_perk_tree");
        c.assertTrue(dispatcher.execute(prefix+"unlock_all",source)==7,"Uses actual form rather than preview tree");
        c.assertTrue(dispatcher.execute(prefix+"unlock_all",source)==0,"Repeated unlock is idempotent");
        var holder=PowerHolderComponent.KEY.get(p);
        var base=PowerTypeRegistry.get(id("sub_form_avali_water_slowness"));
        var upgraded=PowerTypeRegistry.get(id("perks/avali_environmental_protection_1"));
        c.assertTrue(!holder.hasPower(base) && holder.hasPower(upgraded),"Unlock applies replacement");
        var other=id("snow_fox_3_perk_tree");forms.formPerkMap.put(other,new java.util.ArrayList<>(java.util.List.of(id("snow_fox_revenge"))));
        c.assertTrue(dispatcher.execute(prefix+"reset_all",source)==7,"Reset all current nodes");
        c.assertTrue(holder.hasPower(base) && !holder.hasPower(upgraded),"Reset restores base powers");
        c.assertTrue(forms.formPerkMap.get(other).size()==1,"Other tree unlocks preserved");
        dispatcher.execute(prefix+"toggle_free false",source);c.assertTrue(!PerkUtils.isFreeUnlock(p),"Free mode turns off");
        p.discard();c.complete();
    }
    private static Identifier id(String s) { return new Identifier("shape-shifter-curse",s); }
    private static Power add(net.minecraft.entity.player.PlayerEntity p,String s) {
        var type=PowerTypeRegistry.get(id("perks/"+s));
        var holder=PowerHolderComponent.KEY.get(p);holder.addPower(type,id("subform_test"));return holder.getPower(type);
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void registrations(TestContext c) {
        int count=0;
        for(String tree:new String[]{"marbled_polecat","avali"}) for(var node:RegPerks.getPerkTree(id(tree+"_perk_tree")).getAllNodes()) {
            count++;var perk=(NormalPerk)RegPerks.getPerk(node.perkID);
            for(var power:perk.powerAdd)c.assertTrue(PowerTypeRegistry.get(power)!=null,"Loaded "+power);
            for(var power:perk.powerRemove)c.assertTrue(PowerTypeRegistry.get(power)!=null,"Replacement "+power);
        }
        c.assertTrue(count==13,"Thirteen subform perks");c.complete();
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void jumpsDashAndCombat(TestContext c) {
        var p=c.createMockSurvivalPlayer();var jump=(AirJumpPower)add(p,"marbled_polecat_double_air_jump");
        p.setOnGround(false);jump.tick();jump.tick();jump.onUse();jump.onUse();
        c.assertTrue(((NbtInt)jump.toTag()).intValue()==2,"Two extra jumps");
        p.setVelocity(0,-1,0);jump.onUse();c.assertTrue(p.getVelocity().y==-1,"Third extra jump blocked");
        p.setOnGround(true);jump.tick();c.assertTrue(((NbtInt)jump.toTag()).intValue()==0,"Landing resets jumps");
        jump.fromTag(NbtByte.ONE);c.assertTrue(((NbtInt)jump.toTag()).intValue()==1,"Legacy jump NBT");
        c.setBlockState(new net.minecraft.util.math.BlockPos(0,0,0),net.minecraft.block.Blocks.STONE);
        p.setPosition(c.getAbsolute(new net.minecraft.util.math.Vec3d(.5,1,.5)));p.setYaw(0);p.setPitch(80);
        var dash=(ActiveCooldownPower)add(p,"marbled_polecat_leaping_dash");dash.fromTag(NbtLong.of(-10000));
        p.getHungerManager().setFoodLevel(6);dash.onUse();c.assertTrue(p.getVelocity().y==-1,"Six food cannot dash");
        p.getHungerManager().setFoodLevel(7);dash.onUse();
        c.assertTrue(p.getHungerManager().getFoodLevel()==7 && p.getVelocity().z>1.19 && p.getVelocity().y>.59,"Dash horizontal impulse without food cost");
        add(p,"marbled_polecat_combat_noodle_melee");var target=net.minecraft.entity.EntityType.ZOMBIE.create(c.getWorld());
        c.assertTrue(ActionOnCombatHitPower.meleeBonus(p,target,false)==4 && ActionOnCombatHitPower.meleeBonus(p,target,true)==0,"Only noncritical melee gets bonus");c.complete();
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void recipesAndCoatings(TestContext c) {
        var p=c.createMockSurvivalPlayer();
        for(int tier=1;tier<=2;tier++) {
            var craft=(ItemOnItemPower)add(p,"avali_tool_modification_"+tier);
            var using=new ItemStack(tier==1?Items.COAL_BLOCK:Items.DIAMOND,2);
            var old=new ItemStack(tier==1?Items.GOLDEN_SWORD:Items.BOW);old.setDamage(10);
            old.addEnchantment(net.minecraft.enchantment.Enchantments.UNBREAKING,3);
            p.getInventory().setStack(0,old);var slot=new Slot(p.getInventory(),0,0,0);
            c.assertTrue(craft.doesApply(using,old),"Recipe input");craft.execute(using,old,slot);
            c.assertTrue(using.getCount()==1 && slot.getStack().isOf(tier==1?RegCustomItem.GRAPHENE_BLADE:RegCustomItem.COMPOUND_KINETIC_BOW) && !slot.getStack().hasEnchantments() && slot.getStack().getDamage()==0,"Fresh output consumes inputs without enchantments");
        }
        add(p,"avali_nano_coating_1");add(p,"avali_nano_coating_2");
        var iron=new ItemStack(Items.IRON_INGOT,2);p.setStackInHand(Hand.MAIN_HAND,iron);iron.use(c.getWorld(),p,Hand.MAIN_HAND);
        c.assertTrue(iron.getCount()==1 && p.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier()==0,"Tier II retains iron use");
        p.getItemCooldownManager().remove(Items.IRON_INGOT);p.getItemCooldownManager().remove(Items.DIAMOND);
        var diamond=new ItemStack(Items.DIAMOND,2);p.setStackInHand(Hand.MAIN_HAND,diamond);diamond.use(c.getWorld(),p,Hand.MAIN_HAND);
        c.assertTrue(diamond.getCount()==1 && p.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier()==1 && p.hasStatusEffect(StatusEffects.FIRE_RESISTANCE),"Diamond adds stronger coating");c.complete();
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void compoundBowProjectile(TestContext c) {
        var p=c.createMockSurvivalPlayer();
        p.setPosition(c.getAbsolute(new net.minecraft.util.math.Vec3d(.5,2,.5)));
        p.getInventory().setStack(1,new ItemStack(Items.ARROW,2));
        var bow=new ItemStack(RegCustomItem.COMPOUND_KINETIC_BOW);
        p.setStackInHand(Hand.MAIN_HAND,bow);
        bow.getItem().onStoppedUsing(bow,c.getWorld(),p,bow.getMaxUseTime()-20);
        var arrows=c.getWorld().getEntitiesByClass(net.minecraft.entity.projectile.PersistentProjectileEntity.class,p.getBoundingBox().expand(3),a->a.getOwner()==p);
        c.assertTrue(arrows.size()==1 && arrows.get(0).getDamage()==2.5,"Actual bow shot applies 25 percent base damage bonus");
        c.assertTrue(p.getInventory().getStack(1).getCount()==1 && bow.getDamage()==1,"Bow consumes arrow and durability");
        arrows.forEach(net.minecraft.entity.Entity::discard);c.complete();
    }
    @GameTest(templateName=FabricGameTest.EMPTY_STRUCTURE)
    public void rescueRetriggersAtLowHealth(TestContext c) {
        var p=c.createMockSurvivalPlayer();var cooldown=(VariableIntPower)add(p,"avali_emergency_protocol_cooldown");
        var tick=add(p,"avali_emergency_protocol_tick");p.setHealth(7);tick.tick();tick.tick();
        c.assertTrue(cooldown.getValue()==0,"Above threshold does not trigger");
        p.setHealth(6);tick.tick();c.assertTrue(cooldown.getValue()==12000 && p.getStatusEffect(StatusEffects.SPEED).getAmplifier()==1,"Low health triggers ten minute cooldown");
        p.clearStatusEffects();tick.tick();c.assertTrue(!p.hasStatusEffect(StatusEffects.SPEED),"Cooldown blocks rescue");
        cooldown.setValue(1);tick.tick();tick.tick();
        c.assertTrue(p.hasStatusEffect(StatusEffects.SPEED) && cooldown.getValue()==12000,"Retriggers without healing above threshold");c.complete();
    }
}
