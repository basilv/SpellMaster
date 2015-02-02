package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class WaterMagicTome extends Tome {

	public WaterMagicTome() {
		super("Water Magic", 1);
		setCeremonyFocus("Water bucket", ItemType.WaterBucket);
		setSpellBoost(new SpellBoost(0, 0, 0, 1));
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Learn the ways of water.", "", "Boost: +1 second duration"); 
	}

	@Override
	public String getCeremonyName() {
		return "Water Wizardry";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: You must be surrounded by water on all sides, from well below your feet to well above your head."
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 1;
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 1;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {
		if (!MinecraftUtils.isPlayerSurroundedByTypeOfBlock(context.getPlayer(), BlockType.Water, 1, 2, 1)) {
			sendPlayerUnableToPerformCeremonyMessage(context.getPlayer(), "Must be surrounded by water.");
			return false;
		}
		return true;
	}

	
}
