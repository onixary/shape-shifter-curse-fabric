package net.onixary.shapeShifterCurseFabric.player_form.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.ImmobilityEffect;
import net.onixary.shapeShifterCurseFabric.util.ClientTicker;

public class PlayerEffectManager {

    public static void applyInstinctThresholdEffect() {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if(clientPlayer == null){
            return;
        }

        for (int i = 0; i < 1; i++) {
            clientPlayer.getWorld().addParticle(StaticParams.PLAYER_TRANSFORM_PARTICLE,
                    clientPlayer.getX() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.5,
                    clientPlayer.getY() + clientPlayer.getRandom().nextDouble() * 1,
                    clientPlayer.getZ() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.5,
                    0, 1, 0.5);
        }
    }

    public static void applyStartTransformEffect(ServerPlayerEntity player, int duration) {
        // add darkness effect
        StatusEffectInstance darknessEffect = new StatusEffectInstance(StatusEffects.BLINDNESS, duration);
        player.addStatusEffect(darknessEffect);

        // add immobility effect
        StatusEffectInstance immobilityEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 245);
        player.addStatusEffect(immobilityEffect);

        // prevent jump
        StatusEffectInstance preventJumpEffect = new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration, 137);
        player.addStatusEffect(preventJumpEffect);

        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if(clientPlayer == null){
            return;
        }


        Runnable particleTask = () -> {
            for (int i = 0; i < 5; i++) {
                clientPlayer.getWorld().addParticle(StaticParams.PLAYER_TRANSFORM_PARTICLE,
                        clientPlayer.getX() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.9,
                        clientPlayer.getY() + clientPlayer.getRandom().nextDouble() * 1.5 + 1,
                        clientPlayer.getZ() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.9,
                        0, -1, 0);
                //ShapeShifterCurseFabric.LOGGER.info("Particle effect emitted");
            }
        };

        // Get the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();
        // Create and start the client ticker
        ClientTicker ticker = new ClientTicker(client, particleTask, duration);
        ticker.start();
    }

    public static void applyEndTransformEffect(ServerPlayerEntity player, int duration) {
        // add nausea effect
        StatusEffectInstance nauseaEffect = new StatusEffectInstance(StatusEffects.NAUSEA, duration);
        player.addStatusEffect(nauseaEffect);

        // add immobility effect
        StatusEffectInstance immobilityEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 245);
        player.addStatusEffect(immobilityEffect);

        // prevent jump
        StatusEffectInstance preventJumpEffect = new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration, 137);
        player.addStatusEffect(preventJumpEffect);

        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if(clientPlayer == null){
            return;
        }


        // similar to DOTween in Unity
        Runnable particleTask = () -> {
            for (int i = 0; i < 2; i++) {
                clientPlayer.getWorld().addParticle(StaticParams.PLAYER_TRANSFORM_PARTICLE,
                        clientPlayer.getX() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.9,
                        clientPlayer.getY() + clientPlayer.getRandom().nextDouble() * 1.5 + 1,
                        clientPlayer.getZ() + (clientPlayer.getRandom().nextDouble() - 0.5) * 0.9,
                        0, -1, 0);
                //ShapeShifterCurseFabric.LOGGER.info("Particle effect emitted");
            }
        };

        // Get the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();
        // Create and start the client ticker
        ClientTicker ticker = new ClientTicker(client, particleTask, duration);
        ticker.start();
    }

    public static void applyFinaleTransformEffect(ServerPlayerEntity player, int duration){

        // slowness effect remain some time
        StatusEffectInstance immobilityEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 200);
        player.addStatusEffect(immobilityEffect);

    }
}
