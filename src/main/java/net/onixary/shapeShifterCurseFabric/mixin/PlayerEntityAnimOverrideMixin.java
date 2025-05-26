package net.onixary.shapeShifterCurseFabric.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.form_animation.*;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntityAnimOverrideMixin extends PlayerEntity {
    @Unique
    private final ModifierLayer<IAnimation> CONTAINER = new ModifierLayer<>();

    public PlayerEntityAnimOverrideMixin(ClientWorld world, GameProfile gameProfile) {
        super(world, world.getSpawnPos(), world.getSpawnAngle(), gameProfile);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void shape_shifter_curse$init(ClientWorld level, GameProfile profile, CallbackInfo info) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object) this).addAnimLayer(1, CONTAINER);
        // register all form animations here
        AnimationTransform.registerAnims();
        AnimationPlayerBat1.registerAnims();
        AnimationPlayerBat2.registerAnims();
        AnimationPlayerAxolotl2.registerAnims();
        AnimationPlayerAxolotl1.registerAnims();
        AnimationPlayerOcelot2.registerAnims();
        AnimationPlayerAllaySP.registerAnims();
        AnimationPlayerFeralCatSP.registerAnims();

        currentAnimation = null;
        CONTAINER.setAnimation(null);
    }

    public PlayerAnimState currentState = PlayerAnimState.NONE;
    public PlayerAnimState previousState = PlayerAnimState.NONE;

    public float momentum = 0;
    public float turnDelta = 0;
    public Vec3d lastPos = new Vec3d(0, 0, 0);
    public boolean lastOnGround = false;
    public boolean hasSlowFall = false;
    private int tickCounter = 0;

    private PlayerForms curForm;
    KeyframeAnimation currentAnimation = null;
    boolean overrideHandAnim = false;
    AnimationHolder animToPlay = null;
    boolean isAttackAnim = false;
    int continueSwingAnimCounter = 0;

    PlayerForms transformCurrentForm;
    PlayerForms transformToForm;

    @Override
    public void tick() {
        super.tick();
        // 计时来决定是挖掘还是攻击
        /*if(this.handSwinging){
            if(continueSwingAnimCounter < 10){
                continueSwingAnimCounter++;
                isAttackAnim = true;
            }
            else{
                isAttackAnim = false;
            }
        }
        else{
            continueSwingAnimCounter = 0;
            isAttackAnim =false;
        }*/

        // judge current anim state
        if (getVehicle() != null && !(getVehicle() instanceof BoatEntity)) {
            return;
        }
        tickCounter++;
        if (tickCounter == 20) {
            /*if (this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                hasSlowFall = true;
            } else {
                hasSlowFall = false;
            }*/
            tickCounter = 0;
        }
        World world = this.getEntityWorld();
        boolean onGround = this.isOnGround();
        float delta = 1.0f / 20.0f;
        Vec3d pos = getPos();

        curForm = RegPlayerFormComponent.PLAYER_FORM.get(this).getCurrentForm();

        if (!isOnGround() && lastOnGround && getVelocity().y > 0) {
            currentState = PlayerAnimState.ANIM_JUMP;
        }

        if(!isOnGround() && lastOnGround && (Math.abs(getVelocity().z) > 0.15 || Math.abs(getVelocity().x) > 0.15)){
            // 特殊处理Ocelot的rush jump
            if(curForm == PlayerForms.OCELOT_2){
                currentState = PlayerAnimState.ANIM_RUSH_JUMP;
            }
        }
        Block standingBlock = world.getBlockState(getBlockPos().down()).getBlock();

        Vec3f movementVector = new Vec3f((float) (pos.x - lastPos.x), 0, (float) (pos.z - lastPos.z));
        Vec3f lookVector = new Vec3f((float) Math.cos(Math.toRadians(bodyYaw + 90)), 0,
                (float) Math.sin(Math.toRadians(bodyYaw + 90)));

        float movementLength = (float) Math.sqrt(movementVector.getX() * movementVector.getX() +
                movementVector.getY() * movementVector.getY() +
                movementVector.getZ() * movementVector.getZ());

        boolean isWalking = movementLength > 0;

        float dotProduct = movementVector.getX() * lookVector.getX() +
                movementVector.getY() * lookVector.getY() +
                movementVector.getZ() * lookVector.getZ();

        boolean isWalkingForwards = isWalking && dotProduct > 0;

        float walk_sign = isWalking ? isWalkingForwards ? 1 : -1 : 0;

        float sprint_multiplier = ((isSprinting() && isWalkingForwards) ? 2 : 1);
        momentum = MathHelper.lerp(delta * 2 * sprint_multiplier, momentum, (walk_sign) * sprint_multiplier);

        boolean onGroundInWater = isSubmergedInWater() && this.getLandingBlockState().getCollisionShape(this.getWorld(), this.getBlockPos()).isEmpty();
        // getHandSwingDuration() is a private method, so we have to calculate it ourselves
        int handSwingDuration = 0;
        if (StatusEffectUtil.hasHaste(this))
        {
            handSwingDuration = 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
        }
        else
        {
            handSwingDuration = this.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
        }

        if (!this.handSwinging)
        {
            if (getVehicle() != null && getVehicle() instanceof BoatEntity)
            {
                currentState = PlayerAnimState.ANIM_BOAT_IDLE;
            }

            if (world.getBlockState(getBlockPos()).getBlock() instanceof LadderBlock && !isOnGround() && !jumping)
            {
                currentState = PlayerAnimState.ANIM_CLIMBING_IDLE;
                if (getVelocity().y > 0)
                {
                    currentState = PlayerAnimState.ANIM_CLIMBING;
                }
            }
            else if (isUsingItem())
            {
                if (getMainHandStack().getItem().isFood())
                {
                    //todo
                }
                if (getMainHandStack().getUseAction() == UseAction.DRINK)
                {
                    //todo
                }
            }
            else if (isFallFlying())
            {
                currentState = PlayerAnimState.ANIM_ELYTRA_FLY;
            }
            else if ((isOnGround() || onGroundInWater) && !isAttackAnim)
            {
                currentState = PlayerAnimState.ANIM_IDLE;
                if ((isInsideWaterOrBubbleColumn() || isInLava()) && !onGroundInWater)
                {
                    if (this.isSwimming() || this.isSprinting())
                    {
                        currentState = PlayerAnimState.ANIM_SWIMMING;
                    }
                }
                else if (isSneaking())
                {
                    currentState = PlayerAnimState.ANIM_SNEAK_IDLE;

                    if (isWalking || turnDelta != 0)
                    {
                        // 特殊处理Ocelot的sneak rush
                        if(curForm == PlayerForms.OCELOT_2){
                            if(this.getHungerManager().getFoodLevel() >= 6){
                                currentState = PlayerAnimState.ANIM_SNEAK_RUSH;
                            }
                            else{
                                currentState = PlayerAnimState.ANIM_SNEAK_WALK;
                            }
                        }
                        else{
                            currentState = PlayerAnimState.ANIM_SNEAK_WALK;
                        }

                    }
                }
                else
                {
                    if (isWalking)
                    {
                        if (isSprinting() && !isUsingItem())
                        {
                            currentState = PlayerAnimState.ANIM_RUN;
                        }
                        else
                        {
                            currentState = PlayerAnimState.ANIM_WALK;
                        }
                    }
                }
            }
            else
            {
                //if (this.fallDistance > 1)
                //{
                //    if (this.fallDistance > 3)
                //    {
                //       currentState = PlayerAnimState.ANIM_FALLING;
                //    }
                //}

                if (!isOnGround() && getVelocity().y < 0 && hasSlowFall)
                {
                    // todo: hasSlowFall之后他要替换成form相关的逻辑
                    currentState = PlayerAnimState.ANIM_SLOW_FALLING;
                }
            }
            if (isSubmergedInWater() || isInLava())
            {
                if (this.isSwimming() || this.isSprinting())
                {
                    currentState = PlayerAnimState.ANIM_SWIMMING;
                }
                else
                {
                    currentState = PlayerAnimState.ANIM_SWIM_IDLE;
                }
            }
            else if (isCrawling()) {
                if (isWalking) {
                    currentState = PlayerAnimState.ANIM_CRAWLING;
                } else {
                    currentState = PlayerAnimState.ANIM_CRAWLING_IDLE;
                }
            }
            continueSwingAnimCounter = 0; // 重置挖掘动画计数器
        }
        else
        {
            // 需要判断是挖掘（长按）还是攻击（短按）
            // 挖掘动画为loop，攻击动画为单次
            if (continueSwingAnimCounter < 10) {
                continueSwingAnimCounter++;
                currentState = PlayerAnimState.ANIM_ATTACK_ONCE;
            } else {
                currentState = PlayerAnimState.ANIM_TOOL_SWING;
            }
        }
        // isTransforming
        if(TransformManager.isTransforming()){
            //ShapeShifterCurseFabric.LOGGER.info("Player is transforming");
            transformCurrentForm = TransformManager.curFromForm;
            transformToForm = TransformManager.curToForm;
            currentState = PlayerAnimState.ANIM_ON_TRANSFORM;
        }

        lastPos = new Vec3d(pos.x, pos.y, pos.z);
        lastOnGround = onGround;
        if (previousState != currentState)
        {
            //ShapeShifterCurseFabric.LOGGER.info("PlayerAnimState: " + currentState);
            // todo: previousState related logic here
            previousState = currentState;
        }

        // judge form animation
        animToPlay = null;
        if(currentState == PlayerAnimState.ANIM_ON_TRANSFORM){
            animToPlay = AnimationTransform.getFormAnimToPlay(transformCurrentForm, transformToForm);
        }
        else{
            switch (curForm) {
                case BAT_1:
                    animToPlay = AnimationPlayerBat1.getFormAnimToPlay(currentState);
                    hasSlowFall = false;
                    overrideHandAnim = false;
                    break;
                case BAT_2:
                    animToPlay = AnimationPlayerBat2.getFormAnimToPlay(currentState);
                    hasSlowFall = true;
                    overrideHandAnim = true;
                    break;

                case AXOLOTL_1:
                    animToPlay = AnimationPlayerAxolotl1.getFormAnimToPlay(currentState);
                    hasSlowFall = false;
                    overrideHandAnim = false;
                    break;

                case AXOLOTL_2:
                    animToPlay = AnimationPlayerAxolotl2.getFormAnimToPlay(currentState);
                    hasSlowFall = false;
                    overrideHandAnim = false;
                    break;

                case OCELOT_2:
                    animToPlay = AnimationPlayerOcelot2.getFormAnimToPlay(currentState);
                    hasSlowFall = false;
                    overrideHandAnim = false;
                    break;

                case ALLAY_SP:
                    animToPlay = AnimationPlayerAllaySP.getFormAnimToPlay(currentState);
                    hasSlowFall = true;
                    overrideHandAnim = true;
                    break;

                case FERAL_CAT_SP:
                    animToPlay = AnimationPlayerFeralCatSP.getFormAnimToPlay(currentState);
                    hasSlowFall = false;
                    overrideHandAnim = true;
                    break;
                default:
                    break;
            }
        }


        if (animToPlay != null){
            //ShapeShifterCurseFabric.LOGGER.info("Playing animation: " + animToPlay.getAnimation());
            playAnimation(animToPlay.getAnimation(), animToPlay.getSpeed(), animToPlay.getFade());
        }
        else{
            CONTAINER.setAnimation(null);
            currentAnimation = null;
        }

    }

    public void playAnimation(KeyframeAnimation anim) {
        playAnimation(anim, 1.0f, 10);
    }

    private boolean modified = false;
    private boolean armAnimationsEnabled = true;

    public void playAnimation(KeyframeAnimation anim, float speed, int fade) {
        playAnimation(CONTAINER, anim, speed, fade);
    }

    public void playAnimation(ModifierLayer<IAnimation> container, KeyframeAnimation anim, float speed, int fade) {
        if (currentAnimation == anim || anim == null)
            return;

        currentAnimation = anim;
        var builder = anim.mutableCopy();
        builder.leftArm.setEnabled(armAnimationsEnabled);
        builder.rightArm.setEnabled(armAnimationsEnabled);
        anim = builder.build();

        if (modified) {
            container.removeModifier(0);
        }
        modified = true;


        container.addModifierBefore(new SpeedModifier(speed));
        container.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fade, Ease.LINEAR),
                new KeyframeAnimationPlayer(anim));

        container.setupAnim(1.0f / 20.0f);

    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);

        if (getActiveItem().getItem() instanceof FlintAndSteelItem && isUsingItem()) {
            return;
        }
        currentState = PlayerAnimState.ANIM_TOOL_SWING;
    }

    public void disableArmAnimations() {
        if (currentAnimation != null && armAnimationsEnabled) {
            armAnimationsEnabled = false;
            ModifierLayer<IAnimation> animationContainer = CONTAINER;

            var builder = currentAnimation.mutableCopy();

            builder.leftArm.setEnabled(false);
            builder.rightArm.setEnabled(false);

            currentAnimation = builder.build();

            if (modified) {
                animationContainer.removeModifier(0);
            }

            modified = true;

            animationContainer.addModifierBefore(new SpeedModifier(1));
            animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.LINEAR),
                    new KeyframeAnimationPlayer(currentAnimation));
            animationContainer.setupAnim(1.0f / 20.0f);
            animationContainer.tick();
        }
    }
}
