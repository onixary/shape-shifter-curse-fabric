package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * 图鉴第二页（BookOfShapeShifterScreenV2_P2）INSTINCTS 列的扩展点。
 * 注册 Provider 后，该列的说明行/正文/列底贴图优先取自 Provider；
 * 未注册或对应方法返回 null 时保持原版 CodexData 行为，PROS/CONS 列不受影响。
 * 正文文本由页面侧只取一次并同时供主列与“+”详情使用（同源）。
 */
public final class CodexInstinctColumnHooks {

    private static @Nullable Provider provider;

    private CodexInstinctColumnHooks() {
    }

    public static void register(Provider provider) {
        CodexInstinctColumnHooks.provider = provider;
    }

    public static @Nullable Provider provider() {
        return provider;
    }

    public interface Provider {
        /** INSTINCTS 列说明行；null = 使用默认 CodexData。 */
        @Nullable Text instinctsDesc(PlayerEntity player);

        /** INSTINCTS 列正文（主列与“+”详情共用同一次结果）；null = 使用默认 CodexData。 */
        @Nullable Text instinctsContent(PlayerEntity player);

        /** 列底贴图（宽度随列宽，高度按原始宽高比换算，锚定列区底部）；null = 不渲染。 */
        @Nullable BottomTexture bottomTexture(PlayerEntity player);
    }

    /** 贴图及其原始像素尺寸（用于保持宽高比）。 */
    public record BottomTexture(Identifier id, int imageWidth, int imageHeight) {
    }
}
