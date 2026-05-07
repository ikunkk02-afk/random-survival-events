package com.ikunkk02afk.randomsurvivalevents.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.ComponentV3;

public class PlayerEventComponent implements ComponentV3 {
	private long recipeChaosUntilTick;
	private long maxHealthBoostUntilTick;
	private long maxHealthReduceUntilTick;
	private long glassCannonUntilTick;

	public boolean hasRecipeChaos(long gameTime) {
		return recipeChaosUntilTick > gameTime;
	}

	public long getRecipeChaosUntilTick() {
		return recipeChaosUntilTick;
	}

	public void setRecipeChaosUntilTick(long recipeChaosUntilTick) {
		this.recipeChaosUntilTick = recipeChaosUntilTick;
	}

	public long getMaxHealthBoostUntilTick() {
		return maxHealthBoostUntilTick;
	}

	public void setMaxHealthBoostUntilTick(long maxHealthBoostUntilTick) {
		this.maxHealthBoostUntilTick = maxHealthBoostUntilTick;
	}

	public long getMaxHealthReduceUntilTick() {
		return maxHealthReduceUntilTick;
	}

	public void setMaxHealthReduceUntilTick(long maxHealthReduceUntilTick) {
		this.maxHealthReduceUntilTick = maxHealthReduceUntilTick;
	}

	public long getGlassCannonUntilTick() {
		return glassCannonUntilTick;
	}

	public void setGlassCannonUntilTick(long glassCannonUntilTick) {
		this.glassCannonUntilTick = glassCannonUntilTick;
	}

	public void clearExpiredRecipeChaos(long gameTime) {
		if (recipeChaosUntilTick <= gameTime) {
			recipeChaosUntilTick = 0L;
		}
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		recipeChaosUntilTick = tag.getLong("RecipeChaosUntilTick");
		maxHealthBoostUntilTick = tag.getLong("MaxHealthBoostUntilTick");
		maxHealthReduceUntilTick = tag.getLong("MaxHealthReduceUntilTick");
		glassCannonUntilTick = tag.getLong("GlassCannonUntilTick");
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		tag.putLong("RecipeChaosUntilTick", recipeChaosUntilTick);
		tag.putLong("MaxHealthBoostUntilTick", maxHealthBoostUntilTick);
		tag.putLong("MaxHealthReduceUntilTick", maxHealthReduceUntilTick);
		tag.putLong("GlassCannonUntilTick", glassCannonUntilTick);
	}
}
