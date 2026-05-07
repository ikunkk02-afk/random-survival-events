package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public class ItemDropCurseEvent implements RandomEvent {
	@Override
	public String getId() {
		return "item_drop_curse";
	}

	@Override
	public String getName() {
		return "手滑诅咒";
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

		player.addEffect(new MobEffectInstance(ModMobEffects.ITEM_DROP_CURSE, getDefaultEventDurationTicks(), 0, false, true, true));
		RandomEventUtils.sendMessage(player, "你的背包好像漏了。");
	}
}
