package com.ikunkk02afk.randomsurvivalevents.component;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public final class RandomSurvivalEventsComponents implements EntityComponentInitializer {
	public static final ComponentKey<PlayerEventComponent> PLAYER_EVENTS = ComponentRegistry.getOrCreate(
			ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "player_events"),
			PlayerEventComponent.class
	);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(PLAYER_EVENTS, player -> new PlayerEventComponent(), RespawnCopyStrategy.ALWAYS_COPY);
	}
}
