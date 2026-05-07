package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentBlockSafety;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LavaUnderfootEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "lava_underfoot";
	}

	@Override
	public String getName() {
		return "脚底发烫";
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
		return RandomSurvivalEventsConfig.get().lavaTrapRestoreTicks;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive() || !config.allowLavaTrap) {
			return;
		}

		List<BlockPos> candidates = findCandidates(player, world);
		if (candidates.isEmpty()) {
			RandomEventUtils.sendMessage(player, "脚底发烫，但没有找到安全岩浆点。");
			return;
		}

		Collections.shuffle(candidates, RANDOM);
		int count = Math.min(RandomEventUtils.randomBetween(1, 3), candidates.size());
		BlockState lava = Blocks.LAVA.defaultBlockState();
		long expireTick = world.getGameTime() + config.lavaTrapRestoreTicks;
		for (int i = 0; i < count; i++) {
			BlockPos pos = candidates.get(i);
			BlockState originalState = world.getBlockState(pos);
			world.setBlockAndUpdate(pos, lava);
			TemporaryBlockChangeManager.add(world, pos, originalState, lava, expireTick);
		}

		RandomEventUtils.sendMessage(player, "脚下突然传来一阵炽热。");
	}

	private List<BlockPos> findCandidates(ServerPlayer player, ServerLevel world) {
		BlockPos center = player.blockPosition();
		List<BlockPos> candidates = new ArrayList<>();
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				if (dx == 0 && dz == 0) {
					continue;
				}

				BlockPos pos = center.offset(dx, 0, dz);
				BlockPos groundPos = pos.below();
				BlockState state = world.getBlockState(pos);
				BlockState groundState = world.getBlockState(groundPos);
				if (!state.isAir()
						|| !state.getFluidState().isEmpty()
						|| !groundState.isFaceSturdy(world, groundPos, Direction.UP)
						|| world.getBlockEntity(pos) != null
						|| world.getBlockEntity(groundPos) != null
						|| PunishmentBlockSafety.isProtected(groundState)
						|| TemporaryBlockChangeManager.hasPendingChange(world, pos)) {
					continue;
				}
				candidates.add(pos.immutable());
			}
		}
		return candidates;
	}
}
