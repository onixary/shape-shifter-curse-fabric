package net.onixary.shapeShifterCurseFabric.integration.origins.data;

import io.github.apace100.calio.data.SerializableDataType;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Impact;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginUpgrade;

import java.util.List;

public final class OriginsDataTypes {

    public static final SerializableDataType<Impact> IMPACT = SerializableDataType.enumValue(Impact.class);

    public static final SerializableDataType<OriginUpgrade> UPGRADE = new SerializableDataType<>(
        OriginUpgrade.class,
        (buf, upgrade) -> upgrade.write(buf),
        OriginUpgrade::read,
        OriginUpgrade::fromJson);

    public static final SerializableDataType<List<OriginUpgrade>> UPGRADES = SerializableDataType.list(UPGRADE);
}
