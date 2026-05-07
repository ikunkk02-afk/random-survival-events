package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentEntityTags;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;

public class MonsterAmbushEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final List<EntityType<? extends Mob>> DEFAULT_MONSTERS = List.of(
			EntityType.ZOMBIE,
			EntityType.SKELETON,
			EntityType.SPIDER,
			EntityType.CREEPER,
			EntityType.WITCH,
			EntityType.PILLAGER,
			EntityType.ENDERMAN,
			EntityType.BLAZE
	);

	@Override
	public String getId() {
		return "monster_ambush";
	}

	@Override
	public String getName() {
		return "怪物伏击";
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

		int count = RandomEventUtils.randomBetween(4, 8);
		int spawned = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> spawnPos = RandomEventUtils.findNearbySpawnPos(world, player, 8, 16);
			if (spawnPos.isEmpty()) {
				continue;
			}

			Mob mob = pickMonster().create(world, null, spawnPos.get(), MobSpawnType.EVENT, false, false);
			if (mob == null) {
				continue;
			}

			if (mob instanceof Creeper) {
				mob.addTag(PunishmentEntityTags.NO_BLOCK_DAMAGE_CREEPER);
			}
			mob.moveTo(spawnPos.get(), RANDOM.nextFloat() * 360.0F, 0.0F);
			mob.setTarget(player);
			world.addFreshEntity(mob);
			spawned++;
		}

		RandomEventUtils.sendMessage(player, spawned > 0 ? "怪物从四周伏击了你。" : "怪物伏击没有找到安全位置。");
	}

	private EntityType<? extends Mob> pickMonster() {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		double extremeChance = Math.min(0.05D, config.dangerousMobChance);
		if (DestructiveModeRules.canUsePermanentMobDisaster(config)
				&& config.allowExtremeMobs
				&& config.allowBossMobs
				&& RANDOM.nextDouble() < extremeChance) {
			if (RANDOM.nextBoolean()) {
				return EntityType.WITHER;
			}
			return EntityType.WARDEN;
		}

		return DEFAULT_MONSTERS.get(RANDOM.nextInt(DEFAULT_MONSTERS.size()));
	}
}
