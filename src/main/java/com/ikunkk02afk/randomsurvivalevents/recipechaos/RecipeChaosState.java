package com.ikunkk02afk.randomsurvivalevents.recipechaos;

import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import net.minecraft.server.level.ServerPlayer;

public final class RecipeChaosState {
	private RecipeChaosState() {
	}

	public static void activate(ServerPlayer player, long untilTick) {
		RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player).setRecipeChaosUntilTick(untilTick);
	}

	public static boolean isActive(ServerPlayer player) {
		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		return component.hasRecipeChaos(player.serverLevel().getGameTime());
	}
}
