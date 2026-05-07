package com.ikunkk02afk.randomsurvivalevents.event;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface RandomEvent {
	int DEFAULT_DISPLAY_EFFECT_DURATION_TICKS = 8 * 20;

	String getId();

	String getName();

	RandomEventCategory getCategory();

	RandomEventRarity getRarity();

	void execute(ServerPlayer player, ServerLevel world);

	default ResourceLocation getStatusEffectId() {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, getId());
	}

	default int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return DEFAULT_DISPLAY_EFFECT_DURATION_TICKS;
	}

	default int getDefaultEventDurationTicks() {
		return RandomSurvivalEventsConfig.get().getDefaultEventDurationTicks();
	}

	default boolean managesStatusEffect() {
		return false;
	}
}
