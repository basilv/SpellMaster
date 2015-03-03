package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import com.basilv.minecraft.spellmaster.MagicContext;

public class FasterFlightSpell extends AbstractFlightSpell {

	public FasterFlightSpell() {
		super("Faster Flight");
		setCastingMinimumLevel(25);
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: level * 2 - 30 seconds",
		   "Fly speed: 0.01 per 5 levels",
		   "Allows you to fly for a limited time. The spell ends without warning and you can take falling damage if still in the air.",
		   "Maintaining this spell requires that effort be spent throughout the duration."
		));
	}
	
	protected int getDurationInSeconds(MagicContext context) {
		return context.getCastingLevel() * 2 - 30 + context.getSpellboost().getDurationInSeconds();
	}

	protected float getFlightSpeed(MagicContext context) {
		// Default fly speed is 0.05, which is used at minimum casting level
		return (float) (0.01 * context.getCastingLevel() / 5.0);
	}

}
