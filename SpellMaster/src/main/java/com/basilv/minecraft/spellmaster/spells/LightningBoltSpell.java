package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.BlockIterator;
import net.canarymod.LineTracer;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class LightningBoltSpell extends Spell {

	public LightningBoltSpell() {
		super("Lightning Bolt");
		setCastingMinimumLevel(10);
		setCastingFocus("Nether quartz", ItemType.NetherQuartz); 
		setCastingComponent("Redstone", ItemType.RedStone, 2);
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		// Increasing damage in rain/storm is designed to be boosted by Control Weather spell.
		lines.addAll(Arrays.asList(
		   "Range: 3 squares + 1 per 2 levels",
 		   "Damage: 4 to 8 + 1 per 5 levels. +3 damage when raining, +6 damage in a thunderstorm",
		   "Blast target entity or block with lightning. Deals full damage to target entity and decreasing damage to entities further away."
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.AMBIENCE_THUNDER;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		Position targetPosition = calculateTargetPosition(player);
		if (targetPosition == null) {
			return false;
		}

		int maxRange = context.getCastingRange(3, 2);
		int range = MinecraftUtils.getSeparationInBlocks(targetPosition, player.getPosition());
		if (range > maxRange) {
			sendPlayerUnableToCastMessage(player, "Out of range. Maximum range = " + maxRange);
			return false;
		}

		World world = player.getWorld();
		world.makeLightningBolt(targetPosition);

		final int targetDamage = calculateDamageAtTargetPoint(context);

		world.getEntityLivingList().stream().forEach(entity -> damageEntity(entity, player, targetPosition, targetDamage)); 			
		world.getPlayerList().stream().forEach(entity -> damageEntity(entity, player, targetPosition, targetDamage));
		
		return true;
	}

	private Position calculateTargetPosition(Player player) {
		Position targetPosition;
		Entity targetLookingAt = player.getTargetLookingAt();
		if (targetLookingAt != null) {
			targetPosition = targetLookingAt.getPosition().copy();
			// Targeting entity directly causes entity and ground to burn. So target block under entity to avoid this.
			targetPosition.moveY(-1);
		} else {
			boolean includeAir = false;
			BlockIterator sightItr = new BlockIterator(new LineTracer(player), includeAir);
			if (!sightItr.hasNext()) {
				sendPlayerUnableToCastMessage(player, "Only targetting air - must target a non-air block");
				return null;
			}
			targetPosition = sightItr.next().getPosition();
		}
		return targetPosition;
	}

	private int calculateDamageAtTargetPoint(MagicContext context) {
		int damage = MinecraftUtils.randomNumberWithinRange(4, 8) + context.getCastingLevel() / 5 + context.getSpellboost().getDamage();
		if (context.getWorld().isThundering()) {
			damage += 6;
		} else if (context.getWorld().isRaining()) {
			damage += 3;
		}
		return damage;
	}

	private void damageEntity(LivingBase entity, Player player, Position targetPosition, int targetDamage) {
		int separation = MinecraftUtils.getSeparationInBlocks(entity.getPosition(), targetPosition);
		int damage = targetDamage;
		// Separation	Damage with Origin = 10
		// 2			7
		// 3			4
		// 4			1
		if (separation > 1) { // Do max damage when separation = 1 to account for target block being below targetted entity.
			damage -= (3 * (separation - 1));
		}
		if (damage > 0) {
			player.attackEntity(entity, damage); 
		}
	}
	
}
