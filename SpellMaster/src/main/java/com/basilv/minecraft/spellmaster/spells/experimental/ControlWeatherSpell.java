package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class ControlWeatherSpell extends Spell {

	public ControlWeatherSpell() {
		super("Control Weather");
		setCastingMinimumLevel(5);
		setCastingFocus("Large Fern", ItemType.LargeFern); // TODO: Figure out proper component.
//		setCastingComponent(ItemType.Feather, "Feather", 2); // TODO: Consider using more exotic item? Lapis?
	}
	

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: level * 2 seconds", 
		   "Change the weather from sunny to raining, from raining to stormy, and from stormy to sunny."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.AMBIENCE_RAIN;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		World world = context.getWorld();
		int durationInSeconds  = context.getCastingLevel() * 2 + context.getSpellboost().getDurationInSeconds(); 
		
		if (!world.isRaining() && !world.isThundering()) {
			// Sunny - make it raining
			float rainStrength = 20;
			makeRaining(world, rainStrength, durationInSeconds);
			
		} else if (world.isRaining() && !world.isThundering()) {
			// Raining - make it stormy and rain harder
			float rainStrength = 50;
			makeRaining(world, rainStrength, durationInSeconds);
			float thunderStrength = 50;
			makeThundering(world, thunderStrength, durationInSeconds);
			
		} else {
			// Stormy - make it sunny
			world.setRaining(false);
			world.setThundering(false);
		}
		
	    return true;
	}

	private void makeThundering(World world, float thunderStrength, int durationInSeconds) {
		world.setThundering(true);
		world.setThunderTime(durationInSeconds);
		world.setThunderStrength(thunderStrength);
	}

	private void makeRaining(World world, float rainStrength, int durationInSeconds) {
		world.setRaining(true);
		world.setRainTime((int)MinecraftUtils.secondsToTicks(durationInSeconds));
		world.setRainStrength(rainStrength);
	}
}
