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
import com.basilv.minecraft.spellmaster.spells.experimental.GreaterTeleportSpell;

public class ArchmageTome extends Tome {

	public ArchmageTome() {
		super("Archmage Magic", 3);
		setCeremonyFocus("Book", ItemType.Book);
		setCeremonyComponent("Diamond Block", ItemType.DiamondBlock, 1); 
		setSpellBoost(new SpellBoost(2, 1, 1, 2, 1));
		
		addSpell(new GreaterTeleportSpell());
		// TODO: majestic mansion spell?
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Gain the powers of an archmage."
			,"" , "Boost: +2 casting level, +1 damage, +1 range, +2 second duration, -1 exhaustion"); 
	}

	@Override
	public String getCeremonyName() {
		return "Archmage Ascendancy";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: Use the focus while standing on a diamond block " 
			+ "with at least two of the following tomes in your inventory: Air Magic, Fire Magic, Necromancy Magic. "
			// TODO: Added challenge: diamond surrounded by gold??? Something else? Lapis (something not found by sense ore spell?)
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 30; 
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 5;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {

		// TODO: Add Necromancy Tome.
		long tomesPossessed = context.countTomesPlayerHas(AirMagicTome.class, FireMagicTome.class);
		Player player = context.getPlayer();
		if (tomesPossessed < 2) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must possess required tomes.");
			return false;
		}

		// TODO: Change ceremony as per description
		Position position = new Position(player.getPosition());
		position.moveY(-1);
		Block block = context.getWorld().getBlockAt(position);
		if (!block.getType().equals(BlockType.DiamondBlock)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be standing on a diamond block.");
			return false;
		}

		// TODO: On success do announcement to world that new archmage 
		
		return true;		
	}
	
}
