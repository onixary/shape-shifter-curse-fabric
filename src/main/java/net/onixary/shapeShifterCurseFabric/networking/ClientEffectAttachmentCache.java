package net.onixary.shapeShifterCurseFabric.networking;

import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

public class ClientEffectAttachmentCache {
    private static PlayerEffectAttachment cachedAttachment = new PlayerEffectAttachment();

    public static void update(NbtCompound nbt) {
        ShapeShifterCurseFabric.LOGGER.info("Attachment cache updated, get nbt: " + nbt);
        cachedAttachment = PlayerEffectAttachment.fromNbt(nbt);
        ShapeShifterCurseFabric.LOGGER.info("Attachment cache updated: " + cachedAttachment.currentToForm);
    }

    public static PlayerEffectAttachment getAttachment() {
        return cachedAttachment;
    }
}
