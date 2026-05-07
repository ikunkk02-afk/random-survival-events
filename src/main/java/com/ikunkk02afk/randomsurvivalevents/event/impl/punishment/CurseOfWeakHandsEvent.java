package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CurseOfWeakHandsEvent implements RandomEvent {
	private static final int DURATION_TICKS = 45 * 20;

	@Override
	public String getId() {
		return "curse_of_weak_hands";
	}

	@Override
	public String getName() {
		return "废手诅咒";
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
	public boolean managesStatusEffect() {
		return true;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		player.addEffect(new MobEffectInstance(ModMobEffects.WEAK_HANDS, DURATION_TICKS, 0, false, true, true));
		player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, DURATION_TICKS, 4));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DURATION_TICKS, 1));
		RandomEventUtils.sendMessage(player, "废手诅咒缠住了你的双手。");
	}
}
