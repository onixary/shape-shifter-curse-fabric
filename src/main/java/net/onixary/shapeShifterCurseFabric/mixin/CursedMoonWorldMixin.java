package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;


@Mixin(World.class)
public abstract class CursedMoonWorldMixin implements WorldAccess, AutoCloseable {
    @Mutable
    @Final
    @Shadow
    protected final MutableWorldProperties properties;

    protected CursedMoonWorldMixin(MutableWorldProperties properties) {
        this.properties = properties;
    }

    @Unique
    private int LastResetCursedMoonDay = -1;

    /*@Inject(at=@At("HEAD"), method = "tickBlockEntities")
    public void tickBlockEntities(CallbackInfo info) {
        CursedMoon.day_time = getTimeOfDay();
        CursedMoon.day = (int)(getTimeOfDay()/ 24000L)+1;

        World self = (World) (Object) this;
        if (self.isClient()) return;

        ServerWorld world = (ServerWorld) self;
        long worldTime = world.getTimeOfDay();
        int currentDay = (int) (worldTime / 24000L) + 1;

        CursedMoonData data = ShapeShifterCurseFabric.cursedMoonData.getInstance();

        if(data.lastCheckedDay <= 0){
            data.load(world);
        }

        // 新的一天重置逻辑
        if (currentDay > data.lastCheckedDay) {
            data.lastCheckedDay = currentDay;
            CursedMoon.resetCursedMoonForNewDay(world);
            ShapeShifterCurseFabric.LOGGER.info("Cursed moon data saved by CursedMoonWorldMixin new day reset");
            data.save(world);

            // 重置所有玩家的效果状态
            for (ServerPlayerEntity player : world.getPlayers()) {
                CursedMoon.resetMoonEffect(player);
            }
        }

        // 概率检查（白天）
        if (world.getTime() % 20 == 0 && CursedMoon.isDaytime(world)) {
            CursedMoon.checkCursedMoonProbability(world);
        }

        // 同步所有玩家状态
        if (world.getTime() % 100 == 0) { // 每5秒同步一次
            boolean isCursed = CursedMoon.isCursedMoon();
            boolean isNight = CursedMoon.isNight(world);

            for (ServerPlayerEntity player : world.getPlayers()) {
                ModPacketsS2C.sendCursedMoonData(player, worldTime, currentDay, isCursed, isNight);
            }
        }
    }

    @Inject(at=@At("HEAD"), method = "tickEntity")
    public <T extends Entity> void tickEntity(Consumer<T> tickConsumer, T entity, CallbackInfo info) {
        if(entity instanceof ServerPlayerEntity player){

            World world = player.getWorld();
            // 获取当前世界时间
            long worldTime = world.getTimeOfDay();
            int currentDay = (int) (worldTime / 24000L) + 1;
            long timeOfDay = worldTime % 24000L;

            // 每20刻（1秒）同步一次CursedMoon状态到客户端
            if (player.getWorld().getTime() % 20 == 0) {
                boolean currentIsCursedMoon = CursedMoon.isCursedMoon();
                boolean currentIsNight = CursedMoon.isNight();
                ModPacketsS2C.sendCursedMoonData(player, CursedMoon.day_time, CursedMoon.day, currentIsCursedMoon, currentIsNight);
            }

            if(CursedMoon.isCursedMoon()){
                if(player.isSleeping()){
                    player.wakeUp();
                }
                shape_shifter_curse$OnCursedMoon(player,timeOfDay);
            }
            else{
                // CursedMoon结束时的处理
                PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
                if (formComp.isMoonEffectApplied() || formComp.isEndMoonEffectApplied()) {
                    //ModPacketsS2C.sendCursedMoonData(player, CursedMoon.day_time, CursedMoon.day, false, false);
                    // 重置该玩家的状态
                    CursedMoon.resetMoonEffect(player);
                }
            }
        }
    }*/

