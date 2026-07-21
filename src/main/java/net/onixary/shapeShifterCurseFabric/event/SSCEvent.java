package net.onixary.shapeShifterCurseFabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
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
}
