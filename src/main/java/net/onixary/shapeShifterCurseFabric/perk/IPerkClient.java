package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface IPerkClient {
    public Identifier getID();

    public default Identifier getIcon() {
        Identifier id = this.getID();
        String NameSpace = id.getNamespace();
        String Path = id.getPath();
        return new Identifier(NameSpace, "textures/perks/" + Path + ".png");
    }

    public default Text getName() {
        return IPerkClient.getDefaultName(this.getID());
    }

    public default Text getDesc() {
        return IPerkClient.getDefaultDesc(this.getID());
    }

    public static @NotNull Text getDefaultName(@NotNull Identifier perkName) {
        String NameSpace = perkName.getNamespace();
        String Path = perkName.getPath();
        return Text.translatable("ssc_perk." + NameSpace + "." + Path + ".name");
    }

    public static @NotNull Text getDefaultDesc(@NotNull Identifier perkName) {
        String NameSpace = perkName.getNamespace();
        String Path = perkName.getPath();
        return Text.translatable("ssc_perk." + NameSpace + "." + Path + ".desc");
    }
}
