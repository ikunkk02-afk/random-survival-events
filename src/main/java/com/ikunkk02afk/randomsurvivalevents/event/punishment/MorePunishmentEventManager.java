package com.ikunkk02afk.randomsurvivalevents.event.punishment;

import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.effect.ModMobEffects;
import com.ikunkk02afk.randomsurvivalevents.event.block.TemporaryBlockChangeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class MorePunishmentEventManager {
	private static final int REVERSE_PUSH_INTERVAL_TICKS = 40;
	private static final int ITEM_DROP_INTERVAL_TICKS = 5 * 20;
	private static final int INVENTORY_LOCK_MESSAGE_COOLDOWN_TICKS = 40;
	private static final double ITEM_DROP_CHANCE = 0.55D;
	private static final Random RANDOM = new Random();
	private static final Map<UUID, Long> NEXT_REVERSE_PUSH_TICK = new HashMap<>();
	private static final Map<UUID, Long> NEXT_ITEM_DROP_TICK = new HashMap<>();
	private static final Map<UUID, Long> NEXT_INVENTORY_LOCK_MESSAGE_TICK = new HashMap<>();
	private static final List<DisguiseTask> DISGUISE_TASKS = new ArrayList<>();
	private static final List<TemporaryFallingBlock> TEMPORARY_FALLING_BLOCKS = new ArrayList<>();

	private MorePunishmentEventManager() {
	}

	public static void tick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			tickPlayer(player);
		}
		tickDisguises(server);
		tickTemporaryFallingBlocks(server);
	}

	public static void scheduleDisguise(ServerLevel world, Mob animal, EntityType<? extends Mob> hostileType, ServerPlayer target, int delayTicks) {
		if (world == null || animal == null || hostileType == null || target == null || delayTicks <= 0) {
			return;
		}

		DISGUISE_TASKS.add(new DisguiseTask(
				world.dimension(),
				animal.getUUID(),
				hostileType,
				target.getUUID(),
				world.getGameTime() + delayTicks
		));
	}

	public static void trackTemporaryFallingBlock(ServerLevel world, FallingBlockEntity entity, int restoreDelayTicks) {
		if (world == null || entity == null || restoreDelayTicks <= 0) {
			return;
		}

		BlockPos pos = entity.blockPosition();
		TEMPORARY_FALLING_BLOCKS.add(new TemporaryFallingBlock(
				world.dimension(),
				entity.getUUID(),
				pos.immutable(),
				world.getBlockState(pos),
				restoreDelayTicks
		));
	}

	private static void tickPlayer(ServerPlayer player) {
		UUID playerId = player.getUUID();
		if (!canAffect(player)) {
			clearPlayerState(player);
			return;
		}

		long gameTime = player.serverLevel().getGameTime();
		if (player.hasEffect(ModMobEffects.REVERSE_CONTROL)) {
			tickReverseControl(player, gameTime);
		} else {
			NEXT_REVERSE_PUSH_TICK.remove(playerId);
		}

		if (player.hasEffect(ModMobEffects.ITEM_DROP_CURSE)) {
			tickItemDropCurse(player, gameTime);
		} else {
			NEXT_ITEM_DROP_TICK.remove(playerId);
		}

		if (player.hasEffect(ModMobEffects.INVENTORY_LOCK)) {
			tickInventoryLock(player, gameTime);
		} else {
			NEXT_INVENTORY_LOCK_MESSAGE_TICK.remove(playerId);
		}
	}

	private static void tickReverseControl(ServerPlayer player, long gameTime) {
		UUID playerId = player.getUUID();
		long nextTick = NEXT_REVERSE_PUSH_TICK.getOrDefault(playerId, gameTime);
		if (gameTime < nextTick) {
			return;
		}

		double angle = RANDOM.nextDouble() * Math.PI * 2.0D;
		double strength = 0.35D + RANDOM.nextDouble() * 0.25D;
		Vec3 push = new Vec3(Math.cos(angle) * strength, player.onGround() ? 0.08D : 0.0D, Math.sin(angle) * strength);
		player.push(push.x, push.y, push.z);
		NEXT_REVERSE_PUSH_TICK.put(playerId, gameTime + REVERSE_PUSH_INTERVAL_TICKS);
	}

	private static void tickItemDropCurse(ServerPlayer player, long gameTime) {
		UUID playerId = player.getUUID();
		long nextTick = NEXT_ITEM_DROP_TICK.getOrDefault(playerId, gameTime + ITEM_DROP_INTERVAL_TICKS);
		if (gameTime < nextTick) {
			NEXT_ITEM_DROP_TICK.putIfAbsent(playerId, nextTick);
			return;
		}

		NEXT_ITEM_DROP_TICK.put(playerId, gameTime + ITEM_DROP_INTERVAL_TICKS);
		if (RANDOM.nextDouble() > ITEM_DROP_CHANCE) {
			return;
		}

		Inventory inventory = player.getInventory();
		List<Integer> candidates = new ArrayList<>();
		for (int slot = 0; slot <= 35; slot++) {
			if (canDropFromCurse(inventory.getItem(slot))) {
				candidates.add(slot);
			}
		}

		if (candidates.isEmpty()) {
			return;
		}

		int slot = candidates.get(RANDOM.nextInt(candidates.size()));
		ItemStack source = inventory.getItem(slot);
		ItemStack dropped = source.split(1);
		inventory.setChanged();
		if (!dropped.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(
					player.serverLevel(),
					player.getX(),
					player.getY() + 0.2D,
					player.getZ(),
					dropped
			);
			itemEntity.setDefaultPickUpDelay();
			if (!RandomSurvivalEventsConfig.get().destructiveMode) {
				itemEntity.setUnlimitedLifetime();
			}
			player.serverLevel().addFreshEntity(itemEntity);
			player.displayClientMessage(Component.literal("手滑诅咒让一个物品掉了出来。"), true);
		}
	}

	private static void tickInventoryLock(ServerPlayer player, long gameTime) {
		if (player.containerMenu == player.inventoryMenu) {
			return;
		}

		player.closeContainer();
		long nextMessageTick = NEXT_INVENTORY_LOCK_MESSAGE_TICK.getOrDefault(player.getUUID(), Long.MIN_VALUE);
		if (gameTime >= nextMessageTick) {
			player.displayClientMessage(Component.literal("你的背包被一股力量封住了。"), true);
			NEXT_INVENTORY_LOCK_MESSAGE_TICK.put(player.getUUID(), gameTime + INVENTORY_LOCK_MESSAGE_COOLDOWN_TICKS);
		}
	}

	private static void tickDisguises(MinecraftServer server) {
		Iterator<DisguiseTask> iterator = DISGUISE_TASKS.iterator();
		while (iterator.hasNext()) {
			DisguiseTask task = iterator.next();
			ServerLevel world = server.getLevel(task.dimension());
			if (world == null) {
				iterator.remove();
				continue;
			}
			if (world.getGameTime() < task.transformTick()) {
				continue;
			}

			Entity entity = world.getEntity(task.animalId());
			ServerPlayer target = server.getPlayerList().getPlayer(task.targetId());
			if (!(entity instanceof Mob animal) || !animal.isAlive() || !canAffect(target)) {
				iterator.remove();
				continue;
			}

			Mob hostile = task.hostileType().create(world, null, animal.blockPosition(), MobSpawnType.EVENT, false, false);
			if (hostile != null && isAllowedDisguiseHostile(task.hostileType())) {
				hostile.moveTo(animal.getX(), animal.getY(), animal.getZ(), RANDOM.nextFloat() * 360.0F, 0.0F);
				hostile.setTarget(target);
				world.addFreshEntity(hostile);
				animal.discard();
			}
			iterator.remove();
		}
	}

	private static void tickTemporaryFallingBlocks(MinecraftServer server) {
		Iterator<TemporaryFallingBlock> iterator = TEMPORARY_FALLING_BLOCKS.iterator();
		while (iterator.hasNext()) {
			TemporaryFallingBlock tracked = iterator.next();
			ServerLevel world = server.getLevel(tracked.dimension());
			if (world == null) {
				iterator.remove();
				continue;
			}

			Entity entity = world.getEntity(tracked.entityId());
			if (entity instanceof FallingBlockEntity fallingBlock && fallingBlock.isAlive()) {
				BlockPos pos = fallingBlock.blockPosition().immutable();
				tracked.updateLastPos(pos, world.getBlockState(pos));
				continue;
			}

			BlockPos landedPos = tracked.lastPos();
			BlockState current = world.getBlockState(landedPos);
			if (!current.is(Blocks.ANVIL)) {
				landedPos = findNearbyAnvil(world, landedPos);
				current = world.getBlockState(landedPos);
			}
			if (current.is(Blocks.ANVIL) && world.getBlockEntity(landedPos) == null) {
				TemporaryBlockChangeManager.add(
						world,
						landedPos,
						tracked.originalState(),
						current,
						world.getGameTime() + tracked.restoreDelayTicks()
				);
			}
			iterator.remove();
		}
	}

	private static BlockPos findNearbyAnvil(ServerLevel world, BlockPos center) {
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				for (int dz = -1; dz <= 1; dz++) {
					BlockPos pos = center.offset(dx, dy, dz);
					if (world.getBlockState(pos).is(Blocks.ANVIL)) {
						return pos.immutable();
					}
				}
			}
		}
		return center;
	}

	private static boolean canAffect(ServerPlayer player) {
		return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
	}

	private static void clearPlayerState(ServerPlayer player) {
		UUID playerId = player.getUUID();
		NEXT_REVERSE_PUSH_TICK.remove(playerId);
		NEXT_ITEM_DROP_TICK.remove(playerId);
		NEXT_INVENTORY_LOCK_MESSAGE_TICK.remove(playerId);
		if (!player.isAlive()) {
			player.removeEffect(ModMobEffects.REVERSE_CONTROL);
			player.removeEffect(ModMobEffects.ITEM_DROP_CURSE);
			player.removeEffect(ModMobEffects.INVENTORY_LOCK);
		}
	}

	private static boolean canDropFromCurse(ItemStack stack) {
		if (stack == null || stack.isEmpty() || stack.isDamageableItem() || stack.has(DataComponents.CUSTOM_NAME)) {
			return false;
		}
		return !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}

	private static boolean isAllowedDisguiseHostile(EntityType<? extends Mob> type) {
		return type != EntityType.ENDER_DRAGON
				&& type != EntityType.WITHER
				&& type != EntityType.WARDEN
				&& type != EntityType.ELDER_GUARDIAN
				&& Enemy.class.isAssignableFrom(type.getBaseClass());
	}

	private record DisguiseTask(
			ResourceKey<Level> dimension,
			UUID animalId,
			EntityType<? extends Mob> hostileType,
			UUID targetId,
			long transformTick
	) {
	}

	private static final class TemporaryFallingBlock {
		private final ResourceKey<Level> dimension;
		private final UUID entityId;
		private final int restoreDelayTicks;
		private BlockPos lastPos;
		private BlockState originalState;

		private TemporaryFallingBlock(
				ResourceKey<Level> dimension,
				UUID entityId,
				BlockPos lastPos,
				BlockState originalState,
				int restoreDelayTicks
		) {
			this.dimension = dimension;
			this.entityId = entityId;
			this.lastPos = lastPos;
			this.originalState = originalState;
			this.restoreDelayTicks = restoreDelayTicks;
		}

		private ResourceKey<Level> dimension() {
			return dimension;
		}

		private UUID entityId() {
			return entityId;
		}

		private BlockPos lastPos() {
			return lastPos;
		}

		private BlockState originalState() {
			return originalState;
		}

		private int restoreDelayTicks() {
			return restoreDelayTicks;
		}

		private void updateLastPos(BlockPos lastPos, BlockState originalState) {
			this.lastPos = lastPos;
			this.originalState = originalState;
		}
	}
}
