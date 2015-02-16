package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;

public class DetectOreSpell extends AbstractFindOreSpell {

	public DetectOreSpell() {
		super("Detect Ore");
		setCastingMinimumLevel(30);
		
		registerCommonOres();
		registerOre(ItemType.EmeraldBlock, BlockType.EmeraldOre);
		registerOre(ItemType.GoldBlock, BlockType.GoldOre);
		registerOre(ItemType.DiamondBlock, BlockType.DiamondOre);

		// Do last after all ores are registered.
		initializeCastingFocus();
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 1 square per 2 levels", 
		   "Use the focus without placing the block. You will sense the direction to the nearest ore corresponding to the material you are using as a focus "
		   + "if it is within the spell's range. "
		));
	}

	@Override
	protected void sendOreMessage(Player player, int xDiff, int yDiff, int zDiff) {
		sendOreClosenessMessage(player, xDiff, yDiff, zDiff);
		sendOreDirectionMessage(player, xDiff, yDiff, zDiff);
	}

	@Override
	protected String getPastTenseActionVerb() {
		return "detected";
	}

}
