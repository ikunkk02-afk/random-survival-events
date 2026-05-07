package com.ikunkk02afk.randomsurvivalevents.event.punishment;

import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class MiningLockManager {
	private static final int MESSAGE_COOLDOWN_TICKS = 40;
	private static final Map<UUID, Long> LAST_MESSAGE_TICKS = new HashMap<>();
	private static boolean registered;

	private MiningLockManager() {
	}

	public static void register() {
		if (registered) {
			return;
		}

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (world.isClientSide || !isLockedSurvivalPlayer(player)) {
				return true;
			}

			if (player instanceof ServerPlayer serverPlayer) {
				long gameTime = serverPlayer.serverLevel().getGameTime();
				long lastMessageTick = LAST_MESSAGE_TICKS.getOrDefault(serverPlayer.getUUID(), Long.MIN_VALUE);
				if (gameTime - lastMessageTick >= MESSAGE_COOLDOWN_TICKS) {
					serverPlayer.displayClientMessage(Component.literal("你的双手突然不听使唤了。"), true);
					LAST_MESSAGE_TICKS.put(serverPlayer.getUUID(), gameTime);
				}
			}
			return false;
		});

		registered = true;
	}

	private static boolean isLockedSurvivalPlayer(Player player) {
		return player != null
				&& player.isAlive()
				&& !player.isCreative()
				&& !player.isSpectator()
				&& (player.hasEffect(ModMobEffects.MINING_LOCK) || player.hasEffect(ModMobEffects.WEAK_HANDS));
	}
}
