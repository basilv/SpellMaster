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
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

public class WallOfWaterSpell extends Spell {

	public WallOfWaterSpell() {
		super("Wall of Water");
		setCastingMinimumLevel(1); 
		setCastingFocus("Wooden Shovel", ItemType.WoodSpade); 
		// No component: spell isn't that powerful so casting it over and over is okay.
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Range: 1 square + 1 / 5 levels.",
			"Duration: 5 + level * 2 seconds",
			"Conjure a wall of water directly in front of you, extending to each side up to the maximum range. "
			+ "The wall is conjured out of thin air and does not affect existing blocks. "
			+ "The wall is three blocks high, starting from the level that you are standing on top of."
			+ "The bottom level of the wall is permanent, while the upper two levels last only for the duration of the spell."
		));
	}

	
	@Override
	protected Type getSpellSoundEffect() {
		return SoundEffect.Type.WATER;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int maxRange = context.getCastingRange(1, 5);

		Position positionAdjustmentFacing = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position bottomCenterOfWallPosition = new Position(player.getPosition());
		bottomCenterOfWallPosition.move(positionAdjustmentFacing.getBlockX(), -1, positionAdjustmentFacing.getBlockZ());

		World world = context.getWorld();
		List<Position> blockPositionsToRevert = new ArrayList<>();
		
		conjureWallSegment(world, bottomCenterOfWallPosition, blockPositionsToRevert);

		Position positionAdjustmentLeftSide = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);
		Position bottomLeftPosition = new Position(bottomCenterOfWallPosition);
		Position bottomRightPosition = new Position(bottomCenterOfWallPosition);
		for (int offset = 1; offset <= maxRange; offset++) {
			bottomLeftPosition.move(positionAdjustmentLeftSide.getBlockX(), 0, positionAdjustmentLeftSide.getBlockZ());
			bottomRightPosition.move(positionAdjustmentLeftSide.getBlockX() * -1, 0, positionAdjustmentLeftSide.getBlockZ() * -1);
			
			conjureWallSegment(world, bottomLeftPosition, blockPositionsToRevert);
			conjureWallSegment(world, bottomRightPosition, blockPositionsToRevert);
		}
		
		long durationInSeconds = 5 + context.getCastingLevel() * 2 + context.getSpellboost().getDurationInSeconds();
		EndWallTask task = new EndWallTask(context.getWorld(), blockPositionsToRevert, durationInSeconds);
	    Canary.getServer().addSynchronousTask(task);
		
		return true;
	}

	private void conjureWallSegment(World world, Position bottomPosition, List<Position> blockPositionsToRevert) {

		for (int verticalAdjustment = 0; verticalAdjustment < 3; verticalAdjustment++) {
			Position position = new Position(bottomPosition);
			position.moveY(verticalAdjustment);
			Block block = world.getBlockAt(position);
			if (block.getType().equals(BlockType.Air)) {
				world.setBlockAt(position, BlockType.Water);
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
	    		if (world.getBlockAt(position).getType().equals(BlockType.Water)) {
	    			world.setBlockAt(position, BlockType.Air);
	    		}
	    	}
	        Canary.getServer().removeSynchronousTask(this);
	    }
	}
	
}
