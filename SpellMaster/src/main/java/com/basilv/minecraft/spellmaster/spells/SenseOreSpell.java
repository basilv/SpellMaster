package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;

public class SenseOreSpell extends AbstractFindOreSpell {

	public SenseOreSpell() {
		super("Sense Ore");
		setCastingMinimumLevel(5);
		registerCommonOres();
		initializeCastingFocus();
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 1 square per 2 levels", 
		   "Use the focus without placing the block. You will sense the direction to the nearest ore corresponding to the material you are using as a focus "
		   + "if it is within the spell's range. "
		   + "This spell cannot sense emerald, gold, or diamond."
		));
	}

	@Override
	protected void sendOreMessage(Player player, int xDiff, int yDiff, int zDiff) {
		sendOreClosenessMessage(player, xDiff, yDiff, zDiff);
	}

	@Override
	protected String getPastTenseActionVerb() {
		return "sensed";
	}

}
