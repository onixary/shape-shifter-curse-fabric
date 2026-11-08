package net.onixary.shapeShifterCurseFabric.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class RegCustomSound {
    public static final Identifier AVALI_CHATTER_ID = ShapeShifterCurseFabric.identifier("avali_chatter");
    public static final SoundEvent AVALI_CHATTER = Registry.register(
            Registries.SOUND_EVENT, AVALI_CHATTER_ID, SoundEvent.of(AVALI_CHATTER_ID));

    public static void init() {
    }
}
