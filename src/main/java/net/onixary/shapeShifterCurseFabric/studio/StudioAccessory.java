package net.onixary.shapeShifterCurseFabric.studio;

import java.util.List;
import java.util.Set;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import org.jetbrains.annotations.Nullable;

/** Generic registered accessory; power callbacks are supplied by the existing accessory mixin. */
public final class StudioAccessory extends AccessoryItem {
    private final String description;
    private final Set<Identifier> allowedForms;

    public StudioAccessory(Settings settings, String description, Set<Identifier> allowedForms) {
        super(settings);
        this.description = description;
        this.allowedForms = Set.copyOf(allowedForms);
    }

    @Override
    public boolean canEquip(ItemStack stack, LivingEntity entity, SlotData slot) {
        return allowedForms.isEmpty() || (entity instanceof PlayerEntity player &&
                allowedForms.contains(PlayerFormComponent.COMPONENT.get(player).nowFormID));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!description.isEmpty()) tooltip.add(Text.literal(description));
    }
}
