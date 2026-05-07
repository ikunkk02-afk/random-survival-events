package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AirDropEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final Item[] SUPPLIES = {
			Items.BREAD,
			Items.APPLE,
			Items.OAK_LOG,
			Items.IRON_NUGGET,
			Items.TORCH
	};

	@Override
	public String getId() {
		return "air_drop";
	}

	@Override
	public String getName() {
		return "补给空投";
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

		Optional<BlockPos> dropPos = RandomEventUtils.findNearbySpawnPos(world, player, 2, 5);
		if (dropPos.isEmpty()) {
			RandomEventUtils.sendMessage(player, "补给似乎迷路了，没有找到安全投放点。");
			return;
		}

		RandomEventUtils.sendMessage(player, "一份小型补给落在了附近。");
		int stacks = RandomEventUtils.randomBetween(2, 3);
		for (int i = 0; i < stacks; i++) {
			Item item = SUPPLIES[RANDOM.nextInt(SUPPLIES.length)];
			int count = getSupplyCount(item);
			RandomEventUtils.dropItem(world, dropPos.get(), new ItemStack(item, count));
		}
	}

	private int getSupplyCount(Item item) {
		if (item == Items.OAK_LOG) {
			return RandomEventUtils.randomBetween(2, 4);
		}
		if (item == Items.IRON_NUGGET) {
			return RandomEventUtils.randomBetween(2, 6);
		}
		if (item == Items.TORCH) {
			return RandomEventUtils.randomBetween(3, 8);
		}
		return RandomEventUtils.randomBetween(1, 3);
	}
}
