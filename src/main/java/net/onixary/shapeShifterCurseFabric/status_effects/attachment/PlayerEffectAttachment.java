package net.onixary.shapeShifterCurseFabric.status_effects.attachment;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

public class PlayerEffectAttachment{

    public PlayerForms currentToForm;
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

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        if (currentToForm != null) {
            nbt.putString("currentToForm", currentToForm.name());
        }
        nbt.putInt("remainingTicks", remainingTicks);
        if (currentEffect != null) {
            nbt.putString("currentEffect", Registries.STATUS_EFFECT.getId(currentEffect).toString());
        }
        if(currentRegEffect != null){
            nbt.putString("currentRegEffect", Registries.STATUS_EFFECT.getId(currentRegEffect).toString());
        }
        return nbt;
    }

    public static PlayerEffectAttachment fromNbt(NbtCompound nbt) {
        PlayerEffectAttachment attachment = new PlayerEffectAttachment();
        if (nbt.contains("currentToForm", NbtElement.STRING_TYPE)) {
            attachment.currentToForm = PlayerForms.valueOf(nbt.getString("currentToForm"));
        }
        attachment.remainingTicks = nbt.getInt("remainingTicks");
        if (nbt.contains("currentEffect", NbtElement.STRING_TYPE)) {
            Identifier effectId = new Identifier(nbt.getString("currentEffect"));
            attachment.currentEffect = (BaseTransformativeStatusEffect) Registries.STATUS_EFFECT.get(effectId);
        }
        if (nbt.contains("currentRegEffect", NbtElement.STRING_TYPE)) {
            Identifier effectId = new Identifier(nbt.getString("currentRegEffect"));
            attachment.currentRegEffect = (BaseTransformativeStatusEffect) Registries.STATUS_EFFECT.get(effectId);
        }
        return attachment;
    }
}
