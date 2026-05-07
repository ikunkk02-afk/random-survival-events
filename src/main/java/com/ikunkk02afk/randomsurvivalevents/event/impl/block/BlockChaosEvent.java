package com.ikunkk02afk.randomsurvivalevents.event.impl.block;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public class BlockChaosEvent implements RandomEvent {
	@Override
	public String getId() {
		return "block_chaos";
	}

	@Override
	public String getName() {
		return "方块异变";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.BLOCK;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive() || !config.enableBlockChaosEffect) {
			return;
		}

		player.addEffect(new MobEffectInstance(
				ModMobEffects.BLOCK_CHAOS,
				config.blockChaosDurationTicks,
				0,
				false,
				true,
				true
		));
		RandomEventUtils.sendMessage(player, "附近的方块开始变得不正常了。");
		RandomSurvivalEvents.LOGGER.info("[Random Survival Events] Block Chaos triggered for player {}", player.getGameProfile().getName());
	}
}
