package com.ikunkk02afk.randomsurvivalevents.mixin;

import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeShuffleManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResultSlot.class)
public class RecipeResultSlotMixin {
	@Inject(method = "remove", at = @At("RETURN"), cancellable = true)
	private void randomsurvivalevents$replaceRemovedResult(int amount, CallbackInfoReturnable<ItemStack> info) {
		Player player = randomsurvivalevents$getResultSlotPlayer();
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Container container = ((Slot) (Object) this).container;
		ItemStack shuffledResult = RecipeShuffleManager.getShuffledResult(serverPlayer, container, info.getReturnValue());
		if (!shuffledResult.isEmpty()) {
			info.setReturnValue(shuffledResult);
		}
	}

	@Inject(method = "onTake", at = @At("HEAD"))
	private void randomsurvivalevents$checkRecipeShuffleOnTake(Player player, ItemStack stack, CallbackInfo info) {
		if (player instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()) {
			RecipeShuffleManager.onResultTaken(serverPlayer, ((Slot) (Object) this).container, stack);
		}
	}

	private Player randomsurvivalevents$getResultSlotPlayer() {
		return ((ResultSlotAccessor) this).randomsurvivalevents$getPlayer();
	}
}
