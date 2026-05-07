package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class HeavyAirEvent implements RandomEvent {
	@Override
	public String getId() {
		return "heavy_air";
	}

	@Override
	public String getName() {
		return "空气变重了";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.SPECIAL;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.UNCOMMON;
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

		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, getDefaultEventDurationTicks(), 0));
		RandomEventUtils.playSound(world, player.blockPosition(), SoundEvents.AMBIENT_CAVE.value(), 0.9F, 0.55F);
		RandomEventUtils.sendMessage(player, "空气突然变重了。");
	}
}
