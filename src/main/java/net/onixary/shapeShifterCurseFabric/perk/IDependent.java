package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;

public interface IDependent {
    int nodeBaseX = 25;
    int posXPerTier = 50;
    int nodeLineRootXOffset = 11;
    int nodeLineDependXOffset = -10;
    int LineColor = 0xFF9F9F9F;

    int NodeDrawStartX = -7;
    int NodeDrawStartY = -7;

    boolean isDependentPerk(@NotNull Identifier perk);

    boolean isAllDependentGained(PlayerEntity player, @Nullable List<Identifier> playerGainedPerk);

    default void drawDependentLine(DrawContext drawContext, int nowNodeBaseX, Vector2i nodeCenter, PerkTree tree, PerkTree.PerkNode perkNode) {
        return;
    }
}
