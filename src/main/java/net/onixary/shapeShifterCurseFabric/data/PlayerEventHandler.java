package net.onixary.shapeShifterCurseFabric.data;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeEffects;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.*;

public class PlayerEventHandler {
    public static void register() {
        // join event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player.getWorld().isClient()) return;

            // load form
            PlayerEntity player = handler.player;
            FormAbilityManager.loadForm(player);
            //PlayerFormComponent component = RegPlayerFormComponent.PLAYER_FORM.get(player);
            //FormAbilityManager.applyForm(player, component.getCurrentForm());

            // load attachment
            boolean hasAttachment = loadCurrentAttachment(player);
            if(!hasAttachment) {
                resetAttachment(player);
            }
            else{
                ShapeShifterCurseFabric.LOGGER.info("Attachment loaded ");
            }
        });
        // copy event
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            // 仅在服务端执行
            if (newPlayer.getWorld().isClient()) return;

            copyTransformativeEffect(oldPlayer, newPlayer);
            copyFormAndAbility(oldPlayer, newPlayer);
        });

        //load event
        ServerWorldEvents.LOAD.register((server, world) -> {
            for (PlayerEntity player : world.getPlayers()) {
                PlayerFormComponent component = RegPlayerFormComponent.PLAYER_FORM.get(player);
                FormAbilityManager.applyForm(player, component.getCurrentForm());
            }
        });
    }

    private static void copyTransformativeEffect(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        // transformative effect attachment
        PlayerEffectAttachment oldAttachment = oldPlayer.getAttached(EffectManager.EFFECT_ATTACHMENT);
        newPlayer.setAttached(EffectManager.EFFECT_ATTACHMENT, new PlayerEffectAttachment());
        PlayerEffectAttachment newAttachment = newPlayer.getAttached(EffectManager.EFFECT_ATTACHMENT);

        newAttachment.currentToForm = oldAttachment.currentToForm;
        newAttachment.remainingTicks = oldAttachment.remainingTicks;
        newAttachment.currentEffect = oldAttachment.currentEffect;

        // reapply potion effect
        if (newAttachment.currentEffect != null && currentRegEffect != null) {
            ShapeShifterCurseFabric.LOGGER.info("re-apply potion effect here");
            removeEffects(newPlayer);
            newPlayer.addStatusEffect(new StatusEffectInstance(
                    currentRegEffect,
                    newAttachment.remainingTicks
            ));
        }
    }

    private static void copyFormAndAbility(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        PlayerFormComponent oldComponent = RegPlayerFormComponent.PLAYER_FORM.get(oldPlayer);
        PlayerFormComponent newComponent = RegPlayerFormComponent.PLAYER_FORM.get(newPlayer);
        newComponent.setCurrentForm(oldComponent.getCurrentForm());
        newComponent.setCurrentForm(oldComponent.getCurrentForm());
        FormAbilityManager.applyForm(newPlayer, newComponent.getCurrentForm());
    }
}
