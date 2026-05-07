package com.ikunkk02afk.randomsurvivalevents.event.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class TemporaryBlockChangeManager {
	private static final List<TemporaryBlockChange> CHANGES = new ArrayList<>();

	private TemporaryBlockChangeManager() {
	}

	public static void add(ServerLevel world, BlockPos pos, BlockState originalState, long expireTick) {
		CHANGES.add(new TemporaryBlockChange(world.dimension(), pos.immutable(), originalState, expireTick));
	}

	public static void tick(MinecraftServer server) {
		Iterator<TemporaryBlockChange> iterator = CHANGES.iterator();
		while (iterator.hasNext()) {
			TemporaryBlockChange change = iterator.next();
			ServerLevel world = server.getLevel(change.dimension());
			if (world == null || world.getGameTime() < change.expireTick()) {
				continue;
			}

			BlockState currentState = world.getBlockState(change.pos());
			if (currentState.is(Blocks.SLIME_BLOCK)) {
				world.setBlockAndUpdate(change.pos(), change.originalState());
			}
			iterator.remove();
		}
	}

	private record TemporaryBlockChange(ResourceKey<Level> dimension, BlockPos pos, BlockState originalState, long expireTick) {
	}
}
