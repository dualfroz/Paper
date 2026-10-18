package io.papermc.paper.configuration;

import io.papermc.paper.configuration.type.number.IntOr;
import java.nio.file.Path;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Normal
public class PillagerPatrolStartDayTransformationTest {

    @TempDir
    Path configFolder;

    @Test
    public void oldDefaultDayBecomesDefault() throws ConfigurateException {
        final ConfigurationNode worldNode = this.migrate(5, false);
        assertEquals("default", startNode(worldNode).node("day").getString());
        assertEquals(WorldConfiguration.CURRENT_VERSION, worldNode.node(Configuration.VERSION_FIELD).getInt());
    }

    @Test
    public void perPlayerStartKeepsDay() throws ConfigurateException {
        assertEquals(5, startNode(this.migrate(5, true)).node("day").getInt());
    }

    @Test
    public void customDayIsKept() throws ConfigurateException {
        assertEquals(7, startNode(this.migrate(7, false)).node("day").getInt());
    }

    private ConfigurationNode migrate(final int day, final boolean perPlayer) throws ConfigurateException {
        final ConfigurationNode worldNode = YamlConfigurationLoader.builder()
            .defaultOptions(options -> options.serializers(builder -> builder.register(IntOr.Default.SERIALIZER)))
            .build()
            .createNode();
        worldNode.node(Configuration.VERSION_FIELD).set(31);
        startNode(worldNode).node("day").set(day);
        startNode(worldNode).node("per-player").set(perPlayer);
        new PaperConfigurations(this.configFolder).applyWorldConfigTransformations(null, worldNode, null);
        return worldNode;
    }

    private static ConfigurationNode startNode(final ConfigurationNode worldNode) {
        return worldNode.node("entities", "behavior", "pillager-patrols", "start");
    }
}
