package com.ikunkk02afk.randomsurvivalevents.client;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import java.util.function.Consumer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RandomSurvivalEventsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return RandomSurvivalEventsModMenu::createConfigScreen;
	}

	private static Screen createConfigScreen(Screen parent) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.literal("Random Survival Events Config"))
				.setSavingRunnable(() -> {
					RandomSurvivalEventsConfig.save();
					RandomSurvivalEventsConfig.logDestructiveModeWarningIfNeeded();
				});
		ConfigEntryBuilder entries = builder.entryBuilder();

		addBasicCategory(builder, entries, config);
		addEventTypeCategory(builder, entries, config);
		addMorePunishmentCategory(builder, entries, config);
		addDestructiveCategory(builder, entries, config);
		addDangerousMobCategory(builder, entries, config);
		addRecipeShuffleCategory(builder, entries, config);
		addWeightCategory(builder, entries, config);

		return builder.build();
	}

	private static void addBasicCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("基础设置"));
		category.addEntry(bool(entries, "是否启用随机事件", config.enableRandomEvents, true, value -> config.enableRandomEvents = value));
		category.addEntry(intField(
				entries,
				"随机事件触发间隔",
				config.eventIntervalTicks,
				RandomSurvivalEventsConfig.DEFAULT_EVENT_INTERVAL_TICKS,
				600,
				12000,
				"控制随机事件多久触发一次。20 tick = 1 秒，3600 tick = 3 分钟。",
				config::setEventIntervalTicks
		));
		category.addEntry(intField(
				entries,
				"随机事件默认持续时间",
				config.defaultEventDurationTicks,
				RandomSurvivalEventsConfig.DEFAULT_EVENT_DURATION_TICKS,
				200,
				12000,
				"控制大多数持续型随机事件的持续时间。3400 tick = 2 分 50 秒。",
				config::setDefaultEventDurationTicks
		));
		category.addEntry(bool(entries, "允许事件重叠", config.allowEventOverlap, RandomSurvivalEventsConfig.DEFAULT_ALLOW_EVENT_OVERLAP, value -> config.allowEventOverlap = value));
		category.addEntry(intField(
				entries,
				"重力事件持续时间",
				config.gravityEventDurationTicks,
				RandomSurvivalEventsConfig.DEFAULT_GRAVITY_EVENT_DURATION_TICKS,
				200,
				12000,
				"控制重力混乱和重力压制的持续时间。1000 tick = 50 秒。",
				value -> config.gravityEventDurationTicks = value
		));
	}

	private static void addEventTypeCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("事件类型"));
		category.addEntry(bool(entries, "惩罚事件", config.enablePunishmentEvents, true, value -> config.enablePunishmentEvents = value));
		category.addEntry(bool(entries, "奖励事件", config.enableRewardEvents, true, value -> config.enableRewardEvents = value));
		category.addEntry(bool(entries, "中性事件", config.enableNeutralEvents, true, value -> config.enableNeutralEvents = value));
		category.addEntry(bool(entries, "重力混乱", config.enableGravityChaos, true, value -> config.enableGravityChaos = value));
		category.addEntry(bool(entries, "重力压制", config.enableGravityCrush, true, value -> config.enableGravityCrush = value));
		category.addEntry(bool(entries, "配方打乱", config.enableRecipeShuffleEvents, true, value -> {
			config.enableRecipeShuffleEvents = value;
			config.enableRecipeChaosEvents = value;
		}));
		category.addEntry(bool(entries, "方块异变", config.enableBlockChaosEvents, true, value -> {
			config.enableBlockChaosEvents = value;
			config.enableBlockChaosEffect = value;
		}));
		category.addEntry(bool(entries, "属性事件", config.enableAttributeEvents, true, value -> config.enableAttributeEvents = value));
		category.addEntry(bool(entries, "灾难事件", config.enableDisasterEvents, true, value -> config.enableDisasterEvents = value));
	}

	private static void addMorePunishmentCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("新增惩罚事件"));
		category.addEntry(bool(entries, "启用新增惩罚事件总开关", config.enableMorePunishmentEvents, true, value -> config.enableMorePunishmentEvents = value));
		category.addEntry(bool(entries, "方向错乱", config.enableReverseControl, true, value -> config.enableReverseControl = value));
		category.addEntry(bool(entries, "手滑诅咒", config.enableItemDropCurse, true, value -> config.enableItemDropCurse = value));
		category.addEntry(bool(entries, "食物腐坏", config.enableFoodPoison, true, value -> config.enableFoodPoison = value));
		category.addEntry(bool(entries, "护甲生锈", config.enableArmorRust, true, value -> config.enableArmorRust = value));
		category.addEntry(bool(entries, "水源异变", config.enableWaterToLava, true, value -> config.enableWaterToLava = value));
		category.addEntry(bool(entries, "不稳定爆裂", config.enableRandomExplosion, true, value -> config.enableRandomExplosion = value));
		category.addEntry(bool(entries, "伪装袭击", config.enableMobDisguise, true, value -> config.enableMobDisguise = value));
		category.addEntry(bool(entries, "体力崩溃", config.enableHungerCollapse, true, value -> config.enableHungerCollapse = value));
		category.addEntry(bool(entries, "背包封锁", config.enableInventoryLock, true, value -> config.enableInventoryLock = value));
		category.addEntry(bool(entries, "天降铁砧", config.enableFallingAnvil, true, value -> config.enableFallingAnvil = value));
	}

	private static void addDestructiveCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("毁灭模式"));
		category.addEntry(entries.startTextDescription(Component.literal("毁灭模式会永久改变世界，请谨慎开启。"))
				.setColor(0xFF5555)
				.build());
		category.addEntry(bool(entries, "是否开启毁灭模式", config.destructiveMode, false, value -> config.destructiveMode = value));
		category.addEntry(bool(entries, "是否允许永久惩罚事件", config.enablePermanentPunishmentEvents, false, value -> config.enablePermanentPunishmentEvents = value));
		category.addEntry(bool(entries, "是否允许永久区块破坏", config.allowPermanentChunkDamage, false, value -> config.allowPermanentChunkDamage = value));
		category.addEntry(bool(entries, "是否允许永久岩浆陷阱", config.allowPermanentLavaTrap, false, value -> config.allowPermanentLavaTrap = value));
		category.addEntry(bool(entries, "是否允许删除背包物品", config.allowPermanentInventoryPunishment, false, value -> config.allowPermanentInventoryPunishment = value));
		category.addEntry(bool(entries, "是否允许永久怪物灾难", config.allowPermanentMobDisaster, false, value -> config.allowPermanentMobDisaster = value));
		category.addEntry(bool(entries, "是否允许临时地形变化", config.allowTemporaryTerrainChange, true, value -> config.allowTemporaryTerrainChange = value));
		category.addEntry(bool(entries, "是否允许永久地形变化", config.allowPermanentTerrainChange, false, value -> config.allowPermanentTerrainChange = value));
		category.addEntry(bool(entries, "是否允许水源永久变岩浆", config.allowWaterToLavaPermanent, false, value -> config.allowWaterToLavaPermanent = value));
		category.addEntry(bool(entries, "是否允许护甲永久损耗", config.allowArmorPermanentDamage, false, value -> config.allowArmorPermanentDamage = value));
		category.addEntry(bool(entries, "是否允许食物永久腐坏", config.allowFoodPermanentCorruption, false, value -> config.allowFoodPermanentCorruption = value));
		category.addEntry(bool(entries, "是否允许爆炸破坏方块", config.allowExplosionBlockDamage, false, value -> config.allowExplosionBlockDamage = value));
		category.addEntry(intField(entries, "最大临时变化方块数", config.maxTemporaryChangedBlocks, 4096, 1, 65536, value -> config.maxTemporaryChangedBlocks = value));
	}

	private static void addDangerousMobCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("危险生物"));
		category.addEntry(bool(entries, "是否允许极端生物", config.allowExtremeMobs, false, value -> config.allowExtremeMobs = value));
		category.addEntry(bool(entries, "是否允许 Boss 生物", config.allowBossMobs, false, value -> config.allowBossMobs = value));
		category.addEntry(doubleField(entries, "危险生物概率", config.dangerousMobChance, 0.15D, 0.0D, 1.0D, value -> {
			config.dangerousMobChance = value;
			config.blockChaosDangerousMobChance = value;
		}));
	}

	private static void addRecipeShuffleCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("配方打乱"));
		category.addEntry(bool(entries, "是否启用全局配方打乱", config.enableGlobalRecipeShuffle, true, value -> config.enableGlobalRecipeShuffle = value));
		category.addEntry(intField(entries, "配方打乱持续时间 tick", config.recipeShuffleDurationTicks, RandomSurvivalEventsConfig.DEFAULT_EVENT_DURATION_TICKS, 20, 72000, value -> config.recipeShuffleDurationTicks = value));
	}

	private static void addWeightCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("事件权重"));
		category.addEntry(intField(entries, "COMMON 权重", config.commonEventWeight, 50, 0, 10000, value -> config.commonEventWeight = value));
		category.addEntry(intField(entries, "UNCOMMON 权重", config.uncommonEventWeight, 30, 0, 10000, value -> config.uncommonEventWeight = value));
		category.addEntry(intField(entries, "RARE 权重", config.rareEventWeight, 15, 0, 10000, value -> config.rareEventWeight = value));
		category.addEntry(intField(entries, "EPIC 权重", config.epicEventWeight, 4, 0, 10000, value -> config.epicEventWeight = value));
		category.addEntry(intField(entries, "DISASTER 权重", config.disasterEventWeight, 1, 0, 10000, value -> config.disasterEventWeight = value));
	}

	private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<?> bool(
			ConfigEntryBuilder entries,
			String label,
			boolean value,
			boolean defaultValue,
			Consumer<Boolean> saveConsumer
	) {
		return entries.startBooleanToggle(Component.literal(label), value)
				.setDefaultValue(defaultValue)
				.setSaveConsumer(saveConsumer)
				.build();
	}

	private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<?> intField(
			ConfigEntryBuilder entries,
			String label,
			int value,
			int defaultValue,
			int min,
			int max,
			Consumer<Integer> saveConsumer
	) {
		return entries.startIntField(Component.literal(label), value)
				.setDefaultValue(defaultValue)
				.setMin(min)
				.setMax(max)
				.setSaveConsumer(saveConsumer)
				.build();
	}

	private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<?> intField(
			ConfigEntryBuilder entries,
			String label,
			int value,
			int defaultValue,
			int min,
			int max,
			String tooltip,
			Consumer<Integer> saveConsumer
	) {
		return entries.startIntField(Component.literal(label), value)
				.setDefaultValue(defaultValue)
				.setMin(min)
				.setMax(max)
				.setTooltip(Component.literal(tooltip))
				.setSaveConsumer(saveConsumer)
				.build();
	}

	private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<?> doubleField(
			ConfigEntryBuilder entries,
			String label,
			double value,
			double defaultValue,
			double min,
			double max,
			Consumer<Double> saveConsumer
	) {
		return entries.startDoubleField(Component.literal(label), value)
				.setDefaultValue(defaultValue)
				.setMin(min)
				.setMax(max)
				.setSaveConsumer(saveConsumer)
				.build();
	}
}
