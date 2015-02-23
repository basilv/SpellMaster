package com.basilv.minecraft.spellmaster.spells;

import java.util.ArrayList;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

class TemporaryWallHelper {

	private BlockType wallBlockType;

	public TemporaryWallHelper(BlockType wallBlockType) {
		this.wallBlockType = wallBlockType;
	}

	/**
	 * Create a temporary wall with the bottom level below the level of the player permanent.
	 * Wall is created centered directly in front of player going off to each side. 
	 * @param context
	 * @param wallLengthFromPlayer Length of the wall on each side of the player.
	 * @param wallDurationInSeconds
	 */
	void createTemporaryWall(MagicContext context, int wallLengthFromPlayer, int wallDurationInSeconds) {
		
		Player player = context.getPlayer();
		Position positionAdjustmentFacing = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position bottomCenterOfWallPosition = new Position(player.getPosition());
		bottomCenterOfWallPosition.move(positionAdjustmentFacing.getBlockX(), -1, positionAdjustmentFacing.getBlockZ());

		World world = context.getWorld();
		List<Position> blockPositionsToRevert = new ArrayList<>();
		
		conjureWallSegment(world, bottomCenterOfWallPosition, blockPositionsToRevert);

		Position positionAdjustmentLeftSide = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);
		Position bottomLeftPosition = new Position(bottomCenterOfWallPosition);
		Position bottomRightPosition = new Position(bottomCenterOfWallPosition);
		for (int offset = 1; offset <= wallLengthFromPlayer; offset++) {
			bottomLeftPosition.move(positionAdjustmentLeftSide.getBlockX(), 0, positionAdjustmentLeftSide.getBlockZ());
			bottomRightPosition.move(positionAdjustmentLeftSide.getBlockX() * -1, 0, positionAdjustmentLeftSide.getBlockZ() * -1);
			
			conjureWallSegment(world, bottomLeftPosition, blockPositionsToRevert);
			conjureWallSegment(world, bottomRightPosition, blockPositionsToRevert);
		}
		
		EndWallTask task = new EndWallTask(context.getWorld(), blockPositionsToRevert, wallDurationInSeconds);
	    Canary.getServer().addSynchronousTask(task);
	}

	private void conjureWallSegment(World world, Position bottomPosition, List<Position> blockPositionsToRevert) {

		for (int verticalAdjustment = 0; verticalAdjustment < 3; verticalAdjustment++) {
			Position position = new Position(bottomPosition);
			position.moveY(verticalAdjustment);
			Block block = world.getBlockAt(position);
			if (block.getType().equals(BlockType.Air)) {
				world.setBlockAt(position, wallBlockType);
				if (verticalAdjustment > 0) {
					blockPositionsToRevert.add(new Position(position));
				}
			}
		}
	}

	private class EndWallTask extends OneTimeServerTask {

		private World world;
		private List<Position> positions;

	    public EndWallTask(World world, List<Position> positions, long durationInSeconds) {
			super(MinecraftUtils.secondsToTicks(durationInSeconds));
			this.world = world;
			this.positions = positions;
		}

	    public void doTask() {
	    	for (Position position : positions) {
	    		Block block = world.getBlockAt(position);
				if (block.getType().equals(wallBlockType)) {
	    			world.setBlockAt(position, BlockType.Air);
	    		}
	    	}

	    	// After all blocks are converted to air, send updates to deal with any flowing blocks outside the original wall blocks.
	    	for (Position position : positions) {
	    		Block block = world.getBlockAt(position);
				block.update();
				MinecraftUtils.updateRelativesOfBlock(block);
	    	}

	    	Canary.getServer().removeSynchronousTask(this);
	    }

	}

}
