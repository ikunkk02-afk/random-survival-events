package com.ikunkk02afk.randomsurvivalevents.event.impl.recipe;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeChaosState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RecipeChaosEvent implements RandomEvent {
	@Override
	public String getId() {
		return "recipe_chaos";
	}

	@Override
	public String getName() {
		return "配方异变";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.RECIPE;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		RecipeChaosState.activate(player, world.getGameTime() + 60L * 20L);
		RandomEventUtils.sendMessage(player, "配方发生了奇怪的偏移。");
	}
}
