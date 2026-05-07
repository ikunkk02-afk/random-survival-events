package com.ikunkk02afk.randomsurvivalevents.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class RandomEventTicker {
	private static final Map<UUID, Integer> PLAYER_TIMERS = new HashMap<>();
	private static boolean registered;

	private RandomEventTicker() {
	}

	public static void register() {
		if (registered) {
			return;
		}

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			Set<UUID> onlinePlayers = new HashSet<>();

			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				UUID playerId = player.getUUID();
				onlinePlayers.add(playerId);

				if (!canTrigger(player)) {
					PLAYER_TIMERS.remove(playerId);
					continue;
				}

				int intervalTicks = RandomSurvivalEventsConfig.get().eventIntervalTicks;
				int ticks = PLAYER_TIMERS.getOrDefault(playerId, 0) + 1;
				if (ticks >= intervalTicks) {
					PLAYER_TIMERS.put(playerId, 0);
					ServerLevel world = player.serverLevel();
					RandomEventManager.executeRandomEvent(player, world);
				} else {
					PLAYER_TIMERS.put(playerId, ticks);
				}
			}

			PLAYER_TIMERS.keySet().removeIf(playerId -> !onlinePlayers.contains(playerId));
		});

		registered = true;
	}

	private static boolean canTrigger(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}
}
