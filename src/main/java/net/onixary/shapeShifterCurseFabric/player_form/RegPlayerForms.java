package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.forms.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.onixary.shapeShifterCurseFabric.player_form.NormalForm.NORMAL_SCALE_FUNC_BUILDER;
import static net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils.*;

public class RegPlayerForms {
    public static List<Identifier> dynamicPlayerForms = new ArrayList<>();
    public static List<Identifier> dynamicPlayerFormGroups = new ArrayList<>();
    public static LinkedHashMap<Identifier, IForm> playerForms = new LinkedHashMap<>();
    public static LinkedHashMap<Identifier, IFormGroup> playerFormGroups = new LinkedHashMap<>();

    public static String PatronNameSpace = "ssc-patron";  // 在更新数据包时保留

    // Builtin PlayerForms
    // 在Java中null不能使用equals方法
    // 新系统用占位符 别用 后续会改变量名
    public static IForm N_ORIGINAL_BEFORE_ENABLE = null;
    public static IForm N_ORIGINAL_SHIFTER = null;

    // Original
    public static IForm ORIGINAL_BEFORE_ENABLE = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("original_before_enable")).formFlag(NoInstinct, NoAnyInhibitor, NoCursedMoonEffect, NoCursedMoonTFTarget).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm ORIGINAL_SHIFTER = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("original_shifter")).formFlag(NoInstinct, NoAnyInhibitor, NoCursedMoonTFTarget).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IFormGroup BASE_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("base_form")).registerForm(-1, 1, ORIGINAL_BEFORE_ENABLE).registerForm(0, 1, ORIGINAL_SHIFTER));
    // Bat
    public static IForm BAT_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("bat_0")).formFlag(StarterForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.9f, 1.0f)));
    public static IForm BAT_1 = registerPlayerForm(new Form_Bat1(ShapeShifterCurseFabric.identifier("bat_1")).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.75f, 1.0f)));
    public static IForm BAT_2 = registerPlayerForm(new Form_Bat2(ShapeShifterCurseFabric.identifier("bat_2")).formFlag(HasSlowFall, NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.5f, 1.0f)));
    public static IForm BAT_3 = registerPlayerForm(new Form_Bat3(ShapeShifterCurseFabric.identifier("bat_3")).formFlag(HasSlowFall, FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.5f, 0.6f)));
    public static IFormGroup BAT_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("bat_form")).registerForm(1, 1, BAT_0).registerForm(2, 1, BAT_1).registerForm(3, 1, BAT_2).registerForm(4, 1, BAT_3));
    // Axolotl
    public static IForm AXOLOTL_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("axolotl_0")).formFlag(StarterForm).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm AXOLOTL_1 = registerPlayerForm(new Form_Axolotl1(ShapeShifterCurseFabric.identifier("axolotl_1")).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm AXOLOTL_2 = registerPlayerForm(new Form_Axolotl2(ShapeShifterCurseFabric.identifier("axolotl_2")).formFlag(NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.9f, 1.0f)));
    public static IForm AXOLOTL_3 = registerPlayerForm(new Form_Axolotl3(ShapeShifterCurseFabric.identifier("axolotl_3")).formFlag(FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.9f, 1.0f)));
    public static IFormGroup AXOLOTL_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("axolotl_form")).registerForm(1, 1, AXOLOTL_0).registerForm(2, 1, AXOLOTL_1).registerForm(3, 1, AXOLOTL_2).registerForm(4, 1, AXOLOTL_3));
    // Ocelot
    public static IForm OCELOT_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("ocelot_0")).formFlag(StarterForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.95f, 1.0f)));
    public static IForm OCELOT_1 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("ocelot_1")).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.85f, 1.0f)));
    public static IForm OCELOT_2 = registerPlayerForm(new Form_Ocelot2(ShapeShifterCurseFabric.identifier("ocelot_2")).formFlag(NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.65f, 1.0f)));
    public static IForm OCELOT_3 = registerPlayerForm(new Form_Ocelot3(ShapeShifterCurseFabric.identifier("ocelot_3")).formFlag(FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).bodyType(PlayerFormBodyType.FERAL).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.75f, 0.6f)));
    public static IFormGroup OCELOT_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("ocelot_form")).registerForm(1, 1, OCELOT_0).registerForm(2, 1, OCELOT_1).registerForm(3, 1, OCELOT_2).registerForm(4, 1, OCELOT_3));
    // Familiar Fox
    public static IForm FAMILIAR_FOX_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("familiar_fox_0")).formFlag(StarterForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.8f, 1.0f)));
    public static IForm FAMILIAR_FOX_1 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("familiar_fox_1")).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.65f, 1.0f)));
    public static IForm FAMILIAR_FOX_2 = registerPlayerForm(new Form_FamiliarFox2(ShapeShifterCurseFabric.identifier("familiar_fox_2")).formFlag(NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.55f, 1.0f)));
    public static IForm FAMILIAR_FOX_3 = registerPlayerForm(new Form_FamiliarFox3(ShapeShifterCurseFabric.identifier("familiar_fox_3")).formFlag(FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).bodyType(PlayerFormBodyType.FERAL).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.45f,0.6f)));
    public static IFormGroup FAMILIAR_FOX_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("familiar_fox_form")).registerForm(1, 1, FAMILIAR_FOX_0).registerForm(2, 1, FAMILIAR_FOX_1).registerForm(3, 1, FAMILIAR_FOX_2).registerForm(4, 1, FAMILIAR_FOX_3));
    // Snow Fox
    public static IForm SNOW_FOX_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("snow_fox_0")).formFlag(StarterForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.8f, 1.0f)));
    public static IForm SNOW_FOX_1 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("snow_fox_1")).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.65f, 1.0f)));
    public static IForm SNOW_FOX_2 = registerPlayerForm(new Form_SnowFox2(ShapeShifterCurseFabric.identifier("snow_fox_2")).formFlag(NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.55f, 1.0f)));
    public static IForm SNOW_FOX_3 = registerPlayerForm(new Form_SnowFox3(ShapeShifterCurseFabric.identifier("snow_fox_3")).formFlag(FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).bodyType(PlayerFormBodyType.FERAL).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.45f,0.6f)));
    public static IFormGroup SNOW_FOX_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("snow_fox_form")).registerForm(1, 1, SNOW_FOX_0).registerForm(2, 1, SNOW_FOX_1).registerForm(3, 1, SNOW_FOX_2).registerForm(4, 1, SNOW_FOX_3));
    // Anubis Wolf
    public static IForm ANUBIS_WOLF_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("anubis_wolf_0")).formFlag(StarterForm).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm ANUBIS_WOLF_1 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("anubis_wolf_1")).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm ANUBIS_WOLF_2 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("anubis_wolf_2")).formFlag(NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.9f, 1.0f)));
    public static IForm ANUBIS_WOLF_3 = registerPlayerForm(new Form_AnubisWolf3(ShapeShifterCurseFabric.identifier("anubis_wolf_3")).formFlag(FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).bodyType(PlayerFormBodyType.FERAL).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.8f,0.6f)));
    public static IFormGroup ANUBIS_WOLF_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("anubis_wolf_form")).registerForm(1, 1, ANUBIS_WOLF_0).registerForm(2, 1, ANUBIS_WOLF_1).registerForm(3, 1, ANUBIS_WOLF_2).registerForm(4, 1, ANUBIS_WOLF_3));
    // Spider
    public static IForm SPIDER_0 = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("spider_0")).formFlag(StarterForm).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm SPIDER_1 = registerPlayerForm(new Form_Spider1(ShapeShifterCurseFabric.identifier("spider_1")).formFlag(NoCursedMoonTFTarget, NoCursedMoonEffect).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.85f, 1.0f)));
    public static IForm SPIDER_2 = registerPlayerForm(new Form_Spider2(ShapeShifterCurseFabric.identifier("spider_2")).formFlag(NoInhibitor, LockInstinct, CursedMoonFinalForm).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.9f, 1.0f)));
    public static IForm SPIDER_3 = registerPlayerForm(new Form_Spider3(ShapeShifterCurseFabric.identifier("spider_3")).formFlag(FinalForm, NoAnyInhibitor, NoInstinct, NoCursedMoonEffect).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.9f,0.6f)));
    public static IFormGroup SPIDER_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("spider_form")).registerForm(1, 1, SPIDER_0).registerForm(2, 1, SPIDER_1).registerForm(3, 1, SPIDER_2).registerForm(4, 1, SPIDER_3));
    // ALLAY_SP
    public static IForm ALLAY_SP = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("allay_sp")).formFlag(NoInstinct, NoCursedMoonEffect).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.35f,1.0f)));
    public static IFormGroup ALLAY_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("allay_form")).registerForm(1, 1, ALLAY_SP));
    // FERAL_CAT_SP
    public static IForm FERAL_CAT_SP = registerPlayerForm(new NormalForm(ShapeShifterCurseFabric.identifier("feral_cat_sp")).formFlag(NoInstinct, NoCursedMoonEffect).bodyType(PlayerFormBodyType.FERAL).applyScaleFunc(NORMAL_SCALE_FUNC_BUILDER.apply(0.55f,0.6f)));
    public static IFormGroup FERAL_CAT_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("feral_cat_form")).registerForm(1, 1, FERAL_CAT_SP));

    public static <T extends IForm> T registerPlayerForm(T form) {
        playerForms.put(form.getFormID(), form);
        return form;
    }

    public static <T extends DynamicForm> T registerDynamicPlayerForm(T form) {
        if (!dynamicPlayerForms.contains(form.getFormID())) {
            dynamicPlayerForms.add(form.getFormID());
        }
        return registerPlayerForm(form);
    }

    public static DynamicForm buildDynamicPlayerForm(Identifier id, JsonObject dynamicPlayerForm) {
        return DynamicForm.fromJson(id, dynamicPlayerForm);
    }

    // 实现更大的数据包后移除 目前怕包顺序错误
    public static void removeDynamicPlayerFormsExcept(List<Identifier> except) {
        List<Identifier> NeedRemove = new ArrayList<>();
        for (Identifier id : dynamicPlayerForms) {
            for (Identifier exceptID : except) {
                if (id.equals(exceptID)) {
                    continue;
                }
                if (id.getNamespace().equals(PatronNameSpace)) {
                    continue;
                }
                NeedRemove.add(id);
            }
        }
        for (Identifier id : NeedRemove) {
            removeDynamicPlayerForm(id, true);
        }
    }

    public static void ApplyDynamicPlayerForms(JsonObject dynamicPlayerFormList) {
        for (Map.Entry<String, JsonElement> entry : dynamicPlayerFormList.entrySet()) {
            Identifier ID = Identifier.tryParse(entry.getKey());
            if (ID == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Invalid dynamic player form ID: " + entry.getKey());
                continue;
            }
            DynamicForm dynamicPlayerForm = buildDynamicPlayerForm(ID, entry.getValue().getAsJsonObject());
            registerDynamicPlayerForm(dynamicPlayerForm);
        }
    }

    // 每次Reload调用
    public static HashMap<Identifier, DynamicForm> DumpDynamicPlayerForms() {
        HashMap<Identifier, DynamicForm> dynamicPlayerFormMap = new HashMap<Identifier, DynamicForm>();
        for (Identifier id : dynamicPlayerForms) {
            if (playerForms.get(id) instanceof DynamicForm playerFormDynamic) {
                dynamicPlayerFormMap.put(id, playerFormDynamic);
            }
            else {
                ShapeShifterCurseFabric.LOGGER.warn("Attempted to save non-dynamic player form: " + id);
            }
        }
        return dynamicPlayerFormMap;
    }

    public static <T extends IFormGroup> T registerPlayerFormGroup(T formGroup) {
        playerFormGroups.put(formGroup.getGroupID(), formGroup);
        return formGroup;
    }

    public static <T extends IFormGroup> T registerDynamicPlayerFormGroup(T formGroup) {
        if (!dynamicPlayerFormGroups.contains(formGroup.getGroupID())) {
            dynamicPlayerFormGroups.add(formGroup.getGroupID());
        }
        return registerPlayerFormGroup(formGroup);
    }

    public static boolean removeDynamicPlayerForm(Identifier id, boolean RemoveDynamicRegistry) {
        if (!dynamicPlayerForms.contains(id)) {
            ShapeShifterCurseFabric.LOGGER.warn("Attempted to remove non-dynamic player form: " + id);
            return false;
        }
        if (!playerForms.containsKey(id)) {
            ShapeShifterCurseFabric.LOGGER.warn("Attempted to remove non-existent player form: " + id);
            return false;
        }
        if (RemoveDynamicRegistry) {
            dynamicPlayerForms.remove(id);
        }
        playerForms.remove(id);
        return true;
    }

    public static boolean removeDynamicPlayerFormGroup(Identifier id, boolean RemoveDynamicRegistry) {
        if (!dynamicPlayerFormGroups.contains(id)) {
            ShapeShifterCurseFabric.LOGGER.warn("Attempted to remove non-dynamic player form group: " + id);
            return false;
        }
        if (!playerFormGroups.containsKey(id)) {
            ShapeShifterCurseFabric.LOGGER.warn("Attempted to remove non-existent player form group: " + id);
            return false;
        }
        if (RemoveDynamicRegistry) {
            dynamicPlayerFormGroups.remove(id);
        }
        playerFormGroups.remove(id);
        return true;
    }

    public static void ClearAllDynamicPlayerForms() {
        for (Identifier id : dynamicPlayerForms) {
            if (!id.getNamespace().equals(PatronNameSpace)) {
                removeDynamicPlayerForm(id, false);
            }
        }
        dynamicPlayerForms.clear();
        for (Identifier id : dynamicPlayerFormGroups) {
            if (!id.getNamespace().equals(PatronNameSpace)) {
                removeDynamicPlayerFormGroup(id, false);
            }
        }
        dynamicPlayerFormGroups.clear();
    }

    public static IForm getPlayerForm(Identifier id) {
        return playerForms.get(id);
    }

    public static IForm getPlayerForm(String id) {
        return playerForms.get(Identifier.tryParse(id));
    }

    public static IForm getPlayerFormOrThrow(Identifier id) {
        IForm form = getPlayerForm(id);
        if (form == null) {
            throw new IllegalArgumentException("Unknown player form: " + id);
        }
        return form;
    }

    public static IForm getPlayerFormOrThrow(String id) {
        return getPlayerFormOrThrow(Identifier.tryParse(id));
    }

    public static IForm getPlayerFormOrDefault(Identifier id, IForm defaultForm) {
        IForm form = getPlayerForm(id);
        if (form == null) {
            return defaultForm;

        }
        return form;
    }

    public static IForm getPlayerFormOrDefault(String id, IForm defaultForm) {
        return getPlayerFormOrDefault(Identifier.tryParse(id), defaultForm);
    }

    public static Boolean IsPlayerFormEqual(@Nullable IForm form1, @Nullable IForm form2) {
        if (form1 == null || form2 == null) {
            return form1 == null && form2 == null;
        }
        return form1.isEquals(form2);
    }

    public static IFormGroup getPlayerFormGroup(Identifier id) {
        return playerFormGroups.get(id);
    }

    public static IFormGroup getPlayerFormGroup(String id) {
        return playerFormGroups.get(Identifier.tryParse(id));
    }
}
