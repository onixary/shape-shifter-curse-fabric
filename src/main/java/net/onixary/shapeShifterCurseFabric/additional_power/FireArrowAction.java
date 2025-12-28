package net.onixary.shapeShifterCurseFabric.additional_power;

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
    public static void spawnFireArrow(LivingEntity owner, float Damage, float Speed, float Spread) {
        ArrowItem arrowItem = (ArrowItem)(Items.ARROW);
        ItemStack itemStack = new ItemStack(arrowItem);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(owner.getWorld(), itemStack, owner);
        persistentProjectileEntity.setOnFireFor(100);
        persistentProjectileEntity.setNoGravity(true);  // 危险设计 容易制作卡服机 见烈焰弹卡服务器方法
        persistentProjectileEntity.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0F, Speed, Spread);
        persistentProjectileEntity.setDamage(Damage);
        persistentProjectileEntity.setCritical(true);
        persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        owner.getWorld().spawnEntity(persistentProjectileEntity);
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("fire_arrow"),
                new SerializableData()
                        .add("damage", SerializableDataTypes.FLOAT, 2.0f)
                        .add("speed", SerializableDataTypes.FLOAT, 3.0f)
                        .add("spread", SerializableDataTypes.FLOAT, 0.0f),
                (data, e) -> {
                    if (e instanceof LivingEntity livingEntity) {
                    float damage = data.get("damage");
                    float speed = data.get("speed");
                    float spread = data.get("spread");
                    spawnFireArrow(livingEntity, damage, speed, spread);
                }}));
    }
}
