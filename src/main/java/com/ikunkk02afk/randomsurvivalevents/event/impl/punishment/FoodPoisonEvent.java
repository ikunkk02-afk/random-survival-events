package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FoodPoisonEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "food_poison";
	}

	@Override
	public String getName() {
		return "食物腐坏";
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
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return getDefaultEventDurationTicks();
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		if (!DestructiveModeRules.canPermanentlyCorruptFood(config)) {
			applySpoiledFoodEffects(player);
			RandomEventUtils.sendMessage(player, "你的食物散发出奇怪的味道。");
			return;
		}

		Inventory inventory = player.getInventory();
		List<Integer> candidates = new ArrayList<>();
		for (int slot = 0; slot <= 35; slot++) {
			if (canCorrupt(inventory.getItem(slot))) {
				candidates.add(slot);
			}
		}

		if (candidates.isEmpty()) {
			applySpoiledFoodEffects(player);
			RandomEventUtils.sendMessage(player, "你的食物散发出奇怪的味道。");
			return;
		}

		Collections.shuffle(candidates, RANDOM);
		int count = Math.min(RandomEventUtils.randomBetween(1, 3), candidates.size());
		for (int i = 0; i < count; i++) {
			int slot = candidates.get(i);
			ItemStack original = inventory.getItem(slot);
			ItemStack corrupted = new ItemStack(RANDOM.nextBoolean() ? Items.ROTTEN_FLESH : Items.SPIDER_EYE, original.getCount());
			inventory.setItem(slot, corrupted);
		}
		inventory.setChanged();
		RandomEventUtils.sendMessage(player, "你的食物散发出奇怪的味道。");
	}

	private void applySpoiledFoodEffects(ServerPlayer player) {
		int durationTicks = getDefaultEventDurationTicks();
		player.addEffect(new MobEffectInstance(MobEffects.HUNGER, durationTicks, 1));
		player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, durationTicks, 0));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, 0));
	}

	private boolean canCorrupt(ItemStack stack) {
		return stack != null
				&& !stack.isEmpty()
				&& stack.has(DataComponents.FOOD)
				&& !stack.has(DataComponents.CUSTOM_NAME)
				&& !stack.is(Items.ROTTEN_FLESH)
				&& !stack.is(Items.SPIDER_EYE);
	}
}
