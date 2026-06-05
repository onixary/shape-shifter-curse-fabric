package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public interface ITransformReason {
    public static ITransformReason create(Identifier reasonType, BiFunction<PlayerEntity, IForm, IForm> fNextForm, BiFunction<PlayerEntity, IForm, IForm> fPrevForm) {
        return new ITransformReason() {
            @Override
            public Identifier getReasonType() {
                return reasonType;
            }

            @Override
            public @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
                return fNextForm.apply(player, nowForm);
            }

            @Override
            public @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
                return fPrevForm.apply(player, nowForm);
            }
        };
    }

    public static final Identifier InstinctID = ShapeShifterCurseFabric.identifier("instinct");
    public static final ITransformReason Instinct = create(InstinctID,
            (player, nowForm) -> {
                IFormGroup group = nowForm.getFormGroup();
                int tier = nowForm.getFormTier() + 1;
                IForm result = null;
                if (group != null) {
                    result = group.getRandomForm(tier, player.getRandom());
                }
                return result == null ? nowForm : result;
            },
            (player, nowForm) -> {
                IForm prevForm = FormUtils.getPrevForm(player);
                int tier = nowForm.getFormTier() - 1;
                if (prevForm != null && prevForm.getFormTier() == tier) {
                    return prevForm;
                }
                IFormGroup group = nowForm.getFormGroup();
                IForm result = null;
                if (group != null) {
                    result = group.getRandomForm(tier, player.getRandom());
                }
                return result == null ? nowForm : result;
            }
    );

    public Identifier getReasonType();

    default @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
        return null;
    }

    default @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
        return null;
    }
}
