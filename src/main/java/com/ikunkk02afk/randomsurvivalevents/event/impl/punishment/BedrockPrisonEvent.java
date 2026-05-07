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
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BedrockPrisonEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "bedrock_prison";
	}

	@Override
	public String getName() {
		return "基岩牢笼";
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
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "doom_mark");
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
		if (!isSafeArea(player, world)) {
			RandomEventUtils.sendMessage(player, "附近太接近出生点或疑似基地，基岩牢笼被阻止。");
			return;
		}

		boolean keepSomePermanent = DestructiveModeRules.canPermanentlyDamageChunk(config);
		List<BlockPos> positions = cagePositions(player.blockPosition());
		if (positions.isEmpty()) {
			return;
		}

		int durationTicks = getDefaultEventDurationTicks();
		long expireTick = world.getGameTime() + durationTicks;
		int placed = 0;
		int permanent = 0;
		BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
		for (BlockPos pos : positions) {
			BlockState original = world.getBlockState(pos);
			if (!original.isAir() && !PunishmentBlockSafety.canTemporarilyRemove(world, pos, original)) {
				continue;
			}

			world.setBlockAndUpdate(pos, bedrock);
			placed++;
			if (keepSomePermanent && RANDOM.nextDouble() < 0.28D) {
				permanent++;
			} else {
				TemporaryBlockChangeManager.add(world, pos, original, bedrock, expireTick);
			}
		}

		if (placed <= 0) {
			RandomEventUtils.sendMessage(player, "基岩牢笼没有找到可放置位置。");
		} else {
			RandomEventUtils.sendMessage(player, permanent > 0 ? "基岩牢笼落下，一部分基岩将永久保留。" : "基岩牢笼落下，稍后会恢复。");
		}
	}

	private boolean isSafeArea(ServerPlayer player, ServerLevel world) {
		BlockPos center = player.blockPosition();
		if (world.getSharedSpawnPos().distSqr(center) < 64.0D * 64.0D) {
			return false;
		}
		if (!world.getEntitiesOfClass(Villager.class, new AABB(center).inflate(8.0D)).isEmpty()) {
			return false;
		}

		int baseMarkers = 0;
		for (int dx = -5; dx <= 5; dx++) {
			for (int dy = -2; dy <= 3; dy++) {
				for (int dz = -5; dz <= 5; dz++) {
					BlockPos pos = center.offset(dx, dy, dz);
					if (PunishmentBlockSafety.isBaseMarker(world, pos, world.getBlockState(pos)) && ++baseMarkers > 28) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private List<BlockPos> cagePositions(BlockPos center) {
		List<BlockPos> positions = new ArrayList<>();
		for (int dx = -2; dx <= 2; dx++) {
			for (int dy = -1; dy <= 3; dy++) {
				for (int dz = -2; dz <= 2; dz++) {
					boolean shell = dy == -1 || dy == 3 || Math.abs(dx) == 2 || Math.abs(dz) == 2;
					boolean doorway = dx == 0 && dz == -2 && (dy == 0 || dy == 1);
					boolean playerSpace = Math.abs(dx) <= 1 && Math.abs(dz) <= 1 && (dy == 0 || dy == 1 || dy == 2);
					if (shell && !doorway && !playerSpace) {
						positions.add(center.offset(dx, dy, dz).immutable());
					}
				}
			}
		}
		return positions;
	}
}
