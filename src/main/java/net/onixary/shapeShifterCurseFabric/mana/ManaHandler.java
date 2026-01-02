package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ManaHandler {
    // 由于在Server端上没有ClientPlayerEntity 所以通一使用PlayerEntity
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientInit;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerInit;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaTick;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaTick;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaFull;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaFull;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaEmpty;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaEmpty;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaChange;
    private @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaChange;
    private boolean Immutable = false;

    public ManaHandler() {
        this.onClientInit = (component, player) -> {};
        this.onServerInit = (component, player) -> {};
        this.onClientManaTick = (component, player) -> {};
        this.onServerManaTick = (component, player) -> {};
        this.onClientManaFull = (component, player) -> {};
        this.onServerManaFull = (component, player) -> {};
        this.onClientManaEmpty = (component, player) -> {};
        this.onServerManaEmpty = (component, player) -> {};
        this.onClientManaChange = (component, player) -> {};
        this.onServerManaChange = (component, player) -> {};
        this.Immutable = false;
    }

    public ManaHandler(@NotNull BiConsumer<ManaComponent, PlayerEntity> onClientInit,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerInit,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaTick,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaTick,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaFull,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaFull,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaEmpty,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaEmpty,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaChange,
                       @NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaChange
    ) {
        this.onClientInit = onClientInit;
        this.onServerInit = onServerInit;
        this.onClientManaTick = onClientManaTick;
        this.onServerManaTick = onServerManaTick;
        this.onClientManaFull = onClientManaFull;
        this.onServerManaFull = onServerManaFull;
        this.onClientManaEmpty = onClientManaEmpty;
        this.onServerManaEmpty = onServerManaEmpty;
        this.onClientManaChange = onClientManaChange;
        this.onServerManaChange = onServerManaChange;
        this.Immutable = false;
    }

    public ManaHandler setOnClientInit(@NotNull BiConsumer<ManaComponent, PlayerEntity> onClientInit) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onClientInit = onClientInit;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnClientInit() {
        return this.onClientInit;
    }
    public ManaHandler setOnServerInit(@NotNull BiConsumer<ManaComponent, PlayerEntity> onServerInit) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onServerInit = onServerInit;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnServerInit() {
        return this.onServerInit;
    }
    public ManaHandler setOnClientManaTick(@NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaTick) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onClientManaTick = onClientManaTick;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnClientManaTick() {
        return this.onClientManaTick;
    }
    public ManaHandler setOnServerManaTick(@NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaTick) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onServerManaTick = onServerManaTick;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnServerManaTick() {
        return this.onServerManaTick;
    }
    public ManaHandler setOnClientManaFull(@NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaFull) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onClientManaFull = onClientManaFull;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnClientManaFull() {
        return this.onClientManaFull;
    }
    public ManaHandler setOnServerManaFull(@NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaFull) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onServerManaFull = onServerManaFull;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnServerManaFull() {
        return this.onServerManaFull;
    }
    public ManaHandler setOnClientManaEmpty(@NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaEmpty) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onClientManaEmpty = onClientManaEmpty;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnClientManaEmpty() {
        return this.onClientManaEmpty;
    }
    public ManaHandler setOnServerManaEmpty(@NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaEmpty) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onServerManaEmpty = onServerManaEmpty;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnServerManaEmpty() {
        return this.onServerManaEmpty;
    }
    public ManaHandler setOnClientManaChange(@NotNull BiConsumer<ManaComponent, PlayerEntity> onClientManaChange) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onClientManaChange = onClientManaChange;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnClientManaChange() {
        return this.onClientManaChange;
    }
    public ManaHandler setOnServerManaChange(@NotNull BiConsumer<ManaComponent, PlayerEntity> onServerManaChange) {
        if (this.Immutable) { throw new RuntimeException("Cannot modify a immutable ManaHandler"); }
        this.onServerManaChange = onServerManaChange;
        return this;
    }
    public @NotNull BiConsumer<ManaComponent, PlayerEntity> getOnServerManaChange() {
        return this.onServerManaChange;
    }
    public ManaHandler setImmutable() {
        this.Immutable = true;
        return this;
    }
}