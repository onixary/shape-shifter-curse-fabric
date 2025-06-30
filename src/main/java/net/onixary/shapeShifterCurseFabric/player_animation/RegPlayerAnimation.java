package net.onixary.shapeShifterCurseFabric.player_animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegPlayerAnimation {
    public static void register() {
        //You might use the EVENT to register new animations, or you can use Mixin.
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new Identifier(MOD_ID, "player_animation"), 42, (player) -> {
            if (player instanceof ClientPlayerEntity) {
                //animationStack.addAnimLayer(42, testAnimation); //Add and save the animation container for later use.
                ModifierLayer<IAnimation> testAnimation =  new ModifierLayer<>();

                testAnimation.addModifierBefore(new SpeedModifier(0.5f)); //This will be slow
                testAnimation.addModifierBefore(new MirrorModifier(true)); //Mirror the animation
                return testAnimation;
            }
            return null;
        });

        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
            ModifierLayer<IAnimation> layer = new ModifierLayer<>();
            animationStack.addAnimLayer(69, layer);
            PlayerAnimationAccess.getPlayerAssociatedData(player).set(new Identifier(MOD_ID, "player_animation"), layer);
        });
        //You can add modifiers to the ModifierLayer.
    }


    //用于播放动画的示例
    /*public static void playTestAnimation() {
        //Use this for setting an animation without fade
        //PlayerAnimTestmod.testAnimation.setAnimation(new KeyframeAnimationPlayer(AnimationRegistry.animations.get("two_handed_vertical_right_right")));

        ModifierLayer<IAnimation> testAnimation;
        testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(MinecraftClient.getInstance().player).get(new Identifier(MOD_ID, "player_animation"));


        if (testAnimation.getAnimation() != null && new Random().nextBoolean()) {
            //It will fade out from the current animation, null as newAnimation means no animation.
            testAnimation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(20, Ease.LINEAR), null);
        } else {
            //Fade from current animation to a new one.
            //Will not fade if there is no animation currently.
            testAnimation.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
                    new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier(MOD_ID, "two_handed_slash_vertical_right")))
                            .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                            .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false))
            );
        }
    }*/
}
