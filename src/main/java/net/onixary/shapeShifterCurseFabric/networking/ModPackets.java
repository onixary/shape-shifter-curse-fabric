package net.onixary.shapeShifterCurseFabric.networking;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModPackets {
    public static final Identifier CHANNEL = new Identifier(ShapeShifterCurseFabric.MOD_ID, "main");

    public static final Identifier VALIDATE_START_BOOK_BUTTON = new Identifier(ShapeShifterCurseFabric.MOD_ID, "validate_start_book_button");

    // 新增服务端到客户端的附件同步包
    public static final Identifier SYNC_EFFECT_ATTACHMENT = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_effect_attachment");
}
