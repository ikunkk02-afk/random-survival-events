package com.ikunkk02afk.randomsurvivalevents.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
	public static final int DEFAULT_EVENT_INTERVAL_TICKS = 3600;
	public static final int DEFAULT_EVENT_DURATION_TICKS = 3400;
	public static final int DEFAULT_GRAVITY_EVENT_DURATION_TICKS = 1000;
	public static final boolean DEFAULT_ALLOW_EVENT_OVERLAP = false;
	private static final int OLD_DEFAULT_EVENT_INTERVAL_TICKS = 1200;
	private static final int OLD_DEFAULT_RECIPE_SHUFFLE_DURATION_TICKS = 1000;
	private static final int OLD_DEFAULT_BLOCK_CHAOS_DURATION_TICKS = 1200;
	private static final int OLD_DEFAULT_TEMPORARY_TERRAIN_RESTORE_TICKS = 400;
	private static final int OLD_DEFAULT_LAVA_TRAP_RESTORE_TICKS = 200;
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static RandomSurvivalEventsConfig instance = new RandomSurvivalEventsConfig();

	public boolean enableRandomEvents = true;
	public int eventIntervalTicks = DEFAULT_EVENT_INTERVAL_TICKS;
	public int defaultEventDurationTicks = DEFAULT_EVENT_DURATION_TICKS;
	public boolean allowEventOverlap = DEFAULT_ALLOW_EVENT_OVERLAP;
	public boolean enableBlockEvents = true;
	public boolean enableRecipeChaosEvents = true;
	public boolean enableRecipeShuffleEvents = true;
	public boolean enableGlobalRecipeShuffle = true;
	public boolean enableRewardEvents = true;
	public boolean enableNeutralEvents = true;
	public boolean enableGravityChaos = true;
	public boolean enableGravityCrush = true;
	public int gravityEventDurationTicks = DEFAULT_GRAVITY_EVENT_DURATION_TICKS;
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
	public int recipeShuffleDurationTicks = DEFAULT_EVENT_DURATION_TICKS;
	public boolean excludeOverpoweredRecipeResults = true;
	public boolean enableBlockChaosEffect = true;
	public int blockChaosDurationTicks = DEFAULT_EVENT_DURATION_TICKS;
	public double blockChaosDropChance = 1.0D;
	public double blockChaosMobSpawnChance = 0.35D;
	public double blockChaosRareItemChance = 0.15D;
	public double blockChaosDangerousMobChance = 0.15D;
	public int blockChaosCooldownTicks = 10;
	public boolean allowBossMobFromBlockChaos = true;
	public boolean allowDisasterBlockDamage = false;
	public double meteorRainExplosionPower = 2.0D;
	public boolean enablePunishmentEvents = true;
	public boolean enableMorePunishmentEvents = true;
	public boolean enableReverseControl = true;
	public boolean enableItemDropCurse = true;
	public boolean enableFoodPoison = true;
	public boolean enableArmorRust = true;
	public boolean enableWaterToLava = true;
	public boolean enableRandomExplosion = true;
	public boolean enableMobDisguise = true;
	public boolean enableHungerCollapse = true;
	public boolean enableInventoryLock = true;
	public boolean enableFallingAnvil = true;
	public int punishmentEventWeightBonus = 20;
	public boolean allowExtremeMobs = false;
	public boolean allowBossMobs = false;
	public double dangerousMobChance = 0.15D;
	public boolean allowTemporaryTerrainChange = true;
	public boolean allowPermanentTerrainChange = false;
	public int temporaryTerrainRestoreTicks = DEFAULT_EVENT_DURATION_TICKS;
	public int maxTemporaryChangedBlocks = 4096;
	public boolean allowLavaTrap = true;
	public int lavaTrapRestoreTicks = DEFAULT_EVENT_DURATION_TICKS;
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
	public boolean allowWaterToLavaPermanent = false;
	public boolean allowArmorPermanentDamage = false;
	public boolean allowFoodPermanentCorruption = false;
	public boolean allowExplosionBlockDamage = false;
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
				JsonObject configJson = JsonParser.parseReader(reader).getAsJsonObject();
				RandomSurvivalEventsConfig loaded = GSON.fromJson(configJson, RandomSurvivalEventsConfig.class);
				if (loaded != null) {
					loaded.migrateLegacyDefaults(configJson);
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

	public int getDefaultEventDurationTicks() {
		return Math.max(1, defaultEventDurationTicks);
	}

	public int getGravityEventDurationTicks() {
		return Math.max(1, gravityEventDurationTicks);
	}

	public void setEventIntervalTicks(int ticks) {
		eventIntervalTicks = Math.max(20, ticks);
	}

	public void setDefaultEventDurationTicks(int ticks) {
		int previousDefault = defaultEventDurationTicks;
		defaultEventDurationTicks = Math.max(20, ticks);
		updateDurationIfFollowingDefault(previousDefault);
	}

	private static Path getConfigPath() {
		return FabricLoader.getInstance().getConfigDir().resolve("random-survival-events.json");
	}

	private void migrateLegacyDefaults(JsonObject configJson) {
		boolean legacyDurationSchema = !configJson.has("defaultEventDurationTicks");
		if (!configJson.has("eventIntervalTicks")
				|| (legacyDurationSchema && eventIntervalTicks == OLD_DEFAULT_EVENT_INTERVAL_TICKS)) {
			eventIntervalTicks = DEFAULT_EVENT_INTERVAL_TICKS;
		}
		if (!configJson.has("defaultEventDurationTicks")) {
			defaultEventDurationTicks = DEFAULT_EVENT_DURATION_TICKS;
		}
		if (!configJson.has("allowEventOverlap")) {
			allowEventOverlap = DEFAULT_ALLOW_EVENT_OVERLAP;
		}
		if (!configJson.has("enableGravityChaos")) {
			enableGravityChaos = true;
		}
		if (!configJson.has("enableGravityCrush")) {
			enableGravityCrush = true;
		}
		if (!configJson.has("gravityEventDurationTicks")) {
			gravityEventDurationTicks = DEFAULT_GRAVITY_EVENT_DURATION_TICKS;
		}
		if (legacyDurationSchema) {
			if (recipeShuffleDurationTicks == OLD_DEFAULT_RECIPE_SHUFFLE_DURATION_TICKS) {
				recipeShuffleDurationTicks = defaultEventDurationTicks;
			}
			if (blockChaosDurationTicks == OLD_DEFAULT_BLOCK_CHAOS_DURATION_TICKS) {
				blockChaosDurationTicks = defaultEventDurationTicks;
			}
			if (temporaryTerrainRestoreTicks == OLD_DEFAULT_TEMPORARY_TERRAIN_RESTORE_TICKS) {
				temporaryTerrainRestoreTicks = defaultEventDurationTicks;
			}
			if (lavaTrapRestoreTicks == OLD_DEFAULT_LAVA_TRAP_RESTORE_TICKS) {
				lavaTrapRestoreTicks = defaultEventDurationTicks;
			}
		}
	}

	private void updateDurationIfFollowingDefault(int previousDefault) {
		if (recipeShuffleDurationTicks == previousDefault
				|| recipeShuffleDurationTicks == OLD_DEFAULT_RECIPE_SHUFFLE_DURATION_TICKS) {
			recipeShuffleDurationTicks = defaultEventDurationTicks;
		}
		if (blockChaosDurationTicks == previousDefault
				|| blockChaosDurationTicks == OLD_DEFAULT_BLOCK_CHAOS_DURATION_TICKS) {
			blockChaosDurationTicks = defaultEventDurationTicks;
		}
		if (temporaryTerrainRestoreTicks == previousDefault
				|| temporaryTerrainRestoreTicks == OLD_DEFAULT_TEMPORARY_TERRAIN_RESTORE_TICKS) {
			temporaryTerrainRestoreTicks = defaultEventDurationTicks;
		}
		if (lavaTrapRestoreTicks == previousDefault
				|| lavaTrapRestoreTicks == OLD_DEFAULT_LAVA_TRAP_RESTORE_TICKS) {
			lavaTrapRestoreTicks = defaultEventDurationTicks;
		}
	}

	private void sanitize() {
		if (eventIntervalTicks < 20) {
			eventIntervalTicks = 20;
		}
		if (defaultEventDurationTicks < 20) {
			defaultEventDurationTicks = 20;
		}
		if (gravityEventDurationTicks < 20) {
			gravityEventDurationTicks = DEFAULT_GRAVITY_EVENT_DURATION_TICKS;
		}
		updateDurationIfFollowingDefault(DEFAULT_EVENT_DURATION_TICKS);
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
