package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

// Server:
//      玩家进入时:
//          开始30s计时 等待AuthFile 先不执行还原
//      收到客户端AuthFile: (由AuthUtils负责)
//          检查本地Key 如果AuthFile需要熔断当前密钥 触发熔断
//          将AuthFile写入内存
//      密钥熔断时:
//          将旧Key写入forgive组 并使用新Key替换 并将新Key落盘 (由AuthUtils负责)
//          向所有玩家发送新密钥
//      每5s:
//          给每个玩家检查内存中是否有有效认证文件Object 如果没有 触发回调中的还原
//          检查forgive组是否有失效密钥 如果有失效 对当前存储的AuthFile进行检查 如果有AuthFile失效 触发回调中的还原 (由AuthUtils负责)

public final class AuthServer {
    // 赞助者用的变量 如果后续需要新增AuthFile 需要额外添加对应逻辑
    private static final HashMap<UUID, AuthFile> PATRON_AUTH_FILES = new HashMap<>();

    public static void loadPatronAuthFile(ServerPlayerEntity player, PacketByteBuf buf) {
        AuthFile authFile = AuthUtils.readAuthFile(buf);
        if (authFile == null) {
            return;
        }
        // 检查密钥可用性
        KeySegment keySegment = authFile.getKeySegment();
        if (keySegment == null) {
            return;
        }
        if (AuthUtils.loadKey(keySegment)) {
            // TODO 通知所有玩家更新密钥
        }
        if (!AuthUtils.isKeyCanUse(keySegment)) {
            return;
        }
        AuthFile oldAuthFile = PATRON_AUTH_FILES.get(player.getUuid());
        PATRON_AUTH_FILES.put(player.getUuid(), authFile);
        if (oldAuthFile != null) {
            oldAuthFile.onUpdate(authFile);
        }
        authFile.onGain();
    }

    public static void checkPatronAuthFile() {
        for (UUID uuid : PATRON_AUTH_FILES.keySet()) {
            AuthFile authFile = PATRON_AUTH_FILES.get(uuid);
            if (authFile == null) {
                continue;
            }
            KeySegment keySegment = authFile.getKeySegment();
            if (keySegment == null) {
                authFile.onLost();
                PATRON_AUTH_FILES.remove(uuid);
                continue;
            }
            if (!AuthUtils.isKeyCanUse(keySegment)) {
                authFile.onLost();
                PATRON_AUTH_FILES.remove(uuid);
                continue;
            }
        }
    }
}
