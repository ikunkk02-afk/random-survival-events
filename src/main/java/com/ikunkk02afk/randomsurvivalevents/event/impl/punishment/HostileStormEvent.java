package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;

public class HostileStormEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final List<EntityType<? extends Mob>> HOSTILES = List.of(
			EntityType.ZOMBIE,
			EntityType.SKELETON,
			EntityType.CREEPER,
			EntityType.SPIDER,
			EntityType.WITCH,
			EntityType.PILLAGER,
			EntityType.BLAZE,
			EntityType.ENDERMAN
	);

	@Override
	public String getId() {
		return "hostile_storm";
	}

	@Override
	public String getName() {
		return "怪物风暴";
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
	public ResourceLocation getStatusEffectId() {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "doom_mark");
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return 20 * 20;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		int count = RandomEventUtils.randomBetween(12, 20);
		int spawned = 0;
		boolean permanentMobDisaster = DestructiveModeRules.canUsePermanentMobDisaster(config);
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> spawnPos = RandomEventUtils.findNearbySpawnPos(world, player, 9, 22);
			if (spawnPos.isEmpty()) {
				continue;
			}

			Mob mob = pickMonster(config, permanentMobDisaster).create(world, null, spawnPos.get(), MobSpawnType.EVENT, false, false);
			if (mob == null) {
				continue;
			}
			if (mob instanceof Creeper && !permanentMobDisaster) {
				mob.addTag(PunishmentEntityTags.NO_BLOCK_DAMAGE_CREEPER);
			}
			mob.moveTo(spawnPos.get(), RANDOM.nextFloat() * 360.0F, 0.0F);
			mob.setTarget(player);
			world.addFreshEntity(mob);
			spawned++;
		}

		RandomEventUtils.sendMessage(player, spawned > 0 ? "怪物风暴席卷了附近区域。" : "怪物风暴没有找到安全生成位置。");
	}

	private EntityType<? extends Mob> pickMonster(RandomSurvivalEventsConfig config, boolean permanentMobDisaster) {
		double extremeChance = Math.min(0.05D, config.dangerousMobChance);
		if (permanentMobDisaster
				&& config.allowExtremeMobs
				&& config.allowBossMobs
				&& RANDOM.nextDouble() < extremeChance) {
			if (RANDOM.nextBoolean()) {
				return EntityType.WITHER;
			}
			return EntityType.WARDEN;
		}
		return HOSTILES.get(RANDOM.nextInt(HOSTILES.size()));
	}
}
