package com.ikunkk02afk.randomsurvivalevents.event;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.impl.AirDropEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.BarrageJudgementEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.HeavyAirEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.LuckyBlessingEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.MiniLightningEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.MonsterSquadEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.MysteriousTraderEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.ReasonableDuplicationEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.SweatyEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.WeatherShiftEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.attribute.GlassCannonEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.attribute.MaxHealthBoostEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.attribute.MaxHealthReduceEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.block.BlockChaosEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.block.LuckyOreEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.block.RandomChestEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.block.SlimeFloorEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.disaster.MeteorRainEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.BlindHuntEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.BedrockPrisonEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.CreeperRainEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.CurseOfWeakHandsEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.FakeChunkVoidEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.GravityCrushEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.HealthDebtEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.HostileStormEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.InventoryTaxEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.InventoryPanicEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.LavaCorruptionEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.LavaUnderfootEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.MiningLockEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.MonsterAmbushEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.PermanentChunkCollapseEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.RandomTeleportTrapEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.ToolBreakCurseEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.punishment.VoidCrackEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.recipe.RecipeChaosEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.special.BerserkEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.special.GlowingMobsEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.special.GravityChaosEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class RandomEventManager {
	private static final List<RandomEvent> EVENTS = new ArrayList<>();
	private static final Random RANDOM = new Random();

	private RandomEventManager() {
	}

	public static void initialize() {
		if (!EVENTS.isEmpty()) {
			return;
		}

		register(new AirDropEvent());
		register(new SweatyEvent());
		register(new LuckyBlessingEvent());
		register(new MonsterSquadEvent());
		register(new WeatherShiftEvent());
		register(new ReasonableDuplicationEvent());
		register(new BarrageJudgementEvent());
		register(new HeavyAirEvent());
		register(new MiniLightningEvent());
		register(new MysteriousTraderEvent());
		register(new LuckyOreEvent());
		register(new SlimeFloorEvent());
		register(new RandomChestEvent());
		register(new BlockChaosEvent());
		register(new MaxHealthBoostEvent());
		register(new MaxHealthReduceEvent());
		register(new GlassCannonEvent());
		register(new GravityChaosEvent());
		register(new BerserkEvent());
		register(new GlowingMobsEvent());
		register(new RecipeChaosEvent());
		register(new MeteorRainEvent());
		register(new MonsterAmbushEvent());
		register(new MiningLockEvent());
		register(new FakeChunkVoidEvent());
		register(new LavaUnderfootEvent());
		register(new GravityCrushEvent());
		register(new InventoryPanicEvent());
		register(new BlindHuntEvent());
		register(new RandomTeleportTrapEvent());
		register(new HealthDebtEvent());
		register(new CreeperRainEvent());
		register(new PermanentChunkCollapseEvent());
		register(new LavaCorruptionEvent());
		register(new InventoryTaxEvent());
		register(new ToolBreakCurseEvent());
		register(new BedrockPrisonEvent());
		register(new HostileStormEvent());
		register(new VoidCrackEvent());
		register(new CurseOfWeakHandsEvent());

		RandomSurvivalEvents.LOGGER.info("Registered {} random survival events.", EVENTS.size());
	}

	public static List<RandomEvent> getEvents() {
		return Collections.unmodifiableList(EVENTS);
	}

	public static void executeRandomEvent(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive() || EVENTS.isEmpty()) {
			return;
		}

		selectRandomEnabledEvent(player).ifPresent(event -> {
			RandomEventUtils.sendMessage(player, "随机事件：" + event.getName());
			if (executeEvent(player, world, event)) {
				RandomSurvivalEvents.LOGGER.info(
						"[Random Survival Events] Triggered event {} ({}) for player {}",
						event.getId(),
						event.getRarity(),
						player.getGameProfile().getName()
				);
			}
		});
	}

	public static boolean executeEvent(ServerPlayer player, ServerLevel world, RandomEvent event) {
		if (player == null || world == null || event == null || !player.isAlive()) {
			return false;
		}
		if (!isEnabled(event)) {
			return false;
		}
		if (!canExecuteEvent(player, event)) {
			return false;
		}

		event.execute(player, world);
		applyDisplayEffect(player, world, event);
		return true;
	}

	public static boolean canExecuteEvent(ServerPlayer player, RandomEvent event) {
		if (player == null || event == null || !player.isAlive()) {
			return false;
		}
		return event.getCategory() != RandomEventCategory.PUNISHMENT || canReceivePunishment(player);
	}

	public static Optional<RandomEvent> getEvent(String eventId) {
		if (eventId == null) {
			return Optional.empty();
		}
		return EVENTS.stream()
				.filter(event -> event.getId().equals(eventId))
				.findFirst();
	}

	public static Optional<RandomEvent> selectRandomEnabledEvent() {
		return selectWeighted(EVENTS.stream().filter(RandomEventManager::isEnabled).toList());
	}

	public static Optional<RandomEvent> selectRandomEnabledEvent(ServerPlayer player) {
		return selectWeighted(EVENTS.stream().filter(RandomEventManager::isEnabled).toList(), player);
	}

	public static Optional<RandomEvent> selectRandomEnabledEventByRarity(RandomEventRarity rarity) {
		if (rarity == null) {
			return Optional.empty();
		}
		return selectWeighted(EVENTS.stream()
				.filter(RandomEventManager::isEnabled)
				.filter(event -> event.getRarity() == rarity)
				.toList());
	}

	public static Optional<RandomEvent> selectRandomEnabledPunishmentEvent() {
		return selectWeighted(EVENTS.stream()
				.filter(RandomEventManager::isEnabled)
				.filter(event -> event.getCategory() == RandomEventCategory.PUNISHMENT)
				.toList());
	}

	public static void sendPreview(ServerPlayer player, RandomEvent event) {
		if (player == null || event == null) {
			return;
		}
		RandomEventUtils.sendMessage(player, event.getRarity().getPreviewMessage());
		RandomSurvivalEvents.LOGGER.info(
				"[Random Survival Events] Previewed event {} ({}) for player {}.",
				event.getId(),
				event.getRarity(),
				player.getGameProfile().getName()
		);
	}

	private static void register(RandomEvent event) {
		EVENTS.add(event);
	}

	private static Optional<RandomEvent> selectWeighted(List<RandomEvent> candidates) {
		return selectWeighted(candidates, null);
	}

	private static Optional<RandomEvent> selectWeighted(List<RandomEvent> candidates, ServerPlayer player) {
		if (candidates.isEmpty()) {
			return Optional.empty();
		}

		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		int totalWeight = 0;
		for (RandomEvent event : candidates) {
			totalWeight += getAdjustedWeight(event, config, player);
		}
		if (totalWeight <= 0) {
			return Optional.empty();
		}

		int selectedWeight = RANDOM.nextInt(totalWeight);
		for (RandomEvent event : candidates) {
			selectedWeight -= getAdjustedWeight(event, config, player);
			if (selectedWeight < 0) {
				return Optional.of(event);
			}
		}
		return Optional.empty();
	}

	private static void applyDisplayEffect(ServerPlayer player, ServerLevel world, RandomEvent event) {
		if (!canReceiveDisplayEffect(player) || event.managesStatusEffect()) {
			return;
		}

		ModMobEffects.getDisplayEffect(event.getStatusEffectId()).ifPresentOrElse(
				effect -> ModMobEffects.refreshEffect(player, effect, Math.max(1, event.getStatusEffectDurationTicks(player, world))),
				() -> RandomSurvivalEvents.LOGGER.warn(
						"[Random Survival Events] Event {} has no registered display mob effect {}.",
						event.getId(),
						event.getStatusEffectId()
				)
		);
	}

	private static boolean canReceiveDisplayEffect(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}

	private static boolean canReceivePunishment(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}

	private static boolean isEnabled(RandomEvent event) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (!config.enableRandomEvents) {
			return false;
		}

		if (!config.enableDangerousEvents && isDangerous(event)) {
			return false;
		}
		if (event.getRarity() == RandomEventRarity.DISASTER && !config.enableDisasterEvents) {
			return false;
		}
		if (isRewardEvent(event) && !config.enableRewardEvents) {
			return false;
		}
		if (isNeutralEvent(event) && !config.enableNeutralEvents) {
			return false;
		}

		return switch (event.getCategory()) {
			case BLOCK -> config.enableBlockEvents && isBlockEventEnabled(event, config);
			case RECIPE -> config.enableRecipeChaosEvents && config.enableRecipeShuffleEvents && config.enableGlobalRecipeShuffle;
			case ATTRIBUTE -> config.enableAttributeEvents;
			case PUNISHMENT -> config.enablePunishmentEvents && isPunishmentEventEnabled(event, config);
			default -> true;
		};
	}

	private static boolean isDangerous(RandomEvent event) {
		return event.getCategory() == RandomEventCategory.PUNISHMENT
				|| event.getId().equals("monster_squad")
				|| event.getId().equals("max_health_reduce")
				|| event.getId().equals("glass_cannon");
	}

	private static boolean isBlockEventEnabled(RandomEvent event, RandomSurvivalEventsConfig config) {
		if (event.getId().equals("lucky_ore")) {
			return config.allowBlockReplacement;
		}
		if (event.getId().equals("slime_floor")) {
			return config.allowTemporaryBlockChange;
		}
		if (event.getId().equals("block_chaos")) {
			return config.enableBlockChaosEffect && config.enableBlockChaosEvents;
		}
		return true;
	}

	private static boolean isPunishmentEventEnabled(RandomEvent event, RandomSurvivalEventsConfig config) {
		return switch (event.getId()) {
			case "fake_chunk_void" -> config.allowTemporaryTerrainChange;
			case "lava_underfoot" -> config.allowLavaTrap;
			case "inventory_panic" -> config.allowInventoryShuffle;
			case "creeper_rain" -> config.creeperRainCountMax > 0;
			case "permanent_chunk_collapse" -> canPermanentlyDamageChunk(config);
			case "lava_corruption" -> config.allowTemporaryTerrainChange || canPermanentlyDamageLava(config);
			case "inventory_tax" -> config.allowInventoryShuffle || canPermanentlyPunishInventory(config);
			case "void_crack" -> config.allowTemporaryTerrainChange || canPermanentlyDamageChunk(config);
			default -> true;
		};
	}

	private static int getAdjustedWeight(RandomEvent event, RandomSurvivalEventsConfig config, ServerPlayer player) {
		int baseWeight = Math.max(0, config.getEventWeight(event.getRarity()));
		if (baseWeight <= 0) {
			return 0;
		}
		if (event.getCategory() == RandomEventCategory.PUNISHMENT) {
			if (player != null && player.hasEffect(ModMobEffects.DOOM_MARK)) {
				baseWeight += config.punishmentEventWeightBonus + 30;
			}
			if (event.getRarity() == RandomEventRarity.DISASTER) {
				return baseWeight + Math.min(2, config.punishmentEventWeightBonus / 10);
			}
			return baseWeight + config.punishmentEventWeightBonus;
		}
		if (isRewardEvent(event)) {
			return Math.max(1, baseWeight / 2);
		}
		return baseWeight;
	}

	private static boolean isRewardEvent(RandomEvent event) {
		return event.getId().equals("air_drop")
				|| event.getId().equals("lucky_blessing")
				|| event.getId().equals("mysterious_trader")
				|| event.getId().equals("reasonable_duplication")
				|| event.getId().equals("lucky_ore")
				|| event.getId().equals("random_chest")
				|| event.getId().equals("max_health_boost");
	}

	private static boolean isNeutralEvent(RandomEvent event) {
		if (event.getCategory() == RandomEventCategory.PUNISHMENT
				|| event.getCategory() == RandomEventCategory.RECIPE
				|| event.getCategory() == RandomEventCategory.ATTRIBUTE
				|| event.getId().equals("block_chaos")
				|| isRewardEvent(event)) {
			return false;
		}
		return event.getRarity() != RandomEventRarity.DISASTER;
	}

	private static boolean canPermanentlyDamageChunk(RandomSurvivalEventsConfig config) {
		return config.destructiveMode
				&& config.enablePermanentPunishmentEvents
				&& config.allowPermanentTerrainChange
				&& config.allowPermanentChunkDamage;
	}

	private static boolean canPermanentlyDamageLava(RandomSurvivalEventsConfig config) {
		return config.destructiveMode
				&& config.enablePermanentPunishmentEvents
				&& config.allowPermanentTerrainChange
				&& config.allowPermanentLavaTrap;
	}

	private static boolean canPermanentlyPunishInventory(RandomSurvivalEventsConfig config) {
		return config.destructiveMode && config.enablePermanentPunishmentEvents && config.allowPermanentInventoryPunishment;
	}
}
