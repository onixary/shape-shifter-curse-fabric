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
    private float ascentSpeed = 0.5f;
    private int maxAscendDuration = 40;
    private Key key;

    // 运行时状态
    private int ascendProgress = 0;
    private boolean wasActiveLastTick = false;
    private boolean isKeyActive = false;
    private boolean isLevitate = false;
    private boolean HasModifyUpVelocity = false;

    public LevitatePower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        this.setTicking(true);
    }

    @Override
    public void onUse() {
        if (entity instanceof PlayerEntity player) {
            //handleLevitationInput(player);
            this.isKeyActive = true;
            PowerHolderComponent.syncPower(entity, this.type);
        }
    }

    // 恢复ascendProgress
    private void resetLevitateState() {
        this.isLevitate = false;
        this.ascendProgress = 0;
        this.HasModifyUpVelocity = false;
    }

    // 当按键按下时的处理
    private void processLevitate(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        this.isLevitate = true;
        if(this.ascendProgress < this.maxAscendDuration) {
            player.setNoGravity(true);
            if (!this.HasModifyUpVelocity) {
                player.setVelocity(velocity.x, this.ascentSpeed, velocity.z);
                player.velocityModified = true;
                this.HasModifyUpVelocity = true;
            }
            this.ascendProgress++;
        }
        else {
            player.setNoGravity(true);
            if (velocity.y != 0) {
                player.setVelocity(velocity.x, 0, velocity.z);
                player.velocityModified = true;
            }
            this.HasModifyUpVelocity = false;
        }
    }

    // 空中缓降
    private void processStopLevitate(PlayerEntity player) {
        this.isLevitate = false;
        player.setNoGravity(false);
        this.HasModifyUpVelocity = false;
    }

    @Override
    public void tick() {
        if (entity instanceof PlayerEntity player) {
            if(player.getFluidHeight(FluidTags.WATER) > 0.0F || player.getFluidHeight(FluidTags.LAVA) > 0.0F){
                this.resetLevitateState();
                return;
            }

            if(this.isKeyActive || this.wasActiveLastTick){
                this.processLevitate(player);
            }
            else {
                this.processStopLevitate(player);
            }

            player.fallDistance = 0;

            this.wasActiveLastTick = this.isKeyActive;
            this.isKeyActive = false;
            if(entity.isOnGround()){
                this.resetLevitateState();
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
                        .add("ascent_speed", SerializableDataTypes.FLOAT, 0.5f)
                        .add("max_ascend_duration", SerializableDataTypes.INT, 40)
                        .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
                data -> (powerType, entity) -> {
                    LevitatePower power = new LevitatePower(powerType, entity);
                    power.ascentSpeed = data.getFloat("ascent_speed");
                    power.maxAscendDuration = data.getInt("max_ascend_duration");
                    power.setKey(data.get("key"));
                    return power;
                }
        ).allowCondition();
    }


}
