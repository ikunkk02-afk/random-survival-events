package com.ikunkk02afk.randomsurvivalevents.command;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventManager;
import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeShuffleManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
						.then(Commands.literal("recipechaos")
								.executes(context -> activateRecipeChaos(context.getSource())))
						.then(Commands.literal("recipeshuffle")
								.executes(context -> activateRecipeShuffle(context.getSource())))
						.then(Commands.literal("effect")
								.then(Commands.literal("block_chaos")
										.executes(context -> activateBlockChaosEffect(context.getSource()))))
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

		RandomEvent event = RandomEventManager.getEvents().stream()
				.filter(candidate -> candidate.getId().equals(eventId))
				.findFirst()
				.orElse(null);
		if (event == null) {
			source.sendFailure(Component.literal("Unknown RSE event id: " + eventId));
			return 0;
		}

		executeDebugEvent(source, player, event);
		return 1;
	}

	private static int triggerRandomEvent(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("RSE debug event cannot run while the player is dead."));
			return 0;
		}

		List<RandomEvent> events = RandomEventManager.getEvents();
		if (events.isEmpty()) {
			source.sendFailure(Component.literal("No RSE events are registered."));
			return 0;
		}

		RandomEvent event = events.get(ThreadLocalRandom.current().nextInt(events.size()));
		executeDebugEvent(source, player, event);
		return 1;
	}

	private static int activateRecipeChaos(CommandSourceStack source) throws CommandSyntaxException {
		return activateRecipeShuffle(source);
	}

	private static int activateRecipeShuffle(CommandSourceStack source) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (!config.enableGlobalRecipeShuffle) {
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

	private static int activateBlockChaosEffect(CommandSourceStack source) throws CommandSyntaxException {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (!config.enableBlockChaosEffect) {
			source.sendFailure(Component.literal("Block Chaos is disabled in the RSE config."));
			return 0;
		}

		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("Block Chaos cannot be applied while the player is dead."));
			return 0;
		}

		player.addEffect(new MobEffectInstance(
				ModMobEffects.BLOCK_CHAOS,
				config.blockChaosDurationTicks,
				0,
				false,
				true,
				true
		));
		source.sendSuccess(() -> Component.literal("Applied Block Chaos for " + ticksToSeconds(config.blockChaosDurationTicks) + " seconds."), false);
		RandomSurvivalEvents.LOGGER.info("[Random Survival Events] Debug Block Chaos effect applied to player {}", player.getGameProfile().getName());
		return 1;
	}

	private static int showStatus(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		long gameTime = player.serverLevel().getGameTime();

		source.sendSuccess(() -> Component.literal("RSE status for " + player.getGameProfile().getName() + ":"), false);
		sendGlobalRecipeShuffleState(source);
		sendRecipeChaosState(source, player);
		sendBlockChaosState(source, player);
		sendState(source, "Max Health Boost", component.getMaxHealthBoostUntilTick(), gameTime);
		sendState(source, "Max Health Reduce", component.getMaxHealthReduceUntilTick(), gameTime);
		sendState(source, "Glass Cannon", component.getGlassCannonUntilTick(), gameTime);
		return 1;
	}

	private static void executeDebugEvent(CommandSourceStack source, ServerPlayer player, RandomEvent event) {
		ServerLevel world = player.serverLevel();
		event.execute(player, world);
		source.sendSuccess(() -> Component.literal("Triggered RSE event: " + event.getId()), false);
		RandomSurvivalEvents.LOGGER.info(
				"[Random Survival Events] Debug triggered event {} for player {}",
				event.getId(),
				player.getGameProfile().getName()
		);
	}

	private static void sendState(CommandSourceStack source, String label, long untilTick, long gameTime) {
		long remainingTicks = Math.max(0L, untilTick - gameTime);
		String state = remainingTicks > 0L
				? "ACTIVE, " + ticksToSeconds(remainingTicks) + "s remaining"
				: "inactive";
		source.sendSuccess(() -> Component.literal("- " + label + ": " + state), false);
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

	private static long ticksToSeconds(long ticks) {
		return (ticks + 19L) / 20L;
	}
}
