package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class AirMagicTome extends Tome {

	public AirMagicTome() {
		super("Air Magic", 2);
		setCeremonyFocus("Feather", ItemType.Feather); 
		setCeremonyComponent("Feather", ItemType.Feather, 32);
		setSpellBoost(new SpellBoost(0, 0, 1, 0));
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Become one with the air."
			, "", "Boost: +1 range"); 
	}

	@Override
	public String getCeremonyName() {
		return "Air Ally";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: Use the focus while falling. You must be surrounded by nothing but air from well below your feet to well above your head." 
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 10;
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 2;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {

		Player player = context.getPlayer();
		if (!MinecraftUtils.isPlayerSurroundedByTypeOfBlock(player, BlockType.Air, 1, 2, 1)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be surrounded by air.");
			return false;
		}
		return true;
	}

	
}
