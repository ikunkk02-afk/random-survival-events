package com.ikunkk02afk.randomsurvivalevents.recipechaos;

import net.minecraft.server.level.ServerPlayer;

public final class RecipeChaosState {
	private RecipeChaosState() {
	}

	public static void activate(ServerPlayer player, long untilTick) {
		if (player == null) {
			return;
		}
		long durationTicks = Math.max(0L, untilTick - player.serverLevel().getGameTime());
		RecipeShuffleManager.startShuffle(player.serverLevel(), (int) Math.min(Integer.MAX_VALUE, durationTicks));
	}

	public static boolean isActive(ServerPlayer player) {
		return player != null && RecipeShuffleManager.isShuffleActive();
	}
}
