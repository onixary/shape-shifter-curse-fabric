package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.utils.ExtraFunctionInterface;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
                if (result == null && tier == 0) {
                    result = RegPlayerForms.ORIGINAL_SHIFTER;
                }
                return result == null ? nowForm : result;
            }
    );
    public static final Identifier CursedMoonReasonID = ShapeShifterCurseFabric.identifier("cursed_moon");
    public static final ITransformReason CursedMoon = create(CursedMoonReasonID,
            (player, nowForm) -> {
                if (FormUtils.NoCursedMoonEffect.hasFlag(nowForm)) {
                    return nowForm;
                }
                if (RegPlayerForms.ORIGINAL_SHIFTER.isEquals(nowForm)) {
                    List<IForm> formList = FormUtils.getFormByCondition(FormUtils.StarterForm.hasFlag());
                    if (!formList.isEmpty()) {
                        return formList.get(player.getRandom().nextInt(formList.size()));
                    } else {
                        return nowForm;
                    }
                }
                int tier = FormUtils.CursedMoonFinalForm.hasFlag(nowForm) ? 1 : nowForm.getFormTier() + 1;
                IFormGroup group = nowForm.getFormGroup();
                IForm result = null;
                if (group != null) {
                    result = group.getRandomForm(tier, player.getRandom(), FormUtils.NoCursedMoonTFTarget.hasFlag().negate());
                }
                return result == null ? nowForm : result;
            },
            (player, nowForm) -> {
                if (FormUtils.NoCursedMoonEffect.hasFlag(nowForm)) {
                    return nowForm;
                }
                PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
                if (component.BeforeCursedMoonAppliedForm != null && component.AfterCursedMoonAppliedForm != null && nowForm.isEquals(component.AfterCursedMoonAppliedForm)) {
                    return component.BeforeCursedMoonAppliedForm;
                }
                return nowForm;
            }
    );

    public static final Identifier ItemReasonID = ShapeShifterCurseFabric.identifier("item");
    public static final Function<ItemStack, ITransformReasonWithArg<ItemStack>> ItemReasonBuilder = (itemStack) -> create(ItemReasonID,
            (reason, player, nowForm) -> {
                Item item = itemStack.getItem();
                int Tier = nowForm.getFormTier() + 1;
                IFormGroup group = nowForm.getFormGroup();
                IForm result = null;
                if (RegCustomItem.POWERFUL_CATALYST.equals(item) && group != null) {
                    result = group.getRandomForm(Tier, player.getRandom(), FormUtils.FinalForm.hasFlag());
                }
                return result == null ? nowForm : result;
            },
            (reason, player, nowForm) -> {
                Item item = itemStack.getItem();
                boolean canNotAffect = true;
                if (!FormUtils.NoAnyInhibitor.hasFlag(nowForm) && (!FormUtils.NoInhibitor.hasFlag(nowForm) || RegCustomItem.POWERFUL_INHIBITOR.equals(item))) {
                    int Tier = nowForm.getFormTier() - 1;
                    IForm prevForm = FormUtils.getPrevForm(player);
                    if (prevForm != null && prevForm.getFormTier() == Tier) {
                        return prevForm;
                    }
                    IFormGroup group = nowForm.getFormGroup();
                    IForm result = null;
                    if (group != null) {
                        result = group.getRandomForm(Tier, player.getRandom(), null);
                    }
                    if (result == null && Tier == 0) {
                        result = RegPlayerForms.ORIGINAL_SHIFTER;
                    }
                    return result == null ? nowForm : result;
                }
                return nowForm;
            },
            itemStack
    );

    public static final Identifier ForceReasonID = ShapeShifterCurseFabric.identifier("force");
    public static final Function<IForm, ITransformReasonWithArg<IForm>> ForceReasonBuilder = (form) -> create(ForceReasonID,
            (reason, player, nowForm) -> {
                return reason.getArg() == null ? nowForm : reason.getArg();
            },
            (reason, player, nowForm) -> {
                return reason.getArg() == null ? nowForm : reason.getArg();
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
