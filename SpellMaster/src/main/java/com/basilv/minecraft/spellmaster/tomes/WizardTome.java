package com.basilv.minecraft.spellmaster.tomes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.spells.MagicMissileSpell;
import com.basilv.minecraft.spellmaster.spells.TeleportSpell;

public class WizardTome extends Tome {

	public WizardTome() {
		super("Wizard Magic", 2); 
		setCeremonyFocus("Book", ItemType.Book); 
		setCeremonyComponent("Lapis lazuli block", ItemType.LapisBlock, 1); 
		setSpellBoost(new SpellBoost(1, 0, 0, 0, 0));
		
		// TODO: Experimental spells
		// use armor for different effects: chestplate - invulnerability, boots - teleport, etc.

		addSpell(new TeleportSpell());
		addSpell(new MagicMissileSpell());

		addTome(new AirMagicTome());
		addTome(new FireMagicTome());
		// TODO: Necromancy tome 
		addTome(new ArchmageTome());
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
			"Requirements: Use the focus with at least two of the following tomes in your inventory: Earth Magic, Nature Magic, Water Magic. "
			+ "You must be standing on a gold block surrounded by iron blocks on its four sides and with redstone blocks at each of the corners of the gold block. "
			+ "The space above all of these blocks must be completely clear (i.e. air) all the way to the sky. "
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

		long tomesPossessed = context.countTomesPlayerHas(EarthMagicTome.class, NatureMagicTome.class, WaterMagicTome.class);
		Player player = context.getPlayer();
		if (tomesPossessed < 2) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must possess required tomes.");
			return false;
		}

		Position position = player.getPosition().copy();
		position.moveY(-1);
		World world = context.getWorld();
		Block centerBlock = world.getBlockAt(position);
		if (!centerBlock.getType().equals(BlockType.GoldBlock)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be standing on a gold block.");
			return false;
		}
		
		List<Block> blocksExpectedToBeIron = new ArrayList<>();
		blocksExpectedToBeIron.add(centerBlock.getRelative(1, 0, 0));
		blocksExpectedToBeIron.add(centerBlock.getRelative(-1, 0, 0));
		blocksExpectedToBeIron.add(centerBlock.getRelative(0, 0, 1));
		blocksExpectedToBeIron.add(centerBlock.getRelative(0, 0, -1));
		if (blocksExpectedToBeIron.stream().anyMatch(block -> !block.getType().equals(BlockType.IronBlock))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must have iron blocks next to the horizontal faces of the gold block.");
			return false;
		}
		
		List<Block> blocksExpectedToBeRedstone = new ArrayList<>();
		blocksExpectedToBeRedstone.add(centerBlock.getRelative(-1, 0, -1));
		blocksExpectedToBeRedstone.add(centerBlock.getRelative(1, 0, -1));
		blocksExpectedToBeRedstone.add(centerBlock.getRelative(-1, 0, 1));
		blocksExpectedToBeRedstone.add(centerBlock.getRelative(1, 0, 1));
		if (blocksExpectedToBeRedstone.stream().anyMatch(block -> !block.getType().equals(BlockType.RedstoneBlock))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must have redstone blocks diagonally next to the gold block.");
			return false;
		}
		
		List<Block> blocksExpectedToBeOpenToSky = new ArrayList<>();
		blocksExpectedToBeOpenToSky.add(centerBlock);
		blocksExpectedToBeOpenToSky.addAll(blocksExpectedToBeIron);
		blocksExpectedToBeOpenToSky.addAll(blocksExpectedToBeRedstone);
		
		if (blocksExpectedToBeOpenToSky.stream().anyMatch(block -> !isClearFromBlockToSky(world, block))) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be clear from the blocks to the sky.");
			return false;
		}

		blocksExpectedToBeOpenToSky.stream().forEach(block -> spawnParticleAbove(context, block));

		return true;		
	}

	private boolean isClearFromBlockToSky(World world, Block block) {
		int highestBlockAt = world.getHighestBlockAt(block.getX(), block.getZ());
		// API appears to return air block directly above the highest non-air block so need to add 1.
		return highestBlockAt == block.getY() + 1;
	}

	private void spawnParticleAbove(MagicContext context, Block block) {
		Location location = block.getRelative(0, 1, 0).getLocation();
		context.spawnParticle(location, Particle.Type.SPELL);
	}
	
}
