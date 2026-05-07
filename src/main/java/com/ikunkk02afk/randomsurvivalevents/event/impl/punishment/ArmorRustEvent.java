package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorRustEvent implements RandomEvent {
	private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
			EquipmentSlot.HEAD,
			EquipmentSlot.CHEST,
			EquipmentSlot.LEGS,
			EquipmentSlot.FEET
	);

	@Override
	public String getId() {
		return "armor_rust";
	}

	@Override
	public String getName() {
		return "护甲生锈";
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
		if (player == null || world == null || !player.isAlive() || player.isCreative()) {
			return;
		}

		if (!DestructiveModeRules.canPermanentlyDamageArmor(config)) {
			applyRustFatigue(player);
			RandomEventUtils.sendMessage(player, "你的护甲开始发出刺耳的摩擦声。");
			return;
		}

		boolean damaged = false;
		for (EquipmentSlot slot : ARMOR_SLOTS) {
			ItemStack stack = player.getItemBySlot(slot);
			if (stack.isEmpty() || !stack.isDamageableItem()) {
				continue;
			}

			int damage = Math.max(1, stack.getMaxDamage() / RandomEventUtils.randomBetween(8, 14));
			stack.setDamageValue(Math.min(stack.getMaxDamage() - 1, stack.getDamageValue() + damage));
			damaged = true;
		}

		if (!damaged) {
			applyRustFatigue(player);
		}
		RandomEventUtils.sendMessage(player, "你的护甲开始发出刺耳的摩擦声。");
	}

	private void applyRustFatigue(ServerPlayer player) {
		int durationTicks = getDefaultEventDurationTicks();
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, 0));
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, 0));
	}
}
