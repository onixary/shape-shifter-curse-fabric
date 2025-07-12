package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
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

    @Inject(at=@At("HEAD"), method = "tickBlockEntities")
    public void tickBlockEntities(CallbackInfo info) {
        CursedMoon.day_time = getTimeOfDay();
        CursedMoon.day = (int)(getTimeOfDay()/ 24000L)+1;
        //ShapeShifterCurseFabric.LOGGER.info("Day: "+CursedMoon.day + "Time: "+CursedMoon.day_time);
    }

    @Inject(at=@At("HEAD"), method = "tickEntity")
    public <T extends Entity> void tickEntity(Consumer<T> tickConsumer, T entity, CallbackInfo info) {
        if(entity instanceof ServerPlayerEntity player){
            long timeDayMoon = CursedMoon.day_time - (CursedMoon.day -1)*24000L;

            if (timeDayMoon < 20 && CursedMoon.midday_message_sent) {
                CursedMoon.midday_message_sent = false;
            }

            if(CursedMoon.isCursedMoon()){
                if(CursedMoon.isNight() && player.isSleeping()){
                    player.wakeUp();
                }
                shape_shifter_curse$OnCursedMoon(player,timeDayMoon);
            }
            else{
                if(CursedMoon.moon_effect_applied){
                    CursedMoon.moon_effect_applied = false;
                }
                if(CursedMoon.end_moon_effect_applied){
                    CursedMoon.end_moon_effect_applied = false;
                }
            }
        }
    }

    @Shadow
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    @Unique
    public void shape_shifter_curse$OnCursedMoon(ServerPlayerEntity player,long time) {
        if(time >= 6000L && time < 12500L && !CursedMoon.midday_message_sent){
            CursedMoon.midday_message_sent = true;

            // 处于中午时的逻辑
            if(FormAbilityManager.getForm(player) != PlayerForms.ORIGINAL_BEFORE_ENABLE){
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
        else if(time >= 23000L){
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
                }
            }
        }
    }
}
