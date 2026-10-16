package net.onixary.shapeShifterCurseFabric.magic;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.util.util.CachedDataMap;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.magic.RegMagic.*;

public class MagicUtils {
    // 需要2个网络包 一个单次触发(仅包含魔法ID) 一个持续触发(包含魔法ID, 是否按下状态(boolean) 需要考虑网络丢包问题 客户端每10tick发一个包 服务器端超过20tick没收到就自动停止触发)
    public static final CachedDataMap<UUID, PlayerEntity, @Nullable IMagic> nowUsingMagic = new CachedDataMap<>(player -> null, PlayerEntity::getUuid);
    public static final int MAGIC_SLOT_COUNT = 6;

    // 仅客户端数据 不会持久存储 释放魔法是客户端发送释放魔法的包 服务器只知道玩家装备了那些技能 但不知道当前装备(单槽)的是什么
    public static int nowSelectMagicIndex = 0;
    // 还是迁到Component里吧 双端都用这个数据
    public static final long[] nowMagicCooldown = new long[MAGIC_SLOT_COUNT];
    public static final boolean[] nowMagicUsable = new boolean[MAGIC_SLOT_COUNT];

    // 这个函数会被拓展(SSC_Xu_Addon)覆写 其他拓展不要修改 我留着给魔法系统当底子 反正最后也得放入主线中
    public static boolean shouldShowMagicUI(PlayerEntity player) {
        // 需要给形态加个Flag 根据Flag判断是否显示UI
        // 开发中 先True一下
        return true;
    }

    private static final IMagic[] DEFAULT_MAGIC = {ACTIVE_SKILL_1, ACTIVE_SKILL_2, ACTIVE_SKILL_3, ACTIVE_SKILL_4, ACTIVE_SKILL_5, ACTIVE_SKILL_6};

    // 和上面的一样 会被覆写
    public static IMagic[] getPlayerMagic(PlayerEntity player) {
        return DEFAULT_MAGIC;
    }

    public static IMagic getPlayerMagic(PlayerEntity player, int index) {
        return getPlayerMagic(player)[index];
    }

    public static IMagic getNowSelectMagic(PlayerEntity player) {
        return getPlayerMagic(player, nowSelectMagicIndex);
    }

    public static boolean isMagicInCooldown(PlayerEntity player, IMagic magic) {
        return false;
    }

    public static void triggerCooldown(PlayerEntity player, IMagic magic) {
        return;
    }

    public static void invokeNowMagic_CLIENT(PlayerEntity player, boolean continuous) {
        if (player.getWorld().isClient) {
            IMagic nowMagic = getNowSelectMagic(player);
            if (nowMagic == null) {
                return;
            }
            if (nowMagic.virtualInvoke()) {
                return;
            }
            if (nowMagic.continuous() || !continuous) {
                nowMagic.onInvokeClient(player);
            }
        }
    }

    public static void invokeNowMagic_SERVER(PlayerEntity player, boolean continuous) {
        if (!player.getWorld().isClient) {
            IMagic nowMagic = nowUsingMagic.get(player);
            if (nowMagic == null) {
                return;
            }
            if (isMagicInCooldown(player, nowMagic) || !nowMagic.canInvoke(player) || nowMagic.virtualInvoke()) {
                return;
            }
            if (nowMagic.continuous() || !continuous) {
                nowMagic.onInvokeServer(player);
            }
        }
    }
}
