package com.ikunkk02afk.randomsurvivalevents.command;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventManager;
import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeChaosState;
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

public final class RandomSurvivalEventsCommands {
	private static final long RECIPE_CHAOS_DEBUG_TICKS = 60L * 20L;

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
		ServerPlayer player = source.getPlayerOrException();
		if (!player.isAlive()) {
			source.sendFailure(Component.literal("Recipe chaos cannot start while the player is dead."));
			return 0;
		}

		ServerLevel world = player.serverLevel();
		RecipeChaosState.activate(player, world.getGameTime() + RECIPE_CHAOS_DEBUG_TICKS);
		source.sendSuccess(() -> Component.literal("Recipe chaos enabled for 60 seconds."), false);
		RandomSurvivalEvents.LOGGER.info(
				"[Random Survival Events] Debug recipe chaos enabled for player {}",
				player.getGameProfile().getName()
		);
		return 1;
	}

	private static int showStatus(CommandSourceStack source) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		long gameTime = player.serverLevel().getGameTime();

		source.sendSuccess(() -> Component.literal("RSE status for " + player.getGameProfile().getName() + ":"), false);
		sendState(source, "Recipe Chaos", component.getRecipeChaosUntilTick(), gameTime);
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

	private static long ticksToSeconds(long ticks) {
		return (ticks + 19L) / 20L;
	}
}
