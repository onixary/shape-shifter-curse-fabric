package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        return 1;
    }
}
