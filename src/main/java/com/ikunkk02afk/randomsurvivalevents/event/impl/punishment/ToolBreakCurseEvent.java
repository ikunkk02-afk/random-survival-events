package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Random;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

public class ToolBreakCurseEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "tool_break_curse";
	}

	@Override
	public String getName() {
		return "工具破碎诅咒";
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
	public ResourceLocation getStatusEffectId() {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "weak_hands");
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

		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !stack.isDamageableItem()) {
			RandomEventUtils.sendMessage(player, "破碎诅咒没有找到主手工具。");
			return;
		}
		if (isProtectedByConfig(stack, config)) {
			RandomEventUtils.sendMessage(player, "主手物品受配置保护，破碎诅咒被抵消。");
			return;
		}
		if (!config.destructiveMode) {
			int durationTicks = getDefaultEventDurationTicks();
			player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, durationTicks, 3));
			player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, durationTicks, 0));
			RandomEventUtils.sendMessage(player, "破碎诅咒擦过主手工具，毁灭模式关闭阻止了永久耐久损伤。");
			return;
		}

		int remaining = stack.getMaxDamage() - stack.getDamageValue();
		int damage = Math.max(1, stack.getMaxDamage() / RandomEventUtils.randomBetween(4, 7));
		if (damage >= remaining && RANDOM.nextDouble() < 0.45D) {
			player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			RandomEventUtils.sendMessage(player, "你的主手工具被破碎诅咒摧毁了。");
			return;
		}

		int newDamage = Math.min(stack.getMaxDamage() - 1, stack.getDamageValue() + damage);
		stack.setDamageValue(newDamage);
		RandomEventUtils.sendMessage(player, "你的主手工具被严重损坏了。");
	}

	private boolean isProtectedByConfig(ItemStack stack, RandomSurvivalEventsConfig config) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
		return id != null && config.toolBreakCurseProtectedItemIds.stream().anyMatch(value -> id.toString().equals(value));
	}
}
