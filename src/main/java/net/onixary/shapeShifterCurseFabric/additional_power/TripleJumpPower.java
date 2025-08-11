package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Consumer;

public class TripleJumpPower extends Power {

    private final ActionFactory<Entity>.Instance firstJumpAction;
    private final ActionFactory<Entity>.Instance secondJumpAction;
    private final ActionFactory<Entity>.Instance thirdJumpAction;
    private final float firstJumpMultiplier;
    private final float secondJumpMultiplier;
    private final float thirdJumpMultiplier;
    private final int resetTicksOnGround;

    private int jumpCount = 0;
    private int ticksOnGround = 0;
    private float activeMultiplier = 1.0f; // 用于存储当前跳跃的倍率

    public TripleJumpPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance firstJumpAction, ActionFactory<Entity>.Instance secondJumpAction, ActionFactory<Entity>.Instance thirdJumpAction, float firstJumpMultiplier, float secondJumpMultiplier, float thirdJumpMultiplier, int resetTicksOnGround) {
        super(type, entity);
        this.firstJumpAction = firstJumpAction;
        this.secondJumpAction = secondJumpAction;
        this.thirdJumpAction = thirdJumpAction;
        this.firstJumpMultiplier = firstJumpMultiplier;
        this.secondJumpMultiplier = secondJumpMultiplier;
        this.thirdJumpMultiplier = thirdJumpMultiplier;
        this.resetTicksOnGround = resetTicksOnGround;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        boolean shouldReset = false;
        if (!entity.isSprinting()) {
            shouldReset = true;
        }
        if (entity.isOnGround()) {
            ticksOnGround++;
            if (ticksOnGround > resetTicksOnGround) {
                shouldReset = true;
            }
        } else {
            ticksOnGround = 0;
        }
        if (shouldReset) {
            jumpCount = 0;
        }
    }

    // 在跳跃前被 Mixin 调用，用于更新状态
    public void onJump() {
        if (!isActive() || !entity.isOnGround() || !entity.isSprinting()) {
            this.activeMultiplier = 1.0f;
            return;
        }

        ticksOnGround = 0;
        jumpCount++;

        ActionFactory<Entity>.Instance action = null;

        switch (jumpCount) {
            case 1:
                this.activeMultiplier = this.firstJumpMultiplier;
                action = this.firstJumpAction;
                break;
            case 2:
                this.activeMultiplier = this.secondJumpMultiplier;
                action = this.secondJumpAction;
                break;
            case 3:
                this.activeMultiplier = this.thirdJumpMultiplier;
                action = this.thirdJumpAction;
                jumpCount = 0; // 第三跳后重置
                break;
            default:
                // 如果出现意外情况，重置计数并使用默认倍率
                jumpCount = 0;
                this.activeMultiplier = 1.0f;
                break;
        }

        if (action != null) {
            action.accept(entity);
        }
    }

    // 为 Mixin 提供当前的跳跃倍率
    public float getActiveJumpMultiplier() {
        return this.activeMultiplier;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("triple_jump"),
                new SerializableData()
                        .add("first_jump_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("second_jump_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("third_jump_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("first_jump_multiplier", SerializableDataTypes.FLOAT, 1.0f)
                        .add("second_jump_multiplier", SerializableDataTypes.FLOAT, 1.5f)
                        .add("third_jump_multiplier", SerializableDataTypes.FLOAT, 2.0f)
                        .add("reset_ticks_on_ground", SerializableDataTypes.INT, 10),
                data -> (type, entity) -> new TripleJumpPower(
                        type,
                        entity,
                        data.get("first_jump_action"),
                        data.get("second_jump_action"),
                        data.get("third_jump_action"),
                        data.getFloat("first_jump_multiplier"),
                        data.getFloat("second_jump_multiplier"),
                        data.getFloat("third_jump_multiplier"),
                        data.getInt("reset_ticks_on_ground")
                )
        ).allowCondition();
    }
}
