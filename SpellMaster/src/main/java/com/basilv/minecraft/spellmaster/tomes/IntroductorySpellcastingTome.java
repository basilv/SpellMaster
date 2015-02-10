package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.inventory.ItemType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Tome;

public class IntroductorySpellcastingTome extends Tome {

	public static final String NAME = "Introductory Spellcasting";
	
	public IntroductorySpellcastingTome() {
		super(NAME, 0);
		setCeremonyFocus("Paper", ItemType.Paper);
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Become an apprentice spellcaster.", 
				"", "If you lose this tome you can resummon it by using a piece of paper as a focus.", 
				"", "Learning Spells", "",
				"You must first acquire a magic tome through the proper ceremony before you can cast the spells it contains. "
				+ "Each ceremony has a specific set of requirements / conditions that must be met. "
				+ "One common requirement is a focus: holding a particular item and using it. "
				+ "You must also be a minimum level to learn a spell. Many ceremonies also require additional components - " 
				+ "items within your inventory that are typically consumed by the ceremony. "			
				+ "Each ceremony also costs a certain number of levels to perform.", 
				"", "Spellcasting Basics", "",
				"Each spell has a minimum level below which you will be unable to cast the spell. "
				+ "To cast a spell requires a focus: holding a particular item and using it (e.g. a right-click). "
				+ "Many spells require one or more components in your inventory that are consumed each time the spell is cast. "
				+ "Every spell inflicts a cost when cast. Most spells cost one health and four points of exhaustion (roughly two points of hunger). "
				+ "Many aspects of a spell such as the range over which it operates or the damage dealt increase with your level.",
				"", "Magic Tomes", "",
				"Tomes can contain any combination of the following:",
				"Spell: Information on how to cast a particular spell. The spellbook must be in your inventory in order to cast the spell. ",
				"Ceremony: Information on how to perform a ceremony to obtain a different tome. ",
				"Boost: A bonus (increase) to some aspect of your spellcasting ability, such as your effective level for casting or the spell's range."
		);

	}

	@Override
	public String getCeremonyName() {
		return "Apprenticeship";
	}

	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		// Do nothing
	}

	@Override
	protected int getCeremonyMinimumLevel() {
		return 0;
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 0;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {
		return true;
	}

}