    @Inject(at=@At("HEAD"), method = "tickBlockEntities")
    public void tickBlockEntities(CallbackInfo info) {
        World self = (World) (Object) this;
        CursedMoon.day_time = self.getTimeOfDay();
        CursedMoon.day = (int)(CursedMoon.day_time / 24000L) + 1;

        if (self.isClient()) return;

        ServerWorld world = (ServerWorld) self;
        long worldTime = CursedMoon.day_time;
        int currentDay = CursedMoon.day;

        // 新的一天重置逻辑
        if (LastResetCursedMoonDay != currentDay) {
            LastResetCursedMoonDay = currentDay;
            CursedMoon.resetCursedMoonForNewDay(world);
        //     CursedMoon.midday_message_sent = false;
            // 重置所有玩家的效果状态
            for (ServerPlayerEntity player : world.getPlayers()) {
                PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
                if (formComp.isMoonEffectApplied() && !formComp.isEndMoonEffectApplied()) {
                    CursedMoon.applyEndMoonEffect(player);
                }
                CursedMoon.resetMoonEffect(player);
            }
        }

        // 同步所有玩家状态
        if (world.getTime() % 100 == 0) { // 每5秒同步一次
            boolean isCursed = CursedMoon.isCursedMoon(world);
            boolean isNight = CursedMoon.isNight(world);

            for (ServerPlayerEntity player : world.getPlayers()) {
                ModPacketsS2CServer.sendCursedMoonData(player, worldTime, currentDay, isCursed, isNight);
            }
        }
    }

    @Inject(at=@At("HEAD"), method = "tickEntity")
    public <T extends Entity> void tickEntity(Consumer<T> tickConsumer, T entity, CallbackInfo info) {
        if(entity instanceof ServerPlayerEntity player){

            World world = player.getWorld();
            // 获取当前世界时间
            long worldTime = world.getTimeOfDay();
            int currentDay = (int) (worldTime / 24000L) + 1;
            long timeOfDay = worldTime % 24000L;

            // 每20刻（1秒）同步一次CursedMoon状态到客户端
            if (world.getTime() % 20 == 0) {
                boolean currentIsCursedMoon = CursedMoon.isCursedMoon(world); // 使用带参数的版本
                boolean currentIsNight = CursedMoon.isNight(world);
                ModPacketsS2CServer.sendCursedMoonData(player, CursedMoon.day_time, CursedMoon.day, currentIsCursedMoon, currentIsNight);
            }

            if(CursedMoon.isCursedMoon(world)){ // 使用带参数的版本
                if(player.isSleeping() && !ShapeShifterCurseFabric.commonConfig.allowSleepInCursedMoon){
                    player.wakeUp();
                }
                shape_shifter_curse$OnCursedMoon(player,timeOfDay);
            }
            else{
                // CursedMoon结束时的处理
                shape_shifter_curse$OnNoCursedMoon(player,timeOfDay);
            }
        }
    }

    @Shadow
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    @Unique
    public void shape_shifter_curse$OnNoCursedMoon(ServerPlayerEntity player, long time) {
//        if (time % 20 != 0) {
//            return;
//        }
        PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
        if (formComp.isMoonEffectApplied() || formComp.isEndMoonEffectApplied()) {
            // 重置该玩家的状态
            CursedMoon.resetMoonEffect(player);
        }
        else if (formComp.isMoonEffectApplied() && !formComp.isEndMoonEffectApplied()) {
            CursedMoon.applyEndMoonEffect(player);
            CursedMoon.midday_message_sent = false;
        }
    }

    @Unique
    public void shape_shifter_curse$OnCursedMoon(ServerPlayerEntity player, long time) {
//        if (time % 20 != 0) {
//            return;
//        }
        if(time >= 6000L && time < 12500L && !CursedMoon.midday_message_sent){
            CursedMoon.midday_message_sent = true;

            // 处于中午时的逻辑
            if(!FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)){
                if(player.getWorld().getRegistryKey() != World.OVERWORLD){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon_nether").formatted(Formatting.LIGHT_PURPLE));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                }
            }
        }
        else if(time >= 12500L && time < 23000L){
            CursedMoon.applyMoonEffect(player);
            CursedMoon.midday_message_sent = false;
        }
        else if(time >= 23000L || time < 6000L){
            CursedMoon.applyEndMoonEffect(player);
            CursedMoon.midday_message_sent = false;
        }
        else if(time > 6000L) {

            // 使用游戏刻来控制频率（20刻 = 1秒）
            if (player.getWorld().getTime() % 20 == 0) {
                boolean wasByCursedMoon = RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon();
                if (wasByCursedMoon) {
                    // 只有当标记存在时才记录日志，避免过多日志
                    ShapeShifterCurseFabric.LOGGER.info("Forced clear of cursed moon flag for player " +
                            player.getName().getString() + " during non-cursed-moon period");

                    RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(false);
                    RegPlayerFormComponent.PLAYER_FORM.sync(player);
                    FormAbilityManager.saveForm(player);
                }
            }
        }
    }
}
