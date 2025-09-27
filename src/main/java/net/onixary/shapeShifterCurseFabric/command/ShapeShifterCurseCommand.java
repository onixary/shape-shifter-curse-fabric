package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;

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
}
