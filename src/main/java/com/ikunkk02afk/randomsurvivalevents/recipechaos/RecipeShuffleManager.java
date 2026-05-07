package com.ikunkk02afk.randomsurvivalevents.recipechaos;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeShuffleManager {
	private static final Random RANDOM = new Random();
	private static final Map<ResourceLocation, ItemStack> SHUFFLED_RESULTS = new HashMap<>();
	private static final Set<Item> ALWAYS_EXCLUDED_RESULTS = new HashSet<>();
	private static final Set<Item> OVERPOWERED_RESULTS = new HashSet<>();
	private static MinecraftServer activeServer;
	private static int remainingTicks;

	static {
		Collections.addAll(
				ALWAYS_EXCLUDED_RESULTS,
				Items.AIR,
				Items.COMMAND_BLOCK,
				Items.CHAIN_COMMAND_BLOCK,
				Items.REPEATING_COMMAND_BLOCK,
				Items.COMMAND_BLOCK_MINECART,
				Items.BARRIER,
				Items.LIGHT,
				Items.STRUCTURE_BLOCK,
				Items.STRUCTURE_VOID,
				Items.JIGSAW,
				Items.DEBUG_STICK,
				Items.KNOWLEDGE_BOOK,
				Items.SPAWNER,
				Items.TRIAL_SPAWNER
		);
		Collections.addAll(
				OVERPOWERED_RESULTS,
				Items.BEDROCK,
				Items.DRAGON_EGG,
				Items.NETHERITE_BLOCK
		);
	}

	private RecipeShuffleManager() {
	}

	public static boolean startShuffle(ServerLevel world, int durationTicks) {
		if (world == null || world.getServer() == null || durationTicks <= 0 || !RandomSurvivalEventsConfig.get().enableGlobalRecipeShuffle) {
			return false;
		}

		List<RecipeHolder<CraftingRecipe>> recipes = world.getServer().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
		List<RecipeOutput> recipeOutputs = new ArrayList<>();
		List<ItemStack> shuffledPool = new ArrayList<>();

		for (RecipeHolder<CraftingRecipe> recipe : recipes) {
			ItemStack result = recipe.value().getResultItem(world.registryAccess()).copy();
			if (!isLegalShuffleResult(result) || !result.isItemEnabled(world.enabledFeatures())) {
				continue;
			}
			recipeOutputs.add(new RecipeOutput(recipe.id(), result.copy()));
			shuffledPool.add(result.copy());
		}

		if (recipeOutputs.isEmpty() || shuffledPool.isEmpty()) {
			RandomSurvivalEvents.LOGGER.warn("[Random Survival Events] Recipe shuffle could not start because no legal crafting outputs were found.");
			return false;
		}

		Collections.shuffle(shuffledPool, RANDOM);
		SHUFFLED_RESULTS.clear();
		for (int index = 0; index < recipeOutputs.size(); index++) {
			RecipeOutput original = recipeOutputs.get(index);
			ItemStack shuffled = shuffledPool.get(index).copy();
			shuffled.setCount(getSafeOutputCount(original.originalResult(), shuffled));
			if (isLegalShuffleResult(shuffled)) {
				SHUFFLED_RESULTS.put(original.recipeId(), shuffled);
			}
		}

		if (SHUFFLED_RESULTS.isEmpty()) {
			remainingTicks = 0;
			return false;
		}

		remainingTicks = durationTicks;
		activeServer = world.getServer();
		syncRecipeChaosEffects(activeServer);
		broadcast(world.getServer(), "世界配方发生了混乱，50秒后恢复。");
		refreshOpenCraftingMenus(world.getServer());
		RandomSurvivalEvents.LOGGER.info(
				"[Random Survival Events] Started global recipe shuffle for {} ticks with {} mapped crafting recipes.",
				durationTicks,
				SHUFFLED_RESULTS.size()
		);
		return true;
	}

	public static void stopShuffle() {
		removeRecipeChaosEffects(activeServer);
		SHUFFLED_RESULTS.clear();
		remainingTicks = 0;
		activeServer = null;
	}

	public static boolean isShuffleActive() {
		return remainingTicks > 0 && !SHUFFLED_RESULTS.isEmpty();
	}

	public static int getRemainingTicks() {
		return isShuffleActive() ? remainingTicks : 0;
	}

	public static ItemStack getShuffledResult(ResourceLocation recipeId, ItemStack originalResult) {
		if (!isShuffleActive() || recipeId == null || originalResult == null || originalResult.isEmpty()) {
			return copyOrEmpty(originalResult);
		}

		ItemStack shuffled = SHUFFLED_RESULTS.get(recipeId);
		if (!isLegalShuffleResult(shuffled)) {
			return originalResult.copy();
		}

		ItemStack result = shuffled.copy();
		result.setCount(getSafeOutputCount(originalResult, result));
		if (!isLegalShuffleResult(result)) {
			return originalResult.copy();
		}
		return result;
	}

	public static ItemStack getShuffledResult(ServerPlayer player, Container resultContainer, ItemStack originalResult) {
		if (player == null || player.isCreative() || !isShuffleActive()) {
			return copyOrEmpty(originalResult);
		}

		Optional<ResourceLocation> recipeId = getRecipeId(resultContainer);
		return recipeId.map(id -> getShuffledResult(id, originalResult)).orElseGet(() -> copyOrEmpty(originalResult));
	}

	public static Optional<ResourceLocation> getRecipeId(Container resultContainer) {
		if (!(resultContainer instanceof RecipeCraftingHolder recipeHolder)) {
			return Optional.empty();
		}
		RecipeHolder<?> usedRecipe = recipeHolder.getRecipeUsed();
		if (usedRecipe == null) {
			return Optional.empty();
		}
		return Optional.of(usedRecipe.id());
	}

	public static void onResultTaken(ServerPlayer player, Container resultContainer, ItemStack takenStack) {
		if (player == null || player.isCreative() || !isShuffleActive()) {
			return;
		}

		Optional<ResourceLocation> recipeId = getRecipeId(resultContainer);
		if (recipeId.isEmpty() || takenStack == null || takenStack.isEmpty()) {
			return;
		}

		ItemStack expectedResult = getShuffledResult(recipeId.get(), takenStack);
		if (!ItemStack.isSameItemSameComponents(takenStack, expectedResult) || takenStack.getCount() != expectedResult.getCount()) {
			RandomSurvivalEvents.LOGGER.debug(
					"[Random Survival Events] Took shuffled recipe {} as {}, expected {}.",
					recipeId.get(),
					takenStack,
					expectedResult
			);
		}
	}

	public static void tick(MinecraftServer server) {
		if (!isShuffleActive()) {
			if (activeServer != null) {
				stopShuffle();
			}
			return;
		}

		activeServer = server;
		syncRecipeChaosEffects(server);
		remainingTicks--;
		if (remainingTicks > 0) {
			return;
		}

		stopShuffle();
		broadcast(server, "世界配方恢复正常了。");
		refreshOpenCraftingMenus(server);
		RandomSurvivalEvents.LOGGER.info("[Random Survival Events] Global recipe shuffle expired and recipes were restored.");
	}

	private static void syncRecipeChaosEffects(MinecraftServer server) {
		if (server == null) {
			return;
		}

		int duration = getRemainingTicks();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (!shouldHaveRecipeChaosEffect(player)) {
				player.removeEffect(ModMobEffects.RECIPE_CHAOS);
				continue;
			}

			MobEffectInstance currentEffect = player.getEffect(ModMobEffects.RECIPE_CHAOS);
			if (currentEffect != null && Math.abs(currentEffect.getDuration() - duration) <= 1) {
				continue;
			}

			player.removeEffect(ModMobEffects.RECIPE_CHAOS);
			player.addEffect(new MobEffectInstance(
					ModMobEffects.RECIPE_CHAOS,
					duration,
					0,
					false,
					true,
					true
			));
		}
	}

	private static void removeRecipeChaosEffects(MinecraftServer server) {
		if (server == null) {
			return;
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.removeEffect(ModMobEffects.RECIPE_CHAOS);
		}
	}

	private static boolean shouldHaveRecipeChaosEffect(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}

	private static boolean isLegalShuffleResult(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}

		Item item = stack.getItem();
		if (ALWAYS_EXCLUDED_RESULTS.contains(item)) {
			return false;
		}
		return !RandomSurvivalEventsConfig.get().excludeOverpoweredRecipeResults || !OVERPOWERED_RESULTS.contains(item);
	}

	private static int getSafeOutputCount(ItemStack originalResult, ItemStack shuffledResult) {
		int originalCount = originalResult == null || originalResult.isEmpty() ? 1 : originalResult.getCount();
		int maxCount = Math.max(1, shuffledResult.getMaxStackSize());
		return Math.max(1, Math.min(originalCount, maxCount));
	}

	private static ItemStack copyOrEmpty(ItemStack stack) {
		return stack == null ? ItemStack.EMPTY : stack.copy();
	}

	private static void broadcast(MinecraftServer server, String message) {
		if (server == null) {
			return;
		}
		Component component = Component.literal(message);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.displayClientMessage(component, false);
		}
	}

	private static void refreshOpenCraftingMenus(MinecraftServer server) {
		if (server == null) {
			return;
		}

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			AbstractContainerMenu menu = player.containerMenu;
			if (menu instanceof CraftingMenu || menu instanceof InventoryMenu) {
				Inventory inventory = player.getInventory();
				menu.slotsChanged(inventory);
				menu.broadcastChanges();
			}
		}
	}

	private record RecipeOutput(ResourceLocation recipeId, ItemStack originalResult) {
	}
}
