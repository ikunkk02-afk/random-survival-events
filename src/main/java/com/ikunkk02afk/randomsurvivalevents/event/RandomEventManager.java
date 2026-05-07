package com.ikunkk02afk.randomsurvivalevents.event;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
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
import com.ikunkk02afk.randomsurvivalevents.event.impl.recipe.RecipeChaosEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.special.BerserkEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.special.GlowingMobsEvent;
import com.ikunkk02afk.randomsurvivalevents.event.impl.special.GravityChaosEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

		RandomSurvivalEvents.LOGGER.info("Registered {} random survival events.", EVENTS.size());
	}

	public static List<RandomEvent> getEvents() {
		return Collections.unmodifiableList(EVENTS);
	}

	public static void executeRandomEvent(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive() || EVENTS.isEmpty()) {
			return;
		}

		List<RandomEvent> availableEvents = EVENTS.stream().filter(RandomEventManager::isEnabled).toList();
		if (availableEvents.isEmpty()) {
			return;
		}

		RandomEvent event = availableEvents.get(RANDOM.nextInt(availableEvents.size()));
		RandomEventUtils.sendMessage(player, "随机事件：" + event.getName());
		event.execute(player, world);
		RandomSurvivalEvents.LOGGER.info("[Random Survival Events] Triggered event {} for player {}", event.getId(), player.getGameProfile().getName());
	}

	private static void register(RandomEvent event) {
		EVENTS.add(event);
	}

	private static boolean isEnabled(RandomEvent event) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (!config.enableRandomEvents) {
			return false;
		}

		if (!config.enableDangerousEvents && isDangerous(event)) {
			return false;
		}

		return switch (event.getCategory()) {
			case BLOCK -> config.enableBlockEvents && isBlockEventEnabled(event, config);
			case RECIPE -> config.enableRecipeChaosEvents && config.enableGlobalRecipeShuffle;
			case ATTRIBUTE -> config.enableAttributeEvents;
			default -> true;
		};
	}

	private static boolean isDangerous(RandomEvent event) {
		return event.getId().equals("monster_squad")
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
			return config.enableBlockChaosEffect;
		}
		return true;
	}
}
