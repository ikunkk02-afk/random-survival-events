package com.ikunkk02afk.randomsurvivalevents.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class RandomSurvivalEventsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static RandomSurvivalEventsConfig instance = new RandomSurvivalEventsConfig();

	public boolean enableRandomEvents = true;
	public int eventIntervalTicks = 1200;
	public boolean enableBlockEvents = true;
	public boolean enableRecipeChaosEvents = true;
	public boolean enableAttributeEvents = true;
	public boolean enableDangerousEvents = true;
	public boolean allowBlockReplacement = true;
	public boolean allowTemporaryBlockChange = true;

	public static RandomSurvivalEventsConfig get() {
		return instance;
	}

	public static void load() {
		Path path = getConfigPath();
		try {
			if (Files.notExists(path)) {
				save();
				return;
			}

			try (Reader reader = Files.newBufferedReader(path)) {
				RandomSurvivalEventsConfig loaded = GSON.fromJson(reader, RandomSurvivalEventsConfig.class);
				if (loaded != null) {
					instance = loaded;
					instance.sanitize();
				}
			}
			save();
		} catch (IOException exception) {
			RandomSurvivalEvents.LOGGER.warn("Failed to load Random Survival Events config, using defaults.", exception);
			instance = new RandomSurvivalEventsConfig();
		}
	}

	public static void save() {
		Path path = getConfigPath();
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException exception) {
			RandomSurvivalEvents.LOGGER.warn("Failed to save Random Survival Events config.", exception);
		}
	}

	private static Path getConfigPath() {
		return FabricLoader.getInstance().getConfigDir().resolve("random-survival-events.json");
	}

	private void sanitize() {
		if (eventIntervalTicks < 20) {
			eventIntervalTicks = 20;
		}
	}
}
