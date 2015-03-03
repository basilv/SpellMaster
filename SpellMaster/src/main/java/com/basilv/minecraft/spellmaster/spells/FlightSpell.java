package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import com.basilv.minecraft.spellmaster.MagicContext;

public class FlightSpell extends AbstractFlightSpell {

	public FlightSpell() {
		super("Flight");
		setCastingMinimumLevel(20);
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: level - 15 seconds",
		   "Fly speed: 0.01 / 4 levels",
		   "Allows you to fly for a limited time. The spell ends without warning and you can take falling damage if still in the air.",
		   "Maintaining this spell requires that effort be spent throughout the duration."
		));
	}
	
	protected int getDurationInSeconds(MagicContext context) {
		return context.getCastingLevel() - 15 + context.getSpellboost().getDurationInSeconds();
	}

	protected float getFlightSpeed(MagicContext context) {
		// Fly speed of 0.02 at minimum casting level, which is much slower than walking speed.
		return (float) (0.01 * context.getCastingLevel() / 10.0);
	}

}
