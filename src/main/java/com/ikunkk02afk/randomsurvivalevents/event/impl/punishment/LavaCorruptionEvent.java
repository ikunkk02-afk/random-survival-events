package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentBlockSafety;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LavaCorruptionEvent implements RandomEvent {
	private static final int TEMPORARY_DURATION_TICKS = 20 * 20;
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "lava_corruption";
	}

	@Override
	public String getName() {
		return "岩浆侵蚀";
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
	public ResourceLocation getStatusEffectId() {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "lava_corruption");
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return TEMPORARY_DURATION_TICKS;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		boolean permanent = DestructiveModeRules.canPermanentlyPlaceLava(config);
		if (!permanent && !config.allowTemporaryTerrainChange) {
			RandomEventUtils.sendMessage(player, "临时地形变化被配置禁用，岩浆侵蚀被阻止。");
			return;
		}

		List<BlockPos> candidates = findCandidates(player, world);
		if (candidates.isEmpty()) {
			RandomEventUtils.sendMessage(player, "岩浆侵蚀没有找到安全地面。");
			return;
		}

		Collections.shuffle(candidates, RANDOM);
		int count = Math.min(RandomEventUtils.randomBetween(8, 18), candidates.size());
		long expireTick = world.getGameTime() + TEMPORARY_DURATION_TICKS;
		for (int i = 0; i < count; i++) {
			BlockPos pos = candidates.get(i);
			BlockState original = world.getBlockState(pos);
			BlockState replacement = RANDOM.nextDouble() < 0.45D
					? Blocks.LAVA.defaultBlockState()
					: Blocks.BLACKSTONE.defaultBlockState();
			world.setBlockAndUpdate(pos, replacement);
			if (!permanent) {
				TemporaryBlockChangeManager.add(world, pos, original, replacement, expireTick);
			}
		}

		RandomEventUtils.sendMessage(player, permanent ? "岩浆永久侵蚀了附近地面。" : "岩浆侵蚀了附近地面，但裂痕还会恢复。");
	}

	private List<BlockPos> findCandidates(ServerPlayer player, ServerLevel world) {
		BlockPos center = player.blockPosition();
		List<BlockPos> candidates = new ArrayList<>();
		for (int dx = -5; dx <= 5; dx++) {
			for (int dz = -5; dz <= 5; dz++) {
				if (dx * dx + dz * dz > 30 || Math.abs(dx) + Math.abs(dz) < 2 || RANDOM.nextBoolean()) {
					continue;
				}

				BlockPos pos = center.offset(dx, -1, dz);
				BlockState state = world.getBlockState(pos);
				if (PunishmentBlockSafety.canTemporarilyRemove(world, pos, state)
						&& !TemporaryBlockChangeManager.hasPendingChange(world, pos)) {
					candidates.add(pos.immutable());
				}
			}
		}
		return candidates;
	}
}
