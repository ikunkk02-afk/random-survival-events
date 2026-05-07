package com.ikunkk02afk.randomsurvivalevents.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResultSlot.class)
public interface ResultSlotAccessor {
	@Accessor("player")
	Player randomsurvivalevents$getPlayer();
}
