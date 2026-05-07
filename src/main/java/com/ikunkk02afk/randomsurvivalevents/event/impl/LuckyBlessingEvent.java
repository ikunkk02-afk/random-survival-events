package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LuckyBlessingEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final Item[] REWARDS = {
			Items.GOLD_NUGGET,
			Items.EXPERIENCE_BOTTLE,
			Items.APPLE,
			Items.IRON_NUGGET
	};

	@Override
	public String getId() {
		return "lucky_blessing";
	}

	@Override
	public String getName() {
		return "欧皇附体";
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

		player.addEffect(new MobEffectInstance(MobEffects.LUCK, 20 * 20, 0));
		Item reward = REWARDS[RANDOM.nextInt(REWARDS.length)];
		RandomEventUtils.dropItem(world, player.blockPosition(), new ItemStack(reward, getRewardCount(reward)));
		RandomEventUtils.sendMessage(player, "你感觉今天的运气有点离谱。");
	}

	private int getRewardCount(Item reward) {
		if (reward == Items.EXPERIENCE_BOTTLE) {
			return 1;
		}
		return RandomEventUtils.randomBetween(1, 3);
	}
}
