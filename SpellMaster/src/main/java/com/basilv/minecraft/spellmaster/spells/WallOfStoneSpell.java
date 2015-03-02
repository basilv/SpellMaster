package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicComponent;
import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.EarthMagicTome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class WallOfStoneSpell extends EarthMagicTome.EarthSpell {

	public WallOfStoneSpell() {
		super("Wall of Stone");
		setCastingMinimumLevel(3);
		setCastingFocus("Stone Shovel", ItemType.StoneSpade); 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Component: 1 cobblestone for each block created in the wall.",
			"Range: 1 square + 1 / 4 levels.",
			"Conjure a permanent stone wall directly in front of you, extending to each side up to the maximum range. "
			+ "The wall is conjured out of thin air and does not affect existing blocks. "
			+ "The wall extends from one block below you to a height of one block higher than you. "
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.ANVIL_LAND;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int playerCobbleCount = MinecraftUtils.countItemsOfType(player, ItemType.Cobble);
		int cobbleRemainingCount = playerCobbleCount;
		int maxRange = context.getCastingRange(1, 4);

		Position positionAdjustmentFacing = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position bottomCenterOfWallPosition = new Position(player.getPosition());
		bottomCenterOfWallPosition.move(positionAdjustmentFacing.getBlockX(), -1, positionAdjustmentFacing.getBlockZ());

		World world = context.getWorld();
		cobbleRemainingCount = conjureWallSegment(world, bottomCenterOfWallPosition, cobbleRemainingCount);

		Position positionAdjustmentLeftSide = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);
		Position bottomLeftPosition = new Position(bottomCenterOfWallPosition);
		Position bottomRightPosition = new Position(bottomCenterOfWallPosition);
		for (int offset = 1; offset <= maxRange; offset++) {
			bottomLeftPosition.move(positionAdjustmentLeftSide.getBlockX(), 0, positionAdjustmentLeftSide.getBlockZ());
			bottomRightPosition.move(positionAdjustmentLeftSide.getBlockX() * -1, 0, positionAdjustmentLeftSide.getBlockZ() * -1);
			
			cobbleRemainingCount = conjureWallSegment(world, bottomLeftPosition, cobbleRemainingCount);
			cobbleRemainingCount = conjureWallSegment(world, bottomRightPosition, cobbleRemainingCount);
			if (cobbleRemainingCount == 0) {
				break;
			}
		}
		
		int cobbleConsumedCount = playerCobbleCount - cobbleRemainingCount;
		if (cobbleConsumedCount > 0) {
			MagicComponent component = new MagicComponent("Cobblestone", ItemType.Cobble, cobbleConsumedCount);
			component.consumeForUse(player);
			return true;
		} else {
			return false;
		}
	}

	private int conjureWallSegment(World world, Position bottomPosition, int cobbleRemainingCount) {
		
		for (int verticalAdjustment = 0; verticalAdjustment < 4; verticalAdjustment++) {
			Position position = new Position(bottomPosition);
			position.moveY(verticalAdjustment);
			Block block = world.getBlockAt(position);
			if (block.getType().equals(BlockType.Air)) {
				if (cobbleRemainingCount == 0) {
					break;
				}
				world.setBlockAt(position, BlockType.Stone);
				cobbleRemainingCount--;
			}
		}
		return cobbleRemainingCount;
	}
	
}
