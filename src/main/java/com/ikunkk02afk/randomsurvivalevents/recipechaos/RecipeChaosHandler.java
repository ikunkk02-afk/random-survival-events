package com.ikunkk02afk.randomsurvivalevents.recipechaos;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class RecipeChaosHandler {
	private static final Random RANDOM = new Random();

	private RecipeChaosHandler() {
	}

	public static void handleCraft(ServerPlayer player, ItemStack craftedStack) {
		if (player == null || craftedStack == null || craftedStack.isEmpty() || player.isCreative() || !RecipeChaosState.isActive(player)) {
			return;
		}

		ItemStack reward = getReward(craftedStack);
		if (reward.isEmpty()) {
			return;
		}

		if (!player.getInventory().add(reward.copy())) {
			RandomEventUtils.dropItem(player.serverLevel(), player.blockPosition(), reward.copy());
		}
		RandomEventUtils.sendMessage(player, "配方发生了奇怪的偏移。");
	}

	private static ItemStack getReward(ItemStack craftedStack) {
		if (craftedStack.is(Items.STICK) && RANDOM.nextDouble() < 0.30D) {
			return new ItemStack(Items.COAL, 1);
		}
		if (craftedStack.is(Items.TORCH) && RANDOM.nextDouble() < 0.25D) {
			return new ItemStack(Items.TORCH, 2);
		}
		if (craftedStack.is(Items.BREAD) && RANDOM.nextDouble() < 0.20D) {
			return new ItemStack(Items.APPLE, 1);
		}
		if (craftedStack.is(Items.CRAFTING_TABLE) && RANDOM.nextDouble() < 0.15D) {
			return new ItemStack(Items.CHEST, 1);
		}
		return ItemStack.EMPTY;
	}
}
