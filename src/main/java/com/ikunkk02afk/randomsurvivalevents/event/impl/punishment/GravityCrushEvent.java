package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GravityCrushEvent implements RandomEvent {
	private static final int DURATION_TICKS = 20 * 20;

	@Override
	public String getId() {
		return "gravity_crush";
	}

	@Override
	public String getName() {
		return "重力压制";
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
		return DURATION_TICKS;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DURATION_TICKS, 1));
		player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, DURATION_TICKS, 1));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DURATION_TICKS, 0));
		RandomEventUtils.sendMessage(player, "空气像石头一样压在你身上。");
	}
}
