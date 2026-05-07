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

public class VoidCrackEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "void_crack";
	}

	@Override
	public String getName() {
		return "虚空裂缝";
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
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "void_crack");
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

		boolean permanent = DestructiveModeRules.canPermanentlyDamageChunk(config);
		if (!permanent && !config.allowTemporaryTerrainChange) {
			RandomEventUtils.sendMessage(player, "临时地形变化被配置禁用，虚空裂缝被阻止。");
			return;
		}

		List<BlockPos> starts = findCrackStarts(player, world);
		if (starts.isEmpty()) {
			RandomEventUtils.sendMessage(player, "虚空裂缝没有找到可塌陷地面。");
			return;
		}

		BlockState air = Blocks.AIR.defaultBlockState();
		long expireTick = world.getGameTime() + getDefaultEventDurationTicks();
		int changed = 0;
		for (BlockPos start : starts) {
			int depth = RandomEventUtils.randomBetween(1, 3);
			for (int dy = 0; dy < depth; dy++) {
				BlockPos pos = start.below(dy);
				BlockState original = world.getBlockState(pos);
				if (!PunishmentBlockSafety.canTemporarilyRemove(world, pos, original)
						|| TemporaryBlockChangeManager.hasPendingChange(world, pos)) {
					continue;
				}

				world.setBlockAndUpdate(pos, air);
				if (!permanent) {
					TemporaryBlockChangeManager.add(world, pos, original, air, expireTick);
				}
				changed++;
			}
		}

		RandomEventUtils.sendMessage(player, permanent ? "虚空裂缝永久撕开了地面。" : "虚空裂缝撕开了地面，稍后会恢复。");
	}

	private List<BlockPos> findCrackStarts(ServerPlayer player, ServerLevel world) {
		BlockPos center = player.blockPosition();
		List<BlockPos> starts = new ArrayList<>();
		for (int dx = -7; dx <= 7; dx++) {
			for (int dz = -7; dz <= 7; dz++) {
				int dist = dx * dx + dz * dz;
				if (dist < 6 || dist > 54 || RANDOM.nextDouble() > 0.28D) {
					continue;
				}
				BlockPos pos = center.offset(dx, -1, dz);
				if (PunishmentBlockSafety.canTemporarilyRemove(world, pos, world.getBlockState(pos))) {
					starts.add(pos.immutable());
				}
			}
		}
		Collections.shuffle(starts, RANDOM);
		if (starts.size() > 18) {
			return new ArrayList<>(starts.subList(0, 18));
		}
		return starts;
	}
}
