package com.ikunkk02afk.randomsurvivalevents.event.impl.block;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SlimeFloorEvent implements RandomEvent {
	@Override
	public String getId() {
		return "slime_floor";
	}

	@Override
	public String getName() {
		return "史莱姆地板";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.BLOCK;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive() || !RandomSurvivalEventsConfig.get().allowTemporaryBlockChange) {
			return;
		}

		BlockPos center = player.blockPosition().below();
		int changed = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				BlockPos pos = center.offset(dx, 0, dz);
				BlockState originalState = world.getBlockState(pos);
				if (!canTemporarilyReplace(originalState)) {
					continue;
				}

				world.setBlockAndUpdate(pos, Blocks.SLIME_BLOCK.defaultBlockState());
				TemporaryBlockChangeManager.add(world, pos, originalState, world.getGameTime() + 60L * 20L);
				changed++;
			}
		}

		RandomEventUtils.sendMessage(player, changed > 0 ? "脚下的地面突然变得弹弹的。" : "史莱姆地板没有找到合适的地面。");
	}

	private boolean canTemporarilyReplace(BlockState state) {
		return state.is(Blocks.DIRT)
				|| state.is(Blocks.GRASS_BLOCK)
				|| state.is(Blocks.STONE)
				|| state.is(Blocks.COBBLESTONE);
	}
}
