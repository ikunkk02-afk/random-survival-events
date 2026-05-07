package com.ikunkk02afk.randomsurvivalevents.command;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventManager;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventTicker;
import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeShuffleManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public final class RandomSurvivalEventsCommands {
	private RandomSurvivalEventsCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
				Commands.literal("rse")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("trigger")
								.then(Commands.argument("event_id", StringArgumentType.word())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(
												RandomEventManager.getEvents().stream().map(RandomEvent::getId).toList(),
												builder
										))
										.executes(context -> triggerEvent(
												context.getSource(),
												StringArgumentType.getString(context, "event_id")
										))))
						.then(Commands.literal("random")
								.executes(context -> triggerRandomEvent(context.getSource())))
						.then(Commands.literal("destructive")
								.then(Commands.literal("on")
										.executes(context -> setDestructiveMode(context.getSource(), true)))
								.then(Commands.literal("off")
										.executes(context -> setDestructiveMode(context.getSource(), false))))
						.then(Commands.literal("config")
								.then(Commands.literal("reload")
										.executes(context -> reloadConfig(context.getSource())))
								.then(Commands.literal("show")
										.executes(context -> showConfig(context.getSource()))))
						.then(Commands.literal("trigger_punishment")
								.executes(context -> triggerRandomPunishmentEvent(context.getSource())))
						.then(Commands.literal("trigger_rarity")
								.then(Commands.argument("rarity", StringArgumentType.word())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(
												Arrays.stream(RandomEventRarity.values()).map(RandomEventRarity::getCommandName).toList(),
												builder
										))
										.executes(context -> triggerRarity(
												context.getSource(),
												StringArgumentType.getString(context, "rarity")
										))))
						.then(Commands.literal("preview")
								.then(Commands.argument("event_id", StringArgumentType.word())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(
												RandomEventManager.getEvents().stream().map(RandomEvent::getId).toList(),
												builder
										))
										.executes(context -> previewEvent(
												context.getSource(),
												StringArgumentType.getString(context, "event_id")
										))))
						.then(Commands.literal("recipechaos")
								.executes(context -> activateRecipeChaos(context.getSource())))
						.then(Commands.literal("recipeshuffle")
								.executes(context -> activateRecipeShuffle(context.getSource())))
						.then(Commands.literal("effect")
								.then(Commands.argument("effect_id", StringArgumentType.string())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(
												ModMobEffects.getEffectIds(),
												builder
										))
										.executes(context -> activateRseEffect(
												context.getSource(),
												StringArgumentType.getString(context, "effect_id")
										))))
						.then(Commands.literal("status")
								.executes(context -> showStatus(context.getSource())))
		));
	}

	private static int triggerEvent(CommandSourceStack source, String eventId) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("RSE debug event cannot run while the player is dead."));
			return 0;
		}

		RandomEvent event = RandomEventManager.getEvent(eventId).orElse(null);
		if (event == null) {
			source.sendFailure(Component.literal("Unknown RSE event id: " + eventId));
			return 0;
		}

		return executeDebugEvent(source, player, event) ? 1 : 0;
	}

	private static int triggerRandomEvent(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("RSE debug event cannot run while the player is dead."));
			return 0;
		}

		RandomEvent event = RandomEventManager.selectRandomEnabledEvent(player).orElse(null);
		if (event == null) {
			source.sendFailure(Component.literal("No enabled RSE events can be selected with the current weights."));
			return 0;
		}
		return executeDebugEvent(source, player, event) ? 1 : 0;
	}

	private static int triggerRandomPunishmentEvent(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
			source.sendFailure(Component.literal("RSE punishment events require a living survival/adventure player."));
			return 0;
		}

		RandomEvent event = RandomEventManager.selectRandomEnabledPunishmentEvent().orElse(null);
		if (event == null) {
			source.sendFailure(Component.literal("No enabled RSE punishment events can be selected with the current config."));
			return 0;
		}
		return executeDebugEvent(source, player, event) ? 1 : 0;
	}

	private static int setDestructiveMode(CommandSourceStack source, boolean enabled) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		config.destructiveMode = enabled;
		config.enablePermanentPunishmentEvents = enabled;
		config.allowPermanentChunkDamage = enabled;
		config.allowPermanentLavaTrap = enabled;
		config.allowPermanentInventoryPunishment = enabled;
		config.allowPermanentMobDisaster = enabled;
		config.allowPermanentTerrainChange = enabled;
		RandomSurvivalEventsConfig.save();

		if (enabled) {
			RandomSurvivalEventsConfig.logDestructiveModeWarningIfNeeded();
			source.sendSuccess(() -> Component.literal("RSE destructive mode enabled. Permanent punishment damage is now allowed."), true);
		} else {
			source.sendSuccess(() -> Component.literal("RSE destructive mode disabled. Permanent punishment damage is blocked."), true);
		}
		return 1;
	}

	private static int reloadConfig(CommandSourceStack source) {
		RandomSurvivalEventsConfig.load();
		source.sendSuccess(() -> Component.literal("RSE config reloaded from random-survival-events.json."), true);
		return 1;
	}

	private static int showConfig(CommandSourceStack source) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		source.sendSuccess(() -> Component.literal("RSE key config:"), false);
		source.sendSuccess(() -> Component.literal("- enableRandomEvents: " + config.enableRandomEvents), false);
		source.sendSuccess(() -> Component.literal("- eventIntervalTicks: " + config.eventIntervalTicks), false);
		source.sendSuccess(() -> Component.literal("- enablePunishmentEvents: " + config.enablePunishmentEvents), false);
		source.sendSuccess(() -> Component.literal("- enableRewardEvents: " + config.enableRewardEvents), false);
		source.sendSuccess(() -> Component.literal("- enableNeutralEvents: " + config.enableNeutralEvents), false);
		source.sendSuccess(() -> Component.literal("- enableRecipeShuffleEvents: " + config.enableRecipeShuffleEvents), false);
		source.sendSuccess(() -> Component.literal("- enableGlobalRecipeShuffle: " + config.enableGlobalRecipeShuffle), false);
		source.sendSuccess(() -> Component.literal("- enableBlockChaosEvents: " + config.enableBlockChaosEvents), false);
		source.sendSuccess(() -> Component.literal("- enableAttributeEvents: " + config.enableAttributeEvents), false);
		source.sendSuccess(() -> Component.literal("- enableDisasterEvents: " + config.enableDisasterEvents), false);
		source.sendSuccess(() -> Component.literal("- destructiveMode: " + config.destructiveMode), false);
		source.sendSuccess(() -> Component.literal("- enablePermanentPunishmentEvents: " + config.enablePermanentPunishmentEvents), false);
		source.sendSuccess(() -> Component.literal("- allowPermanentTerrainChange: " + config.allowPermanentTerrainChange), false);
		source.sendSuccess(() -> Component.literal("- allowBossMobs: " + config.allowBossMobs), false);
		source.sendSuccess(() -> Component.literal("- allowExtremeMobs: " + config.allowExtremeMobs), false);
		return 1;
	}

	private static int triggerRarity(CommandSourceStack source, String rarityName) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("RSE debug event cannot run while the player is dead."));
			return 0;
		}

		RandomEventRarity rarity = RandomEventRarity.fromCommandName(rarityName).orElse(null);
		if (rarity == null) {
			source.sendFailure(Component.literal("Unknown RSE rarity: " + rarityName));
			return 0;
		}

		RandomEvent event = RandomEventManager.selectRandomEnabledEventByRarity(rarity).orElse(null);
		if (event == null) {
			source.sendFailure(Component.literal("No enabled RSE events can be selected for rarity: " + rarity.getCommandName()));
			return 0;
		}

		return executeDebugEvent(source, player, event) ? 1 : 0;
	}

	private static int previewEvent(CommandSourceStack source, String eventId) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
			source.sendFailure(Component.literal("RSE preview requires a living survival/adventure player."));
			return 0;
		}

		RandomEvent event = RandomEventManager.getEvent(eventId).orElse(null);
		if (event == null) {
			source.sendFailure(Component.literal("Unknown RSE event id: " + eventId));
			return 0;
		}

		if (!RandomEventTicker.schedulePreview(player, event)) {
			source.sendFailure(Component.literal("RSE preview could not start; the player may already have a pending preview."));
			return 0;
		}

		source.sendSuccess(() -> Component.literal("Previewing RSE event " + event.getId() + " for 5 seconds before trigger."), false);
		return 1;
	}

	private static int activateRecipeChaos(CommandSourceStack source) throws CommandSyntaxException {
		return activateRecipeShuffle(source);
	}

	private static int activateRecipeShuffle(CommandSourceStack source) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (!config.enableRecipeShuffleEvents || !config.enableGlobalRecipeShuffle) {
			source.sendFailure(Component.literal("Global recipe shuffle is disabled in the RSE config."));
			return 0;
		}

		ServerLevel world = source.getLevel();
		if (!RecipeShuffleManager.startShuffle(world, config.recipeShuffleDurationTicks)) {
			source.sendFailure(Component.literal("Global recipe shuffle could not start."));
			return 0;
		}

		source.sendSuccess(() -> Component.literal("Global recipe shuffle enabled for " + ticksToSeconds(config.recipeShuffleDurationTicks) + " seconds."), false);
		RandomSurvivalEvents.LOGGER.info("[Random Survival Events] Debug global recipe shuffle enabled from command.");
		return 1;
	}

	private static int activateRseEffect(CommandSourceStack source, String effectId) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("RSE effect cannot be applied while the player is dead."));
			return 0;
		}

		return ModMobEffects.getDisplayEffect(effectId).map(effect -> {
			ModMobEffects.refreshEffect(player, effect, 60 * 20);
			applyLinkedEffectBehavior(player, effectId, 60 * 20);
			source.sendSuccess(() -> Component.literal("Applied RSE effect " + effectId + " for 60 seconds."), false);
			RandomSurvivalEvents.LOGGER.info(
					"[Random Survival Events] Debug RSE effect {} applied to player {}",
					effectId,
					player.getGameProfile().getName()
			);
			return 1;
		}).orElseGet(() -> {
			source.sendFailure(Component.literal("Unknown RSE effect id: " + effectId));
			return 0;
		});
	}

	private static int showStatus(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		long gameTime = player.serverLevel().getGameTime();

		source.sendSuccess(() -> Component.literal("RSE status for " + player.getGameProfile().getName() + ":"), false);
		sendRseEffectStates(source, player);
		sendGlobalRecipeShuffleState(source);
		sendRecipeChaosState(source, player);
		sendBlockChaosState(source, player);
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		source.sendSuccess(() -> Component.literal("- Destructive Mode: " + (config.destructiveMode ? "enabled" : "disabled")), false);
		sendState(source, "Max Health Boost", component.getMaxHealthBoostUntilTick(), gameTime);
		sendState(source, "Max Health Reduce", component.getMaxHealthReduceUntilTick(), gameTime);
		sendState(source, "Glass Cannon", component.getGlassCannonUntilTick(), gameTime);
		sendState(source, "Health Debt", component.getHealthDebtUntilTick(), gameTime);
		return 1;
	}

	private static boolean executeDebugEvent(CommandSourceStack source, ServerPlayer player, RandomEvent event) {
		ServerLevel world = player.serverLevel();
		if (!RandomEventManager.executeEvent(player, world, event)) {
			source.sendFailure(Component.literal("RSE event cannot run for the current player state: " + event.getId()));
			return false;
		}
		source.sendSuccess(() -> Component.literal("Triggered RSE event: " + event.getId()), false);
		RandomSurvivalEvents.LOGGER.info(
				"[Random Survival Events] Debug triggered event {} for player {}",
				event.getId(),
				player.getGameProfile().getName()
		);
		return true;
	}

	private static void sendState(CommandSourceStack source, String label, long untilTick, long gameTime) {
		long remainingTicks = Math.max(0L, untilTick - gameTime);
		String state = remainingTicks > 0L
				? "ACTIVE, " + ticksToSeconds(remainingTicks) + "s remaining"
				: "inactive";
		source.sendSuccess(() -> Component.literal("- " + label + ": " + state), false);
	}

	private static void sendRseEffectStates(CommandSourceStack source, ServerPlayer player) {
		List<ActiveRseEffect> effects = new ArrayList<>();
		for (MobEffectInstance effect : player.getActiveEffects()) {
			ModMobEffects.getEffectPath(effect.getEffect())
					.ifPresent(effectId -> effects.add(new ActiveRseEffect(effectId, effect.getDuration())));
		}

		if (effects.isEmpty()) {
			source.sendSuccess(() -> Component.literal("- RSE Effects: none"), false);
			return;
		}

		effects.sort(Comparator.comparing(ActiveRseEffect::effectId));
		source.sendSuccess(() -> Component.literal("- RSE Effects:"), false);
		for (ActiveRseEffect effect : effects) {
			Component name = Component.translatable("effect." + RandomSurvivalEvents.MOD_ID + "." + effect.effectId());
			String suffix = " (" + effect.effectId() + "): " + ticksToSeconds(effect.durationTicks()) + "s remaining";
			source.sendSuccess(() -> Component.literal("  - ").append(name).append(Component.literal(suffix)), false);
		}
	}

	private static void sendGlobalRecipeShuffleState(CommandSourceStack source) {
		int remainingTicks = RecipeShuffleManager.getRemainingTicks();
		String state = remainingTicks > 0
				? "ACTIVE, " + ticksToSeconds(remainingTicks) + "s remaining"
				: "inactive";
		source.sendSuccess(() -> Component.literal("- Recipe Shuffle: " + state), false);
	}

	private static void sendRecipeChaosState(CommandSourceStack source, ServerPlayer player) {
		MobEffectInstance effect = player.getEffect(ModMobEffects.RECIPE_CHAOS);
		int remainingTicks = effect == null ? 0 : effect.getDuration();
		String state = remainingTicks > 0
				? "ACTIVE, " + ticksToSeconds(remainingTicks) + "s remaining"
				: "inactive";
		source.sendSuccess(() -> Component.literal("- Recipe Chaos Effect: " + state), false);
	}

	private static void sendBlockChaosState(CommandSourceStack source, ServerPlayer player) {
		MobEffectInstance effect = player.getEffect(ModMobEffects.BLOCK_CHAOS);
		int remainingTicks = effect == null ? 0 : effect.getDuration();
		String state = remainingTicks > 0
				? "ACTIVE, " + ticksToSeconds(remainingTicks) + "s remaining"
				: "inactive";
		source.sendSuccess(() -> Component.literal("- Block Chaos: " + state), false);
	}

	private static void applyLinkedEffectBehavior(ServerPlayer player, String effectId, int durationTicks) {
		String normalized = effectId.contains(":") ? effectId.substring(effectId.indexOf(':') + 1) : effectId;
		if (normalized.equals("weak_hands")) {
			player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN, durationTicks, 4));
			player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, durationTicks, 1));
		}
	}

	private static long ticksToSeconds(long ticks) {
		return (ticks + 19L) / 20L;
	}

	private record ActiveRseEffect(String effectId, int durationTicks) {
	}
}
