package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.state.AttributeEventHelper;
import com.ikunkk02afk.randomsurvivalevents.event.state.AttributeEventIds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class HealthDebtEvent implements RandomEvent {
	@Override
	public String getId() {
		return "health_debt";
	}

	@Override
	public String getName() {
		return "生命透支";
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
		return getDefaultEventDurationTicks();
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		int durationTicks = getDefaultEventDurationTicks();
		component.setHealthDebtUntilTick(world.getGameTime() + durationTicks);
		AttributeEventHelper.applyMaxHealthModifier(player, AttributeEventIds.HEALTH_DEBT, -6.0D);
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, 0));
		RandomEventUtils.sendMessage(player, "你的生命力被短暂抽走了。");
	}
}
