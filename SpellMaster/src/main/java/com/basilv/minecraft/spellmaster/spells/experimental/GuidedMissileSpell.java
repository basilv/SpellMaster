package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.DamageType;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.EntityLiving;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class GuidedMissileSpell extends Spell {

	public GuidedMissileSpell() {
		super("Guided Missile");
		setCastingMinimumLevel(0); // TODO: FIX to 10? 
		setCastingFocus("Arrow", ItemType.Arrow); // TODO: Figure out focus, components
		setCastingComponent("Arrow", ItemType.Arrow, 1);
		// TODO: Switch to rain of arrows???
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList( 
		   "Range: ?? ", // TODO
		   "Damage: ??", // TODO
		   "Guide arrow towards target."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.BOW_HIT;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int maxRange = 20; // TODO
		Entity entity = player.getTargetLookingAt(maxRange);
		if (entity == null) {
			return false;
		}
		
		if (!(entity instanceof EntityLiving)) {
			return false;
		}
		EntityLiving livingEntity = (EntityLiving) entity;
		livingEntity.setDisplayName("Hit by GuidedArrow");
		livingEntity.setFireTicks((int)MinecraftUtils.secondsToTicks(2));
		int damage = 10; // TODO
		// TODO: Solve problem of not getting experience (see Lighting Bolt spell)
		log("Entity health " + livingEntity.getHealth());
		// TODO: Was dealing damage as damage type lightning bolt, switching back to arrow damage type
		livingEntity.dealDamage(DamageType.ARROW, damage);
		livingEntity.setArrowCountInEntity(livingEntity.getArrowCountInEntity()+1);
		log("Entity health after " + livingEntity.getHealth());
		if (livingEntity.getHealth() <= 0) {
			livingEntity.destroy();
		}
		
	    return true;
	}
	
}
