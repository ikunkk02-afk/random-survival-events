package com.ikunkk02afk.randomsurvivalevents.event.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
		if (world == null || pos == null || originalState == null) {
			return;
		}

		add(world, pos, originalState, world.getBlockState(pos), expireTick);
	}

	public static void add(ServerLevel world, BlockPos pos, BlockState originalState, BlockState replacementState, long expireTick) {
		if (world == null || pos == null || originalState == null || replacementState == null) {
			return;
		}

		removePendingChange(world.dimension(), pos);
		CHANGES.add(new TemporaryBlockChange(
				world.dimension(),
				pos.immutable(),
				originalState,
				replacementState,
				expireTick
		));
	}

	public static boolean hasPendingChange(ServerLevel world, BlockPos pos) {
		if (world == null || pos == null) {
			return false;
		}
		return CHANGES.stream().anyMatch(change -> change.dimension().equals(world.dimension()) && change.pos().equals(pos));
	}

	public static void tick(MinecraftServer server) {
		Iterator<TemporaryBlockChange> iterator = CHANGES.iterator();
		while (iterator.hasNext()) {
			TemporaryBlockChange change = iterator.next();
			ServerLevel world = server.getLevel(change.dimension());
			if (world == null || world.getGameTime() < change.expireTick()) {
				continue;
			}

			restore(world, change);
			iterator.remove();
		}
	}

	public static void restoreAll(MinecraftServer server) {
		if (server == null) {
			CHANGES.clear();
			return;
		}

		for (TemporaryBlockChange change : List.copyOf(CHANGES)) {
			ServerLevel world = server.getLevel(change.dimension());
			if (world != null) {
				restore(world, change);
			}
		}
		CHANGES.clear();
	}

	private static void restore(ServerLevel world, TemporaryBlockChange change) {
		if (world.getBlockEntity(change.pos()) != null) {
			return;
		}

		BlockState currentState = world.getBlockState(change.pos());
		if (shouldRestore(currentState, change.replacementState())) {
			world.setBlockAndUpdate(change.pos(), change.originalState());
		}
	}

	private static boolean shouldRestore(BlockState currentState, BlockState replacementState) {
		if (currentState.isAir() && replacementState.isAir()) {
			return true;
		}
		if (currentState.is(Blocks.LAVA) && replacementState.is(Blocks.LAVA)) {
			return true;
		}
		return currentState.is(replacementState.getBlock()) && Objects.equals(currentState.getFluidState(), replacementState.getFluidState());
	}

	private static void removePendingChange(ResourceKey<Level> dimension, BlockPos pos) {
		CHANGES.removeIf(change -> change.dimension().equals(dimension) && change.pos().equals(pos));
	}

	private record TemporaryBlockChange(
			ResourceKey<Level> dimension,
			BlockPos pos,
			BlockState originalState,
			BlockState replacementState,
			long expireTick
	) {
	}
}
