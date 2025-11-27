package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import net.onixary.shapeShifterCurseFabric.minion.mobs.WolfMinion;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleDirectTransform;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.setFormDirectly;

public class ShapeShifterCurseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                literal("shape_shifter_curse")
                        .then(literal("set_form").requires(cs -> cs.hasPermissionLevel(2))
                                .then(argument("target", EntityArgumentType.player())
                                        .then(argument("form", FormArgumentType.form())
                                                .executes(ShapeShifterCurseCommand::setForm)
                                        )
                                )
                        )
                        .then(literal("transform_to_form").requires(cs -> cs.hasPermissionLevel(2))
                                .then(argument("target", EntityArgumentType.player())
                                        .then(argument("form", FormArgumentType.form())
                                                .executes(ShapeShifterCurseCommand::transformToForm)
                                        )
                                )
                        )
                        .then(literal("set_custom_form").requires(cs -> cs.hasPermissionLevel(2))
                                .then(argument("target", EntityArgumentType.player())
                                        .then(argument("form", CustomFormArgumentType.form())
                                                .executes(ShapeShifterCurseCommand::setCustomForm)
                                        )
                                )
                        )
                        .then(literal("transform_to_custom_form").requires(cs -> cs.hasPermissionLevel(2))
                                .then(argument("target", EntityArgumentType.player())
                                        .then(argument("form", CustomFormArgumentType.form())
                                                .executes(ShapeShifterCurseCommand::transformToCustomForm)
                                        )
                                )
                        )
                        .then(literal("jump_to_next_cursed_moon").requires(cs -> cs.hasPermissionLevel(2))
                                .executes(ShapeShifterCurseCommand::jumpToNextCursedMoon)
                        )
                        .then(literal("world_time").requires(cs -> cs.hasPermissionLevel(2))
                                .then(literal("set").then(argument("time", IntegerArgumentType.integer())
                                        .executes(ShapeShifterCurseCommand::setWorldTime))
                                )
                                .then(literal("add").then(argument("time", IntegerArgumentType.integer())
                                        .executes(ShapeShifterCurseCommand::addWorldTime))
                                )
                        )
                        .then(literal("adjust_feral_item_loc").requires(cs -> cs.hasPermissionLevel(2))
                                .then(argument("rot_center", Vec3ArgumentType.vec3())
                                        .then(argument("pos_offset", Vec3ArgumentType.vec3())
                                                .then(argument("euler_x", FloatArgumentType.floatArg())
                                                        .executes(ShapeShifterCurseCommand::adjustFeralItemLoc)
                                                )
                                        )
                                )
                        )
                        .then(literal("keep_original_skin").requires(cs -> cs.hasPermissionLevel(0))
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(ShapeShifterCurseCommand::setPlayerSkin)
                                )
                        )
                        .then(literal("set_form_color").requires(cs -> cs.hasPermissionLevel(0))
                                .executes(ShapeShifterCurseCommand::logFormColorSetting)
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(ShapeShifterCurseCommand::setFormColorEnable)
                                        .then(argument("primaryColorRGBA", StringArgumentType.string())
                                                .then(argument("accentColor1RGBA", StringArgumentType.string())
                                                        .then(argument("accentColor2RGBA", StringArgumentType.string())
                                                                .then(argument("eyeColor", StringArgumentType.string())
                                                                        .then(argument("primaryGreyReverse", BoolArgumentType.bool())
                                                                                .then(argument("accent1GreyReverse", BoolArgumentType.bool())
                                                                                        .then(argument("accent2GreyReverse", BoolArgumentType.bool())
                                                                                                .executes(ShapeShifterCurseCommand::setFormColor)
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("dev_command").requires(cs -> cs.hasPermissionLevel(2))
                                .executes(ShapeShifterCurseCommand::devCommand)
                        )
                        .then(literal("patron_info").requires(cs -> cs.hasPermissionLevel(0))
                                .executes(ShapeShifterCurseCommand::logPatronInfo)
                        )
        );
    }

    private static int setForm(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        // set form without transform effect
        ServerPlayerEntity target = EntityArgumentType.getPlayer(commandContext, "target");
        PlayerFormBase form = FormArgumentType.getForm(commandContext, "form");
        ServerCommandSource serverCommandSource = commandContext.getSource();
        if (form == null) {
            commandContext.getSource().sendError(Text.literal("Invalid Form Id!"));
            return 0;
        }
        try {
            setFormDirectly(target, form);
        }
        catch (Exception e){
            // 调试时在此打断点
            ShapeShifterCurseFabric.LOGGER.error("Exception when set form", e);
            throw e;
        }

        return 1;

    }

    private static int transformToForm(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        // this with transform effect
        ServerPlayerEntity target = EntityArgumentType.getPlayer(commandContext, "target");
        PlayerFormBase form = FormArgumentType.getForm(commandContext, "form");
        ServerCommandSource serverCommandSource = commandContext.getSource();
        if (form == null) {
            commandContext.getSource().sendError(Text.literal("Invalid Form Id!"));
            return 0;
        }
        handleDirectTransform(target, form, false);

        return 1;

    }

    private static int setCustomForm(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        // set form without transform effect
        ServerPlayerEntity target = EntityArgumentType.getPlayer(commandContext, "target");
        PlayerFormBase form = CustomFormArgumentType.getForm(commandContext, "form");
        ServerCommandSource serverCommandSource = commandContext.getSource();
        if (form == null) {
            commandContext.getSource().sendError(Text.literal("Invalid Form Id!"));
            return 0;
        }
        try {
            setFormDirectly(target, form);
        }
        catch (Exception e){
            // 调试时在此打断点
            ShapeShifterCurseFabric.LOGGER.error("Exception when set custom form", e);
            throw e;
        }

        return 1;

    }

    private static int transformToCustomForm(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        // this with transform effect
        ServerPlayerEntity target = EntityArgumentType.getPlayer(commandContext, "target");
        PlayerFormBase form = CustomFormArgumentType.getForm(commandContext, "form");
        ServerCommandSource serverCommandSource = commandContext.getSource();
        if (form == null) {
            commandContext.getSource().sendError(Text.literal("Invalid Form Id!"));
            return 0;
        }
        handleDirectTransform(target, form, false);

        return 1;

    }

    private static int jumpToNextCursedMoon(CommandContext<ServerCommandSource> commandContext) {
        ServerWorld world = commandContext.getSource().getWorld();
        CursedMoon.forceTriggerCursedMoon(world);
        ServerCommandSource serverCommandSource = commandContext.getSource();
        serverCommandSource.sendFeedback(() -> Text.literal("Set cursed moon to next night!"), true);
        return 1;
    }

    private static int adjustFeralItemLoc(CommandContext<ServerCommandSource> commandContext) {
        Vec3d rotCenter = Vec3ArgumentType.getVec3(commandContext, "rot_center");
        Vec3d posOffset = Vec3ArgumentType.getVec3(commandContext, "pos_offset");
        float eulerX = FloatArgumentType.getFloat(commandContext, "euler_x");
        ShapeShifterCurseFabric.feralItemCenter = rotCenter;
        ShapeShifterCurseFabric.feralItemPosOffset = posOffset;
        ShapeShifterCurseFabric.feralItemEulerX = eulerX;
        ServerCommandSource serverCommandSource = commandContext.getSource();
        serverCommandSource.sendFeedback(() -> Text.literal("Location adjusted! Center : " + rotCenter + " Offset: " + posOffset + " RotationX : " + eulerX), true);
        return 1;
    }

    private static int setPlayerSkin(CommandContext<ServerCommandSource> commandContext) {
        try {
            ServerPlayerEntity player = commandContext.getSource().getPlayer();
            boolean newSetting = BoolArgumentType.getBool(commandContext, "value");
            RegPlayerSkinComponent.SKIN_SETTINGS.get(player).setKeepOriginalSkin(newSetting);
            RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);
            String message = newSetting
                    ? "Successfully set to use your original skin!"
                    : "Successfully set to use built-in skin!";
            player.sendMessage(Text.literal(message), false);

            return 1;
        } catch (Exception e) {
            // 处理其他可能的错误
            commandContext.getSource().sendError(Text.literal("Error when change player skin: " + e.getMessage()));
            ShapeShifterCurseFabric.LOGGER.error("Error when change player skin: ", e);
            return 0;
        }
    }

    private static String getColorHexFormABGR(int color) {
        int ARGB = FormTextureUtils.ABGR2ARGB(color);
        String String = Integer.toHexString(ARGB);
        if (String.length() < 8) {
            return "00000000".substring(0, 8 - String.length()) + String;
        }
        return String;
    }

    private static int logFormColorSetting(CommandContext<ServerCommandSource> commandContext) {
        try {
            ServerPlayerEntity player = commandContext.getSource().getPlayer();
            if (player == null) {
                commandContext.getSource().sendError(Text.literal("Must be a player!"));
                return 0;
            }
            String message = "Form color setting: \n";
            message += "Enable: " + RegPlayerSkinComponent.SKIN_SETTINGS.get(player).isEnableFormColor() + "\n";
            message += "Primary Color ARGB: " + getColorHexFormABGR(RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getPrimaryColor()) + "\n";
            message += "Accent Color 1 ARGB: " + getColorHexFormABGR(RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getAccentColor1()) + "\n";
            message += "Accent Color 2 ARGB: " + getColorHexFormABGR(RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getAccentColor2()) + "\n";
            message += "Eye Color A ARGB: " + getColorHexFormABGR(RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getEyeColorA()) + "\n";
            message += "Eye Color B ARGB: " + getColorHexFormABGR(RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getEyeColorB()) + "\n";
            message += "Primary Grey Reverse: " + RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getPrimaryGreyReverse() + "\n";
            message += "Accent 1 Grey Reverse: " + RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getAccent1GreyReverse() + "\n";
            message += "Accent 2 Grey Reverse: " + RegPlayerSkinComponent.SKIN_SETTINGS.get(player).getFormColor().getAccent2GreyReverse() + "\n";
            player.sendMessage(Text.literal(message), false);
            return 1;
        }
        catch (Exception e) {
            // 处理其他可能的错误
            commandContext.getSource().sendError(Text.literal("Error when log player form color: " + e.getMessage()));
            ShapeShifterCurseFabric.LOGGER.error("Error when log player form color: ", e);
            return 0;
        }
    }

    private static int setFormColorEnable(CommandContext<ServerCommandSource> commandContext) {
        try {
            ServerPlayerEntity player = commandContext.getSource().getPlayer();
            if (player == null) {
                commandContext.getSource().sendError(Text.literal("Must be a player!"));
                return 0;
            }
            boolean enable = BoolArgumentType.getBool(commandContext, "enable");
            RegPlayerSkinComponent.SKIN_SETTINGS.get(player).setEnableFormColor(enable);
            RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);
            return 1;
        } catch (Exception e) {
            // 处理其他可能的错误
            commandContext.getSource().sendError(Text.literal("Error when change player form color: " + e.getMessage()));
            ShapeShifterCurseFabric.LOGGER.error("Error when change player form color: ", e);
            return 0;
        }
    }

    private static int setFormColor(CommandContext<ServerCommandSource> commandContext) {
        try {
            ServerPlayerEntity player = commandContext.getSource().getPlayer();
            if (player == null) {
                commandContext.getSource().sendError(Text.literal("Must be a player!"));
                return 0;
            }
            boolean enable = BoolArgumentType.getBool(commandContext, "enable");
            String primaryColorRGBA = StringArgumentType.getString(commandContext, "primaryColorRGBA");
            String accentColor1RGBA = StringArgumentType.getString(commandContext, "accentColor1RGBA");
            String accentColor2RGBA = StringArgumentType.getString(commandContext, "accentColor2RGBA");
            String eyeColorA = StringArgumentType.getString(commandContext, "eyeColorA");
            String eyeColorB = StringArgumentType.getString(commandContext, "eyeColorB");
            if (!RegPlayerSkinComponent.SKIN_SETTINGS.get(player).setFormColor(primaryColorRGBA, accentColor1RGBA, accentColor2RGBA, eyeColorA, eyeColorB, BoolArgumentType.getBool(commandContext, "primaryGreyReverse"), BoolArgumentType.getBool(commandContext, "accent1GreyReverse"), BoolArgumentType.getBool(commandContext, "accent2GreyReverse"))) {
                commandContext.getSource().sendError(Text.literal("Invalid color format!"));
                return 0;
            }
            RegPlayerSkinComponent.SKIN_SETTINGS.get(player).setEnableFormColor(enable);
            RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);
            return 1;
        } catch (Exception e) {
            // 处理其他可能的错误
            commandContext.getSource().sendError(Text.literal("Error when change player form color: " + e.getMessage()));
            ShapeShifterCurseFabric.LOGGER.error("Error when change player form color: ", e);
            return 0;
        }
    }

    private static int logPatronInfo(CommandContext<ServerCommandSource> commandContext) {
        if (!PatronUtils.EnablePatronFeature) {
            commandContext.getSource().sendError(Text.literal("Patron feature is disabled!"));
            return 0;
        }
        try {
            ServerPlayerEntity player = commandContext.getSource().getPlayer();
            if (player == null) {
                commandContext.getSource().sendError(Text.literal("Must be a player!"));
                return 0;
            }
            StringBuilder message = new StringBuilder("Patron Info:\n");
            message.append("UUID: ").append(player.getUuid()).append("\n");
            message.append("Patron Level: ").append(PatronUtils.PatronLevels.getOrDefault(player.getUuid(), 0)).append("\n");
            message.append("Available FormID: ");
            for (Identifier formID : getAvailableForms(player)) {
                message.append(formID.toString()).append(" ");
            }
            message.append("\n");
            player.sendMessage(Text.literal(message.toString()), false);
        } catch (Exception e) {
            // 处理其他可能的错误
            commandContext.getSource().sendError(Text.literal("Error when log player patron info: " + e.getMessage()));
            ShapeShifterCurseFabric.LOGGER.error("Error when log player patron info: ", e);
        }
        return 1;
    }

    // 仅用于logPatronInfo 使用
    private static List<Identifier> getAvailableForms(ServerPlayerEntity player) {
        List<Identifier> availableForms = new ArrayList<>();
        for (Identifier formID : RegPlayerForms.dynamicPlayerForms) {
            PlayerFormBase form = RegPlayerForms.getPlayerForm(formID);
            if (form instanceof PlayerFormDynamic pfd) {
                if (pfd.IsPatronForm && pfd.IsPlayerCanUse(player)) {
                    if (!availableForms.contains(formID)) {
                        availableForms.add(formID);
                    }
                }
            }
        }
        return availableForms;
    }

    private static int setWorldTime(CommandContext<ServerCommandSource> commandContext) {
        ServerWorld world = commandContext.getSource().getWorld();
        world.setTimeOfDay(IntegerArgumentType.getInteger(commandContext, "time"));
        commandContext.getSource().sendFeedback(() -> {return Text.literal("World time set to " + commandContext.getSource().getWorld().getTimeOfDay());}, false);
        return 1;
    }

    private static int addWorldTime(CommandContext<ServerCommandSource> commandContext) {
        ServerWorld world = commandContext.getSource().getWorld();
        long TargetTime = world.getTimeOfDay() + IntegerArgumentType.getInteger(commandContext, "time");
        world.setTimeOfDay(TargetTime);
        commandContext.getSource().sendFeedback(() -> {return Text.literal("World time set to " + TargetTime);}, false);
        return 1;
    }

    private static int devCommand(CommandContext<ServerCommandSource> commandContext) {
        ServerPlayerEntity player = commandContext.getSource().getPlayer();
        ServerWorld world = commandContext.getSource().getWorld();
        if (player == null) {
            return 1;
        }
        try {
            WolfMinion wolfMinion = MinionRegister.SpawnMinion(MinionRegister.WOLF_MINION, world, player.getBlockPos(), player);
            wolfMinion.setMinionLevel(3);
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error when spawn minion: ", e);
            return 0;
        }
        return 0;
    }
}
