package com.ikunkk02afk.randomsurvivalevents.event.impl.special;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GravityChaosEvent implements RandomEvent {
	private static final int DURATION_TICKS = 20 * 20;

	@Override
	public String getId() {
		return "gravity_chaos";
	}

	@Override
	public String getName() {
		return "重力混乱";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.SPECIAL;
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

		player.addEffect(new MobEffectInstance(MobEffects.JUMP, DURATION_TICKS, 0));
		player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, DURATION_TICKS, 0));
		RandomEventUtils.sendMessage(player, "重力好像暂时坏掉了。");
	}
}
