package com.ikunkk02afk.randomsurvivalevents.event.impl.attribute;

import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.state.AttributeEventHelper;
import com.ikunkk02afk.randomsurvivalevents.event.state.AttributeEventIds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MaxHealthReduceEvent implements RandomEvent {
	@Override
	public String getId() {
		return "max_health_reduce";
	}

	@Override
	public String getName() {
		return "生命压缩";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.ATTRIBUTE;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		component.setMaxHealthReduceUntilTick(world.getGameTime() + 120L * 20L);
		AttributeEventHelper.applyMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_REDUCE, -4.0D);
		RandomEventUtils.sendMessage(player, "你的最大生命值被暂时压低了。");
	}
}
