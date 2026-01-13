package net.onixary.shapeShifterCurseFabric.mana;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class ManaRegistriesClient {

    private static final HashMap<Identifier, IManaRender> manaRenderRegistry = new HashMap<>();

    public static void registerManaTypeRender(Identifier identifier, @NotNull IManaRender render) {
        manaRenderRegistry.put(identifier, render);
    }

    public static boolean hasManaRender(@Nullable Identifier identifier) {
        return manaRenderRegistry.containsKey(identifier);
    }

    public static @Nullable IManaRender getManaRender(@Nullable Identifier identifier) {
        return manaRenderRegistry.get(identifier);
    }

    static {
        registerManaTypeRender(ManaRegistries.FAMILIAR_FOX_MANA, new FamiliarFoxManaBar());
        registerManaTypeRender(ManaRegistries.DP_MANA, new FamiliarFoxManaBar());
    }

    public static void register() {}
}
