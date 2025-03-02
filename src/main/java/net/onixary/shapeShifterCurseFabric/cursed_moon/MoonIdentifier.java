package net.onixary.shapeShifterCurseFabric.cursed_moon;

import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class MoonIdentifier extends Identifier {

    private final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    private final Identifier CURSED_MOON_PHASES = new Identifier(MOD_ID,"textures/environment/cursed_moon_phases.png");

    public MoonIdentifier(String id) {
        super(id);
    }

    public Identifier getIdentifier() {
        return CursedMoon.isCursedMoon()? CURSED_MOON_PHASES : MOON_PHASES;
    }

    public String getPath() {
        return getIdentifier().getPath();
    }

    public String getNamespace() {
        return getIdentifier().getNamespace();
    }

    public String toString() {
        return this.getNamespace() + ":" + this.getPath();
    }

    public boolean equals(Object o) {
        return getIdentifier().equals(o);
    }

    public int hashCode() {
        return getIdentifier().hashCode();
    }

    public int compareTo(Identifier identifier) {
        return getIdentifier().compareTo(identifier);
    }

    public String toUnderscoreSeparatedString() {
        return getIdentifier().toUnderscoreSeparatedString();
    }

}
