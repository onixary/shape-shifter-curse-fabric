package net.onixary.shapeShifterCurseFabric.networking;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModPackets {
    public static final Identifier CHANNEL = new Identifier(ShapeShifterCurseFabric.MOD_ID, "main");

    public static final Identifier VALIDATE_START_BOOK_BUTTON = new Identifier(ShapeShifterCurseFabric.MOD_ID, "validate_start_book_button");

    // 新增服务端到客户端的附件同步包
    // New server-to-client attachment sync packet
    public static final Identifier SYNC_EFFECT_ATTACHMENT = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_effect_attachment");

    public static final Identifier TRANSFORM_EFFECT_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "transform_effect");

    public static final Identifier INSTINCT_THRESHOLD_EFFECT_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "instinct_threshold_effect");

    public static final Identifier SYNC_CURSED_MOON_DATA = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_cursed_moon_data");
}
