package com.ikunkk02afk.randomsurvivalevents.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DisplayOnlyMobEffect extends MobEffect {
	public DisplayOnlyMobEffect(int color) {
		super(MobEffectCategory.NEUTRAL, color);
	}
}
