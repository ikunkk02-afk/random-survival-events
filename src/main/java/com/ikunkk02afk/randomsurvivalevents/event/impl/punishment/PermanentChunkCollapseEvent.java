package com.ikunkk02afk.randomsurvivalevents.event.impl.punishment;

import com.ikunkk02afk.randomsurvivalevents.RandomSurvivalEvents;
import com.ikunkk02afk.randomsurvivalevents.config.RandomSurvivalEventsConfig;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.DestructiveModeRules;
import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentBlockSafety;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PermanentChunkCollapseEvent implements RandomEvent {
	private static final Random RANDOM = new Random();
	private static final int BELOW_PLAYER_LIMIT = 12;
	private static final int ABOVE_PLAYER_LIMIT = 28;
	private static final List<BlockState> DEBRIS = List.of(
			Blocks.COBBLESTONE.defaultBlockState(),
			Blocks.GRAVEL.defaultBlockState(),
			Blocks.COBBLED_DEEPSLATE.defaultBlockState(),
			Blocks.BLACKSTONE.defaultBlockState()
	);

	@Override
	public String getId() {
		return "permanent_chunk_collapse";
	}

	@Override
	public String getName() {
		return "永久区块塌陷";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.PUNISHMENT;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.DISASTER;
	}

	@Override
	public ResourceLocation getStatusEffectId() {
		return ResourceLocation.fromNamespaceAndPath(RandomSurvivalEvents.MOD_ID, "void_crack");
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return 20 * 20;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		RandomSurvivalEventsConfig config = RandomSurvivalEventsConfig.get();
		if (player == null || world == null || !player.isAlive() || !DestructiveModeRules.canPermanentlyDamageChunk(config)) {
			RandomEventUtils.sendMessage(player, "毁灭模式未完整开启，永久区块塌陷被阻止。");
			return;
		}

		BlockPos playerPos = player.blockPosition();
		ChunkPos chunkPos = new ChunkPos(playerPos);
		int minY = Math.max(world.getMinBuildHeight(), playerPos.getY() - BELOW_PLAYER_LIMIT);
		int maxY = Math.min(world.getMaxBuildHeight() - 1, playerPos.getY() + ABOVE_PLAYER_LIMIT);
		int changed = 0;
		int limit = Math.min(config.maxTemporaryChangedBlocks, 2048);

		for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX() && changed < limit; x++) {
			for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ() && changed < limit; z++) {
				for (int y = minY; y <= maxY && changed < limit; y++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (pos.distSqr(playerPos) < 9.0D || RANDOM.nextDouble() > 0.34D) {
						continue;
					}

					BlockState state = world.getBlockState(pos);
					if (!PunishmentBlockSafety.canTemporarilyRemove(world, pos, state)) {
						continue;
					}

					BlockState replacement = RANDOM.nextDouble() < 0.12D
							? DEBRIS.get(RANDOM.nextInt(DEBRIS.size()))
							: Blocks.AIR.defaultBlockState();
					world.setBlockAndUpdate(pos, replacement);
					changed++;
				}
			}
		}

		RandomEventUtils.sendMessage(player, changed > 0 ? "脚下的区块被永久撕裂了。" : "永久区块塌陷没有找到可破坏方块。");
	}
}
