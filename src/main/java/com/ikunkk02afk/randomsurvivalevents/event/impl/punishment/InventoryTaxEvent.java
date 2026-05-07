package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class InventoryTaxEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "inventory_tax";
	}

	@Override
	public String getName() {
		return "背包献祭";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.EPIC;
	}

	@Override
	public ResourceLocation getStatusEffectId() {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "inventory_panic");
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		if (!DestructiveModeRules.canPermanentlyPunishInventory(config)) {
			if (config.allowInventoryShuffle) {
				shuffleInventory(player, config);
				RandomEventUtils.sendMessage(player, "毁灭模式未开启，背包献祭改为背包混乱。");
			} else {
				RandomEventUtils.sendMessage(player, "背包献祭被配置阻止。");
			}
			return;
		}

		Inventory inventory = player.getInventory();
		List<Integer> candidates = new ArrayList<>();
		for (int slot = 0; slot <= 35; slot++) {
			if (canSacrifice(inventory.getItem(slot))) {
				candidates.add(slot);
			}
		}

		if (candidates.isEmpty()) {
			RandomEventUtils.sendMessage(player, "背包献祭没有找到可献祭的普通物品。");
			return;
		}

		Collections.shuffle(candidates, RANDOM);
		int count = Math.min(RandomEventUtils.randomBetween(1, 3), candidates.size());
		for (int i = 0; i < count; i++) {
			inventory.setItem(candidates.get(i), ItemStack.EMPTY);
		}
		inventory.setChanged();
		RandomEventUtils.sendMessage(player, "你的背包献祭了几组普通物品。");
	}

	private void shuffleInventory(ServerPlayer player, RandomSurvivalEventsConfig config) {
		Inventory inventory = player.getInventory();
		int firstSlot = config.inventoryShuffleAffectsHotbar ? 0 : 9;
		List<Integer> slots = new ArrayList<>();
		List<ItemStack> stacks = new ArrayList<>();
		for (int slot = firstSlot; slot <= 35; slot++) {
			slots.add(slot);
			stacks.add(inventory.getItem(slot).copy());
		}
		Collections.shuffle(stacks, RANDOM);
		for (int i = 0; i < slots.size(); i++) {
			inventory.setItem(slots.get(i), stacks.get(i));
		}
		inventory.setChanged();
	}

	private boolean canSacrifice(ItemStack stack) {
		if (stack == null || stack.isEmpty() || stack.isDamageableItem() || stack.has(DataComponents.CUSTOM_NAME)) {
			return false;
		}
		return !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}
}
