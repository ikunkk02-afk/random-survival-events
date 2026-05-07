package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RandomTeleportTrapEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "random_teleport_trap";
	}

	@Override
	public String getName() {
		return "空间错位";
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
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		Optional<BlockPos> landingPos = findLandingPos(player, world);
		if (landingPos.isEmpty()) {
			RandomEventUtils.sendMessage(player, "空间扭曲了一下，但没找到安全落点。");
			return;
		}

		BlockPos pos = landingPos.get();
		player.teleportTo(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), player.getXRot());
		RandomEventUtils.sendMessage(player, "你周围的空间突然扭曲了。");
	}

	private Optional<BlockPos> findLandingPos(ServerPlayer player, ServerLevel world) {
		BlockPos origin = player.blockPosition();
		for (int attempt = 0; attempt < 64; attempt++) {
			int radius = RandomEventUtils.randomBetween(16, 48);
			double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			int startY = Math.min(world.getMaxBuildHeight() - 2, origin.getY() + 16);
			int endY = Math.max(world.getMinBuildHeight() + 2, origin.getY() - 24);

			for (int y = startY; y >= endY; y--) {
				BlockPos pos = new BlockPos(x, y, z);
				if (isSafeTeleportPos(world, pos)) {
					return Optional.of(pos);
				}
			}
		}
		return Optional.empty();
	}

	private boolean isSafeTeleportPos(ServerLevel world, BlockPos feetPos) {
		return world.getWorldBorder().isWithinBounds(feetPos)
				&& RandomEventUtils.isSafeStandingPos(world, feetPos)
				&& world.getFluidState(feetPos).isEmpty()
				&& world.getFluidState(feetPos.above()).isEmpty()
				&& world.getFluidState(feetPos.below()).isEmpty();
	}
}
