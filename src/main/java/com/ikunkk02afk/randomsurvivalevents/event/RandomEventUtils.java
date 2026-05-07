package com.ikunkk02afk.randomsurvivalevents.event;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public final class RandomEventUtils {
	private static final Random RANDOM = new Random();

	private RandomEventUtils() {
	}

	public static void sendMessage(ServerPlayer player, String message) {
		if (player != null) {
			player.displayClientMessage(Component.literal(message), false);
		}
	}

	public static void playSound(ServerLevel world, BlockPos pos, SoundEvent sound, float volume, float pitch) {
		if (world != null && pos != null && sound != null) {
			world.playSound(null, pos, sound, SoundSource.PLAYERS, volume, pitch);
		}
	}

	public static void dropItem(ServerLevel world, BlockPos pos, ItemStack stack) {
		if (world == null || pos == null || stack == null || stack.isEmpty()) {
			return;
		}

		ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, stack);
		itemEntity.setDefaultPickUpDelay();
		world.addFreshEntity(itemEntity);
	}

	public static Optional<BlockPos> findNearbySpawnPos(ServerLevel world, ServerPlayer player, int minRadius, int maxRadius) {
		if (world == null || player == null || maxRadius < minRadius) {
			return Optional.empty();
		}

		BlockPos origin = player.blockPosition();
		for (int attempt = 0; attempt < 32; attempt++) {
			int radius = randomBetween(minRadius, maxRadius);
			double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			int startY = Math.min(world.getMaxBuildHeight() - 2, origin.getY() + 6);
			int endY = Math.max(world.getMinBuildHeight() + 1, origin.getY() - 8);

			for (int y = startY; y >= endY; y--) {
				BlockPos pos = new BlockPos(x, y, z);
				if (isSafeStandingPos(world, pos)) {
					return Optional.of(pos);
				}
			}
		}

		return Optional.empty();
	}

	public static boolean isSafeStandingPos(ServerLevel world, BlockPos feetPos) {
		if (world == null || feetPos == null || feetPos.getY() <= world.getMinBuildHeight()) {
			return false;
		}

		BlockPos groundPos = feetPos.below();
		BlockPos headPos = feetPos.above();
		return world.getBlockState(feetPos).isAir()
				&& world.getBlockState(headPos).isAir()
				&& world.getBlockState(groundPos).isFaceSturdy(world, groundPos, Direction.UP);
	}

	public static int randomBetween(int minInclusive, int maxInclusive) {
		if (maxInclusive <= minInclusive) {
			return minInclusive;
		}
		return minInclusive + RANDOM.nextInt(maxInclusive - minInclusive + 1);
	}
}
