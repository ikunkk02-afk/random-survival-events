package com.ikunkk02afk.randomsurvivalevents.event.impl.block;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RandomChestEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final Item[] LOOT = {
			Items.APPLE,
			Items.BREAD,
			Items.TORCH,
			Items.OAK_LOG,
			Items.IRON_NUGGET,
			Items.EXPERIENCE_BOTTLE
	};

	@Override
	public String getId() {
		return "random_chest";
	}

	@Override
	public String getName() {
		return "随机宝箱";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.BLOCK;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		Optional<BlockPos> chestPos = RandomEventUtils.findNearbySpawnPos(world, player, 4, 8);
		if (chestPos.isEmpty() || !world.getBlockState(chestPos.get()).isAir()) {
			RandomEventUtils.sendMessage(player, "一个箱子差点出现，但没有安全位置。");
			return;
		}

		world.setBlockAndUpdate(chestPos.get(), Blocks.CHEST.defaultBlockState());
		BlockEntity blockEntity = world.getBlockEntity(chestPos.get());
		if (blockEntity instanceof Container container) {
			fillChest(container);
		}
		RandomEventUtils.sendMessage(player, "附近出现了一个来路不明的小箱子。");
	}

	private void fillChest(Container container) {
		int stacks = RandomEventUtils.randomBetween(3, 5);
		for (int i = 0; i < stacks; i++) {
			Item item = LOOT[RANDOM.nextInt(LOOT.length)];
			int slot = RANDOM.nextInt(container.getContainerSize());
			container.setItem(slot, new ItemStack(item, getCount(item)));
		}
	}

	private int getCount(Item item) {
		if (item == Items.TORCH) {
			return RandomEventUtils.randomBetween(3, 8);
		}
		if (item == Items.OAK_LOG) {
			return RandomEventUtils.randomBetween(2, 4);
		}
		if (item == Items.IRON_NUGGET) {
			return RandomEventUtils.randomBetween(2, 6);
		}
		return item == Items.EXPERIENCE_BOTTLE ? 1 : RandomEventUtils.randomBetween(1, 3);
	}
}
