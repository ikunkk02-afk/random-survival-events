package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentBlockSafety;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FakeChunkVoidEvent implements RandomEvent {
	private static final int BELOW_PLAYER_LIMIT = 16;
	private static final int ABOVE_PLAYER_LIMIT = 32;
	private static final int BASE_MARKER_SKIP_THRESHOLD = 64;

	@Override
	public String getId() {
		return "fake_chunk_void";
	}

	@Override
	public String getName() {
		return "区块塌陷";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.DISASTER;
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return RandomSurvivalEventsConfig.get().temporaryTerrainRestoreTicks;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive() || !config.allowTemporaryTerrainChange) {
			return;
		}

		List<SavedBlock> savedBlocks = collectBlocks(player, world, config.maxTemporaryChangedBlocks);
		if (savedBlocks.isEmpty()) {
			RandomEventUtils.sendMessage(player, "区块塌陷没有找到安全的临时替换范围。");
			return;
		}

		BlockState air = Blocks.AIR.defaultBlockState();
		long expireTick = world.getGameTime() + config.temporaryTerrainRestoreTicks;
		for (SavedBlock savedBlock : savedBlocks) {
			world.setBlockAndUpdate(savedBlock.pos(), air);
			TemporaryBlockChangeManager.add(world, savedBlock.pos(), savedBlock.originalState(), air, expireTick);
		}

		RandomEventUtils.sendMessage(player, "你脚下的区块突然塌陷了。");
	}

	private List<SavedBlock> collectBlocks(ServerPlayer player, ServerLevel world, int maxChangedBlocks) {
		BlockPos playerPos = player.blockPosition();
		ChunkPos chunkPos = new ChunkPos(playerPos);
		int minY = Math.max(world.getMinBuildHeight(), playerPos.getY() - BELOW_PLAYER_LIMIT);
		int maxY = Math.min(world.getMaxBuildHeight() - 1, playerPos.getY() + ABOVE_PLAYER_LIMIT);
		List<SavedBlock> savedBlocks = new ArrayList<>();
		int baseMarkers = 0;

		for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
			for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
				for (int y = minY; y <= maxY; y++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = world.getBlockState(pos);
					if (PunishmentBlockSafety.isBaseMarker(world, pos, state) && ++baseMarkers > BASE_MARKER_SKIP_THRESHOLD) {
						return List.of();
					}
					if (!PunishmentBlockSafety.canTemporarilyRemove(world, pos, state)
							|| TemporaryBlockChangeManager.hasPendingChange(world, pos)) {
						continue;
					}

					savedBlocks.add(new SavedBlock(pos.immutable(), state));
					if (savedBlocks.size() > maxChangedBlocks) {
						return List.of();
					}
				}
			}
		}

		return savedBlocks;
	}

	private record SavedBlock(BlockPos pos, BlockState originalState) {
	}
}
