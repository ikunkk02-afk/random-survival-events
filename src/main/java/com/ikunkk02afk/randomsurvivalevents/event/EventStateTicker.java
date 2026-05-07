package com.ikunkk02afk.randomsurvivalevents.event;

import com.ikunkk02afk.randomsurvivalevents.event.block.BlockChaosManager;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import com.ikunkk02afk.randomsurvivalevents.event.state.PlayerEventStateManager;
import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeShuffleManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class EventStateTicker {
	private static boolean registered;

	private EventStateTicker() {
	}

	public static void register() {
		if (registered) {
			return;
		}

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			PlayerEventStateManager.tick(server);
			TemporaryBlockChangeManager.tick(server);
			BlockChaosManager.tick(server);
			RecipeShuffleManager.tick(server);
		});
		registered = true;
	}
}
