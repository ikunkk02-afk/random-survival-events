package com.ikunkk02afk.randomsurvivalevents.event.impl;

import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class WeatherShiftEvent implements RandomEvent {
	private static final Random RANDOM = new Random();

	@Override
	public String getId() {
		return "weather_shift";
	}

	@Override
	public String getName() {
		return "天气突变";
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

		if (world.dimension() != Level.OVERWORLD) {
			RandomEventUtils.sendMessage(player, "这里的天空没有回应天气突变。");
			return;
		}

		boolean thunder = RANDOM.nextBoolean();
		world.setWeatherParameters(0, RandomEventUtils.randomBetween(20 * 60, 20 * 180), true, thunder);
		RandomEventUtils.sendMessage(player, thunder ? "天气突然变成了雷暴。" : "天气突然下起了雨。");
	}
}
