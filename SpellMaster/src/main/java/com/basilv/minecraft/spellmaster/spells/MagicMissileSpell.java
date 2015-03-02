package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.BlockIterator;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.EntityLiving;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class MagicMissileSpell extends Spell {

	public MagicMissileSpell() {
		super("Magic Missile");
		setCastingMinimumLevel(10);  
		setCastingFocus("Arrow", ItemType.Arrow); 
		setCastingComponent("Arrow", ItemType.Arrow, 1);
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList( 
		   "Range: 1 square per level", 
		   "Damage: 3 to 5 + 1 per 5 levels", 
		   "Create a magic missile which unerringly strikes the target creature."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.BOW_HIT;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int maxRange = context.getCastingRange(0, 1);
		Entity entity = player.getTargetLookingAt(maxRange);
		if (entity == null || !(entity instanceof EntityLiving)) {
			sendPlayerUnableToCastMessage(player, "Must target a creature.");
			return false;
		}
		
		BlockIterator iterator = new BlockIterator(player, true);
		while (iterator.hasNext()) {
			Block block = iterator.next();
			context.spawnParticle(block.getLocation(), Particle.Type.SPELL_INSTANT);
			int separation = MinecraftUtils.getSeparationInBlocks(entity.getPosition(), block.getPosition());
			if (separation <= 1) {
				break;
			}
		}
		
		EntityLiving livingEntity = (EntityLiving) entity;
		// Damage from fully charged bow is 9. This spell is slightly better at a high casting level.
		int damage = MinecraftUtils.randomNumberWithinRange(3, 5) + context.getCastingLevel() / 5 + context.getSpellboost().getDamage();

		player.attackEntity(livingEntity, damage);
		livingEntity.setArrowCountInEntity(livingEntity.getArrowCountInEntity()+1);
		
	    return true;
	}
	
}
