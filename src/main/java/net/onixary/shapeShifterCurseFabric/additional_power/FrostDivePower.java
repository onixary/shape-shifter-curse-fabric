package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import java.util.function.Consumer;

public class FrostDivePower extends CooldownPower implements Active {
    private final double speed, minimumHeight;
    private final Consumer<Entity> startAction, landingAction;
    private Key key;
    private boolean diving;
    private double startY;
    private Vec3d previousPosition;
    private World startWorld;

    public FrostDivePower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity, data.getInt("cooldown"), HudRender.DONT_RENDER);
        speed = data.getDouble("speed"); minimumHeight = data.getDouble("minimum_height");
        startAction = data.get("start_action"); landingAction = data.get("landing_action");
        key = data.get("key"); setTicking(true);
    }
    @Override public Key getKey() { return key; }
    @Override public void setKey(Key key) { this.key = key; }
    @Override public void onUse() {
        if (entity.getWorld().isClient || !canUse() || diving || !airborne(entity)) return;
        diving = true; startY = entity.getY(); startWorld = entity.getWorld(); previousPosition = entity.getPos();
        startAction.accept(entity);
        entity.fallDistance = 0;
        PerkActions.setVelocity(entity, new Vec3d(0, -speed, 0));
        use();
    }
    public static boolean airborne(LivingEntity entity) {
        return !entity.isOnGround() && !entity.isTouchingWater() && !entity.isInLava()
                && !entity.hasVehicle() && !entity.isFallFlying() && !entity.isClimbing()
                && !(entity instanceof PlayerEntity player && player.getAbilities().flying);
    }
    public static boolean isDiving(Entity entity) {
        return entity instanceof LivingEntity && PowerHolderComponent.KEY.get(entity).getPowers(FrostDivePower.class, true)
                .stream().anyMatch(p -> p.diving);
    }
    public static Vec3d constrainMovement(Entity entity, Vec3d movement) {
        if (!isDiving(entity)) return movement;
        // Upward skills, knockback and steering cannot interrupt an active dive.
        return new Vec3d(0, Math.min(movement.y, 0), 0);
    }
    @Override public void tick() {
        if (!diving) return;
        entity.fallDistance = 0;
        if (!entity.getWorld().isClient) {
            if (!entity.isAlive() || entity.getWorld() != startWorld
                    || previousPosition.squaredDistanceTo(entity.getPos()) > 64) { stop(); return; }
            if (entity.isOnGround()) {
                boolean impact = startY - entity.getY() > minimumHeight;
                stop();
                if (impact) landingAction.accept(entity);
                return;
            }
            if (!airborne(entity)) { stop(); return; }
            previousPosition = entity.getPos();
        }
        PerkActions.setVelocity(entity, new Vec3d(0, -speed, 0));
    }
    private void stop() {
        diving = false; entity.fallDistance = 0;
        if (!entity.getWorld().isClient) PowerHolderComponent.syncPower(entity, getType());
    }
    @Override public void onRemoved() { stop(); }
    @Override public NbtElement toTag() {
        var tag = new NbtCompound(); tag.put("cooldown", super.toTag()); tag.putBoolean("diving", diving); return tag;
    }
    @Override public void fromTag(NbtElement tag) {
        if (tag instanceof NbtCompound data) {
            super.fromTag(data.contains("cooldown") ? data.get("cooldown") : NbtLong.of(0));
            // Only network clients resume the transient state; never resume it after login.
            diving = entity.getWorld().isClient && data.getBoolean("diving");
        }
    }
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("frost_dive"), new SerializableData()
                .add("speed", SerializableDataTypes.DOUBLE, 0.8)
                .add("minimum_height", SerializableDataTypes.DOUBLE, 4.0)
                .add("cooldown", SerializableDataTypes.INT, 160)
                .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
                .add("start_action", ApoliDataTypes.ENTITY_ACTION)
                .add("landing_action", ApoliDataTypes.ENTITY_ACTION),
                data -> (type, entity) -> new FrostDivePower(type, entity, data)).allowCondition();
    }
}
