package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HissPhantomPower extends Power {
    private final @Nullable Consumer<Pair<Entity, Entity>> onHissPhantomAction;

    public HissPhantomPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.onHissPhantomAction = data.get("on_hiss_phantom_action");
    }

    public void invokeAction(LivingEntity powerOwner, PhantomEntity phantom) {
        if (this.onHissPhantomAction != null) {
            this.onHissPhantomAction.accept(new Pair<>(powerOwner, phantom));
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("hiss_phantom_power"),
                new SerializableData()
                        .add("on_hiss_phantom_action", ApoliDataTypes.BIENTITY_ACTION, null),
                data -> (type, entity) -> new HissPhantomPower(type, entity, data)
        ).allowCondition();
    }
}
