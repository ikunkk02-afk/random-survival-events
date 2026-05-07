package com.ikunkk02afk.randomsurvivalevents.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

public class RandomSurvivalEventsConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static RandomSurvivalEventsConfig instance = new RandomSurvivalEventsConfig();

	public boolean enableRandomEvents = true;
	public int eventIntervalTicks = 1200;
	public boolean enableBlockEvents = true;
	public boolean enableRecipeChaosEvents = true;
	public boolean enableRecipeShuffleEvents = true;
	public boolean enableGlobalRecipeShuffle = true;
	public boolean enableRewardEvents = true;
	public boolean enableNeutralEvents = true;
	public boolean enableBlockChaosEvents = true;
	public boolean enableAttributeEvents = true;
	public boolean enableDangerousEvents = true;
	public boolean enableDisasterEvents = true;
	public boolean allowBlockReplacement = true;
	public boolean allowTemporaryBlockChange = true;
	public int commonEventWeight = 50;
	public int uncommonEventWeight = 30;
	public int rareEventWeight = 15;
	public int epicEventWeight = 4;
	public int disasterEventWeight = 1;
	public int recipeShuffleDurationTicks = 1000;
	public boolean excludeOverpoweredRecipeResults = true;
	public boolean enableBlockChaosEffect = true;
	public int blockChaosDurationTicks = 1200;
	public double blockChaosDropChance = 1.0D;
	public double blockChaosMobSpawnChance = 0.35D;
	public double blockChaosRareItemChance = 0.15D;
	public double blockChaosDangerousMobChance = 0.15D;
	public int blockChaosCooldownTicks = 10;
	public boolean allowBossMobFromBlockChaos = true;
	public boolean allowDisasterBlockDamage = false;
	public double meteorRainExplosionPower = 2.0D;
	public boolean enablePunishmentEvents = true;
	public int punishmentEventWeightBonus = 20;
	public boolean allowExtremeMobs = false;
	public boolean allowBossMobs = false;
	public double dangerousMobChance = 0.15D;
	public boolean allowTemporaryTerrainChange = true;
	public boolean allowPermanentTerrainChange = false;
	public int temporaryTerrainRestoreTicks = 400;
	public int maxTemporaryChangedBlocks = 4096;
	public boolean allowLavaTrap = true;
	public int lavaTrapRestoreTicks = 200;
	public boolean allowInventoryShuffle = true;
	public boolean inventoryShuffleAffectsHotbar = false;
	public boolean allowCreeperRainBlockDamage = false;
	public int creeperRainCountMin = 3;
	public int creeperRainCountMax = 5;
	public boolean destructiveMode = false;
	public boolean enablePermanentPunishmentEvents = false;
	public boolean allowPermanentChunkDamage = false;
	public boolean allowPermanentLavaTrap = false;
	public boolean allowPermanentInventoryPunishment = false;
	public boolean allowPermanentMobDisaster = false;
	public List<String> toolBreakCurseProtectedItemIds = new ArrayList<>();

	public static RandomSurvivalEventsConfig get() {
		return instance;
	}

	public static void load() {
		Path path = getConfigPath();
		try {
			if (Files.notExists(path)) {
				instance = new RandomSurvivalEventsConfig();
				instance.sanitize();
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
			logDestructiveModeWarningIfNeeded();
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

	public static void logDestructiveModeWarningIfNeeded() {
		if (instance.destructiveMode) {
			RandomSurvivalEvents.LOGGER.warn("[Random Survival Events] Destructive Mode is enabled. Permanent world damage may occur.");
		}
	}

	private static Path getConfigPath() {
		return FabricLoader.getInstance().getConfigDir().resolve("random-survival-events.json");
	}

	private void sanitize() {
		if (eventIntervalTicks < 20) {
			eventIntervalTicks = 20;
		}
		if (recipeShuffleDurationTicks < 20) {
			recipeShuffleDurationTicks = 20;
		}
		if (blockChaosDurationTicks < 20) {
			blockChaosDurationTicks = 20;
		}
		if (blockChaosCooldownTicks < 1) {
			blockChaosCooldownTicks = 1;
		}
		if (temporaryTerrainRestoreTicks < 20) {
			temporaryTerrainRestoreTicks = 20;
		}
		if (lavaTrapRestoreTicks < 20) {
			lavaTrapRestoreTicks = 20;
		}
		if (maxTemporaryChangedBlocks < 1) {
			maxTemporaryChangedBlocks = 1;
		}
		if (creeperRainCountMin < 0) {
			creeperRainCountMin = 0;
		}
		if (creeperRainCountMax < creeperRainCountMin) {
			creeperRainCountMax = creeperRainCountMin;
		}
		if (toolBreakCurseProtectedItemIds == null) {
			toolBreakCurseProtectedItemIds = new ArrayList<>();
		}
		commonEventWeight = sanitizeWeight(commonEventWeight);
		uncommonEventWeight = sanitizeWeight(uncommonEventWeight);
		rareEventWeight = sanitizeWeight(rareEventWeight);
		epicEventWeight = sanitizeWeight(epicEventWeight);
		disasterEventWeight = sanitizeWeight(disasterEventWeight);
		punishmentEventWeightBonus = sanitizeWeight(punishmentEventWeightBonus);
		meteorRainExplosionPower = sanitizeMeteorPower(meteorRainExplosionPower);
		blockChaosDropChance = sanitizeChance(blockChaosDropChance);
		blockChaosMobSpawnChance = sanitizeChance(blockChaosMobSpawnChance);
		blockChaosRareItemChance = sanitizeChance(blockChaosRareItemChance);
		blockChaosDangerousMobChance = sanitizeChance(blockChaosDangerousMobChance);
		dangerousMobChance = sanitizeChance(dangerousMobChance);
	}

	public int getEventWeight(RandomEventRarity rarity) {
		return switch (rarity) {
			case COMMON -> commonEventWeight;
			case UNCOMMON -> uncommonEventWeight;
			case RARE -> rareEventWeight;
			case EPIC -> epicEventWeight;
			case DISASTER -> disasterEventWeight;
		};
	}

	private int sanitizeWeight(int weight) {
		return Math.max(0, weight);
	}

	private double sanitizeMeteorPower(double power) {
		if (Double.isNaN(power)) {
			return 2.0D;
		}
		return Math.max(1.5D, Math.min(2.5D, power));
	}

	private double sanitizeChance(double chance) {
		if (Double.isNaN(chance)) {
			return 0.0D;
		}
		return Math.max(0.0D, Math.min(1.0D, chance));
	}
}
