package com.ikunkk02afk.randomsurvivalevents.event.state;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class AttributeEventHelper {
	private AttributeEventHelper() {
	}

	public static void applyMaxHealthModifier(ServerPlayer player, ResourceLocation id, double amount) {
		AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		if (maxHealth == null) {
			return;
		}

		maxHealth.removeModifier(id);
		maxHealth.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
		clampHealth(player);
	}

	public static void ensureMaxHealthModifier(ServerPlayer player, ResourceLocation id, double amount) {
		AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		if (maxHealth == null) {
			return;
		}

		if (!maxHealth.hasModifier(id)) {
			maxHealth.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
		}
		clampHealth(player);
	}

	public static void removeMaxHealthModifier(ServerPlayer player, ResourceLocation id) {
		AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		if (maxHealth != null) {
			maxHealth.removeModifier(id);
		}
		clampHealth(player);
	}

	public static void removeAll(ServerPlayer player) {
		removeMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_BOOST);
		removeMaxHealthModifier(player, AttributeEventIds.MAX_HEALTH_REDUCE);
		removeMaxHealthModifier(player, AttributeEventIds.GLASS_CANNON);
		removeMaxHealthModifier(player, AttributeEventIds.HEALTH_DEBT);
	}

	private static void clampHealth(ServerPlayer player) {
		if (player.getHealth() > player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
	}
}
