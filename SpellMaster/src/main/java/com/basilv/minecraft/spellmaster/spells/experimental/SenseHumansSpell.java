package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.Village;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;

public class SenseHumansSpell extends Spell {

	public SenseHumansSpell() {
		super("Sense Humans");
		setCastingMinimumLevel(10);
		setCastingFocus("Snow block", ItemType.Stick); // TODO: Figure out focus
//		setCastingComponent(ItemType.RottenFlesh, "Rotten Flesh", 6); // TODO: Consider using more exotic item?
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Sense location of nearest humans." // TODO: figure out.
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.AMBIENCE_CAVE; // TODO: Figure out
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		// Either approach below only works if village is loaded, which significantly reduces the utility of this spell. 
		// Much better off doing random teleports or even better using Flight, so dropping development of this spell. 
		int range = 1000;
		Village village = context.getWorld().getNearestVillage(context.getPlayer().getPosition(), range);
		if (village != null) {
			Location location = village.getCenter();
			logger.info("Village location = " + location);
		} else {
			context.getPlayer().message("No village sensed.");
		}
		
//		context.getWorld().getEntityLivingList().stream()
//			.filter(entity -> entity instanceof Villager)
//			.forEach(entity -> {
//				logger.info("have villager");
//			});
			
	    return true;
	}

}
