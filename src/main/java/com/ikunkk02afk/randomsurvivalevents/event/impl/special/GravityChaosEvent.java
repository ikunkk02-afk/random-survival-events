package com.ikunkk02afk.randomsurvivalevents.event.impl.special;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.gravity.GravityEventManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class GravityChaosEvent implements RandomEvent {
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
		return RandomSurvivalEventsConfig.get().getGravityEventDurationTicks();
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive() || player.isCreative() || player.isSpectator()) {
			return;
		}

		int durationTicks = RandomSurvivalEventsConfig.get().getGravityEventDurationTicks();
		player.fallDistance = 0.0F;
		player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, durationTicks, 0, false, true, true));
		Vec3 movement = player.getDeltaMovement();
		player.setDeltaMovement(movement.x, Math.max(movement.y, 0.35D), movement.z);
		player.hurtMarked = true;
		GravityEventManager.scheduleSlowFallingAfterLevitation(player, durationTicks);
		RandomEventUtils.sendMessage(player, "重力彻底失控，你开始飘向空中。");
	}
}
