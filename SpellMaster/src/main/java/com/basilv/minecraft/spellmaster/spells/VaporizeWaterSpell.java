package com.basilv.minecraft.spellmaster.spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.canarymod.BlockIterator;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.effects.SoundEffect.Type;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.FireMagicTome.FireSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class VaporizeWaterSpell extends FireSpell {

	public VaporizeWaterSpell() {
		super("Vaporize Water");
		setCastingMinimumLevel(10); 
		setCastingFocus("Coal", ItemType.Coal); 
		setCastingComponent("Sand", ItemType.Sand, 1); 
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Point your focus at water and use it to vaporize the block you are pointing at and nearby water. "
			+ "You can vaporize up to a maximum number of blocks equal to your casting level."
		));
	}

	
	@Override
	protected Type getSpellSoundEffect() {
		return SoundEffect.Type.FIZZ;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		
		BlockIterator blockIterator = new BlockIterator(player, false);
		if (!blockIterator.hasNext()) {
			return false;
		}
		
		Block initialBlock = blockIterator.next();
		if (!isWaterBlock(initialBlock)) {
			return false;
		}
		
		LinkedList<Block> blocksToVaporize = new LinkedList<>();
		blocksToVaporize.add(initialBlock);
		int blocksRemaining = context.getCastingLevel();
		List<Position> positionsToUpdate = new ArrayList<>();
		World world = context.getWorld();
		while (!blocksToVaporize.isEmpty()) {
			Block currentBlock = blocksToVaporize.removeFirst();
			// Perform what might be a duplicate check to avoid infinite loops when neighbor of neighbor ends up being a loop.
			if (!isWaterBlock(currentBlock)) {
				continue;
			}
			Position currentPosition = currentBlock.getPosition();
			positionsToUpdate.add(currentPosition);
			world.setBlockAt(currentPosition, BlockType.Air);
			context.spawnParticle(currentBlock.getLocation(), Particle.Type.CLOUD);
			blocksRemaining--;
			if (blocksRemaining == 0) {
				break;
			}
			
			blocksToVaporize.add(currentBlock.getRelative(0, 1, 0));
			blocksToVaporize.add(currentBlock.getRelative(0, -1, 0));
			blocksToVaporize.add(currentBlock.getRelative(1, 0, 0));
			blocksToVaporize.add(currentBlock.getRelative(-1, 0, 0));
			blocksToVaporize.add(currentBlock.getRelative(0, 0, 1));
			blocksToVaporize.add(currentBlock.getRelative(0, 0, -1));
		}

		positionsToUpdate.stream().forEach(position -> {
			Block block = world.getBlockAt(position);
			block.update();
			// This does not work perfectly - blocks are still left that are not properly updated.
			MinecraftUtils.updateRelativesOfBlock(block);
		});
		
		return true;
	}

	private boolean isWaterBlock(Block initialBlock) {
		return initialBlock.getType().equals(BlockType.Water) || initialBlock.getType().equals(BlockType.WaterFlowing);
	}

}
