package com.ikunkk02afk.randomsurvivalevents.event.impl.special;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;

public class GlowingMobsEvent implements RandomEvent {
	@Override
	public String getId() {
		return "glowing_mobs";
	}

	@Override
	public String getName() {
		return "怪物显形";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.SPECIAL;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.UNCOMMON;
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return getDefaultEventDurationTicks();
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		AABB box = player.getBoundingBox().inflate(16.0D);
		int durationTicks = getDefaultEventDurationTicks();
		int affected = 0;
		for (Entity entity : world.getEntities(player, box, entity -> entity instanceof LivingEntity && entity instanceof Enemy)) {
			((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.GLOWING, durationTicks, 0));
			affected++;
		}

		RandomEventUtils.sendMessage(player, affected > 0 ? "附近的怪物轮廓突然清晰了起来。" : "附近没有怪物可以显形。");
	}
}
