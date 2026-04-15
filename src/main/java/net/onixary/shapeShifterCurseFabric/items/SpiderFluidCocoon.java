package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpiderFluidCocoon extends Item {
    public SpiderFluidCocoon(Settings settings) {
        super(settings
                .maxCount(64)
                .food(
                        new FoodComponent.Builder()
                                .hunger(6)
                                .saturationModifier(0.8f)
                                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 150, 0), 1.0f)
                                .build()
                ));
    }

    @Override
    public SoundEvent getEatSound(){
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.shape-shifter-curse.spider_fluid_cocoon.tooltip").formatted(Formatting.YELLOW));
    }
}