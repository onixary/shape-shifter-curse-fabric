package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.CommandDispatcher;
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
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleDirectTransform;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.setFormDirectly;

public class ShapeShifterCurseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                literal("shape_shifter_curse").requires(cs -> cs.hasPermissionLevel(2))
                        .then(literal("set_form")
                                .then(argument("target", EntityArgumentType.player())
                                        .then(argument("form", FormArgumentType.form())
                                                .executes(ShapeShifterCurseCommand::setForm)
                                        )
                                )
                        )
                        .then(literal("transform_to_form")
                                .then(argument("target", EntityArgumentType.player())
                                        .then(argument("form", FormArgumentType.form())
                                                .executes(ShapeShifterCurseCommand::transformToForm)
                                        )
                                )
                        )
                        .then(literal("jump_to_next_cursed_moon")
                                .executes(ShapeShifterCurseCommand::jumpToNextCursedMoon)
                        )
                        .then(literal("adjust_feral_item_loc")
                                .then(argument("rot_center", Vec3ArgumentType.vec3())
                                        .then(argument("pos_offset", Vec3ArgumentType.vec3())
                                                .then(argument("euler_x", FloatArgumentType.floatArg())
                                                        .executes(ShapeShifterCurseCommand::adjustFeralItemLoc)
                                                )
                                        )
                                )
                        )
        );
    }

    private static int setForm(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        // set form without transform effect
        ServerPlayerEntity target = EntityArgumentType.getPlayer(commandContext, "target");
        PlayerForms form = FormArgumentType.getForm(commandContext, "form");
        ServerCommandSource serverCommandSource = commandContext.getSource();

        setFormDirectly(target, form);

        return 1;

    }

    private static int transformToForm(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        // this with transform effect
        ServerPlayerEntity target = EntityArgumentType.getPlayer(commandContext, "target");
        PlayerForms form = FormArgumentType.getForm(commandContext, "form");
        ServerCommandSource serverCommandSource = commandContext.getSource();

        handleDirectTransform(target, form, false);

        return 1;

    }

    private static int jumpToNextCursedMoon(CommandContext<ServerCommandSource> commandContext) {
        ServerWorld world = commandContext.getSource().getWorld();
        CursedMoon.jumpToNextCursedMoon(world);
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
}
