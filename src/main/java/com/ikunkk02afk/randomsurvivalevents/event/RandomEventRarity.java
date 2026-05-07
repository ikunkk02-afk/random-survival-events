package com.ikunkk02afk.randomsurvivalevents.event;

import java.util.Arrays;
import java.util.Optional;

public enum RandomEventRarity {
	COMMON("common", "普通", "你感觉周围有些轻微变化……"),
	UNCOMMON("uncommon", "少见", "附近的空气开始变得不对劲……"),
	RARE("rare", "稀有", "一个异常事件正在靠近……"),
	EPIC("epic", "史诗", "危险的气息正在聚集……"),
	DISASTER("disaster", "灾难", "灾难正在降临。");

	private final String commandName;
	private final String displayName;
	private final String previewMessage;

	RandomEventRarity(String commandName, String displayName, String previewMessage) {
		this.commandName = commandName;
		this.displayName = displayName;
		this.previewMessage = previewMessage;
	}

	public String getCommandName() {
		return commandName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPreviewMessage() {
		return previewMessage;
	}

	public static Optional<RandomEventRarity> fromCommandName(String commandName) {
		if (commandName == null) {
			return Optional.empty();
		}
		return Arrays.stream(values())
				.filter(rarity -> rarity.commandName.equalsIgnoreCase(commandName))
				.findFirst();
	}
}
