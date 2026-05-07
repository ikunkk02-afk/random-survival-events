package com.ikunkk02afk.randomsurvivalevents.mixin;

import com.ikunkk02afk.randomsurvivalevents.recipechaos.RecipeChaosHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class RecipeResultSlotMixin {
	@Inject(method = "onTake", at = @At("TAIL"))
	private void randomsurvivalevents$afterTakeResult(Player player, ItemStack stack, CallbackInfo info) {
		if (player instanceof ServerPlayer serverPlayer) {
			RecipeChaosHandler.handleCraft(serverPlayer, stack);
		}
	}
}
