package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

// 由于网络同步问题 仅支持玩家实体 非玩家实体不会触发客户端效果
public class VirtualTotemPower extends CooldownPower {
    public int virtualTotemType;  // 用于播放动画
    public ItemStack totemStack;  // 当VirtualTotemPowerID == 0时 模拟原版不死图腾
    private final List<Consumer<Entity>> entityAction;
    private final int totemHealth;
    private final List<StatusEffectInstance> totemStatusEffects;

    public VirtualTotemPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity, data.get("cooldown"), data.get("hud_render"));
        this.virtualTotemType = data.get("virtual_totem_type");
        this.totemStack = data.get("totem_stack");
        this.entityAction = data.get("entity_actions");
        this.totemHealth = data.get("totem_health");
        this.totemStatusEffects = data.get("totem_status_effects");
    }

    // 应该不用同步配置 Apoli应该会把SerializableData.Instance同步到客户端
    public NbtElement toTag() {
        return super.toTag();
    }

    public void fromTag(NbtElement tag) {
        super.fromTag(tag);
    }

    public void use() {
        if (this.entity == null) {
            ShapeShifterCurseFabric.LOGGER.error("VirtualTotemPower: entity is null");
            return;
        }
        this.entity.setHealth(this.totemHealth);
        if (this.totemStatusEffects != null) {
            for (StatusEffectInstance statusEffectInstance : this.totemStatusEffects) {
                this.entity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
            }
        }
        if (this.entityAction != null) {
            for (Consumer<Entity> consumer : this.entityAction) {
                consumer.accept(this.entity);
            }
        }
        if (!this.entity.getWorld().isClient && this.entity instanceof ServerPlayerEntity serverPlayerEntity) {
            ModPacketsS2CServer.sendActiveVirtualTotem(serverPlayerEntity, this);
        }
        super.use();
    }

    public @Nullable PacketByteBuf create_packet_byte_buf() {
        if (this.entity instanceof ServerPlayerEntity serverPlayerEntity) {
            PacketByteBuf packetByteBuf = PacketByteBufs.create();
            packetByteBuf.writeUuid(serverPlayerEntity.getUuid());
            packetByteBuf.writeInt(this.virtualTotemType);
            packetByteBuf.writeItemStack(this.totemStack);
            return packetByteBuf;
        }
        return null;
    }

    public static void process_virtual_totem_type(@NotNull PlayerEntity entity, int virtualTotemType, @Nullable ItemStack totemStack) {
        MinecraftClient client = MinecraftClient.getInstance();
        switch (virtualTotemType) {
            case 0:
                if (totemStack == null) {
                    totemStack = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
                }
                if (client.world != null) {
                    client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    client.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
                    if (entity != client.player) break;
                    client.gameRenderer.showFloatingItem(totemStack);
                }
                break;
            case 1:  // 凋零不死能力
                if (client.world != null) {
                    client.particleManager.addEmitter(entity, ParticleTypes.SMOKE, 30);
                    client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    client.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_WITHER_DEATH, entity.getSoundCategory(), 0.75f, 0.8f, false);
                }
                break;
            default:
                ShapeShifterCurseFabric.LOGGER.error("VirtualTotemPower: unknown virtualTotemType: {}", virtualTotemType);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("virtual_totem"),
                new SerializableData()
                        .add("virtual_totem_type", SerializableDataTypes.INT, 0)  // 默认0
                        .add("totem_stack", SerializableDataTypes.ITEM_STACK, new ItemStack(Items.TOTEM_OF_UNDYING, 1))
                        .add("entity_actions", ApoliDataTypes.ENTITY_ACTIONS, null)
                        .add("totem_health", SerializableDataTypes.INT, 1)  // 默认1
                        .add("totem_status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null)
                        .add("cooldown", SerializableDataTypes.INT, 1200)  // 默认1分钟
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
                data -> (powerType, entity) -> new VirtualTotemPower(powerType, entity, data)
        ).allowCondition();
    }

}
