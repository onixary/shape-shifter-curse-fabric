package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Consumer;

public class FireArrowAction {
    public static void spawnFireArrow(LivingEntity owner, float Damage, float Speed, float Spread, int FireTime, boolean NoGravity, boolean Critical, boolean hasOwner, Consumer<Entity> projectileAction) {
        ArrowItem arrowItem = (ArrowItem)(Items.ARROW);
        ItemStack itemStack = new ItemStack(arrowItem);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(owner.getWorld(), itemStack, hasOwner ? owner : null);
        if (FireTime > 0) {
            persistentProjectileEntity.setOnFireFor(FireTime);
        }
        if (NoGravity) {
            persistentProjectileEntity.setNoGravity(true);  // 危险设计 容易制作卡服机 见烈焰弹卡服务器方法
        }
        persistentProjectileEntity.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0F, Speed, Spread);
        persistentProjectileEntity.setDamage(Damage);
        if (Critical) {
            persistentProjectileEntity.setCritical(true);
        }
        persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        boolean success = owner.getWorld().spawnEntity(persistentProjectileEntity);
        if (success) {
            if (projectileAction != null) {
                projectileAction.accept(persistentProjectileEntity);
            }
        }
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("fire_arrow"),
                new SerializableData()
                        .add("damage", SerializableDataTypes.FLOAT, 2.0f)
                        .add("speed", SerializableDataTypes.FLOAT, 3.0f)
                        .add("spread", SerializableDataTypes.FLOAT, 0.0f)
                        .add("fire_time", SerializableDataTypes.INT, 0)
                        .add("no_gravity", SerializableDataTypes.BOOLEAN, false)
                        .add("critical", SerializableDataTypes.BOOLEAN, false)
                        .add("has_owner", SerializableDataTypes.BOOLEAN, true)
                        .add("projectile_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("count", SerializableDataTypes.INT, 1),
                (data, e) -> {
                    if (e instanceof LivingEntity livingEntity) {
                    float damage = data.get("damage");
                    float speed = data.get("speed");
                    float spread = data.get("spread");
                    int fireTime = data.get("fire_time");
                    boolean noGravity = data.get("no_gravity");
                    boolean critical = data.get("critical");
                    boolean hasOwner = data.get("has_owner");
                    Consumer<Entity> projectileAction = data.get("projectile_action");
                    int count = data.get("count");
                    for (int i = 0; i < count; i++) {
                        spawnFireArrow(livingEntity, damage, speed, spread, fireTime, noGravity, critical, hasOwner, projectileAction);
                    }
                }}));
    }
}
