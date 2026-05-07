package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;

public class HungerCollapseEvent implements RandomEvent {
	@Override
	public String getId() {
		return "hunger_collapse";
	}

	@Override
	public String getName() {
		return "体力崩溃";
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
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return getDefaultEventDurationTicks();
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		FoodData foodData = player.getFoodData();
		foodData.setFoodLevel(Math.max(1, foodData.getFoodLevel() - RandomEventUtils.randomBetween(6, 10)));
		foodData.setSaturation(0.0F);
		int durationTicks = getDefaultEventDurationTicks();
		player.addEffect(new MobEffectInstance(MobEffects.HUNGER, durationTicks, 1));
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, 0));
		RandomEventUtils.sendMessage(player, "你的体力突然被抽空了。");
	}
}
