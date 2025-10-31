package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class LevitatePower extends Power implements Active {
    // 配置参数
    private float startAscentSpeed = 0.5f;
    private float endAscentSpeed = 0.1f;
    private int accelerationDuration = 20;
    private int maxAscendDuration = 40;
    private Key key;

    // 运行时状态
    private int ascendProgress = 0;
    private boolean wasActiveLastTick = false;
    private boolean isKeyActive = false;
    private boolean isLevitate = false;

    private int accelerationProcess = 0;

    private float getAscendSpeed() {
        return startAscentSpeed + ((endAscentSpeed - startAscentSpeed) * ((float)accelerationProcess / accelerationDuration));
    }

    public LevitatePower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        this.setTicking(true);
    }

    @Override
    public void onUse() {
        if (entity instanceof PlayerEntity player) {
            //handleLevitationInput(player);
            isKeyActive = true;
            PowerHolderComponent.syncPower(entity, this.type);
        }
    }



    @Override
    public void tick() {
        if (entity instanceof PlayerEntity player) {
            if(player.getFluidHeight(FluidTags.WATER) > 0.0F || player.getFluidHeight(FluidTags.LAVA) > 0.0F){
                isLevitate = false;
                ascendProgress = 0;
                return;
            }

            //ShapeShifterCurseFabric.LOGGER.info("IsAscending: " + isAscending);
            if(isKeyActive){
                isLevitate = true;
                accelerationProcess++;
                accelerationProcess = Math.min(accelerationProcess, accelerationDuration);
                if(ascendProgress < maxAscendDuration){
                    // player.setVelocity(player.getVelocity().x, 0, player.getVelocity().z);
                    player.setNoGravity(true);
                    player.setVelocity(player.getVelocity().x, this.getAscendSpeed(), player.getVelocity().z);
                    player.velocityModified = true;
                    ascendProgress ++;
                    //ShapeShifterCurseFabric.LOGGER.info("Ascending Progress: " + ascendProgress);
                }
                else{
                    player.setNoGravity(true);
                    Vec3d velocity = player.getVelocity();
                    if (velocity.y != 0) {
                        player.setVelocity(velocity.x, 0, velocity.z);
                        player.velocityModified = true;
                    }
                }
            }
            else if(!isKeyActive && !wasActiveLastTick){
                accelerationProcess = 0;
                isLevitate = false;
                player.setNoGravity(false);
            }
            player.fallDistance = 0;

            wasActiveLastTick = isKeyActive;
            isKeyActive = false;
            if(entity.isOnGround()){
                isLevitate = false;
                ascendProgress = 0;
            }
            PowerHolderComponent.syncPower(entity, this.type);
        }
    }

    // Active接口实现
    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public boolean isActive() {
        //ShapeShifterCurseFabric.LOGGER.info("isLev: " + isLevitate);
        return this.isLevitate && super.isActive();
    }

    @Override
    public void onRemoved() {
        entity.setNoGravity(false);
    }

    // 工厂方法
    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("levitate"),
                new SerializableData()
                        .add("start_ascent_speed", SerializableDataTypes.FLOAT, 0.5f)
                        .add("end_ascent_speed", SerializableDataTypes.FLOAT, 0.1f)
                        .add("acceleration_duration", SerializableDataTypes.INT, 20)
                        .add("max_ascend_duration", SerializableDataTypes.INT, 40)
                        .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
                data -> (powerType, entity) -> {
                    LevitatePower power = new LevitatePower(powerType, entity);
                    power.startAscentSpeed = data.getFloat("start_ascent_speed");
                    power.endAscentSpeed = data.getFloat("end_ascent_speed");
                    power.accelerationDuration = data.getInt("acceleration_duration");
                    power.maxAscendDuration = data.getInt("max_ascend_duration");
                    power.setKey(data.get("key"));
                    return power;
                }
        ).allowCondition();
    }


}
