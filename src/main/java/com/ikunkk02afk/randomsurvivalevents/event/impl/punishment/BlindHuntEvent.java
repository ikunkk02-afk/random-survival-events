package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentEntityTags;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;

public class BlindHuntEvent implements RandomEvent {
	private static final int DURATION_TICKS = 25 * 20;
	private static final Random RANDOM = new Random();
	private static final List<EntityType<? extends Mob>> HUNTERS = List.of(
			EntityType.ZOMBIE,
			EntityType.SKELETON,
			EntityType.SPIDER,
			EntityType.CREEPER,
			EntityType.ENDERMAN
	);

	@Override
	public String getId() {
		return "blind_hunt";
	}

	@Override
	public String getName() {
		return "黑暗猎杀";
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
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return DURATION_TICKS;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, DURATION_TICKS, 0));
		int count = RandomEventUtils.randomBetween(2, 4);
		int spawned = 0;
		for (int i = 0; i < count; i++) {
			Optional<BlockPos> spawnPos = RandomEventUtils.findNearbySpawnPos(world, player, 7, 14);
			if (spawnPos.isEmpty()) {
				continue;
			}

			EntityType<? extends Mob> type = HUNTERS.get(RANDOM.nextInt(HUNTERS.size()));
			Mob mob = type.create(world, null, spawnPos.get(), MobSpawnType.EVENT, false, false);
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

		RandomEventUtils.sendMessage(player, spawned > 0 ? "黑暗中有什么东西正在靠近。" : "黑暗降临了，但猎手没有找到落脚点。");
	}
}
