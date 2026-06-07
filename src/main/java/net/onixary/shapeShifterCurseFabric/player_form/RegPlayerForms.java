package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.old.forms.*;
import net.onixary.shapeShifterCurseFabric.player_form.old.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.old.PlayerFormPhase;

import java.util.*;

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
    public static IForm ORIGINAL_BEFORE_ENABLE = registerPlayerForm(new NormalForm(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_before_enable")).formFlag(NoInstinct, NoAnyInhibitor, NoCursedMoonEffect, NoCursedMoonTFTarget).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    public static IForm ORIGINAL_SHIFTER = registerPlayerForm(new NormalForm(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_shifter")).formFlag(NoInstinct, NoAnyInhibitor, NoCursedMoonTFTarget).applyScaleFunc(NormalForm.RESET_SCALE_FUNC));
    // Bat
    public static PlayerFormBase BAT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase BAT_1 = registerPlayerForm(new Form_Bat1(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase BAT_2 = registerPlayerForm(new Form_Bat2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_2")).setPhase(PlayerFormPhase.PHASE_2).setHasSlowFall(true).setOverrideHandAnim(true));
    public static PlayerFormBase BAT_3 = registerPlayerForm(new Form_Bat3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_3")).setPhase(PlayerFormPhase.PHASE_3).setHasSlowFall(true).setOverrideHandAnim(true));
    // Axolotl
    public static PlayerFormBase AXOLOTL_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase AXOLOTL_1 = registerPlayerForm(new Form_Axolotl1(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase AXOLOTL_2 = registerPlayerForm(new Form_Axolotl2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase AXOLOTL_3 = registerPlayerForm(new Form_Axolotl3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_3")).setPhase(PlayerFormPhase.PHASE_3)).setCanRushJump(true);
    // Ocelot
    public static PlayerFormBase OCELOT_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase OCELOT_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase OCELOT_2 = registerPlayerForm(new Form_Ocelot2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_2")).setPhase(PlayerFormPhase.PHASE_2).setCanSneakRush(true).setCanRushJump(true));
    public static PlayerFormBase OCELOT_3 = registerPlayerForm(new Form_Ocelot3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_3")).setPhase(PlayerFormPhase.PHASE_3).setCanSneakRush(true));
    // FamiliarFox
    public static PlayerFormBase FAMILIAR_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase FAMILIAR_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase FAMILIAR_FOX_2 = registerPlayerForm(new Form_FamiliarFox2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase FAMILIAR_FOX_3 = registerPlayerForm(new Form_FamiliarFox3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_3")).setPhase(PlayerFormPhase.PHASE_3));
    // SnowFox
    public static PlayerFormBase SNOW_FOX_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase SNOW_FOX_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase SNOW_FOX_2 = registerPlayerForm(new Form_SnowFox2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase SNOW_FOX_3 = registerPlayerForm(new Form_SnowFox3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_3")).setPhase(PlayerFormPhase.PHASE_3));
    // AnubisWolf
    public static PlayerFormBase ANUBIS_WOLF_0 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "anubis_wolf_0")).setPhase(PlayerFormPhase.PHASE_0));
    public static PlayerFormBase ANUBIS_WOLF_1 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "anubis_wolf_1")).setPhase(PlayerFormPhase.PHASE_1));
    public static PlayerFormBase ANUBIS_WOLF_2 = registerPlayerForm(new PlayerFormBase(new Identifier(ShapeShifterCurseFabric.MOD_ID, "anubis_wolf_2")).setPhase(PlayerFormPhase.PHASE_2));
    public static PlayerFormBase ANUBIS_WOLF_3 = registerPlayerForm(new Form_AnubisWolf3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "anubis_wolf_3")).setPhase(PlayerFormPhase.PHASE_3));
    // Spider
    public static PlayerFormBase SPIDER_0 = registerPlayerForm(new Form_Spider0(new Identifier(ShapeShifterCurseFabric.MOD_ID, "spider_0")).setPhase(PlayerFormPhase.PHASE_0)).setIgnoreCursedMoon(true).setIgnoreCatalyst(true);
    public static PlayerFormBase SPIDER_1 = registerPlayerForm(new Form_Spider1(new Identifier(ShapeShifterCurseFabric.MOD_ID, "spider_1")).setPhase(PlayerFormPhase.PHASE_1)).setIgnoreCursedMoon(true).setIgnoreCatalyst(true);
    public static PlayerFormBase SPIDER_2 = registerPlayerForm(new Form_Spider2(new Identifier(ShapeShifterCurseFabric.MOD_ID, "spider_2")).setPhase(PlayerFormPhase.PHASE_2)).setIgnoreCursedMoon(true).setIgnoreCatalyst(true);
    public static PlayerFormBase SPIDER_3 = registerPlayerForm(new Form_Spider3(new Identifier(ShapeShifterCurseFabric.MOD_ID, "spider_3")).setPhase(PlayerFormPhase.PHASE_3));
    // ALLAY_SP
    public static PlayerFormBase ALLAY_SP = registerPlayerForm(new Form_Allay(new Identifier(ShapeShifterCurseFabric.MOD_ID, "allay_sp")).setPhase(PlayerFormPhase.PHASE_SP));
    // FERAL_CAT_SP
    public static PlayerFormBase FERAL_CAT_SP = registerPlayerForm(new Form_FeralCatSP(new Identifier(ShapeShifterCurseFabric.MOD_ID, "feral_cat_sp")).setPhase(PlayerFormPhase.PHASE_SP));

    // Builtin PlayerFormGroups
    public static IFormGroup BASE_FORM = registerPlayerFormGroup(new NormalGroup(ShapeShifterCurseFabric.identifier("original_form")).registerForm(-1, 1, ORIGINAL_BEFORE_ENABLE).registerForm(0, 1, ORIGINAL_SHIFTER));
    // public static PlayerFormGroup ORIGINAL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "original_form")).addForm(ORIGINAL_BEFORE_ENABLE, -2));
    // public static PlayerFormGroup BASE_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "base_form")).addForm(ORIGINAL_SHIFTER, -1));
    // public static PlayerFormGroup BAT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "bat_form")).addForm(BAT_0, 0).addForm(BAT_1, 1).addForm(BAT_2, 2).addForm(BAT_3, 3));
    // public static PlayerFormGroup AXOLOTL_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "axolotl_form")).addForm(AXOLOTL_0, 0).addForm(AXOLOTL_1, 1).addForm(AXOLOTL_2, 2).addForm(AXOLOTL_3, 3));
    // public static PlayerFormGroup OCELOT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "ocelot_form")).addForm(OCELOT_0, 0).addForm(OCELOT_1, 1).addForm(OCELOT_2, 2).addForm(OCELOT_3, 3));
    // public static PlayerFormGroup FAMILIAR_FOX_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "familiar_fox_form")).addForm(FAMILIAR_FOX_0, 0).addForm(FAMILIAR_FOX_1, 1).addForm(FAMILIAR_FOX_2, 2).addForm(FAMILIAR_FOX_3, 3));
    // public static PlayerFormGroup SNOW_FOX_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "snow_fox_form")).addForm(SNOW_FOX_0, 0).addForm(SNOW_FOX_1, 1).addForm(SNOW_FOX_2, 2).addForm(SNOW_FOX_3, 3));
    // public static PlayerFormGroup ALLAY_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "allay_form")).addForm(ALLAY_SP, 5));
    // public static PlayerFormGroup FERAL_CAT_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "feral_cat_form")).addForm(FERAL_CAT_SP, 5));
    // public static PlayerFormGroup ANUBIS_WOLF_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "anubis_wolf_form")).addForm(ANUBIS_WOLF_0, 0).addForm(ANUBIS_WOLF_1, 1).addForm(ANUBIS_WOLF_2, 2).addForm(ANUBIS_WOLF_3, 3));
    // public static PlayerFormGroup SPIDER_FORM = registerPlayerFormGroup(new PlayerFormGroup(new Identifier(ShapeShifterCurseFabric.MOD_ID, "spider_form")).addForm(SPIDER_0, 0).addForm(SPIDER_1, 1).addForm(SPIDER_2, 2).addForm(SPIDER_3, 3));


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

    // 比较两个PlayerForm是否相等 支持null
    public static Boolean IsPlayerFormEqual(IForm form1, IForm form2) {
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
