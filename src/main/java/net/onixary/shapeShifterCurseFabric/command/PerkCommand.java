package net.onixary.shapeShifterCurseFabric.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.perk.*;
import net.onixary.shapeShifterCurseFabric.player_form.utils.*;
import io.github.apace100.apoli.component.PowerHolderComponent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        // nowPerkTree就是当前的PerkTree devCommand和名字一样 是调试命令 实际上就是修改当前的PerkTree而不是预览 根本就没做预览这个功能
        Identifier treeID = component.nowPerkTree;
        PerkTree tree = RegPerks.getPerkTree(treeID);
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
        List<Identifier> unlocked = component.formPerkMap.computeIfAbsent(treeID, ignored -> new ArrayList<>());
        if (action.equals("list")) {
            source.sendFeedback(() -> Text.translatable("command.shape_shifter_curse.perk.tree", treeID, unlocked.size(), tree.getAllNodes().size()), false);
            for (PerkTree.PerkNode node : tree.getAllNodes()) {
                source.sendFeedback(() -> Text.literal(unlocked.contains(node.perkID) ? "[+] " : "[ ] ")
                        .append(RegPerks.getPerkName(node.perkID)).append(" (" + node.perkID + ")"), false);
            }
            return tree.getAllNodes().size();
        }
        int changed = 0;
        if (action.equals("reset_all")) {
            changed = unlocked.size();
            component.formPerkMap.remove(treeID);
        }
        if (action.equals("unlock_all")) {
            List<PerkTree.PerkNode> pending = tree.getAllNodes();
            boolean progress = true;
            while (!pending.isEmpty() && progress) {
                progress = false;
                Iterator<PerkTree.PerkNode> iterator = pending.iterator();
                while (iterator.hasNext()) {
                    PerkTree.PerkNode node = iterator.next();
                    if (unlocked.contains(node.perkID)) {
                        iterator.remove();
                        progress = true;
                        continue;
                    }
                    IPerk perk = RegPerks.getPerk(node.perkID);
                    if (perk != null && !perk.canRepeat() && unlocked.containsAll(node.dependentPerkIDs)) {
                        unlocked.add(node.perkID);
                        perk.onGain(player, component.nowForm);
                        changed++;
                        iterator.remove();
                        progress = true;
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
