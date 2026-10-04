package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.FormUpgradeScreen;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import net.onixary.shapeShifterCurseFabric.util.util.BaseSprite;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPCostType implements IFUSDrawableCostType<XPCostType> {
    private static final Identifier id = ShapeShifterCurseFabric.identifier("xp");
    private static final ISprite xpIconSprite = new BaseSprite(FormUpgradeScreen.TEXTURE, FormUpgradeScreen.TEXTURE_WIDTH, FormUpgradeScreen.TEXTURE_HEIGHT, 434, 17, 18, 18);

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public void drawIcon(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z) {
        xpIconSprite.draw(context, x, y, z, 0, 0, 18, 18);
    }

    @Override
    public void drawOnHover(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z, int mouseX, int mouseY) {
        // NOP
    }

    @Override
    public boolean canPay(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        int costAmount = costObject.getAmount();
        if (player == null) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                player = ClientUtils.getPlayer();
            } else {
                throw new RuntimeException("CostType.canPay Player Argument In ServerSide Must NotNull");
            }
        }
        return player.totalExperience >= costAmount;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull PlayerEntity player) {
        player.addExperience(costObject.getAmount());
    }
}
