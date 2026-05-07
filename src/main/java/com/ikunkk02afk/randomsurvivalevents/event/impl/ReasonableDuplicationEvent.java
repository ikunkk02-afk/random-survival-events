package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ReasonableDuplicationEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "reasonable_duplication";
	}

	@Override
	public String getName() {
		return "这合理吗";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PLAYER;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		List<ItemStack> candidates = new ArrayList<>();
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (canDuplicate(stack)) {
				candidates.add(stack);
			}
		}

		if (candidates.isEmpty()) {
			RandomEventUtils.sendMessage(player, "这合理吗？但背包里没有适合复制的普通物品。");
			return;
		}

		ItemStack source = candidates.get(RANDOM.nextInt(candidates.size()));
		ItemStack copy = source.copyWithCount(1);
		boolean added = player.getInventory().add(copy);
		if (!added) {
			RandomEventUtils.dropItem(world, player.blockPosition(), copy);
		}
		RandomEventUtils.sendMessage(player, "这合理吗？你的一个普通物品多出来了 1 个。");
	}

	private boolean canDuplicate(ItemStack stack) {
		if (stack == null || stack.isEmpty() || stack.getMaxStackSize() <= 1 || stack.isEnchanted() || stack.isDamaged()) {
			return false;
		}

		Item item = stack.getItem();
		if (item == Items.DIAMOND || item == Items.DIAMOND_BLOCK || item == Items.DIAMOND_ORE || item == Items.DEEPSLATE_DIAMOND_ORE
				|| item == Items.NETHERITE_INGOT || item == Items.NETHERITE_SCRAP || item == Items.NETHERITE_BLOCK
				|| item == Items.ENDER_EYE || item == Items.ENCHANTED_GOLDEN_APPLE || item == Items.GOLDEN_APPLE
				|| item == Items.SHULKER_SHELL || item == Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
			return false;
		}

		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
			return false;
		}

		return true;
	}
}
