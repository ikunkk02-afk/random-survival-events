package com.ikunkk02afk.randomsurvivalevents.event.impl.block;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LuckyOreEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final Block[] ORES = {
			Blocks.COAL_ORE,
			Blocks.IRON_ORE,
			Blocks.COPPER_ORE,
			Blocks.REDSTONE_ORE
	};

	@Override
	public String getId() {
		return "lucky_ore";
	}

	@Override
	public String getName() {
		return "幸运矿脉";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.BLOCK;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive() || world.dimension() != Level.OVERWORLD
				|| !RandomSurvivalEventsConfig.get().allowBlockReplacement) {
			RandomEventUtils.sendMessage(player, "幸运矿脉没有在这里出现。");
			return;
		}

		int targetCount = RandomEventUtils.randomBetween(3, 6);
		int replaced = 0;
		BlockPos playerFeet = player.blockPosition();
		for (int attempt = 0; attempt < 96 && replaced < targetCount; attempt++) {
			BlockPos pos = randomNearbyPos(playerFeet);
			if (pos.equals(playerFeet) || pos.equals(playerFeet.below())) {
				continue;
			}

			BlockState state = world.getBlockState(pos);
			if (!canReplace(state)) {
				continue;
			}

			Block ore = ORES[RANDOM.nextInt(ORES.length)];
			world.setBlockAndUpdate(pos, ore.defaultBlockState());
			replaced++;
		}

		RandomEventUtils.sendMessage(player, replaced > 0 ? "附近的石头里闪过了一点矿物光泽。" : "幸运矿脉试图出现，但附近没有合适石头。");
	}

	private BlockPos randomNearbyPos(BlockPos origin) {
		int radius = RandomEventUtils.randomBetween(5, 10);
		double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
		int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
		int y = origin.getY() + RandomEventUtils.randomBetween(-4, 4);
		int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
		return new BlockPos(x, y, z);
	}

	private boolean canReplace(BlockState state) {
		return state.getFluidState().isEmpty()
				&& !state.hasBlockEntity()
				&& (state.is(Blocks.STONE) || state.is(Blocks.COBBLESTONE) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.TUFF));
	}
}
