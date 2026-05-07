package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class RandomExplosionEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "random_explosion";
	}

	@Override
	public String getName() {
		return "不稳定爆裂";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.EPIC;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		Level.ExplosionInteraction interaction = DestructiveModeRules.canDamageExplosionBlocks(config)
				? Level.ExplosionInteraction.BLOCK
				: Level.ExplosionInteraction.NONE;
		int count = RandomEventUtils.randomBetween(2, 4);
		int exploded = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> pos = findExplosionPos(player, world);
			if (pos.isEmpty()) {
				continue;
			}

			BlockPos target = pos.get();
			world.explode(null, target.getX() + 0.5D, target.getY() + 0.15D, target.getZ() + 0.5D, 1.6F, false, interaction);
			exploded++;
		}

		RandomEventUtils.sendMessage(player, exploded > 0 ? "周围的空气变得极不稳定。" : "周围的空气震颤了一下，但没有找到爆裂点。");
	}

	private Optional<BlockPos> findExplosionPos(ServerPlayer player, ServerLevel world) {
		BlockPos origin = player.blockPosition();
		for (int attempt = 0; attempt < 24; attempt++) {
			int radius = RandomEventUtils.randomBetween(6, 12);
			double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			for (int y = Math.min(world.getMaxBuildHeight() - 2, origin.getY() + 4); y >= Math.max(world.getMinBuildHeight() + 1, origin.getY() - 6); y--) {
				BlockPos pos = new BlockPos(x, y, z);
				if (pos.distSqr(origin) >= 36.0D && RandomEventUtils.isSafeStandingPos(world, pos)) {
					return Optional.of(pos);
				}
			}
		}
		return Optional.empty();
	}
}
