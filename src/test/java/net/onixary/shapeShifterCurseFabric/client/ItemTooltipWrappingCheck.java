package net.onixary.shapeShifterCurseFabric.client;

import net.minecraft.client.font.TextHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

/** ./gradlew checkTooltipWrapping; fixed glyph widths avoid requiring a running client. */
public final class ItemTooltipWrappingCheck {
    public static void main(String[] args) {
        TextHandler handler = new TextHandler((codePoint, style) -> 1);
        Text description = Text.literal("abcdef").formatted(Formatting.YELLOW)
                .append(Text.literal("ghijkl").formatted(Formatting.RED, Formatting.BOLD));
        var lines = ItemTooltipWrapping.wrap(handler, description, 4);
        check(lines.size() == 3, "Long text should wrap at the requested width");
        check(lines.stream().map(Text::getString).reduce("", String::concat).equals("abcdefghijkl"), "Do not lose characters");
        StringBuilder yellow = new StringBuilder();
        StringBuilder red = new StringBuilder();
        for (Text line : lines) {
            check(handler.getWidth(line) <= 4, "Each line must fit");
            line.visit((style, content) -> {
                if (content.isEmpty()) return Optional.empty();
                if (style.getColor().equals(Style.EMPTY.withColor(Formatting.YELLOW).getColor())) {
                    yellow.append(content);
                } else {
                    check(style.getColor().equals(Style.EMPTY.withColor(Formatting.RED).getColor()) && style.isBold(), "Preserve nested styles");
                    red.append(content);
                }
                return Optional.empty();
            }, Style.EMPTY);
        }
        check(yellow.toString().equals("abcdef") && red.toString().equals("ghijkl"), "Keep styles on their original spans");
        var explicit = ItemTooltipWrapping.wrap(handler, Text.literal("one\ntwo"), 220);
        check(explicit.size() == 2 && explicit.get(1).getString().equals("two"), "Honor explicit line breaks");
        Text shortLine = Text.literal("short");
        check(ItemTooltipWrapping.wrap(handler, shortLine, 220).get(0) == shortLine, "Retain short lines");
        check(ItemTooltipWrapping.wrap(handler, Text.empty(), 220).size() == 1, "Retain blank separators");
        System.out.println("Tooltip wrapping checks passed.");
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
