package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.MorePunishmentEventManager;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public class MobDisguiseEvent implements RandomEvent {
	private static final int TRANSFORM_DELAY_TICKS = 5 * 20;
	private static final Random RANDOM = new Random();
	private static final List<DisguisePair> DISGUISES = List.of(
			new DisguisePair(EntityType.CHICKEN, EntityType.ZOMBIE),
			new DisguisePair(EntityType.SHEEP, EntityType.SKELETON),
			new DisguisePair(EntityType.COW, EntityType.PILLAGER),
			new DisguisePair(EntityType.PIG, EntityType.SPIDER)
	);

	@Override
	public String getId() {
		return "mob_disguise";
	}

	@Override
	public String getName() {
		return "伪装袭击";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.RARE;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		int count = RandomEventUtils.randomBetween(3, 5);
		int spawned = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> spawnPos = RandomEventUtils.findNearbySpawnPos(world, player, 5, 10);
			if (spawnPos.isEmpty()) {
				continue;
			}

			DisguisePair pair = DISGUISES.get(RANDOM.nextInt(DISGUISES.size()));
			Mob animal = pair.animalType().create(world, null, spawnPos.get(), MobSpawnType.EVENT, false, false);
			if (animal == null) {
				continue;
			}

			animal.moveTo(spawnPos.get(), RANDOM.nextFloat() * 360.0F, 0.0F);
			world.addFreshEntity(animal);
			MorePunishmentEventManager.scheduleDisguise(world, animal, pair.hostileType(), player, TRANSFORM_DELAY_TICKS);
			spawned++;
		}

		RandomEventUtils.sendMessage(player, spawned > 0 ? "这些动物看起来有点不对劲。" : "这些动物看起来有点不对劲，但没有找到安全生成点。");
	}

	private record DisguisePair(EntityType<? extends Mob> animalType, EntityType<? extends Mob> hostileType) {
	}
}
