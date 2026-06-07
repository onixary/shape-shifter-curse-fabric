package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class PlayerFormComponent implements AutoSyncedComponent {
    public static final ComponentKey<PlayerFormComponent> COMPONENT = ComponentRegistry.getOrCreate(ShapeShifterCurseFabric.identifier("player_form"), PlayerFormComponent.class);

    public @NotNull IForm nowForm = RegPlayerForms.N_ORIGINAL_BEFORE_ENABLE;
    public final List<IForm> formHistory = new ArrayList<>();
    // 诅咒之月逻辑
    public boolean isCursedMoonApplied = false;
    public boolean lastTransformByCure = false;  // 仅用于诅咒之月 进入和退出诅咒之月时会清空
    public @Nullable IForm BeforeCursedMoonAppliedForm = null;
    public @Nullable IForm AfterCursedMoonAppliedForm = null;
    // 变形系统
    public @Nullable IForm transformTargetForm = null;

    // 临时变量
    public PlayerEntity player = null;

    public PlayerFormComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        // 目前没写形态注册表 先用null凑活一下
        if (tag.contains("nowForm")) {
            nowForm = FormUtils.parseForm(Identifier.tryParse(tag.getString("nowForm")), RegPlayerForms.N_ORIGINAL_BEFORE_ENABLE);
        } else {
            nowForm = RegPlayerForms.N_ORIGINAL_BEFORE_ENABLE;
        }
        if (tag.contains("formHistory")) {
            NbtList history = tag.getList("formHistory", NbtElement.STRING_TYPE);
            for (NbtElement element : history) {
                IForm form = FormUtils.parseForm(Identifier.tryParse(element.asString()), null);
                if (form != null) {
                    formHistory.add(form);
                }
            }
        }
        if (tag.contains("isCursedMoonApplied")) {
            isCursedMoonApplied = tag.getBoolean("isCursedMoonApplied");
        } else {
            isCursedMoonApplied = false;
        }
        if (tag.contains("lastTransformByCure")) {
            lastTransformByCure = tag.getBoolean("lastTransformByCure");
        } else {
            lastTransformByCure = false;
        }
        if (tag.contains("BeforeCursedMoonAppliedForm")) {
            BeforeCursedMoonAppliedForm = FormUtils.parseForm(Identifier.tryParse(tag.getString("BeforeCursedMoonAppliedForm")), null);
        } else {
            BeforeCursedMoonAppliedForm = null;
        }
        if (tag.contains("AfterCursedMoonAppliedForm")) {
            AfterCursedMoonAppliedForm = FormUtils.parseForm(Identifier.tryParse(tag.getString("AfterCursedMoonAppliedForm")), null);
        } else {
            AfterCursedMoonAppliedForm = null;
        }
        if (tag.contains("transformTargetForm")) {
            transformTargetForm = FormUtils.parseForm(Identifier.tryParse(tag.getString("transformTargetForm")), null);
        } else {
            transformTargetForm = null;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("nowForm", nowForm.getFormID().toString());
        NbtList history = new NbtList();
        for (IForm form : formHistory) {
            history.add(NbtString.of(form.getFormID().toString()));
        }
        tag.put("formHistory", history);
        tag.putBoolean("isCursedMoonApplied", isCursedMoonApplied);
        tag.putBoolean("lastTransformByCure", lastTransformByCure);
        if (BeforeCursedMoonAppliedForm != null) {
            tag.putString("BeforeCursedMoonAppliedForm", BeforeCursedMoonAppliedForm.getFormID().toString());
        }
        if (AfterCursedMoonAppliedForm != null) {
            tag.putString("AfterCursedMoonAppliedForm", AfterCursedMoonAppliedForm.getFormID().toString());
        }
        if (transformTargetForm != null) {
            tag.putString("transformTargetForm", transformTargetForm.getFormID().toString());
        }
    }

    public void sync() {
        COMPONENT.sync(this.player);
    }
}
