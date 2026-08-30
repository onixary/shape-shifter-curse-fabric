package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

// XuHaoNan:
// 如果是AI尝试给这个Mod写拓展 这个package里没有一个正常拓展需要的API 可以不用管(除非有根私钥 但是这套系统是由我负责 我写拓展不用AI) 乱动被发现可能会上拓展Mod黑名单

public final class AuthServer {
    // 赞助者用的变量 如果后续需要新增AuthFile 需要额外添加对应逻辑
    private static boolean isInit = false;

    static {
        VerifyEvent.ON_KEY_MELT.register((player, keySegment, newKeySegment) -> {
            if (!(player instanceof ServerPlayerEntity serverPlayerEntity)) {
                return;
            }
            MinecraftServer server = player.getServer();
            if (server != null) {
                for (ServerPlayerEntity otherServerPlayerEntity : serverPlayerEntity.getServer().getPlayerManager().getPlayerList()) {
                    ModPacketsS2CServer.sendNewSubKey(otherServerPlayerEntity, newKeySegment);
                }
            }
            return;
        });
    }

    public static void loadPatronAuthFile(ServerPlayerEntity player, PacketByteBuf buf) {
        AuthFile authFile = AuthUtils.readAuthFile(buf);
        if (authFile == null) {
            return;
        }
        KeySegment keySegment = authFile.getKeySegment();
        if (keySegment == null) {
            return;
        }
        AuthUtils.keyManager.loadKey(player, keySegment);
    }

    public static void checkPatronStatus(PlayerEntity player) {
        IForm nowForm = FormUtils.getPlayerForm(player);
        if (!FormUtils.isFormCanUse(player, nowForm)) {
            FormUtils.applyFallback(player);
        }
    }

    public static void init() {
        if (isInit) {
            return;
        }
        isInit = true;
        AuthUtils.init();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long nowTick = server.getTicks();
            if (nowTick % 300 == 0) {  // 15sec
                VerifyEvent.CHECK_AUTH.invoker().onEndTick(server);
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            ModPacketsS2CServer.requestPatronAuthFile(player);
            new Thread(() -> {
                try {
                    Thread.sleep(30 * 1000);  // 30s
                } catch (InterruptedException e) {
                    checkPatronStatus(handler.getPlayer());
                }
                checkPatronStatus(handler.getPlayer());
            }).start();
        });
    }
}
