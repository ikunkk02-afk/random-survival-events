package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class BarrageJudgementEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final String[] COMMENTS = {
			"弹幕：这波操作有点抽象。",
			"弹幕：主播你确定这是生存？",
			"弹幕：不是哥们，这也能触发？"
	};

	@Override
	public String getId() {
		return "barrage_judgement";
	}

	@Override
	public String getName() {
		return "弹幕审判";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.SPECIAL;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		RandomEventUtils.sendMessage(player, COMMENTS[RANDOM.nextInt(COMMENTS.length)]);
	}
}
