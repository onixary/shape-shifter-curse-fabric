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

    // 添加形态变化同步包
    public static final Identifier SYNC_FORM_CHANGE = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_form_change");

    // 添加变身状态同步包
    public static final Identifier SYNC_TRANSFORM_STATE = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_transform_state");

    // 添加Overlay效果相关的网络包
    public static final Identifier UPDATE_OVERLAY_EFFECT = new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_overlay_effect");
    public static final Identifier UPDATE_OVERLAY_FADE_EFFECT = new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_overlay_fade_effect");
    public static final Identifier TRANSFORM_COMPLETE_EFFECT = new Identifier(ShapeShifterCurseFabric.MOD_ID, "transform_complete_effect");
    public static final Identifier RESET_FIRST_PERSON = new Identifier(ShapeShifterCurseFabric.MOD_ID, "reset_first_person");
}
