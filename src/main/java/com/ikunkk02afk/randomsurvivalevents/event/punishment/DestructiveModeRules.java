package com.ikunkk02afk.randomsurvivalevents.event.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;

public final class DestructiveModeRules {
	private DestructiveModeRules() {
	}

	public static boolean canPermanentlyDamageChunk(RandomSurvivalEventsConfig config) {
		return config.destructiveMode
				&& config.enablePermanentPunishmentEvents
				&& config.allowPermanentTerrainChange
				&& config.allowPermanentChunkDamage;
	}

	public static boolean canPermanentlyPlaceLava(RandomSurvivalEventsConfig config) {
		return config.destructiveMode
				&& config.enablePermanentPunishmentEvents
				&& config.allowPermanentTerrainChange
				&& config.allowPermanentLavaTrap;
	}

	public static boolean canPermanentlyPunishInventory(RandomSurvivalEventsConfig config) {
		return config.destructiveMode && config.enablePermanentPunishmentEvents && config.allowPermanentInventoryPunishment;
	}

	public static boolean canUsePermanentMobDisaster(RandomSurvivalEventsConfig config) {
		return config.destructiveMode && config.enablePermanentPunishmentEvents && config.allowPermanentMobDisaster;
	}
}
