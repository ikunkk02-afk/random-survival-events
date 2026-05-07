package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

public class MonsterSquadEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "monster_squad";
	}

	@Override
	public String getName() {
		return "怪物小队";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.MOB;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.UNCOMMON;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		int count = RandomEventUtils.randomBetween(2, 4);
		int spawned = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> spawnPos = RandomEventUtils.findNearbySpawnPos(world, player, 5, 10);
			if (spawnPos.isEmpty()) {
				continue;
			}

			EntityType<? extends Mob> type = pickMonster(world);
			Mob mob = type.create(world, null, spawnPos.get(), MobSpawnType.EVENT, false, false);
			if (mob != null) {
				mob.moveTo(spawnPos.get(), RANDOM.nextFloat() * 360.0F, 0.0F);
				world.addFreshEntity(mob);
				spawned++;
			}
		}

		if (spawned > 0) {
			RandomEventUtils.sendMessage(player, "附近出现了一支怪物小队。");
		} else {
			RandomEventUtils.sendMessage(player, "怪物小队想来，但没有找到合适的位置。");
		}
	}

	private EntityType<? extends Mob> pickMonster(ServerLevel world) {
		if (world.dimension() == Level.NETHER) {
			return RANDOM.nextBoolean() ? EntityType.PIGLIN : EntityType.MAGMA_CUBE;
		}
		if (world.dimension() == Level.END) {
			return EntityType.ENDERMITE;
		}

		List<EntityType<? extends Mob>> overworldTypes = List.of(
				EntityType.ZOMBIE,
				EntityType.SKELETON,
				EntityType.SPIDER
		);
		return overworldTypes.get(RANDOM.nextInt(overworldTypes.size()));
	}
}
