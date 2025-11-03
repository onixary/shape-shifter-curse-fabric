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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.ApoliClientAccessor;

public class LevitatePower extends Power implements Active {
    // 配置参数
    private float ascentSpeed = 0.5f;
    private int maxAscendDuration = 40;
    private Key key;

    // 运行时状态
    private int ascendProgress = 0;  // 客户端使用
    private boolean wasActiveLastTick = false;  // 服务端和客户端共用 不同步
    private boolean isKeyActive = false; // 服务端和客户端共用 不同步
    private boolean isLevitate = false;  // 服务端使用
    // private boolean HasModifyUpVelocity = false;
    // private Vec3d currentVelocity = Vec3d.ZERO;

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
        // this.HasModifyUpVelocity = false;
    }

    private float easeOutQuadProgress(){
        float a = (1 - ((float)this.ascendProgress / (float)this.maxAscendDuration));
        //ShapeShifterCurseFabric.LOGGER.info("a: " + a);
        return a * a;
    }

    // 当按键按下时的处理
    // private void processLevitate(PlayerEntity player) {
    //     //Vec3d velocity = player.getVelocity();
    //     this.isLevitate = true;
    //     if(this.ascendProgress < this.maxAscendDuration) {
    //         // 加入了easing来实现平滑过渡，不过在NoGravity进入时似乎会重置XZ的速度输入
    //         // 先使用旧逻辑(使用无重力+客户端处理) 使用持续修改动量需要在客户端进行
    //         player.setNoGravity(true);
    //         if (!this.HasModifyUpVelocity) {
    //             player.setVelocity(this.currentVelocity.x, this.ascentSpeed * easeOutQuadProgress(), this.currentVelocity.z);
    //             player.velocityModified = true;
    //             this.HasModifyUpVelocity = true;
    //         }
    //         this.ascendProgress++;
    //     }
    //     else {
    //         player.setNoGravity(true);
    //         if (this.currentVelocity.y != 0) {
    //             player.setVelocity(this.currentVelocity.x, 0, this.currentVelocity.z);
    //             player.velocityModified = true;
    //         }
    //         this.HasModifyUpVelocity = false;
    //     }
    // }

    private void processLevitate(PlayerEntity player) {
        // 飞行逻辑由客户端处理 或许需要反作弊系统
        this.isLevitate = true;
        player.setNoGravity(true);
    }

    // 空中缓降
    private void processStopLevitate(PlayerEntity player) {
        this.isLevitate = false;
        player.setNoGravity(false);
        // this.HasModifyUpVelocity = false;
    }

    @Override
    public void tick() {
        if (entity instanceof PlayerEntity player) {
            if(player.getFluidHeight(FluidTags.WATER) > 0.0F || player.getFluidHeight(FluidTags.LAVA) > 0.0F){
                this.resetLevitateState();
                return;
            }
            // this.currentVelocity = player.getVelocity();

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

    @Environment(EnvType.CLIENT)
    private void ClientProcessLevitate(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        if(this.ascendProgress < this.maxAscendDuration) {
            player.setVelocity(velocity.x, this.ascentSpeed * easeOutQuadProgress(), velocity.z);
            this.ascendProgress++;
        }
        else {
            player.setVelocity(velocity.x, 0, velocity.z);
        }
    }

    @Environment(EnvType.CLIENT)
    private void ClientProcessStopLevitate(PlayerEntity player) {
        // 应该会自动同步 就不用手动同步了
        // player.setNoGravity(false);
    }

    @Environment(EnvType.CLIENT)
    public boolean clientIsActive() {
        // 由于客户端无法得到服务器上isKeyActive的值，所以需要通过按键状态来判定.
        KeyBinding keyBinding = ApoliClientAccessor.get_idToKeyBindingMap().get(this.key.key);
        if (keyBinding != null) {
            return keyBinding.isPressed();
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void clientTick(PlayerEntity player) {
        // 上面的数据不一定会同步到客户端 由外部调用 复制时记得给外部添加调用
        if(player.getFluidHeight(FluidTags.WATER) > 0.0F || player.getFluidHeight(FluidTags.LAVA) > 0.0F){
            this.resetLevitateState();
            return;
        }
        this.isKeyActive = this.clientIsActive();
        if(this.isKeyActive || this.wasActiveLastTick){
            this.ClientProcessLevitate(player);
        }
        else {
            this.ClientProcessStopLevitate(player);
        }
        this.wasActiveLastTick = this.isKeyActive;
        this.isKeyActive = false;
        if(entity.isOnGround()){
            this.resetLevitateState();
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
