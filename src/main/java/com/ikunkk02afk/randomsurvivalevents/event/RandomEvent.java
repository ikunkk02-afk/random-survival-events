package com.ikunkk02afk.randomsurvivalevents.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface RandomEvent {
	String getId();

	String getName();

	RandomEventCategory getCategory();

	void execute(ServerPlayer player, ServerLevel world);
}
