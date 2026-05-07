package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class MiniLightningEvent implements RandomEvent {
	@Override
	public String getId() {
		return "mini_lightning";
	}

	@Override
	public String getName() {
		return "小型雷击";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.WEATHER;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		if (!world.canSeeSkyFromBelowWater(player.blockPosition())) {
			RandomEventUtils.playSound(world, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
			RandomEventUtils.sendMessage(player, "远处传来一声闷雷，但这里挡住了雷击。");
			return;
		}

		Optional<BlockPos> strikePos = RandomEventUtils.findNearbySpawnPos(world, player, 6, 12);
		if (strikePos.isEmpty() || !world.canSeeSkyFromBelowWater(strikePos.get())) {
			RandomEventUtils.playSound(world, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
			RandomEventUtils.sendMessage(player, "雷声在附近炸响，但没有找到合适落点。");
			return;
		}

		LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(world);
		if (lightningBolt != null) {
			BlockPos pos = strikePos.get();
			lightningBolt.setVisualOnly(true);
			lightningBolt.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			world.addFreshEntity(lightningBolt);
			RandomEventUtils.sendMessage(player, "一道小型雷击落在了附近。");
		}
	}
}
