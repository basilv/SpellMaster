package com.basilv.minecraft.spellmaster.spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.effects.SoundEffect.Type;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.WaterMagicTome.WaterSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

public class AcidErosionSpell extends WaterSpell {

	public AcidErosionSpell() {
		super("Acid Erosion");
		setCastingMinimumLevel(3);
		setCastingFocus("Ink Sack", ItemType.InkSack);
		setCastingComponent("Ink Sack", ItemType.InkSack, 1); 
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 2 squares + 1 per 3 levels", 
		   "Conjure a powerful acid that eats away at blocks in a downward staircase direction starting with the block in front of you. "
		   + "The subsequent direction of erosion is somewhat random."
		   + "The range specifies the number of squares downwards that the spell erodes. "
		   + "The spell ends if it encounters a material it cannot erode."
		));
	}
	
	@Override
	protected Type getSpellSoundEffect() {
		return SoundEffect.Type.FIZZ;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position position = new Position(player.getPosition()); 

		List<BlockType> allowedBlockTypes = Arrays.asList(BlockType.Andesite, BlockType.Clay, BlockType.CoalOre, BlockType.CoarseDirt,
			BlockType.Diorite, BlockType.Dirt, BlockType.Granite, BlockType.Gravel, BlockType.HardenedClay, BlockType.Netherrack, BlockType.Podzol,
			BlockType.Sand, BlockType.Sandstone, BlockType.SoulSand, BlockType.Stone, BlockType.Air, BlockType.Torch);
		World world = context.getWorld();

		int maxRange = 2 + context.getCastingLevel() / 3 + context.getSpellboost().getRange();

		List<Position> waterPositions = new ArrayList<>();
		boolean stop = false;
		for (int rangeIndex = 0; rangeIndex < maxRange; rangeIndex++) {
			position.move(positionAdjustment.getBlockX(), +2, positionAdjustment.getBlockZ()); 
		
			// Delete vertical column of blocks
			for (int columnIndex = 3; columnIndex > 0; columnIndex--) {
				position.moveY(-1);
				Block block = world.getBlockAt(position);
				if (block.getBlockMaterial().isLiquid()) {
					stop = true;
					break;
				}
				if (!allowedBlockTypes.contains(block.getType())) {
					stop = true;
					break;
				}
				BlockType blockType = BlockType.Air;
				if (rangeIndex > 1 || (rangeIndex == 1 && columnIndex < 3) || rangeIndex ==0 && columnIndex < 2) {
					blockType = BlockType.Water;
					waterPositions.add(new Position(position));
				}
				world.setBlockAt(position, blockType);
			}

			if (stop) {
				break;
			}
			
			// Randomly set a new direction.
			if (rangeIndex % 2 == 1) {
				switch (randomNumberWithinRange(1, 3)) {
				case 1:
					positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);
					break;
				case 2:
					positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
					break;
				case 3:
					positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);
					positionAdjustment.setX(positionAdjustment.getBlockX() * -1);
					positionAdjustment.setZ(positionAdjustment.getBlockZ() * -1);
					break;
				}
			}
		}
		
	    Canary.getServer().addSynchronousTask(new EndWaterTask(world, waterPositions));
		
		return true;
	}

	private static class EndWaterTask extends OneTimeServerTask {

		private World world;
		private List<Position> waterPositions;
		
	    public EndWaterTask(World world,
				List<Position> waterPositions) {
			super(MinecraftUtils.secondsToTicks(1));
			this.world = world;
			this.waterPositions = waterPositions;
		}

	    public void doTask() {
	    	waterPositions.stream().forEach(position -> world.setBlockAt(position, BlockType.Air));
	    }
	}
	
}
