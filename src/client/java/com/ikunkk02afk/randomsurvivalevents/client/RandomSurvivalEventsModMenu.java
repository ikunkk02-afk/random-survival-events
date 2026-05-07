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
		addDestructiveCategory(builder, entries, config);
		addDangerousMobCategory(builder, entries, config);
		addRecipeShuffleCategory(builder, entries, config);
		addWeightCategory(builder, entries, config);

		return builder.build();
	}

	private static void addBasicCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("基础设置"));
		category.addEntry(bool(entries, "是否启用随机事件", config.enableRandomEvents, true, value -> config.enableRandomEvents = value));
		category.addEntry(intField(entries, "事件间隔 tick", config.eventIntervalTicks, 1200, 20, 72000, value -> config.eventIntervalTicks = value));
	}

	private static void addEventTypeCategory(ConfigBuilder builder, ConfigEntryBuilder entries, RandomSurvivalEventsConfig config) {
		ConfigCategory category = builder.getOrCreateCategory(Component.literal("事件类型"));
		category.addEntry(bool(entries, "惩罚事件", config.enablePunishmentEvents, true, value -> config.enablePunishmentEvents = value));
		category.addEntry(bool(entries, "奖励事件", config.enableRewardEvents, true, value -> config.enableRewardEvents = value));
		category.addEntry(bool(entries, "中性事件", config.enableNeutralEvents, true, value -> config.enableNeutralEvents = value));
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
		category.addEntry(intField(entries, "配方打乱持续时间 tick", config.recipeShuffleDurationTicks, 1000, 20, 72000, value -> config.recipeShuffleDurationTicks = value));
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
