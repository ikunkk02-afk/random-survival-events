package com.ikunkk02afk.randomsurvivalevents.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.ComponentV3;

public class PlayerEventComponent implements ComponentV3 {
	private long maxHealthBoostUntilTick;
	private long maxHealthReduceUntilTick;
	private long glassCannonUntilTick;
	private long healthDebtUntilTick;

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

	public long getHealthDebtUntilTick() {
		return healthDebtUntilTick;
	}

	public void setHealthDebtUntilTick(long healthDebtUntilTick) {
		this.healthDebtUntilTick = healthDebtUntilTick;
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		maxHealthBoostUntilTick = tag.getLong("MaxHealthBoostUntilTick");
		maxHealthReduceUntilTick = tag.getLong("MaxHealthReduceUntilTick");
		glassCannonUntilTick = tag.getLong("GlassCannonUntilTick");
		healthDebtUntilTick = tag.getLong("HealthDebtUntilTick");
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		tag.putLong("MaxHealthBoostUntilTick", maxHealthBoostUntilTick);
		tag.putLong("MaxHealthReduceUntilTick", maxHealthReduceUntilTick);
		tag.putLong("GlassCannonUntilTick", glassCannonUntilTick);
		tag.putLong("HealthDebtUntilTick", healthDebtUntilTick);
	}
}
