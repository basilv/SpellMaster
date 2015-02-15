package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.FireMagicTome.FireSpell;

public class FireballSpell extends FireSpell {

	public FireballSpell() {
		super("Fireball");
		setCastingMinimumLevel(20);
		setCastingFocus("Blaze rod", ItemType.BlazeRod); 
//		setCastingComponent("Blaze powder", ItemType.BlazePowder, 1); // TODO: Enable after testing is done. 
	}
	

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: level squares", // TODO: Damage, etc. 
		   "Launch a fireball in the direction you are pointing."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.GHAST_FIREBALL;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		// TODO: Game effect
		
	    return true;
	}

}
