package com.ikunkk02afk.randomsurvivalevents.event.impl.disaster;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class MeteorRainEvent implements RandomEvent {
	@Override
	public String getId() {
		return "meteor_rain";
	}

	@Override
	public String getName() {
		return "陨石雨";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.WEATHER;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.DISASTER;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive() || !config.enableDisasterEvents) {
			return;
		}

		RandomEventUtils.sendMessage(player, "陨石雨降临了！");
		RandomEventUtils.playSound(world, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 0.65F);

		int strikes = RandomEventUtils.randomBetween(3, 6);
		int exploded = 0;
		for (int i = 0; i < strikes; i++) {
			Optional<BlockPos> landingPos = findLandingPos(world, player);
			if (landingPos.isEmpty()) {
				continue;
			}

			BlockPos pos = landingPos.get();
			float power = (float) Math.max(1.5D, Math.min(2.5D, config.meteorRainExplosionPower));
			Level.ExplosionInteraction interaction = config.allowDisasterBlockDamage
					? Level.ExplosionInteraction.BLOCK
					: Level.ExplosionInteraction.NONE;
			world.explode(null, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, power, false, interaction);
			exploded++;
		}

		if (exploded <= 0) {
			RandomEventUtils.sendMessage(player, "陨石雨擦过天边，没有找到合适落点。");
		}
	}

	private Optional<BlockPos> findLandingPos(ServerLevel world, ServerPlayer player) {
		for (int attempt = 0; attempt < 12; attempt++) {
			Optional<BlockPos> pos = RandomEventUtils.findNearbySpawnPos(world, player, 8, 18);
			if (pos.isPresent() && pos.get().distSqr(player.blockPosition()) >= 36.0D) {
				return pos;
			}
		}
		return Optional.empty();
	}
}
