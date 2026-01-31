package net.onixary.shapeShifterCurseFabric.mixin.accessor;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// 由于AccessWidener无法修改Mod里的类，所以只能用Mixin
@Mixin(PowerTypeRegistry.class)
public interface  PowerTypeRegistryAccessor {
    @Invoker("update")
    public static PowerType Invoke_Update(Identifier id, PowerType powerType) {return null;}
}
