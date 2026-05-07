package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GravityCrushEvent implements RandomEvent {
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
		return RandomSurvivalEventsConfig.get().getGravityEventDurationTicks();
	}

	@Override
	public boolean managesStatusEffect() {
		return true;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive() || player.isCreative() || player.isSpectator()) {
			return;
		}

		int durationTicks = RandomSurvivalEventsConfig.get().getGravityEventDurationTicks();
		player.addEffect(new MobEffectInstance(ModMobEffects.GRAVITY_CRUSH, durationTicks, 0, false, true, true));
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, 6, false, true, true));
		player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, durationTicks, 1, false, true, true));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, 0, false, true, true));
		RandomEventUtils.sendMessage(player, "重力像铁块一样压住了你的身体。");
	}
}
