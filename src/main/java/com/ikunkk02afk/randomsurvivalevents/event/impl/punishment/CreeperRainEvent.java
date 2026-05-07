package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentEntityTags;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRainEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "creeper_rain";
	}

	@Override
	public String getName() {
		return "苦力怕雨";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.DISASTER;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}
		if (!world.canSeeSky(player.blockPosition().above())) {
			RandomEventUtils.sendMessage(player, "天空被挡住了，苦力怕雨没有落下来。");
			return;
		}

		int count = RandomEventUtils.randomBetween(config.creeperRainCountMin, config.creeperRainCountMax);
		int spawned = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> spawnPos = findAirDropPos(player, world);
			if (spawnPos.isEmpty()) {
				continue;
			}

			Creeper creeper = EntityType.CREEPER.create(world);
			if (creeper == null) {
				continue;
			}
			if (!config.allowCreeperRainBlockDamage) {
				creeper.addTag(PunishmentEntityTags.NO_BLOCK_DAMAGE_CREEPER);
			}
			BlockPos pos = spawnPos.get();
			creeper.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, RANDOM.nextFloat() * 360.0F, 0.0F);
			creeper.setTarget(player);
			world.addFreshEntity(creeper);
			spawned++;
		}

		RandomEventUtils.sendMessage(player, spawned > 0 ? "有些绿色的东西从天上掉下来了。" : "苦力怕雨没有找到开阔的下落点。");
	}

	private Optional<BlockPos> findAirDropPos(ServerPlayer player, ServerLevel world) {
		BlockPos origin = player.blockPosition();
		for (int attempt = 0; attempt < 24; attempt++) {
			int radius = RandomEventUtils.randomBetween(5, 12);
			double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			int y = Math.min(world.getMaxBuildHeight() - 2, origin.getY() + RandomEventUtils.randomBetween(14, 22));
			BlockPos pos = new BlockPos(x, y, z);
			if (world.getWorldBorder().isWithinBounds(pos)
					&& world.canSeeSky(pos)
					&& world.getBlockState(pos).isAir()
					&& world.getBlockState(pos.above()).isAir()) {
				return Optional.of(pos);
			}
		}
		return Optional.empty();
	}
}
