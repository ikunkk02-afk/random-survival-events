package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public class ReverseControlEvent implements RandomEvent {
	@Override
	public String getId() {
		return "reverse_control";
	}

	@Override
	public String getName() {
		return "方向错乱";
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
	public boolean managesStatusEffect() {
		return true;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		player.addEffect(new MobEffectInstance(ModMobEffects.REVERSE_CONTROL, getDefaultEventDurationTicks(), 0, false, true, true));
		RandomEventUtils.sendMessage(player, "你的方向感彻底乱了。");
	}
}
