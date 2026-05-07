package com.ikunkk02afk.randomsurvivalevents.event.state;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import net.minecraft.resources.ResourceLocation;

public final class AttributeEventIds {
	public static final ResourceLocation MAX_HEALTH_BOOST = id("max_health_boost");
	public static final ResourceLocation MAX_HEALTH_REDUCE = id("max_health_reduce");
	public static final ResourceLocation GLASS_CANNON = id("glass_cannon");

	private AttributeEventIds() {
	}

	private static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, path);
	}
}
