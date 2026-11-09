package net.onixary.shapeShifterCurseFabric.custom_ui;

/** ./gradlew checkAltarFuelTooltip */
public final class AltarFuelTooltipCheck {
    public static void main(String[] args) {
        int[] fuels = {0, 200, 800, 25400, 102400, 799, 4};
        String[] expected = {"0", "0.25", "1", "31.75", "128", "1", "0.01"};
        for (int i = 0; i < fuels.length; i++) {
            check(AltarCraftUI.formatMoondust(fuels[i]).equals(expected[i]), "Moondust formatting: " + fuels[i]);
        }
        check(AltarCraftUI.isOverFuelBar(84, 49), "Top-left of full bar");
        check(AltarCraftUI.isOverFuelBar(126, 51), "Unfilled right edge is hoverable");
        check(!AltarCraftUI.isOverFuelBar(83, 50), "Outside left edge");
        check(!AltarCraftUI.isOverFuelBar(127, 50), "Outside right edge");
        check(!AltarCraftUI.isOverFuelBar(90, 48), "Progress bar does not trigger fuel tooltip");
        check(!AltarCraftUI.isOverFuelBar(90, 52), "Fuel slot border does not trigger fuel tooltip");
        check(!AltarCraftUI.isOverFuelBar(90, 53), "Fuel item slot retains its own tooltip");
        System.out.println("Altar fuel tooltip checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
