package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.item.RegCustomPotions;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.team.MobTeamManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin {

    private static final float POTION_REPLACE_CHANCE = 0.6f;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addToSorceryTeam(EntityType<? extends HostileEntity> entityType, World world, CallbackInfo ci) {
        if (!world.isClient) {
            Entity entity = (Entity)(Object)this;
            Scoreboard scoreboard = world.getScoreboard();

            Team sorceryTeam = scoreboard.getTeam(MobTeamManager.SORCERY_TEAM_NAME);
            if (sorceryTeam != null) {
                scoreboard.addPlayerToTeam(entity.getEntityName(), sorceryTeam);
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void injectCustomPotionAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
        WitchEntity witch = (WitchEntity) (Object) this;
        World world = witch.getWorld();

        if(target instanceof PlayerEntity){
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(target).getCurrentForm();
            if(curForm == PlayerForms.ORIGINAL_SHIFTER){
                double randomChance = Math.random();
                if(randomChance < POTION_REPLACE_CHANCE){
                    Vec3d vec3d = target.getVelocity();
                    double d = target.getX() + vec3d.x - witch.getX();
                    double e = target.getEyeY() - (double)1.1F - witch.getY();
                    double f = target.getZ() + vec3d.z - witch.getZ();
                    double g = Math.sqrt(d * d + f * f);

                    // 创建自定义溅射式药水
                    PotionEntity customPotion = new PotionEntity(world, witch);
                    ItemStack potionStack = PotionUtil.setPotion(new ItemStack(net.minecraft.item.Items.SPLASH_POTION), RegCustomPotions.FAMILIAR_FOX_FORM_POTION);
                    customPotion.setItem(potionStack);

                    customPotion.setPitch(customPotion.getPitch() - -20.0F);
                    customPotion.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);

                    if (!witch.isSilent()) {
                        witch.getWorld().playSound((PlayerEntity)null, witch.getX(), witch.getY(), witch.getZ(), SoundEvents.ENTITY_WITCH_THROW, witch.getSoundCategory(), 1.0F, 0.8F);
                    }
                    // 发射自定义药水
                    world.spawnEntity(customPotion);

                    // 取消原始攻击逻辑
                    ci.cancel();
                }
            }
        }
    }


}
