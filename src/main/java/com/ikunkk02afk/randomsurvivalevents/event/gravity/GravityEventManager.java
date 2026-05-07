package com.ikunkk02afk.randomsurvivalevents.event.gravity;

import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public final class GravityEventManager {
	private static final int POST_LEVITATION_SLOW_FALLING_TICKS = 5 * 20;
	private static final Map<UUID, Long> SLOW_FALLING_AT_TICK = new HashMap<>();

	private GravityEventManager() {
	}

	public static void scheduleSlowFallingAfterLevitation(ServerPlayer player, int levitationDurationTicks) {
		if (player == null || levitationDurationTicks <= 0) {
			return;
		}

		SLOW_FALLING_AT_TICK.put(player.getUUID(), player.serverLevel().getGameTime() + levitationDurationTicks);
	}

	public static void tick(MinecraftServer server) {
		if (server == null) {
			return;
		}

		tickGravityCrush(server);
		tickSlowFallingTasks(server);
	}

	private static void tickGravityCrush(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!player.hasEffect(ModMobEffects.GRAVITY_CRUSH)) {
				continue;
			}
			if (!canAffect(player)) {
				player.removeEffect(ModMobEffects.GRAVITY_CRUSH);
				continue;
			}

			Vec3 movement = player.getDeltaMovement();
			if (Math.abs(movement.x) > 0.001D || Math.abs(movement.z) > 0.001D) {
				player.setDeltaMovement(0.0D, movement.y, 0.0D);
				player.hurtMarked = true;
			}
		}
	}

	private static void tickSlowFallingTasks(MinecraftServer server) {
		if (SLOW_FALLING_AT_TICK.isEmpty()) {
			return;
		}

		Iterator<Map.Entry<UUID, Long>> iterator = SLOW_FALLING_AT_TICK.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, Long> entry = iterator.next();
			ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
			if (player == null || !canAffect(player)) {
				iterator.remove();
				continue;
			}
			if (player.serverLevel().getGameTime() < entry.getValue()) {
				continue;
			}

			player.fallDistance = 0.0F;
			player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, POST_LEVITATION_SLOW_FALLING_TICKS, 0, false, true, true));
			iterator.remove();
		}
	}

	private static boolean canAffect(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}
}
