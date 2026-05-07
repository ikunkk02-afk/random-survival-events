package com.ikunkk02afk.randomsurvivalevents.effect;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public final class ModMobEffects {
	public static final Holder.Reference<MobEffect> BLOCK_CHAOS = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "block_chaos"),
			new BlockChaosMobEffect()
	);
	public static final Holder.Reference<MobEffect> RECIPE_CHAOS = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "recipe_chaos"),
			new RecipeChaosMobEffect()
	);

	private ModMobEffects() {
	}

	public static void register() {
		RandomSurvivalEvents.LOGGER.info("Registered Random Survival Events mob effects.");
	}
}
