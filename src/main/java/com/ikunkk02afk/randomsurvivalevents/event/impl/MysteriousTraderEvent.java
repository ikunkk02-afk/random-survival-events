package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.WanderingTrader;

public class MysteriousTraderEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "mysterious_trader";
	}

	@Override
	public String getName() {
		return "神秘商机";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.MOB;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.COMMON;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		Optional<BlockPos> spawnPos = RandomEventUtils.findNearbySpawnPos(world, player, 5, 10);
		if (spawnPos.isEmpty()) {
			RandomEventUtils.sendMessage(player, "一个神秘商人想来，但附近没有合适的位置。");
			return;
		}

		WanderingTrader trader = EntityType.WANDERING_TRADER.create(world, null, spawnPos.get(), MobSpawnType.EVENT, false, false);
		if (trader == null) {
			return;
		}

		trader.moveTo(spawnPos.get(), RANDOM.nextFloat() * 360.0F, 0.0F);
		world.addFreshEntity(trader);
		RandomEventUtils.sendMessage(player, "一个神秘商人闻着味就来了。");
	}
}
