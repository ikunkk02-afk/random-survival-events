package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class SweatyEvent implements RandomEvent {
	@Override
	public String getId() {
		return "sweaty";
	}

	@Override
	public String getName() {
		return "汗流浃背";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PLAYER;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15 * 20, 0));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 0));
		RandomEventUtils.sendMessage(player, "你突然感觉汗流浃背了。");
	}
}
