package com.ikunkk02afk.randomsurvivalevents.event;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class RandomEventTicker {
	private static final int PREVIEW_DURATION_TICKS = 5 * 20;
	private static final Map<UUID, Integer> PLAYER_TIMERS = new HashMap<>();
	private static final Map<UUID, PendingPreview> PENDING_PREVIEWS = new HashMap<>();
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
					cancelPreview(player, "player can no longer receive random events");
					PLAYER_TIMERS.remove(playerId);
					continue;
				}

				if (tickPendingPreview(player)) {
					continue;
				}

				int intervalTicks = RandomSurvivalEventsConfig.get().eventIntervalTicks;
				int ticks = PLAYER_TIMERS.getOrDefault(playerId, 0) + 1;
				if (ticks >= intervalTicks) {
					PLAYER_TIMERS.put(playerId, 0);
					RandomEventManager.selectRandomEnabledEvent(player).ifPresent(event -> schedulePreview(player, event));
				} else {
					PLAYER_TIMERS.put(playerId, ticks);
				}
			}

			PLAYER_TIMERS.keySet().removeIf(playerId -> !onlinePlayers.contains(playerId));
			removeOfflinePreviews(onlinePlayers);
		});

		registered = true;
	}

	public static boolean schedulePreview(ServerPlayer player, RandomEvent event) {
		if (!canTrigger(player) || event == null || PENDING_PREVIEWS.containsKey(player.getUUID())) {
			return false;
		}

		PENDING_PREVIEWS.put(player.getUUID(), new PendingPreview(event, PREVIEW_DURATION_TICKS));
		RandomEventManager.sendPreview(player, event);
		return true;
	}

	public static boolean hasPendingPreview(ServerPlayer player) {
		return player != null && PENDING_PREVIEWS.containsKey(player.getUUID());
	}

	private static boolean tickPendingPreview(ServerPlayer player) {
		PendingPreview preview = PENDING_PREVIEWS.get(player.getUUID());
		if (preview == null) {
			return false;
		}

		int remainingTicks = preview.remainingTicks() - 1;
		if (remainingTicks > 0) {
			PENDING_PREVIEWS.put(player.getUUID(), new PendingPreview(preview.event(), remainingTicks));
			return true;
		}

		PENDING_PREVIEWS.remove(player.getUUID());
		PLAYER_TIMERS.put(player.getUUID(), 0);
		ServerLevel world = player.serverLevel();
		RandomEventUtils.sendMessage(player, "随机事件：" + preview.event().getName());
		if (RandomEventManager.executeEvent(player, world, preview.event())) {
			RandomSurvivalEvents.LOGGER.info(
					"[Random Survival Events] Triggered previewed event {} ({}) for player {}.",
					preview.event().getId(),
					preview.event().getRarity(),
					player.getGameProfile().getName()
			);
		}
		return true;
	}

	private static void cancelPreview(ServerPlayer player, String reason) {
		if (player == null) {
			return;
		}

		PendingPreview removed = PENDING_PREVIEWS.remove(player.getUUID());
		if (removed != null) {
			RandomSurvivalEvents.LOGGER.info(
					"[Random Survival Events] Canceled previewed event {} for player {}: {}.",
					removed.event().getId(),
					player.getGameProfile().getName(),
					reason
			);
		}
	}

	private static void removeOfflinePreviews(Set<UUID> onlinePlayers) {
		Iterator<Map.Entry<UUID, PendingPreview>> iterator = PENDING_PREVIEWS.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, PendingPreview> entry = iterator.next();
			if (!onlinePlayers.contains(entry.getKey())) {
				RandomSurvivalEvents.LOGGER.info(
						"[Random Survival Events] Canceled previewed event {} for offline player {}.",
						entry.getValue().event().getId(),
						entry.getKey()
				);
				iterator.remove();
			}
		}
	}

	private static boolean canTrigger(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}

	private record PendingPreview(RandomEvent event, int remainingTicks) {
	}
}
