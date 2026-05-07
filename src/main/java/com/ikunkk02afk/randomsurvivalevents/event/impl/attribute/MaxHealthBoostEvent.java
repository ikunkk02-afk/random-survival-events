package com.ikunkk02afk.randomsurvivalevents.event.impl.attribute;

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

public class MaxHealthBoostEvent implements RandomEvent {
	@Override
	public String getId() {
		return "max_health_boost";
	}

	@Override
	public String getName() {
		return "生命扩容";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.ATTRIBUTE;
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
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		component.setMaxHealthBoostUntilTick(world.getGameTime() + getDefaultEventDurationTicks());
		AttributeEventHelper.applyMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_BOOST, 4.0D);
		player.heal(4.0F);
		RandomEventUtils.sendMessage(player, "你的最大生命值暂时提高了。");
	}
}
