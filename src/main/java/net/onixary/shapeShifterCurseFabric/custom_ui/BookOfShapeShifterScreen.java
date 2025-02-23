package net.onixary.shapeShifterCurseFabric.custom_ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.onixary.shapeShifterCurseFabric.custom_ui.util.ScaledLabelComponent;

public class BookOfShapeShifterScreen extends BaseOwoScreen<FlowLayout> {
    // player
    public PlayerEntity currentPlayer;
    // font scale
    private float textScale = 0.75f;
    // layout
    private FlowLayout leftPage;
    private FlowLayout rightPage1;
    private FlowLayout rightPage2;
    private FlowLayout bookArea;
    private FlowLayout rightArea;
    private boolean showingPage1 = true;


    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    private int getScaledWidth(int width) {
        return (int) (width / textScale);
    }

    @Override
    protected void build(FlowLayout root) {
        // UI base resolution is 320x240
        root.surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        /*root.child(
                Containers.horizontalFlow(Sizing.fixed(100), Sizing.fill(95))
                        .surface(Surface.DARK_PANEL)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .verticalAlignment(VerticalAlignment.TOP)
        );*/

        bookArea = Containers.horizontalFlow(Sizing.fixed(300), Sizing.fixed(220));
        root.child(bookArea
                .surface(Surface.DARK_PANEL)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER));

        // Left page - vertical flow with three modules
        leftPage = Containers.verticalFlow(Sizing.fixed(120), Sizing.fixed(210));
        bookArea.child(leftPage
                .surface(Surface.PANEL)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(Insets.of(5))
                .margins(Insets.of(5, 5, 0, 5)));


        // First module: a placeholder for the player doll
        leftPage.child(Components.entity(Sizing.fixed(80), currentPlayer)
                .lookAtCursor(false)
                .allowMouseRotation(true)
                .transform(matrices -> {
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentPlayer.getYaw() + 45));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-35f));
                })
                .scaleToFit(true));

        // Second module: text module
        FlowLayout titleLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(50));
        leftPage.child(titleLayout
                .surface(Surface.PANEL)
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(Insets.of(5)));

        titleLayout.child(new ScaledLabelComponent(Text.literal("整体而言，如果设法隐藏掉你身上的那些异常之处的话，\n其他人会认为你是一个有点奇怪的人类"), textScale).maxWidth(getScaledWidth(100)));

        FlowLayout statusLayout = Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(70));
        leftPage.child(statusLayout
                .surface(Surface.PANEL)
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(Insets.of(5)));
        // Third module: text module
        statusLayout.child(new ScaledLabelComponent(Text.literal("你身上没有什么特别的状态"), textScale).maxWidth(getScaledWidth(100)));

        rightArea = Containers.verticalFlow(Sizing.fixed(170), Sizing.fixed(210));
        bookArea.child(rightArea
                .surface(Surface.PANEL)
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(Insets.of(5))
                .margins(Insets.of(5, 5, 5, 0)));
        /*
        // Right page 1: single text module
        rightPage1 = Containers.verticalFlow(Sizing.fixed(150), Sizing.fill(100))
                .child(Components.label(Text.literal("Page 1 - single text module")));
        bookArea.child(rightPage1)
                .surface(Surface.BLANK)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(Insets.of(10));

        // Right page 2: vertical flow => first child is a horizontal flow with two text modules
        // second child is another text module placed below
        rightPage2 = Containers.verticalFlow(Sizing.fixed(150), Sizing.fill(100));
        bookArea.child(rightPage2)
                .surface(Surface.BLANK)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(Insets.of(10));

        FlowLayout rightPage2Top = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(30));
        rightPage2Top.child(Components.label(Text.literal("Page 2 - Module A")));
        rightPage2Top.child(Components.label(Text.literal("Page 2 - Module B")));
        rightPage2.child(rightPage2Top);
        rightPage2.child(Components.label(Text.literal("Page 2 - Module C (below)")));

        // A button to toggle between right pages
        FlowLayout toggleContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());
        toggleContainer.child(Components.button(Text.literal("Toggle Page"), b -> {
            showingPage1 = !showingPage1;
            rebuildRightPage(root);
        }));
        // Put the toggle button in a small container on the right, under or above the pages
        // Adjust the alignment/positioning as desired
        rightArea.child(toggleContainer);*/
    }

    private void rebuildRightPage(FlowLayout root) {
        // Remove both pages
        bookArea.removeChild(rightPage1);
        bookArea.removeChild(rightPage2);
        // Add the page currently showing
        if (showingPage1) {
            bookArea.child(rightPage1);
        } else {
            bookArea.child(rightPage2);
        }
    }

    @Override
    public boolean shouldPause() {
        return false; // 核心控制
    }
}
