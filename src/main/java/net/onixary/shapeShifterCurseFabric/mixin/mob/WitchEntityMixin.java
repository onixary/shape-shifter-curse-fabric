package net.onixary.shapeShifterCurseFabric.mixin.mob;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.WitchFriendlyPower;
import net.onixary.shapeShifterCurseFabric.items.RegCustomPotions;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin {

    private static final float POTION_REPLACE_CHANCE = 0.6f;

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void injectCustomPotionAttack(LivingEntity target, float pullProgress, CallbackInfo ci) {
        WitchEntity witch = (WitchEntity) (Object) this;
        World world = witch.getWorld();

        if(target instanceof PlayerEntity){
            PlayerFormBase curForm = RegPlayerFormComponent.PLAYER_FORM.get(target).getCurrentForm();
            if(curForm.equals(RegPlayerForms.ORIGINAL_SHIFTER)){
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
            else if (PowerHolderComponent.hasPower((PlayerEntity)target, WitchFriendlyPower.class)) {
                ci.cancel();
            }
        }
    }


}
