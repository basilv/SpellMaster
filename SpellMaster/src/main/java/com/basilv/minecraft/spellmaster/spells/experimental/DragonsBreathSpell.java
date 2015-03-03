package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.effects.SoundEffect.Type;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.FireMagicTome.FireSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class DragonsBreathSpell extends FireSpell {

	public DragonsBreathSpell() {
		super("Dragon's Breath");
		setCastingMinimumLevel(30);
		setCastingFocus("Blaze Powder", ItemType.BlazePowder);
//		setCastingComponent("Blaze powder", ItemType.BlazePowder, 1); // TODO: Enable
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 2 squares + 1 per 4 levels", // TODO: range, damage, duration
		   "Breathe fire in a cone extending in the direction you are facing. "
		));
	}
	
	@Override
	protected Type getSpellSoundEffect() {
		return SoundEffect.Type.ENDERDRAGON_GROWL;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		// Calculate direction to breathe fire in
		Position positionFacingAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position positionLeftSideAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);

		int coneWidth = 1;
		Position currentPosition = player.getPosition().copy();
		int range = 10;
		World world = context.getWorld();
		for (int i = 0; i < range; i++) {
			currentPosition.move(positionFacingAdjustment.getBlockX(), 0, positionFacingAdjustment.getBlockZ());
			Block block = world.getBlockAt(currentPosition);
			if (block.isAir()) {
				block.setType(BlockType.FireBlock);
				block.update();
				// Keep track of which blocks had fire breathed at in order to do a later check for entities located at the same positions to deal damage to them?
				// 
//				breathFireAt(currentPosition);
			}
			// TODO: Expand cone in all directions, stopping in a given direction if hit solid block?  
			// If solid block then it will block breathe
//			breathFireAt(currentPosition);
		
		}

		return true;
	}

}
