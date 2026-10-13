package net.onixary.shapeShifterCurseFabric.magic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RegMagic {
    public static HashMap<Identifier, IMagic> magicRegistry = new HashMap<>();

    public static IMagic ACTIVE_SKILL_1 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_1"), RegPerks.FALLBACK_PERK_ICON, "key.shape-shifter-curse.active_skill_1"));
    public static IMagic ACTIVE_SKILL_2 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_2"), RegPerks.FALLBACK_PERK_ICON, "key.shape-shifter-curse.active_skill_2"));
    public static IMagic ACTIVE_SKILL_3 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_3"), RegPerks.FALLBACK_PERK_ICON, "key.shape-shifter-curse.active_skill_3"));
    public static IMagic ACTIVE_SKILL_4 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_4"), RegPerks.FALLBACK_PERK_ICON, "key.shape-shifter-curse.active_skill_4"));
    public static IMagic ACTIVE_SKILL_5 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_5"), RegPerks.FALLBACK_PERK_ICON, "key.shape-shifter-curse.active_skill_5"));
    public static IMagic ACTIVE_SKILL_6 = registerMagic(new PressKeyMagic(ShapeShifterCurseFabric.identifier("active_skill_6"), RegPerks.FALLBACK_PERK_ICON, "key.shape-shifter-curse.active_skill_6"));

    public static IMagic registerMagic(IMagic magic) {
        Identifier id = magic.getId();
        magicRegistry.put(id, magic);
        return magic;
    }

    public static @Nullable IMagic getMagic(Identifier id) {
        return magicRegistry.get(id);
    }

    // 这个函数会被拓展(SSC_Xu_Addon)覆写 其他拓展不要修改 我留着给魔法系统当底子 反正最后也得放入主线中
    public static boolean shouldShowMagicUI(PlayerEntity player) {
        // 需要给形态加个Flag 根据Flag判断是否显示UI
        return false;
    }

    private static final IMagic[] DEFAULT_MAGIC = {ACTIVE_SKILL_1, ACTIVE_SKILL_2, ACTIVE_SKILL_3, ACTIVE_SKILL_4, ACTIVE_SKILL_5, ACTIVE_SKILL_6};

    // 和上面的一样 会被覆写
    public static IMagic[] getPlayerMagic(PlayerEntity player) {
        return DEFAULT_MAGIC;
    }
}
