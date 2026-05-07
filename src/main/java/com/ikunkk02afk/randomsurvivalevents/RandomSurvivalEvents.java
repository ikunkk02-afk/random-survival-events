package com.ikunkk02afk.randomsurvivalevents;

import net.fabricmc.api.ModInitializer;

import com.ikunkk02afk.randomsurvivalevents.command.RandomSurvivalEventsCommands;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.block.BlockChaosManager;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventManager;
import com.ikunkk02afk.randomsurvivalevents.event.EventStateTicker;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventTicker;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.MiningLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomSurvivalEvents implements ModInitializer {
	public static final String MOD_ID = "random-survival-events";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		RandomSurvivalEventsConfig.load();
		ModMobEffects.register();
		RandomEventManager.initialize();
		BlockChaosManager.register();
		MiningLockManager.register();
		RandomEventTicker.register();
		EventStateTicker.register();
		RandomSurvivalEventsCommands.register();
		LOGGER.info("Random Survival Events initialized.");
	}
}
