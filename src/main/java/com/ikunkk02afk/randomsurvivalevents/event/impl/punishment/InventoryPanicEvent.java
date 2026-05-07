package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class InventoryPanicEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "inventory_panic";
	}

	@Override
	public String getName() {
		return "背包混乱";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.RARE;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive() || !config.allowInventoryShuffle) {
			return;
		}

		Inventory inventory = player.getInventory();
		int firstSlot = config.inventoryShuffleAffectsHotbar ? 0 : 9;
		List<Integer> slots = new ArrayList<>();
		List<ItemStack> stacks = new ArrayList<>();
		boolean hasItem = false;

		for (int slot = firstSlot; slot <= 35; slot++) {
			ItemStack stack = inventory.getItem(slot);
			slots.add(slot);
			stacks.add(stack.copy());
			hasItem = hasItem || !stack.isEmpty();
		}

		if (!hasItem || slots.size() < 2) {
			RandomEventUtils.sendMessage(player, "你的背包想乱，但里面没什么可乱的。");
			return;
		}

		Collections.shuffle(stacks, RANDOM);
		for (int i = 0; i < slots.size(); i++) {
			inventory.setItem(slots.get(i), stacks.get(i));
		}
		inventory.setChanged();
		RandomEventUtils.sendMessage(player, "你的背包突然乱成一团。");
	}
}
