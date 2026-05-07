package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.MorePunishmentEventManager;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;

public class FallingAnvilEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "falling_anvil";
	}

	@Override
	public String getName() {
		return "天降铁砧";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.RARE;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		if (!hasEnoughVerticalSpace(world, player.blockPosition())) {
			RandomEventUtils.sendMessage(player, "小心头顶，但这里空间太低，铁砧没有落下。");
			return;
		}

		int count = RandomEventUtils.randomBetween(3, 5);
		int spawned = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> pos = findAnvilPos(world, player);
			if (pos.isEmpty()) {
				continue;
			}

			FallingBlockEntity anvil = FallingBlockEntity.fall(world, pos.get(), Blocks.ANVIL.defaultBlockState());
			anvil.setHurtsEntities(2.0F, 40);
			if (!RandomSurvivalEventsConfig.get().destructiveMode) {
				MorePunishmentEventManager.trackTemporaryFallingBlock(world, anvil, getDefaultEventDurationTicks());
			}
			spawned++;
		}

		RandomEventUtils.sendMessage(player, spawned > 0 ? "小心头顶。" : "小心头顶，但没有找到足够开阔的坠落点。");
	}

	private Optional<BlockPos> findAnvilPos(ServerLevel world, ServerPlayer player) {
		BlockPos origin = player.blockPosition();
		for (int attempt = 0; attempt < 32; attempt++) {
			int radius = RandomEventUtils.randomBetween(3, 7);
			double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			if (x == origin.getX() && z == origin.getZ()) {
				continue;
			}

			BlockPos pos = new BlockPos(x, Math.min(world.getMaxBuildHeight() - 2, origin.getY() + RandomEventUtils.randomBetween(8, 12)), z);
			if (pos.distSqr(origin) < 9.0D || pos.distSqr(origin) > 64.0D) {
				continue;
			}
			if (world.getBlockState(pos).isAir() && world.getBlockState(pos.above()).isAir() && hasClearDropColumn(world, pos, origin.getY() + 2)) {
				return Optional.of(pos);
			}
		}
		return Optional.empty();
	}

	private boolean hasEnoughVerticalSpace(ServerLevel world, BlockPos origin) {
		return hasClearDropColumn(world, origin.above(7), origin.getY() + 2);
	}

	private boolean hasClearDropColumn(ServerLevel world, BlockPos top, int minY) {
		for (int y = Math.max(minY, world.getMinBuildHeight()); y <= top.getY(); y++) {
			if (!world.getBlockState(new BlockPos(top.getX(), y, top.getZ())).isAir()) {
				return false;
			}
		}
		return true;
	}
}
