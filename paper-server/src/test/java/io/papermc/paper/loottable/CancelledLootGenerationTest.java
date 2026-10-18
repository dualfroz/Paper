package io.papermc.paper.loottable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Normal
class CancelledLootGenerationTest {

    private static final ResourceKey<LootTable> LOOT_TABLE_KEY = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("test", "pot"));

    private LootTable lootTable;
    private DecoratedPotBlockEntity pot;

    @BeforeEach
    void setup() {
        this.lootTable = mock(LootTable.class);
        final ReloadableServerRegistries.Holder registries = mock(ReloadableServerRegistries.Holder.class);
        when(registries.getLootTable(LOOT_TABLE_KEY)).thenReturn(this.lootTable);
        final MinecraftServer server = mock(MinecraftServer.class);
        when(server.reloadableRegistries()).thenReturn(registries);
        final ServerLevel level = mock(ServerLevel.class);
        when(level.getServer()).thenReturn(server);

        this.pot = new DecoratedPotBlockEntity(BlockPos.ZERO, Blocks.DECORATED_POT.defaultBlockState());
        this.pot.setLevel(level);
        this.pot.setLootTable(LOOT_TABLE_KEY, 42L);
    }

    @Test
    void cancelledGenerationKeepsLootTable() {
        when(this.lootTable.fill(any(), any(), anyLong())).thenReturn(false);
        this.pot.unpackLootTable(null);
        assertEquals(LOOT_TABLE_KEY, this.pot.getLootTable());
        assertEquals(42L, this.pot.getLootTableSeed());
    }

    @Test
    void successfulGenerationClearsLootTable() {
        when(this.lootTable.fill(any(), any(), anyLong())).thenReturn(true);
        this.pot.unpackLootTable(null);
        assertNull(this.pot.getLootTable());
    }

    @Test
    void forcedClearIgnoresCancelledGeneration() {
        when(this.lootTable.fill(any(), any(), anyLong())).thenReturn(false);
        this.pot.unpackLootTable(null, true);
        assertNull(this.pot.getLootTable());
    }
}
