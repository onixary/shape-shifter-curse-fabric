package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FormColorData {
    public boolean enableDefaultFormColor = false;
    public final HashMap<Identifier, FormTextureUtils.ColorSetting> formDefaultSetting = new HashMap<>();

    public final HashMap<String, FormTextureUtils.ColorSetting> customSetting = new HashMap<>();
    public final HashMap<Identifier, HashMap<String, FormTextureUtils.ColorSetting>> customSettingByForm = new HashMap<>();

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
        return nbt;
    }

    public void loadCompound(NbtCompound compound) {
        formDefaultSetting.clear();
        customSetting.clear();
        customSettingByForm.clear();
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
    }

    // 挂一个钩子在网络接受形态上 比如客户端的SYNC_FORM_CHANGE接收函数上
    public void onClientFormChange(Identifier form) {
        if (this.enableDefaultFormColor && this.formDefaultSetting.containsKey(form)) {
            ModPacketsS2C.sendUpdateCustomSetting(this.formDefaultSetting.get(form));
        }
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
}
