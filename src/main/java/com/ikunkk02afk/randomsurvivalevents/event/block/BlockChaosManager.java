package com.ikunkk02afk.randomsurvivalevents.event.block;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockChaosManager {
	private static final Random RANDOM = new Random();
	private static final Map<UUID, Long> COOLDOWN_UNTIL_TICK = new HashMap<>();

	private static final Item[] COMMON_DROPS = {
			Items.APPLE,
			Items.BREAD,
			Items.TORCH,
			Items.STICK,
			Items.OAK_LOG,
			Items.COAL,
			Items.IRON_NUGGET,
			Items.GOLD_NUGGET,
			Items.EXPERIENCE_BOTTLE,
			Items.BONE_MEAL,
			Items.STRING,
			Items.FEATHER,
			Items.LEATHER,
			Items.REDSTONE,
			Items.LAPIS_LAZULI
	};

	private static final Item[] RARE_DROPS = {
			Items.IRON_INGOT,
			Items.GOLD_INGOT,
			Items.EMERALD,
			Items.DIAMOND,
			Items.ENDER_PEARL,
			Items.ENCHANTED_BOOK
	};

	private static final EntityType<?>[] COMMON_MOBS = {
			EntityType.CHICKEN,
			EntityType.COW,
			EntityType.PIG,
			EntityType.SHEEP,
			EntityType.RABBIT,
			EntityType.WOLF,
			EntityType.ZOMBIE,
			EntityType.SKELETON,
			EntityType.SPIDER,
			EntityType.CREEPER,
			EntityType.SLIME,
			EntityType.ENDERMAN
	};

	private static final EntityType<?>[] DANGEROUS_MOBS = {
			EntityType.WITHER,
			EntityType.WARDEN,
			EntityType.ELDER_GUARDIAN,
			EntityType.RAVAGER,
			EntityType.WITCH,
			EntityType.BLAZE
	};

	private static final EntityType<?>[] DANGEROUS_MOBS_WITHOUT_BOSSES = {
			EntityType.ELDER_GUARDIAN,
			EntityType.RAVAGER,
			EntityType.WITCH,
			EntityType.BLAZE
	};

	private static boolean registered;

	private BlockChaosManager() {
	}

	public static void register() {
		if (registered) {
			return;
		}

		PlayerBlockBreakEvents.AFTER.register(BlockChaosManager::afterBlockBreak);
		registered = true;
	}

	public static void tick(MinecraftServer server) {
		if (server == null || COOLDOWN_UNTIL_TICK.isEmpty()) {
			return;
		}

		Set<UUID> onlinePlayers = new HashSet<>();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			onlinePlayers.add(player.getUUID());
		}
		COOLDOWN_UNTIL_TICK.keySet().removeIf(playerId -> !onlinePlayers.contains(playerId));
	}

	private static void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (!(level instanceof ServerLevel world) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (!config.enableBlockChaosEffect || !canTrigger(serverPlayer) || !serverPlayer.hasEffect(ModMobEffects.BLOCK_CHAOS)) {
			return;
		}

		long gameTime = world.getGameTime();
		UUID playerId = serverPlayer.getUUID();
		if (gameTime < COOLDOWN_UNTIL_TICK.getOrDefault(playerId, 0L)) {
			return;
		}
		COOLDOWN_UNTIL_TICK.put(playerId, gameTime + Math.max(1, config.blockChaosCooldownTicks));

		if (RANDOM.nextDouble() < config.blockChaosDropChance) {
			RandomEventUtils.dropItem(world, pos, createRandomDrop(config));
		}
		if (RANDOM.nextDouble() < config.blockChaosMobSpawnChance) {
			trySpawnRandomMob(world, serverPlayer, pos, config);
		}
	}

	private static boolean canTrigger(ServerPlayer player) {
		return player.isAlive() && !player.isCreative() && !player.isSpectator();
	}

	private static ItemStack createRandomDrop(RandomSurvivalEventsConfig config) {
		boolean rare = RANDOM.nextDouble() < config.blockChaosRareItemChance;
		Item item = rare ? RARE_DROPS[RANDOM.nextInt(RARE_DROPS.length)] : COMMON_DROPS[RANDOM.nextInt(COMMON_DROPS.length)];
		int count = rare ? 1 : RandomEventUtils.randomBetween(1, 4);
		return new ItemStack(item, count);
	}

	private static void trySpawnRandomMob(ServerLevel world, ServerPlayer player, BlockPos brokenPos, RandomSurvivalEventsConfig config) {
		Optional<BlockPos> spawnPos = findSafeSpawnPos(world, player, brokenPos);
		if (spawnPos.isEmpty()) {
			return;
		}

		EntityType<?> entityType = selectMobType(config);
		Entity entity = entityType.spawn(world, spawnPos.get(), MobSpawnType.TRIGGERED);
		if (entity != null && isBossWarningMob(entityType)) {
			RandomEventUtils.sendMessage(player, "傻愣着干嘛？还不赶紧跑。");
		}
	}

	private static EntityType<?> selectMobType(RandomSurvivalEventsConfig config) {
		if (RANDOM.nextDouble() >= config.blockChaosDangerousMobChance) {
			return COMMON_MOBS[RANDOM.nextInt(COMMON_MOBS.length)];
		}

		EntityType<?>[] dangerousPool = config.allowBossMobFromBlockChaos ? DANGEROUS_MOBS : DANGEROUS_MOBS_WITHOUT_BOSSES;
		return dangerousPool[RANDOM.nextInt(dangerousPool.length)];
	}

	private static boolean isBossWarningMob(EntityType<?> entityType) {
		return entityType == EntityType.WITHER || entityType == EntityType.WARDEN;
	}

	private static Optional<BlockPos> findSafeSpawnPos(ServerLevel world, ServerPlayer player, BlockPos origin) {
		if (origin.getY() <= world.getMinBuildHeight() || origin.getY() >= world.getMaxBuildHeight()) {
			return Optional.empty();
		}

		for (int attempt = 0; attempt < 32; attempt++) {
			int radius = RandomEventUtils.randomBetween(2, 5);
			double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			int startY = Math.min(world.getMaxBuildHeight() - 2, origin.getY() + 3);
			int endY = Math.max(world.getMinBuildHeight() + 1, origin.getY() - 3);

			for (int y = startY; y >= endY; y--) {
				BlockPos candidate = new BlockPos(x, y, z);
				if (isTooCloseToPlayer(candidate, player.blockPosition())) {
					continue;
				}
				if (RandomEventUtils.isSafeStandingPos(world, candidate)) {
					return Optional.of(candidate);
				}
			}
		}
		return Optional.empty();
	}

	private static boolean isTooCloseToPlayer(BlockPos candidate, BlockPos playerPos) {
		int dx = candidate.getX() - playerPos.getX();
		int dy = candidate.getY() - playerPos.getY();
		int dz = candidate.getZ() - playerPos.getZ();
		return dx * dx + dy * dy + dz * dz < 6;
	}
}
