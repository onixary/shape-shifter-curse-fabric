package net.onixary.shapeShifterCurseFabric.status_effects.attachment;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

public class PlayerEffectAttachment{

    public PlayerFormBase currentToForm;
    public int remainingTicks;
    public BaseTransformativeStatusEffect currentEffect;
    // used in present potion effect
    public BaseTransformativeStatusEffect currentRegEffect;
    // instinct value

    public PlayerEffectAttachment() {
        this.currentToForm = null;
        this.remainingTicks = 0;
        this.currentEffect = null;
        currentRegEffect = null;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        if (currentToForm != null) {
            nbt.putString("currentToForm", currentToForm.getIDString());
        }
        else{
            nbt.putString("currentToForm", "null");
        }
        nbt.putInt("remainingTicks", remainingTicks);
        if (currentEffect != null) {
            nbt.putString("currentEffect", Registries.STATUS_EFFECT.getId(currentEffect).toString());
        }
        else{
            nbt.putString("currentEffect", "null");
        }
        if(currentRegEffect != null){
            nbt.putString("currentRegEffect", Registries.STATUS_EFFECT.getId(currentRegEffect).toString());
        }
        else{
            nbt.putString("currentRegEffect", "null");
        }
        return nbt;
    }

    public static PlayerEffectAttachment fromNbt(NbtCompound nbt) {
        PlayerEffectAttachment attachment = new PlayerEffectAttachment();
        if (nbt.contains("currentToForm", NbtElement.STRING_TYPE)) {
            if(nbt.getString("currentToForm").equals("null")){
                attachment.currentToForm = null;
            }
            else {
                attachment.currentToForm = RegPlayerForms.getPlayerFormOrThrow(nbt.getString("currentToForm"));
            }
        }
        attachment.remainingTicks = nbt.getInt("remainingTicks");
        if (nbt.contains("currentEffect", NbtElement.STRING_TYPE)) {
            if(nbt.getString("currentEffect").equals("null")){
                attachment.currentEffect = null;
            }
            else {
                Identifier effectId = new Identifier(nbt.getString("currentEffect"));
                attachment.currentEffect = (BaseTransformativeStatusEffect) Registries.STATUS_EFFECT.get(effectId);
            }
        }
        if (nbt.contains("currentRegEffect", NbtElement.STRING_TYPE)) {
            if(nbt.getString("currentRegEffect").equals("null")){
                attachment.currentRegEffect = null;
            }
            else {
            Identifier effectId = new Identifier(nbt.getString("currentRegEffect"));
            attachment.currentRegEffect = (BaseTransformativeStatusEffect) Registries.STATUS_EFFECT.get(effectId);
            }
        }
        return attachment;
    }

}
