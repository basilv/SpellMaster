package com.basilv.minecraft.spellmaster.tomes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.spells.DetectOreSpell;
import com.basilv.minecraft.spellmaster.spells.DragonsBreathSpell;
import com.basilv.minecraft.spellmaster.spells.FasterFlightSpell;
import com.basilv.minecraft.spellmaster.spells.GreaterTeleportSpell;

public class ArchmageTome extends Tome {

	public ArchmageTome() {
		super("Archmage Magic", 3);
		setCeremonyFocus("Book", ItemType.Book);
		setCeremonyComponent("Diamond Block", ItemType.DiamondBlock, 1);  
		setSpellBoost(new SpellBoost(2, 1, 1, 2, 1));
		
		addSpell(new GreaterTeleportSpell());
		// TODO: majestic mansion spell?

		// TODO: Move following spells to Elemental Master book 
		addSpell(new DetectOreSpell()); 
		addSpell(new FasterFlightSpell()); 
		addSpell(new DragonsBreathSpell()); 
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
			// TODO: Add Necromancy tome
			"Requirements: Use the focus with at least two of the following tomes in your inventory: Air Magic, Fire Magic. "
			+ "You must be standing on a diamond block surrounded by gold blocks on its four sides with lapis lazuli blocks at each of the corners of the diamond block. "
			+ "A block of glowstone must be placed above each lapis lazuli block with a gap of one block inbetween the two."
			// TODO: Cast X spells in Y seconds and then use focus again?
//			+ "After using the focus you must cast X different spells within Y seconds and then use the focus again." // TODO: Get this working.

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

		Position position = player.getPosition().copy();
		position.moveY(-1);
		World world = context.getWorld();
		Block centerBlock = world.getBlockAt(position);
		if (!centerBlock.getType().equals(BlockType.DiamondBlock)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be standing on a diamond block.");
			return false;
		}

		List<Block> blocksExpectedToBeGold = new ArrayList<>();
		blocksExpectedToBeGold.add(centerBlock.getRelative(1, 0, 0));
		blocksExpectedToBeGold.add(centerBlock.getRelative(-1, 0, 0));
		blocksExpectedToBeGold.add(centerBlock.getRelative(0, 0, 1));
		blocksExpectedToBeGold.add(centerBlock.getRelative(0, 0, -1));
		if (blocksExpectedToBeGold.stream().anyMatch(block -> !block.getType().equals(BlockType.GoldBlock))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must have gold blocks next to the horizontal faces of the diamond block.");
			return false;
		}
		
		List<Block> blocksExpectedToBeLapis = new ArrayList<>();
		blocksExpectedToBeLapis.add(centerBlock.getRelative(-1, 0, -1));
		blocksExpectedToBeLapis.add(centerBlock.getRelative(1, 0, -1));
		blocksExpectedToBeLapis.add(centerBlock.getRelative(-1, 0, 1));
		blocksExpectedToBeLapis.add(centerBlock.getRelative(1, 0, 1));
		if (blocksExpectedToBeLapis.stream().anyMatch(block -> !block.getType().equals(BlockType.LapisBlock))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must have lapis lazuli blocks diagonally next to the diamond block.");
			return false;
		}

		if (blocksExpectedToBeLapis.stream().map(block -> block.getRelative(0, 1, 0)).anyMatch(block -> !block.getType().equals(BlockType.Air))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must have a block of glowstone above each lapis lazuli block separated by a gap.");
			return false;
		}

		if (blocksExpectedToBeLapis.stream().map(block -> block.getRelative(0, 2, 0)).anyMatch(block -> !block.getType().equals(BlockType.GlowStone))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must have a block of glowstone above each lapis lazuli block separated by a gap.");
			return false;
		}

		
		// TODO: On success do announcement to world that new archmage 
		
		return true;		
	}
	
}
