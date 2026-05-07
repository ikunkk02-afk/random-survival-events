package com.ikunkk02afk.randomsurvivalevents.event.impl.special;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BerserkEvent implements RandomEvent {
	@Override
	public String getId() {
		return "berserk";
	}

	@Override
	public String getName() {
		return "狂暴";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.SPECIAL;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0));
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 0));
		player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30 * 20, 0));
		RandomEventUtils.sendMessage(player, "你感觉自己像失控了一样。");
	}
}
