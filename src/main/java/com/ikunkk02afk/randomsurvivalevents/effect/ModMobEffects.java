package com.ikunkk02afk.randomsurvivalevents.effect;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ModMobEffects {
	private static final Map<String, Holder.Reference<MobEffect>> RSE_EFFECTS = new LinkedHashMap<>();

	public static final Holder.Reference<MobEffect> AIR_DROP = registerDisplayOnly("air_drop", 0xD99A45);
	public static final Holder.Reference<MobEffect> SWEATY = registerDisplayOnly("sweaty", 0x4FB3E8);
	public static final Holder.Reference<MobEffect> LUCKY_BLESSING = registerDisplayOnly("lucky_blessing", 0xF4D35E);
	public static final Holder.Reference<MobEffect> MONSTER_SQUAD = registerDisplayOnly("monster_squad", 0x9F2D35);
	public static final Holder.Reference<MobEffect> WEATHER_SHIFT = registerDisplayOnly("weather_shift", 0x6A91C9);
	public static final Holder.Reference<MobEffect> REASONABLE_DUPLICATION = registerDisplayOnly("reasonable_duplication", 0xDA72E8);
	public static final Holder.Reference<MobEffect> BARRAGE_JUDGEMENT = registerDisplayOnly("barrage_judgement", 0xF2F2F2);
	public static final Holder.Reference<MobEffect> HEAVY_AIR = registerDisplayOnly("heavy_air", 0x68707C);
	public static final Holder.Reference<MobEffect> MINI_LIGHTNING = registerDisplayOnly("mini_lightning", 0xF8E66B);
	public static final Holder.Reference<MobEffect> MYSTERIOUS_TRADER = registerDisplayOnly("mysterious_trader", 0x47B56B);
	public static final Holder.Reference<MobEffect> LUCKY_ORE = registerDisplayOnly("lucky_ore", 0xD7B56D);
	public static final Holder.Reference<MobEffect> SLIME_FLOOR = registerDisplayOnly("slime_floor", 0x66CC55);
	public static final Holder.Reference<MobEffect> RANDOM_CHEST = registerDisplayOnly("random_chest", 0xA66A35);
	public static final Holder.Reference<MobEffect> MAX_HEALTH_BOOST = registerDisplayOnly("max_health_boost", 0xE85A5A);
	public static final Holder.Reference<MobEffect> MAX_HEALTH_REDUCE = registerDisplayOnly("max_health_reduce", 0x7E3A46);
	public static final Holder.Reference<MobEffect> GLASS_CANNON = registerDisplayOnly("glass_cannon", 0xE66D6D);
	public static final Holder.Reference<MobEffect> GRAVITY_CHAOS = registerDisplayOnly("gravity_chaos", 0x70A7D8);
	public static final Holder.Reference<MobEffect> BERSERK = registerDisplayOnly("berserk", 0xD13A2F);
	public static final Holder.Reference<MobEffect> GLOWING_MOBS = registerDisplayOnly("glowing_mobs", 0x9BEA66);
	public static final Holder.Reference<MobEffect> METEOR_RAIN = registerDisplayOnly("meteor_rain", 0xE1783F);
	public static final Holder.Reference<MobEffect> MONSTER_AMBUSH = registerDisplayOnly("monster_ambush", 0x8E2A2A);
	public static final Holder.Reference<MobEffect> FAKE_CHUNK_VOID = registerDisplayOnly("fake_chunk_void", 0x1E1B26);
	public static final Holder.Reference<MobEffect> LAVA_UNDERFOOT = registerDisplayOnly("lava_underfoot", 0xF36A22);
	public static final Holder.Reference<MobEffect> GRAVITY_CRUSH = registerDisplayOnly("gravity_crush", 0x56515F);
	public static final Holder.Reference<MobEffect> INVENTORY_PANIC = registerDisplayOnly("inventory_panic", 0xC06D2E);
	public static final Holder.Reference<MobEffect> BLIND_HUNT = registerDisplayOnly("blind_hunt", 0x2A2438);
	public static final Holder.Reference<MobEffect> RANDOM_TELEPORT_TRAP = registerDisplayOnly("random_teleport_trap", 0x6C4CD6);
	public static final Holder.Reference<MobEffect> HEALTH_DEBT = registerDisplayOnly("health_debt", 0x6F1F35);
	public static final Holder.Reference<MobEffect> CREEPER_RAIN = registerDisplayOnly("creeper_rain", 0x4B8F35);
	public static final Holder.Reference<MobEffect> BLOCK_CHAOS = register("block_chaos", new BlockChaosMobEffect());
	public static final Holder.Reference<MobEffect> RECIPE_CHAOS = register("recipe_chaos", new RecipeChaosMobEffect());
	public static final Holder.Reference<MobEffect> MINING_LOCK = register("mining_lock", new MiningLockMobEffect());
	public static final Holder.Reference<MobEffect> DOOM_MARK = register("doom_mark", new PunishmentMobEffect(0x5A0E14));
	public static final Holder.Reference<MobEffect> WEAK_HANDS = register("weak_hands", new PunishmentMobEffect(0x777170));
	public static final Holder.Reference<MobEffect> LAVA_CORRUPTION = register("lava_corruption", new PunishmentMobEffect(0xE65320));
	public static final Holder.Reference<MobEffect> VOID_CRACK = register("void_crack", new PunishmentMobEffect(0x3B245E));

	private ModMobEffects() {
	}

	public static void register() {
		RandomSurvivalEvents.LOGGER.info("Registered {} Random Survival Events mob effects.", RSE_EFFECTS.size());
	}

	public static Optional<Holder.Reference<MobEffect>> getDisplayEffect(ResourceLocation effectId) {
		if (effectId == null || !RandomSurvivalEvents.MOD_ID.equals(effectId.getNamespace())) {
			return Optional.empty();
		}
		return Optional.ofNullable(RSE_EFFECTS.get(effectId.getPath()));
	}

	public static Optional<Holder.Reference<MobEffect>> getDisplayEffect(String effectId) {
		String path = normalizePath(effectId);
		if (path.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(RSE_EFFECTS.get(path));
	}

	public static Optional<String> getEffectPath(Holder<MobEffect> effect) {
		for (Map.Entry<String, Holder.Reference<MobEffect>> entry : RSE_EFFECTS.entrySet()) {
			if (entry.getValue().equals(effect)) {
				return Optional.of(entry.getKey());
			}
		}
		return Optional.empty();
	}

	public static Set<String> getEffectIds() {
		return Collections.unmodifiableSet(RSE_EFFECTS.keySet());
	}

	public static void refreshEffect(ServerPlayer player, Holder<MobEffect> effect, int durationTicks) {
		if (player == null || effect == null || durationTicks <= 0) {
			return;
		}

		player.removeEffect(effect);
		player.addEffect(new MobEffectInstance(
				effect,
				durationTicks,
				0,
				false,
				true,
				true
		));
	}

	private static Holder.Reference<MobEffect> registerDisplayOnly(String id, int color) {
		return register(id, new DisplayOnlyMobEffect(color));
	}

	private static Holder.Reference<MobEffect> register(String id, MobEffect effect) {
		Holder.Reference<MobEffect> holder = Registry.registerForHolder(
				BuiltInRegistries.MOB_EFFECT,
				ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, id),
				effect
		);
		RSE_EFFECTS.put(id, holder);
		return holder;
	}

	private static String normalizePath(String effectId) {
		if (effectId == null) {
			return "";
		}

		String trimmed = effectId.trim();
		int separator = trimmed.indexOf(':');
		if (separator < 0) {
			return trimmed;
		}

		String namespace = trimmed.substring(0, separator);
		if (!RandomSurvivalEvents.MOD_ID.equals(namespace)) {
			return "";
		}
		return trimmed.substring(separator + 1);
	}
}
