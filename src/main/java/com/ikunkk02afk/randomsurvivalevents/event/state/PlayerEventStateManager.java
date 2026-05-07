package com.ikunkk02afk.randomsurvivalevents.event.state;

import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

public final class PlayerEventStateManager {
	private PlayerEventStateManager() {
	}

	public static void tick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!player.isAlive()) {
				AttributeEventHelper.removeAll(player);
				continue;
			}

			PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
			long gameTime = player.serverLevel().getGameTime();
			tickMaxHealthBoost(player, component, gameTime);
			tickMaxHealthReduce(player, component, gameTime);
			tickGlassCannon(player, component, gameTime);
		}
	}

	private static void tickMaxHealthBoost(ServerPlayer player, PlayerEventComponent component, long gameTime) {
		long untilTick = component.getMaxHealthBoostUntilTick();
		if (untilTick > gameTime) {
			AttributeEventHelper.ensureMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_BOOST, 4.0D);
		} else if (untilTick > 0L) {
			AttributeEventHelper.removeMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_BOOST);
			component.setMaxHealthBoostUntilTick(0L);
		}
	}

	private static void tickMaxHealthReduce(ServerPlayer player, PlayerEventComponent component, long gameTime) {
		long untilTick = component.getMaxHealthReduceUntilTick();
		if (untilTick > gameTime) {
			AttributeEventHelper.ensureMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_REDUCE, -4.0D);
		} else if (untilTick > 0L) {
			AttributeEventHelper.removeMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_REDUCE);
			component.setMaxHealthReduceUntilTick(0L);
		}
	}

	private static void tickGlassCannon(ServerPlayer player, PlayerEventComponent component, long gameTime) {
		long untilTick = component.getGlassCannonUntilTick();
		if (untilTick > gameTime) {
			AttributeEventHelper.ensureMaxHealthModifier(player, AttributeEventIds.GLASS_CANNON, -6.0D);
		} else if (untilTick > 0L) {
			AttributeEventHelper.removeMaxHealthModifier(player, AttributeEventIds.GLASS_CANNON);
			player.removeEffect(MobEffects.DAMAGE_BOOST);
			player.removeEffect(MobEffects.MOVEMENT_SPEED);
			component.setGlassCannonUntilTick(0L);
		}
	}
}
