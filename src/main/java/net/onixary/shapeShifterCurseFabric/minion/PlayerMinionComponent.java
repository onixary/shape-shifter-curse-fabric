package net.onixary.shapeShifterCurseFabric.minion;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMinionComponent implements Component, AutoSyncedComponent {
    public ConcurrentHashMap<Identifier, ArrayList<UUID>> minions = new ConcurrentHashMap<>();

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        try {
            NbtCompound minionsNbt = nbtCompound.getCompound("minions");
            for (String key : minionsNbt.getKeys()) {
                NbtList uuidList = minionsNbt.getList(key, 11);
                ArrayList<UUID> uuids = new ArrayList<>();
                for (net.minecraft.nbt.NbtElement nbtElement : uuidList) {
                    uuids.add(NbtHelper.toUuid(nbtElement));
                }
                this.minions.put(new Identifier(key), uuids);
            }
        } catch (IllegalArgumentException e) {
            this.minions = new ConcurrentHashMap<>();
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error reading minions from NBT", e);
            this.minions = new ConcurrentHashMap<>();
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound minionsNbt = new NbtCompound();
        for (Identifier key : this.minions.keySet()) {
            NbtList uuidList = new NbtList();
            for (UUID uuid : this.minions.get(key)) {
                NbtIntArray uuidNBT = NbtHelper.fromUuid(uuid);
                uuidList.add(uuidNBT);
            }
            minionsNbt.put(key.toString(), uuidList);
        }
        nbtCompound.put("minions", minionsNbt);
    }
}
