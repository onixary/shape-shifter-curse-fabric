package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;

public class PatronSelectItem extends Item {
    public PatronSelectItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ModPacketsS2CServer.OpenPatronFormSelectMenu(((ServerPlayerEntity) user));
        }
        return super.use(world, user, hand);
    }
}
