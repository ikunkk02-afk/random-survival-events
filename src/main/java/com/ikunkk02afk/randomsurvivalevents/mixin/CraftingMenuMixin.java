package com.ikunkk02afk.randomsurvivalevents.mixin;

import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeShuffleManager;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {
	@Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
	private static void randomsurvivalevents$shuffleDisplayedResult(
			AbstractContainerMenu menu,
			Level level,
			Player player,
			CraftingContainer craftSlots,
			ResultContainer resultSlots,
			RecipeHolder<CraftingRecipe> recipeHolder,
			CallbackInfo info
	) {
		if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer) || serverPlayer.isCreative() || !RecipeShuffleManager.isShuffleActive()) {
			return;
		}

		ItemStack originalResult = resultSlots.getItem(0);
		if (originalResult.isEmpty()) {
			return;
		}

		RecipeHolder<?> usedRecipe = resultSlots.getRecipeUsed();
		if (usedRecipe == null) {
			return;
		}

		ItemStack shuffledResult = RecipeShuffleManager.getShuffledResult(usedRecipe.id(), originalResult);
		if (shuffledResult.isEmpty()) {
			return;
		}

		resultSlots.setItem(0, shuffledResult);
		menu.setRemoteSlot(0, shuffledResult);
		serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
				menu.containerId,
				menu.incrementStateId(),
				0,
				shuffledResult
		));
	}
}
