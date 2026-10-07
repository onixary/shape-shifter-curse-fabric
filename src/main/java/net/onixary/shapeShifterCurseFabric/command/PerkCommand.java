package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.perk.*;
import net.onixary.shapeShifterCurseFabric.player_form.utils.*;
import io.github.apace100.apoli.component.PowerHolderComponent;
import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.*;

public final class PerkCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> builder() {
        return literal("perk").requires(source -> source.hasPermissionLevel(2))
                .then(literal("list").executes(c -> execute(c, "list")))
                .then(literal("toggle_free").then(argument("enabled", BoolArgumentType.bool())
                        .executes(c -> execute(c, "toggle_free"))))
                .then(literal("unlock_all").executes(c -> execute(c, "unlock_all")))
                .then(literal("reset_all").executes(c -> execute(c, "reset_all")));
    }

    private static int execute(CommandContext<ServerCommandSource> context, String action) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();
        var component = PlayerFormComponent.COMPONENT.get(player);
        // Use the actual form, even if the old dev_command selected a preview tree.
        var treeID = component.nowForm.getPerkTreeID();
        var tree = RegPerks.getPerkTree(treeID);
        if (tree == null || tree.getAllNodes().isEmpty()) {
            source.sendError(Text.translatable("command.shape_shifter_curse.perk.empty"));
            return 0;
        }
        if (action.equals("toggle_free")) {
            boolean enabled = BoolArgumentType.getBool(context, "enabled");
            if (enabled) component.freePerkForms.add(component.nowForm.getFormID());
            else component.freePerkForms.remove(component.nowForm.getFormID());
            component.sync();
            source.sendFeedback(() -> Text.translatable("command.shape_shifter_curse.perk.free", component.nowForm.getFormID(), enabled), false);
            return 1;
        }
        var unlocked = component.formPerkMap.computeIfAbsent(treeID, ignored -> new ArrayList<>());
        if (action.equals("list")) {
            source.sendFeedback(() -> Text.translatable("command.shape_shifter_curse.perk.tree", treeID, unlocked.size(), tree.getAllNodes().size()), false);
            for (var node : tree.getAllNodes()) {
                source.sendFeedback(() -> Text.literal(unlocked.contains(node.perkID) ? "[+] " : "[ ] ")
                        .append(RegPerks.getPerkName(node.perkID)).append(" (" + node.perkID + ")"), false);
            }
            return tree.getAllNodes().size();
        }
        int changed = 0;
        if (action.equals("reset_all")) {
            changed = unlocked.size();
            component.formPerkMap.remove(treeID);
        } else {
            // Parents load first so upgraded powers replace their earlier variants.
            var pending = tree.getAllNodes();
            boolean progress = true;
            while (!pending.isEmpty() && progress) {
                progress = false;
                var iterator = pending.iterator();
                while (iterator.hasNext()) {
                    var node = iterator.next();
                    if (unlocked.contains(node.perkID)) { iterator.remove(); progress = true; continue; }
                    var perk = RegPerks.getPerk(node.perkID);
                    if (perk != null && !perk.canRepeat() && unlocked.containsAll(node.dependentPerkIDs)) {
                        unlocked.add(node.perkID);
                        perk.onGain(player, component.nowForm);
                        changed++;
                        iterator.remove(); progress = true;
                    }
                }
            }
        }
        FormUtils.reApplyPower(player);
        component.sync();
        PowerHolderComponent.KEY.sync(player);
        int count = changed;
        source.sendFeedback(() -> Text.translatable("command.shape_shifter_curse.perk." + action, treeID, count), false);
        return changed;
    }
}
