package net.onixary.shapeShifterCurseFabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SSCEvent {
    // 挂在FormUtils._loadForm上的 oldForm有很大可能性和newForm相等
    // 而且由于oldForm从Component上读取 虽然大概率不会为null 但是还是需要判断一下
    @FunctionalInterface
    public static interface FormChange {
        void onFormChange(@NotNull PlayerEntity player, @Nullable IForm oldForm, @NotNull IForm newForm);
    }

    @FunctionalInterface
    public static interface TransformManagerSetForm {
        @NotNull IForm onSetForm(@NotNull PlayerEntity player, @Nullable IForm oldForm, @NotNull IForm newForm, @NotNull IForm finalForm);
    }

    @FunctionalInterface
    public static interface NormalPlayerEvent {
        void onEvent(@NotNull PlayerEntity player);
    }

    public static final Event<FormChange> FORM_CHANGE_START = EventFactory.createArrayBacked(FormChange.class, callbacks -> (player, oldForm, newForm) -> {
        for (FormChange callback : callbacks) {
            callback.onFormChange(player, oldForm, newForm);
        }
    });

    public static final Event<FormChange> FORM_CHANGE_END = EventFactory.createArrayBacked(FormChange.class, callbacks -> (player, oldForm, newForm) -> {
        for (FormChange callback : callbacks) {
            callback.onFormChange(player, oldForm, newForm);
        }
    });

    // 最后一个参数没用 但是Event注册需要和对应Event参数一致 "_"得java22才能用
    public static final Event<TransformManagerSetForm> TRANSFORM_MANAGER_SET_FORM = EventFactory.createArrayBacked(TransformManagerSetForm.class, callbacks -> (player, oldForm, newForm, _form) -> {
        IForm finalForm = newForm;
        for (TransformManagerSetForm callback : callbacks) {
            finalForm = callback.onSetForm(player, oldForm, newForm, finalForm);
            // 仅测试环境下可用
            assert finalForm != null : "TRANSFORM_MANAGER_SET_FORM: finalForm is null";
        }
        return finalForm;
    });

    public static final Event<NormalPlayerEvent> CURSED_MOON_BEGIN = EventFactory.createArrayBacked(NormalPlayerEvent.class, callbacks -> (player) -> {
        for (NormalPlayerEvent callback : callbacks) {
            callback.onEvent(player);
        }
    });

    public static final Event<NormalPlayerEvent> CURSED_MOON_END = EventFactory.createArrayBacked(NormalPlayerEvent.class, callbacks -> (player) -> {
        for (NormalPlayerEvent callback : callbacks) {
            callback.onEvent(player);
        }
    });
}
