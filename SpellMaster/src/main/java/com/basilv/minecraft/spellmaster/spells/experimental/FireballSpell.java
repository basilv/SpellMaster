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
//		world.makeExplosion(victim, 
//		          loc.getX(), loc.getY(), loc.getZ(), 
//		          100.0f, true);
//	      boolean includeAir = true;
//	      BlockIterator sightItr = new BlockIterator(new LineTracer(me), includeAir);
//	      while (sightItr.hasNext()) {
//	        Block b = sightItr.next();
//	        spawnParticle(b.getLocation(), Particle.Type.FIREWORKS_SPARK);    
//	        if (b.getType() != BlockType.Air) {
//	          
//	          Location newLocation = new Location(b.getLocation());
//	          newLocation.setY(newLocation.getY()+2);
//	          Cow cow = (Cow)spawnEntityLiving(newLocation, EntityType.COW);
//	          cow.setDisplayName("Summoned");
//	          playSound(b.getLocation(), SoundEffect.Type.COW_SAY);
//	          
//	          // b.getWorld().setBlockAt(newLocation, BlockType.Sand);
////	          playSound(b.getLocation(), SoundEffect.Type.ANVIL_LAND);
//	          break;
//	        }
//	      }

	    return true;
	}

}
