package com.ikunkk02afk.randomsurvivalevents.mixin;

import com.ikunkk02afk.randomsurvivalevents.event.punishment.PunishmentEntityTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Creeper.class)
public class CreeperExplosionMixin {
	@Redirect(
			method = "explodeCreeper",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"
			)
	)
	private Explosion randomsurvivalevents$disableTaggedCreeperBlockDamage(
			Level level,
			Entity source,
			double x,
			double y,
			double z,
			float power,
			Level.ExplosionInteraction interaction
	) {
		if (((Creeper) (Object) this).getTags().contains(PunishmentEntityTags.NO_BLOCK_DAMAGE_CREEPER)) {
			return level.explode(source, x, y, z, power, Level.ExplosionInteraction.NONE);
		}
		return level.explode(source, x, y, z, power, interaction);
	}
}
