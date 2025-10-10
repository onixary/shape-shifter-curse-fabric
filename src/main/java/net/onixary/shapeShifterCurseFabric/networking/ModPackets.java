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

    // Bat attach power sync packet
    public static final Identifier SYNC_BAT_ATTACH_STATE = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_bat_attach_state");
    public static final Identifier JUMP_DETACH_REQUEST_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "jump_detach_request");
    public static final Identifier SYNC_OTHER_PLAYER_BAT_ATTACH_STATE = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_other_player_bat_attach_state");

    // jump_event packets
    public static final Identifier JUMP_EVENT_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "jump_event");

    public static final Identifier SPRINTING_TO_SNEAKING_EVENT_ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sprinting_to_sneaking_event");

    public static final Identifier SYNC_FORCE_SNEAK_STATE = new Identifier(ShapeShifterCurseFabric.MOD_ID, "sync_force_sneak_state");

    public static final Identifier UPDATE_DYNAMIC_FORM = new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_dynamic_form");
    public static final Identifier REMOVE_DYNAMIC_FORM_EXCEPT = new Identifier(ShapeShifterCurseFabric.MOD_ID, "remove_dynamic_form_except");

    public static final Identifier LOGIN_PACKET = new Identifier(ShapeShifterCurseFabric.MOD_ID, "login_packet");  // 我暂时没找到玩家进入服务去时的Hook，所以暂时由服务器询问来代替
    public static final Identifier UPDATE_CUSTOM_SETTING = new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_custom_setting");

    public static final Identifier UPDATE_PATRON_LEVEL = new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_patron_level");
    public static final Identifier OPEN_PATRON_FORM_SELECT_MENU = new Identifier(ShapeShifterCurseFabric.MOD_ID, "open_patron_form_select_menu");
    public static final Identifier SET_PATRON_FORM = new Identifier(ShapeShifterCurseFabric.MOD_ID, "set_patron_form");
}
