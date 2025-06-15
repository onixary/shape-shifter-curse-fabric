package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.additional_power.LootingPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class EntityLootingMixin {

    @Inject(at = @At("HEAD"), method = "dropLoot", cancellable = true)
    private void modifyEntityLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;

        if (!(entity instanceof LivingEntity)) return;

        LivingEntity attacker = (LivingEntity) source.getAttacker();

        // 只处理由玩家造成的击杀
        if (!(attacker instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) attacker;

        // 如果世界不是服务器世界，跳过
        if (!(entity.getWorld() instanceof ServerWorld)) return;

        // 计算总抢夺等级 = 手持物品附魔 + 常驻能力
        int toolLooting = EnchantmentHelper.getLooting(player);
        int powerLooting = PowerHolderComponent.getPowers(player, LootingPower.class)
                .stream()
                .mapToInt(LootingPower::getLevel)
                .sum(); // 叠加所有能力等级

        int totalLooting = toolLooting + powerLooting;

        // 如果没有效果，跳过
        if (totalLooting <= 0) return;

        // 获取实体的战利品表ID
        EntityType<?> entityType = entity.getType();
        Identifier lootTableId = entityType.getLootTableId();

        // 如果没有关联的战利品表，跳过
        if (lootTableId == null) return;

        // 创建 LootContext.Builder
        LootContextParameterSet.Builder parameterSetBuilder = new LootContextParameterSet.Builder((ServerWorld) entity.getWorld())
                .add(LootContextParameters.THIS_ENTITY, entity)
                .add(LootContextParameters.ORIGIN, entity.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, source)
                .add(LootContextParameters.KILLER_ENTITY, player)
                .add(LootContextParameters.LAST_DAMAGE_PLAYER, player)
                .luck(totalLooting);

        // 构建 LootContext
        LootContextParameterSet parameterSet = parameterSetBuilder.build(LootContextTypes.ENTITY);
        LootContext lootContext = new LootContext.Builder(parameterSet).build(null);

        // 生成战利品
        LootTable lootTable = Objects.requireNonNull(entity.getWorld().getServer()).getLootManager().getLootTable(lootTableId);
        List<ItemStack> drops = lootTable.generateLoot(parameterSet);

        // 应用掉落
        for (ItemStack stack : drops) {
            entity.dropStack(processLootingPower(stack, lootContext, totalLooting));
        }

        // 取消原版掉落逻辑
        ci.cancel();
    }

    public ItemStack processLootingPower(ItemStack stack, LootContext context, int lootingLevel) {
        Entity entity = context.get(LootContextParameters.KILLER_ENTITY);
        if (entity instanceof LivingEntity) {
            int i = lootingLevel;
            if (i == 0) {
                return stack;
            }
            float f = (float)i * UniformLootNumberProvider.create(0.0f, 1.0f).nextFloat(context);
            stack.increment(Math.round(f));
        }
        return stack;
    }
}
