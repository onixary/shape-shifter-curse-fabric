package net.onixary.shapeShifterCurseFabric.util.Verify.KeyManager;

import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.Verify.AuthUtils;
import net.onixary.shapeShifterCurseFabric.util.Verify.KeySegment;
import net.onixary.shapeShifterCurseFabric.util.Verify.VerifyEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RootKeyManager extends KeyManager {
    public RootKeyManager() {
        this.loadFormLocal();
    }

    @Override
    public void mountEvent() {
        return;
    }

    @Override
    public void onKeyLoad(KeySegment keySegment) {
        return;
    }

    public boolean canLoad(KeySegment keySegment) {
        @Nullable KeySegment oldKeySegment = this.getKeySegment(keySegment.getType());
        return oldKeySegment == null || keySegment.getVersion() >= oldKeySegment.getVersion();
    }

    public void loadKey(KeySegment keySegment) {
        if (!canLoad(keySegment)) {
            return;
        }
        @Nullable KeySegment oldKeySegment = this.getKeySegment(keySegment.getType());
        this.keySegments.put(keySegment.getType(), keySegment);
        VerifyEvent.ON_KEY_LOAD.invoker().onKeyLoad(keySegment);
        if (oldKeySegment != null && keySegment.isUseMeltdown() && keySegment.getVersion() > oldKeySegment.getVersion()) {
            VerifyEvent.ON_KEY_MELT.invoker().onKeyMelt(oldKeySegment, keySegment);
        }
        if (oldKeySegment == null || keySegment.getVersion() > oldKeySegment.getVersion()) {
            this.saveKey(keySegment);
        }
    }

    public static Path getLocalKeyFolderPath() { return FabricLoader.getInstance().getConfigDir().resolve("ssc_auth/keys"); }

    public void loadFormLocal() {
        Path folderPath = getLocalKeyFolderPath();
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.warn("Failed to create key folder: " + e.getMessage());
            }
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path path : stream) {
                if (path.getFileName().toString().endsWith(".key")) {
                    KeySegment keySegment = AuthUtils.readKeySegment(new PacketByteBuf(Unpooled.wrappedBuffer(Files.readAllBytes(path))));
                    if (keySegment != null) {
                        this.loadKey(keySegment);
                    }
                }
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to load key segments: " + e.getMessage());
        }
    }

    public void saveKey(KeySegment keySegment) {
        Path folderPath = getLocalKeyFolderPath();
        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            Path filePath = folderPath.resolve(keySegment.getType() + ".key");
            Files.write(filePath, keySegment.getRaw());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to save key segment: " + e.getMessage());
        }
    }
}
