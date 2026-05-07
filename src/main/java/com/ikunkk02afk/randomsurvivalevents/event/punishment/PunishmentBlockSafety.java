package com.ikunkk02afk.randomsurvivalevents.event.punishment;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class PunishmentBlockSafety {
	private PunishmentBlockSafety() {
	}

	public static boolean canTemporarilyRemove(Level world, BlockPos pos, BlockState state) {
		if (world == null || pos == null || state == null || state.isAir() || !state.getFluidState().isEmpty()) {
			return false;
		}
		if (world.getBlockEntity(pos) != null || isProtected(state)) {
			return false;
		}
		return state.getDestroySpeed(world, pos) >= 0.0F;
	}

	public static boolean isBaseMarker(Level world, BlockPos pos, BlockState state) {
		if (world == null || pos == null || state == null) {
			return false;
		}
		return world.getBlockEntity(pos) != null
				|| isProtected(state)
				|| state.is(BlockTags.PLANKS)
				|| state.is(BlockTags.WOOL)
				|| state.is(BlockTags.WOOL_CARPETS)
				|| state.is(BlockTags.DOORS)
				|| state.is(BlockTags.TRAPDOORS)
				|| state.is(BlockTags.BEDS)
				|| state.is(Blocks.CRAFTING_TABLE)
				|| state.is(Blocks.FURNACE)
				|| state.is(Blocks.BLAST_FURNACE)
				|| state.is(Blocks.SMOKER)
				|| state.is(Blocks.ANVIL)
				|| state.is(Blocks.CHIPPED_ANVIL)
				|| state.is(Blocks.DAMAGED_ANVIL);
	}

	public static boolean isProtected(BlockState state) {
		if (state == null) {
			return true;
		}

		Block block = state.getBlock();
		return state.is(Blocks.BEDROCK)
				|| state.is(Blocks.CHEST)
				|| state.is(Blocks.TRAPPED_CHEST)
				|| state.is(Blocks.ENDER_CHEST)
				|| state.is(Blocks.SPAWNER)
				|| state.is(Blocks.COMMAND_BLOCK)
				|| state.is(Blocks.CHAIN_COMMAND_BLOCK)
				|| state.is(Blocks.REPEATING_COMMAND_BLOCK)
				|| state.is(Blocks.BARRIER)
				|| state.is(Blocks.STRUCTURE_BLOCK)
				|| state.is(Blocks.STRUCTURE_VOID)
				|| state.is(Blocks.JIGSAW)
				|| block instanceof ShulkerBoxBlock
				|| block instanceof BedBlock;
	}
}
