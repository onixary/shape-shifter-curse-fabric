package net.onixary.shapeShifterCurseFabric.command;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

import java.util.function.ToIntFunction;

public class TransformCommandCheck {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 200)
    public void commandsWaitForTheWholeTransformation(TestContext test) {
        var player = test.createMockCreativeServerPlayerInWorld();
        var source = player.getCommandSource().withLevel(2);
        var manager = test.getWorld().getServer().getCommandManager();
        ToIntFunction<String> run = command -> manager.executeWithPrefix(source, "shape_shifter_curse " + command);
        var target = RegPlayerForms.BAT_1.getFormID();
        var alternate = RegPlayerForms.BAT_2.getFormID();
        test.assertTrue(run.applyAsInt("transform_to_form @s " + target) == 1, "Idle player can start transforming");

        Runnable assertBlocked = () -> {
            test.assertTrue(run.applyAsInt("set_form @s " + alternate) == 0, "Cannot interrupt the current transformation");
            test.assertTrue(run.applyAsInt("transform_to_form @s " + alternate) == 0, "Cannot restart the animation");
        };
        test.waitAndRun(2, assertBlocked);
        int middle = StaticParams.TRANSFORM_FX_DURATION_IN;
        test.waitAndRun(middle + 2, () -> {
            test.assertTrue(FormUtils.getPlayerForm(player) == RegPlayerForms.BAT_1, "Original target reached");
            assertBlocked.run();
        });
        test.waitAndRun(middle + StaticParams.TRANSFORM_FX_DURATION_OUT + 2, () -> {
            test.assertTrue(run.applyAsInt("set_form @s " + alternate) == 1, "Command unlocked after the animation");
            test.assertTrue(FormUtils.getPlayerForm(player) == RegPlayerForms.BAT_2, "New form applied");
            test.complete();
        });
    }
}
