package net.onixary.shapeShifterCurseFabric.items.accessory;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.lang.reflect.Method;
import java.util.Arrays;

// 换一下方案 使用额外mixin法 大约是我利用一些手段 直接写一个mixin补充Accessory_ICurio
public class Accessory_ICurio implements ICurioItem {
    public final AccessoryItem accessoryItem;

    public Accessory_ICurio(AccessoryItem accessoryItem) {
        this.accessoryItem = accessoryItem;
    }

    // TODO 在 ShapeShifterCurseFabric 里注册一下 记得加一下判断条件 仅启用了Curio 未启用Trinket时调用
    public static void Init() {
        try {
            Method registerCurioFunc = Arrays.stream(CuriosApi.class.getMethods()).filter(method -> method.getName().equals("registerCurio")).findFirst().get();
            for (Item item : Registries.ITEM) {
                if (item instanceof AccessoryItem accessoryItem) {
                    Accessory_ICurio curioItem = new Accessory_ICurio(accessoryItem);
                    registerCurioFunc.invoke(item, curioItem);
                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
