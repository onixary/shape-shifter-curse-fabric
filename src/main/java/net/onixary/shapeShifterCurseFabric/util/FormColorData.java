package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.FormColorSelectMenu;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class FormColorData {
    public boolean enableDefaultFormColor = true;
    public final HashMap<Identifier, FormTextureUtils.ColorSetting> formDefaultSetting = new HashMap<>();

    public final HashMap<String, FormTextureUtils.ColorSetting> customSetting = new HashMap<>();
    public final HashMap<Identifier, HashMap<String, FormTextureUtils.ColorSetting>> customSettingByForm = new HashMap<>();

    public final HashMap<Identifier, List<String>> FormColorSelectMenu_Form_Local_Names = new HashMap<>();
    public final List<String> FormColorSelectMenu_Global_Names = new ArrayList<String>();
    public final HashMap<Identifier, String> FormColorSelectMenu_Form_Default_Names = new HashMap<>();

    public NbtCompound dumpColorSetting(FormTextureUtils.ColorSetting colorSetting) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("primaryColor", colorSetting.getPrimaryColor());
        nbt.putInt("accentColor1", colorSetting.getAccentColor1());
        nbt.putInt("accentColor2", colorSetting.getAccentColor2());
        nbt.putInt("eyeColorA", colorSetting.getEyeColorA());
        nbt.putInt("eyeColorB", colorSetting.getEyeColorB());
        nbt.putBoolean("primaryGreyReverse", colorSetting.getPrimaryGreyReverse());
        nbt.putBoolean("accent1GreyReverse", colorSetting.getAccent1GreyReverse());
        nbt.putBoolean("accent2GreyReverse", colorSetting.getAccent2GreyReverse());
        return nbt;
    }

    public FormTextureUtils.ColorSetting loadColorSetting(NbtCompound nbt) {
        return new FormTextureUtils.ColorSetting(nbt.getInt("primaryColor"), nbt.getInt("accentColor1"), nbt.getInt("accentColor2"), nbt.getInt("eyeColorA"), nbt.getInt("eyeColorB"), nbt.getBoolean("primaryGreyReverse"), nbt.getBoolean("accent1GreyReverse"), nbt.getBoolean("accent2GreyReverse"));
    }

    public NbtCompound saveCompound() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enableDefaultFormColor", enableDefaultFormColor);
        NbtCompound formDefaultSettingNbt = new NbtCompound();
        for (Identifier form : formDefaultSetting.keySet()) {
            formDefaultSettingNbt.put(form.toString(), dumpColorSetting(formDefaultSetting.get(form)));
        }
        nbt.put("formDefaultSetting", formDefaultSettingNbt);
        NbtCompound customSettingNbt = new NbtCompound();
        for (String name : customSetting.keySet()) {
            customSettingNbt.put(name, dumpColorSetting(customSetting.get(name)));
        }
        nbt.put("customSetting", customSettingNbt);
        NbtCompound customSettingByFormNbt = new NbtCompound();
        for (Identifier form : customSettingByForm.keySet()) {
            NbtCompound formNbt = new NbtCompound();
            for (String name : customSettingByForm.get(form).keySet()) {
                formNbt.put(name, dumpColorSetting(customSettingByForm.get(form).get(name)));
            }
            customSettingByFormNbt.put(form.toString(), formNbt);
        }
        nbt.put("customSettingByForm", customSettingByFormNbt);
        NbtCompound formColorSelectMenuNbt = new NbtCompound();
        for (Identifier form : FormColorSelectMenu_Form_Local_Names.keySet()) {
            NbtList nbtList = new NbtList();
            for (String name : FormColorSelectMenu_Form_Local_Names.get(form)) {
                nbtList.add(NbtString.of(name));
            }
            formColorSelectMenuNbt.put(form.toString(), nbtList);
        }
        nbt.put("FCS_form_local_setting_names", formColorSelectMenuNbt);
        NbtList nbtList = new NbtList();
        for (String name : FormColorSelectMenu_Global_Names) {
            nbtList.add(NbtString.of(name));
        }
        nbt.put("FCS_global_setting_names", nbtList);
        NbtCompound formColorSelectMenuDefaultNbt = new NbtCompound();
        for (Identifier form : FormColorSelectMenu_Form_Default_Names.keySet()) {
            formColorSelectMenuDefaultNbt.putString(form.toString(), FormColorSelectMenu_Form_Default_Names.get(form));
        }
        nbt.put("FCS_form_default_setting_names", formColorSelectMenuDefaultNbt);
        return nbt;
    }

    public void loadCompound(NbtCompound compound) {
        formDefaultSetting.clear();
        customSetting.clear();
        customSettingByForm.clear();
        FormColorSelectMenu_Form_Local_Names.clear();
        FormColorSelectMenu_Global_Names.clear();
        if (compound.contains("enableDefaultFormColor")) {
            enableDefaultFormColor = compound.getBoolean("enableDefaultFormColor");
        }
        if (compound.contains("formDefaultSetting")) {
            NbtCompound formDefaultSettingNbt = compound.getCompound("formDefaultSetting");
            for (String form : formDefaultSettingNbt.getKeys()) {
                try {
                    formDefaultSetting.put(Identifier.tryParse(form), loadColorSetting(formDefaultSettingNbt.getCompound(form)));
                } catch (Exception e) {
                    ShapeShifterCurseFabric.LOGGER.warn("Failed to load form default color setting for " + form + ": " + e.getMessage());
                }
            }
        }
        if (compound.contains("customSetting")) {
            NbtCompound customSettingNbt = compound.getCompound("customSetting");
            for (String name : customSettingNbt.getKeys()) {
                try {
                    customSetting.put(name, loadColorSetting(customSettingNbt.getCompound(name)));
                } catch (Exception e) {
                    ShapeShifterCurseFabric.LOGGER.warn("Failed to load custom color setting for " + name + ": " + e.getMessage());
                }
            }
        }
        if (compound.contains("customSettingByForm")) {
            NbtCompound customSettingByFormNbt = compound.getCompound("customSettingByForm");
            for (String form : customSettingByFormNbt.getKeys()) {
                Identifier formId = Identifier.tryParse(form);
                NbtCompound formNbt = customSettingByFormNbt.getCompound(form);
                for (String name : formNbt.getKeys()) {
                    try {
                        customSettingByForm.computeIfAbsent(formId, k -> new HashMap<>()).put(name, loadColorSetting(formNbt.getCompound(name)));
                    } catch (Exception e) {
                        ShapeShifterCurseFabric.LOGGER.warn("Failed to load custom color setting for " + name + " on form " + form + ": " + e.getMessage());
                    }
                }
            }
        }
        if (compound.contains("FCS_form_local_setting_names")) {
            NbtCompound nbtList = compound.getCompound("FCS_form_local_setting_names");
            for (String form : nbtList.getKeys()) {
                Identifier formId = Identifier.tryParse(form);
                List<String> formSlotNames = FormColorSelectMenu_Form_Local_Names.computeIfAbsent(formId, k -> new ArrayList<>());
                NbtList nbtList2 = nbtList.getList(form, NbtElement.STRING_TYPE);
                for (int i = 0; i < nbtList2.size(); i++) {
                    formSlotNames.add(nbtList2.getString(i));
                }
            }
        }
        if (compound.contains("FCS_global_setting_names")) {
            NbtList nbtList = compound.getList("FCS_global_setting_names", NbtElement.STRING_TYPE);
            for (int i = 0; i < nbtList.size(); i++) {
                FormColorSelectMenu_Global_Names.add(nbtList.getString(i));
            }
        }
        if (compound.contains("FCS_form_default_setting_names")) {
            NbtCompound nbtList = compound.getCompound("FCS_form_default_setting_names");
            for (String form : nbtList.getKeys()) {
                Identifier formId = Identifier.tryParse(form);
                FormColorSelectMenu_Form_Default_Names.put(formId, nbtList.getString(form));
            }
        }
    }

    // 挂一个钩子在网络接受形态上 比如客户端的SYNC_FORM_CHANGE接收函数上
    public void onClientFormChange(Identifier form) {
        // TODO 挂载系统 + 新增配置
        if (this.enableDefaultFormColor && this.formDefaultSetting.containsKey(form)) {
            ModPacketsS2C.sendUpdateCustomSetting(this.formDefaultSetting.get(form));
        }
        // 延时一下 好同步 "sendUpdateCustomSetting" 的更新
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                FormColorSelectMenu.onFormChange_STATIC();
            } catch (InterruptedException ignored) {
                FormColorSelectMenu.onFormChange_STATIC();
            }
        }).start();
    }

    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("shape-shifter-curse-form-color-data.nbt");
    }

    // 每次修改后调用
    public void writeToConfig() {
        Path configPath = getConfigPath();
        try {
            NbtIo.writeCompressed(this.saveCompound(), configPath.toFile());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to write form color data to config file: " + e);
        }
    }

    // ShapeShifterCurseFabricClient 里挂一个钩子
    public void loadFormConfig() {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                NbtCompound compound = NbtIo.readCompressed(configPath.toFile());
                this.loadCompound(compound);
            } catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to load form color data from config file: " + e);
            }
        }
    }

    public static byte[] ColorSettingToBytes(FormTextureUtils.ColorSetting colorSetting) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            dos.writeInt(1);  // 版本号
            dos.writeInt(colorSetting.getPrimaryColor());
            dos.writeInt(colorSetting.getAccentColor1());
            dos.writeInt(colorSetting.getAccentColor2());
            dos.writeInt(colorSetting.getEyeColorA());
            dos.writeInt(colorSetting.getEyeColorB());
            byte bools = 0;
            bools |= (byte) (colorSetting.getPrimaryGreyReverse() ? 1 : 0);
            bools |= (byte) (colorSetting.getAccent1GreyReverse() ? 2 : 0);
            bools |= (byte) (colorSetting.getAccent2GreyReverse() ? 4 : 0);
            dos.writeByte(bools);
            dos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            // 理论上不会发生，但若出错则返回空数组（或可抛运行时异常）
            return new byte[0];
        }
    }

    public static @Nullable FormTextureUtils.ColorSetting ColorSettingFromBytes(byte[] bytes) {
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes))) {
            if (dis.readInt() != 1) {
                return null;
            }
            int primaryColor = dis.readInt();
            int accentColor1 = dis.readInt();
            int accentColor2 = dis.readInt();
            int eyeColorA = dis.readInt();
            int eyeColorB = dis.readInt();
            byte bools = dis.readByte();
            boolean primaryGreyReverse = (bools & 1) != 0;
            boolean accent1GreyReverse = (bools & 2) != 0;
            boolean accent2GreyReverse = (bools & 4) != 0;
            return new FormTextureUtils.ColorSetting(primaryColor, accentColor1, accentColor2,
                    eyeColorA, eyeColorB, primaryGreyReverse, accent1GreyReverse, accent2GreyReverse);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] formHex(String hex) {
        if (hex == null || hex.isEmpty() || hex.length() % 2 != 0) {
            return null;
        }
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            String byteStr = hex.substring(i * 2, i * 2 + 2);
            try {
                int val = Integer.parseInt(byteStr, 16);
                result[i] = (byte) val;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return result;
    }

    public static String toHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X", b & 0xFF));
        }
        return stringBuilder.toString();
    }

    public static @Nullable FormTextureUtils.ColorSetting ColorSettingFormString(String data) {
        try {
            if (data.startsWith("b")) {
                byte[] bytes = Base64.getDecoder().decode(data.substring(1));
                return ColorSettingFromBytes(bytes);
            } else if (data.startsWith("#")) {
                String hex = data.substring(1);
                byte[] bytes = formHex(hex);
                return ColorSettingFromBytes(bytes);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String ColorSettingtoString(FormTextureUtils.ColorSetting data, boolean useBase64) {
        if (useBase64) {
            byte[] bytes = FormColorData.ColorSettingToBytes(data);
            return "b" + Base64.getEncoder().encodeToString(bytes);
        } else {
            return "#" + toHex(FormColorData.ColorSettingToBytes(data));
        }
    }

    public String getName_LocalFormSlot(Identifier formID, int index) {
        List<String> list = this.FormColorSelectMenu_Form_Local_Names.get(formID);
        if (list != null && index < list.size()) {
            return list.get(index);
        }
        return "";
    }

    public void setName_LocalFormSlot(Identifier formID, int index, String name) {
        if (index > 9) {
            return;
        }
        List<String> list = this.FormColorSelectMenu_Form_Local_Names.computeIfAbsent(formID, k -> new ArrayList<>());
        if (index >= list.size()) {
            for (int i = list.size(); i <= index; i++) {
                list.add("");
            }
        }
        list.set(index, name);
    }

    public String getName_GlobalSlot(int index) {
        if (index < FormColorSelectMenu_Global_Names.size()) {
            return FormColorSelectMenu_Global_Names.get(index);
        }
        return "";
    }

    public void setName_GlobalSlot(int index, String name) {
        if (index > 4) {
            return;
        }
        if (index >= FormColorSelectMenu_Global_Names.size()) {
            for (int i = FormColorSelectMenu_Global_Names.size(); i <= index; i++) {
                FormColorSelectMenu_Global_Names.add("");
            }
        }
        FormColorSelectMenu_Global_Names.set(index, name);
    }

    public String getName_DefaultSlot(Identifier formID) {
        return this.FormColorSelectMenu_Form_Default_Names.get(formID);
    }

    public void setName_DefaultSlot(Identifier formID, String name) {
        this.FormColorSelectMenu_Form_Default_Names.put(formID, name);
    }
}
