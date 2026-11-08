package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import net.onixary.shapeShifterCurseFabric.util.util.BaseSprite;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPCostType implements IFUSDrawableCostType<XPCostType> {
    private static final Identifier id = ShapeShifterCurseFabric.identifier("xp");
    public static final Identifier TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/shape_shifter_tuner_ui.png");
    public static final int TEXTURE_WIDTH = 454;
    public static final int TEXTURE_HEIGHT = 190;
    private static final ISprite xpIconSprite = new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 434, 17, 18, 18);

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
    public Text getAmountText(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        if (player == null) {
            return IFUSDrawableCostType.super.getAmountText(costObject, player);
        }
        return Text.literal(String.valueOf(costObject.getAmount()))
                .append(Text.translatable("gui.shape-shifter-curse.perk.cost_separator"))
                .append(String.valueOf(getTotalExperience(player.experienceLevel, MathHelper.floor(player.experienceProgress * (float) player.getNextLevelExperience()))));
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
        return getTotalExperience(player.experienceLevel, MathHelper.floor(player.experienceProgress * (float) player.getNextLevelExperience())) >= costAmount;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull PlayerEntity player) {
        player.addExperience(-costObject.getAmount());
    }

    // 还是用我很久之前写的代码吧 至少用到现在没炸过
    public static int getTotalExperience(int level, int xp) {
        int totalExp;
        if (level <= 16) {
            totalExp = level * level + 6 * level;
        }
        else if (level <= 31) {
            totalExp = (int) (2.5 * level * level - 40.5 * level + 360);
        }
        else {
            totalExp = (int) (4.5 * level * level - 162.5 * level + 2220);
        }
        int sum = totalExp + xp;
        return sum < 0 ? totalExp : sum;
    }

    @Override
    public Text getResourceNotEnoughText(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        return Text.translatable("gui.shape-shifter-curse.perk.insufficient_xp");
    }
}
