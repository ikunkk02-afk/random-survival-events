package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WaterToLavaEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "water_to_lava";
	}

	@Override
	public String getName() {
		return "水源异变";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.EPIC;
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return getDefaultEventDurationTicks();
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		boolean permanent = DestructiveModeRules.canPermanentlyReplaceWaterWithLava(config);
		if (!permanent && !config.allowTemporaryTerrainChange) {
			RandomEventUtils.sendMessage(player, "附近的水开始沸腾，但临时地形变化已被配置阻止。");
			return;
		}

		List<BlockPos> candidates = findWaterSources(player, world);
		if (candidates.isEmpty()) {
			RandomEventUtils.sendMessage(player, "附近的水开始沸腾，但没有找到安全水源。");
			return;
		}

		Collections.shuffle(candidates, RANDOM);
		int count = Math.min(RandomEventUtils.randomBetween(2, 6), candidates.size());
		long expireTick = world.getGameTime() + getDefaultEventDurationTicks();
		for (int i = 0; i < count; i++) {
			BlockPos pos = candidates.get(i);
			BlockState original = world.getBlockState(pos);
			BlockState replacement = Blocks.LAVA.defaultBlockState();
			world.setBlockAndUpdate(pos, replacement);
			if (!permanent) {
				TemporaryBlockChangeManager.add(world, pos, original, replacement, expireTick);
			}
		}

		RandomEventUtils.sendMessage(player, "附近的水开始沸腾。");
	}

	private List<BlockPos> findWaterSources(ServerPlayer player, ServerLevel world) {
		BlockPos center = player.blockPosition();
		List<BlockPos> candidates = new ArrayList<>();
		for (int dx = -8; dx <= 8; dx++) {
			for (int dy = -3; dy <= 3; dy++) {
				for (int dz = -8; dz <= 8; dz++) {
					if (dx * dx + dy * dy + dz * dz > 64 || RANDOM.nextDouble() > 0.35D) {
						continue;
					}

					BlockPos pos = center.offset(dx, dy, dz);
					BlockState state = world.getBlockState(pos);
					if (state.is(Blocks.WATER)
							&& state.getFluidState().isSource()
							&& !TemporaryBlockChangeManager.hasPendingChange(world, pos)
							&& !isNearChest(world, pos, 3)) {
						candidates.add(pos.immutable());
					}
				}
			}
		}
		return candidates;
	}

	private boolean isNearChest(ServerLevel world, BlockPos pos, int radius) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					BlockState state = world.getBlockState(pos.offset(dx, dy, dz));
					Block block = state.getBlock();
					if (state.is(Blocks.CHEST)
							|| state.is(Blocks.TRAPPED_CHEST)
							|| state.is(Blocks.ENDER_CHEST)
							|| block instanceof ShulkerBoxBlock) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
