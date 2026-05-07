package com.ikunkk02afk.randomsurvivalevents.event.impl.attribute;

import com.ikunkk02afk.randomsurvivalevents.component.PlayerEventComponent;
import com.ikunkk02afk.randomsurvivalevents.component.RandomSurvivalEventsComponents;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEvent;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventCategory;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventRarity;
import com.ikunkk02afk.randomsurvivalevents.event.RandomEventUtils;
import com.ikunkk02afk.randomsurvivalevents.event.state.AttributeEventHelper;
import com.ikunkk02afk.randomsurvivalevents.event.state.AttributeEventIds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GlassCannonEvent implements RandomEvent {
	private static final int DURATION_TICKS = 120 * 20;

	@Override
	public String getId() {
		return "glass_cannon";
	}

	@Override
	public String getName() {
		return "玻璃大炮";
	}

	@Override
	public RandomEventCategory getCategory() {
		return RandomEventCategory.ATTRIBUTE;
	}

	@Override
	public RandomEventRarity getRarity() {
		return RandomEventRarity.EPIC;
	}

	@Override
	public int getStatusEffectDurationTicks(ServerPlayer player, ServerLevel world) {
		return DURATION_TICKS;
	}

	@Override
	public void execute(ServerPlayer player, ServerLevel world) {
		if (player == null || world == null || !player.isAlive()) {
			return;
		}

		PlayerEventComponent component = RandomSurvivalEventsComponents.PLAYER_EVENTS.get(player);
		component.setGlassCannonUntilTick(world.getGameTime() + DURATION_TICKS);
		AttributeEventHelper.applyMaxHealthModifier(player, AttributeEventIds.GLASS_CANNON, -6.0D);
		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURATION_TICKS, 0));
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION_TICKS, 0));
		RandomEventUtils.sendMessage(player, "你变得更危险，也更脆弱了。");
	}
}
