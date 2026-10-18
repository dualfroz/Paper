package io.papermc.paper.configuration.transformation.world.versioned;

import io.papermc.paper.configuration.type.number.IntOr;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;

import static org.spongepowered.configurate.NodePath.path;

/**
 * The pillager patrol start day used to default to 5, which always overrode the
 * vanilla {@code can_pillager_patrol_spawn} environment attribute timeline.
 * The old default is replaced with "default" unless the per-player start is enabled.
 */
public final class V32_PillagerPatrolStartDay implements TransformAction {

    private static final int VERSION = 32;
    private static final int OLD_DEFAULT_DAY = 5;
    private static final NodePath START_DAY_PATH = path("entities", "behavior", "pillager-patrols", "start", "day");
    private static final String PER_PLAYER_KEY = "per-player";

    private static final V32_PillagerPatrolStartDay INSTANCE = new V32_PillagerPatrolStartDay();

    private V32_PillagerPatrolStartDay() {
    }

    public static void apply(final ConfigurationTransformation.VersionedBuilder builder) {
        builder.addVersion(VERSION, ConfigurationTransformation.builder().addAction(START_DAY_PATH, INSTANCE).build());
    }

    @Override
    public Object @Nullable [] visitPath(final NodePath path, final ConfigurationNode value) throws ConfigurateException {
        if (value.getInt() == OLD_DEFAULT_DAY && !value.parent().node(PER_PLAYER_KEY).getBoolean(false)) {
            value.set(IntOr.Default.USE_DEFAULT);
        }
        return null;
    }
}
