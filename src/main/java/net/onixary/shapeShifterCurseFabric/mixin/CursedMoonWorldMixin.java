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
            }
        }
    }

    @Shadow
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    @Unique
    public void shape_shifter_curse$OnCursedMoon(ServerPlayerEntity player,long time) {
        if(time == 6000L){
            // 处于中午时的逻辑
            if(FormAbilityManager.getForm(player) != PlayerForms.ORIGINAL_BEFORE_ENABLE){
                player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
            }
        }
        else if(time >= 13000L){
            CursedMoon.applyMoonEffect(player);
        }
    }
}
