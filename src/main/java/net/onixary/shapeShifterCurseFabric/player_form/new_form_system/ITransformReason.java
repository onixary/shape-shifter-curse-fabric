package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ITransformReason {
    public static interface ITransformReasonWithArg <T> extends ITransformReason {
        T getArg();

        void setArg(T arg);
    }


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

    public static <T> ITransformReasonWithArg<T> create(Identifier reasonType, ExtraFunctionInterface.TriFunction<ITransformReasonWithArg<T>, PlayerEntity, IForm, IForm> fNextForm, ExtraFunctionInterface.TriFunction<ITransformReasonWithArg<T>, PlayerEntity, IForm, IForm> fPrevForm, T arg) {
        return new ITransformReasonWithArg<T>() {
            private T storedArg = arg;

            @Override
            public T getArg() {
                return storedArg;
            }

            @Override
            public void setArg(T arg) {
                storedArg = arg;
            }

            @Override
            public Identifier getReasonType() {
                return reasonType;
            }

            @Override
            public @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
                return fNextForm.apply(this, player, nowForm);
            }

            @Override
            public @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
                return fPrevForm.apply(this, player, nowForm);
            }
        };
    }

    public static final Identifier InstinctReasonID = ShapeShifterCurseFabric.identifier("instinct");
    public static final ITransformReason Instinct = create(InstinctReasonID,
            (player, nowForm) -> {
                IFormGroup group = nowForm.getFormGroup();
                int tier = nowForm.getFormTier() + 1;
                IForm result = null;
                if (group != null) {
                    result = group.getRandomForm(tier, player.getRandom(), FormUtils.NoInstinctTFTarget.hasFlag().negate());
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
                    result = group.getRandomForm(tier, player.getRandom(), FormUtils.NoInstinctTFTarget.hasFlag().negate());
                }
                return result == null ? nowForm : result;
            }
    );
    public static final Identifier CursedMoonReasonID = ShapeShifterCurseFabric.identifier("cursed_moon");
    public static final ITransformReason CursedMoon = create(CursedMoonReasonID,
            (player, nowForm) -> {
                // TODO
                return nowForm;
            },
            (player, nowForm) -> {
                // TODO
                return nowForm;
            }
    );

    public static final Identifier ItemReasonID = ShapeShifterCurseFabric.identifier("item");
    public static final Function<ItemStack, ITransformReasonWithArg<ItemStack>> ItemReasonBuilder = (itemStack) -> create(ItemReasonID,
            (reason, player, nowForm) -> {
                // TODO
                return nowForm;
            },
            (reason, player, nowForm) -> {
                // TODO
                return nowForm;
            },
            itemStack
    );

    public static final Identifier ForceReasonID = ShapeShifterCurseFabric.identifier("force");
    public static final Function<IForm, ITransformReasonWithArg<IForm>> ForceReasonBuilder = (form) -> create(ForceReasonID,
            (reason, player, nowForm) -> {
                return reason.getArg();
            },
            (reason, player, nowForm) -> {
                return reason.getArg();
            },
            form
    );


    public Identifier getReasonType();

    default @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
        return null;
    }

    default @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
        return null;
    }
}
