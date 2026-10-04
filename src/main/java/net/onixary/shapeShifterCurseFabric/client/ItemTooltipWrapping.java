package net.onixary.shapeShifterCurseFabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class ItemTooltipWrapping {
    private ItemTooltipWrapping() {}

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            boolean modItem = Registries.ITEM.getId(stack.getItem()).getNamespace()
                    .equals(ShapeShifterCurseFabric.MOD_ID);
            TextHandler handler = MinecraftClient.getInstance().textRenderer.getTextHandler();
            // Keep the item name intact; wrap descriptions, including our potion/tool annotations.
            for (var iterator = lines.listIterator(Math.min(1, lines.size())); iterator.hasNext();) {
                Text line = iterator.next();
                boolean modDescription = line.getContent() instanceof TranslatableTextContent translation
                        && (translation.getKey().startsWith("tooltip.shape_shifter_curse.")
                        || translation.getKey().startsWith("item.shape-shifter-curse."));
                if (modItem || modDescription) {
                    List<Text> wrapped = wrap(handler, line, StaticParams.ITEM_TOOLTIP_MAX_WIDTH);
                    iterator.remove();
                    wrapped.forEach(iterator::add);
                }
            }
        });
    }

    static List<Text> wrap(TextHandler handler, Text text, int maxWidth) {
        int width = Math.max(1, maxWidth);
        if (handler.getWidth(text) <= width && !text.getString().contains("\n")) {
            return List.of(text);
        }
        List<Text> result = new ArrayList<>();
        for (var line : handler.wrapLines(text, width, Style.EMPTY)) {
            MutableText wrapped = Text.empty();
            line.visit((style, content) -> {
                wrapped.append(Text.literal(content).setStyle(style));
                return Optional.empty();
            }, Style.EMPTY);
            result.add(wrapped);
        }
        return result;
    }
}
