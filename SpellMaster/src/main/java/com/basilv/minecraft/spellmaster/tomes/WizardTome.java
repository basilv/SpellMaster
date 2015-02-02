package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;

public class WizardTome extends Tome {

	public WizardTome() {
		super("Wizard's Way", 2);
		setCeremonyFocus("Book", ItemType.Book);
		setCeremonyComponent("Iron Block", ItemType.IronBlock, 1);
		setSpellBoost(new SpellBoost(1, 0, 0, 0));
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Learn more powerful magic as a wizard."
			,"" , "Boost: +1 casting level"); 
	}

	@Override
	public String getCeremonyName() {
		return "Way of the Wizard";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: Use the focus while standing on an iron block "
			+ "with at least two of the following tomes in your inventory: Earth Magic, Nature Magic, Water Magic. "
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 10;
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 3;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {

		int tomesPossessed = context.countTomesPlayerHas(EarthMagicTome.class, NatureMagicTome.class, WaterMagicTome.class);
		Player player = context.getPlayer();
		if (tomesPossessed < 2) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must possess required tomes.");
			return false;
		}

		Position position = new Position(player.getPosition());
		position.moveY(-1);
		Block block = context.getWorld().getBlockAt(position);
		if (!block.getType().equals(BlockType.IronBlock)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be standing on an iron block.");
			return false;
		}

		return true;		
	}

	
}
