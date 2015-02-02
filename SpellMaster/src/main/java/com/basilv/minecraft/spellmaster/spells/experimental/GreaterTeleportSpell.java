package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;
import net.canarymod.api.world.position.Vector3D;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class GreaterTeleportSpell extends Spell {

	public GreaterTeleportSpell() {
		super("Greater Teleport");
		setCastingMinimumLevel(0); // TODO: FIX 
		setCastingFocus("Diamond boots", ItemType.DiamondBoots); // TODO: Figure out focus, components 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: level * 20 squares - 300", 
		   "Teleport horizontally in the direction you are facing. If your destination is solid earth, you will be redirected up to the first open space."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.ENDERMAN_PORTAL;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int range = context.getCastingRange(-300, 0.05);
		range = 300; // TODO: REMOVE
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		positionAdjustment.setX(positionAdjustment.getBlockX() * range);
		positionAdjustment.setZ(positionAdjustment.getBlockZ() * range);
		Position position = new Position(player.getPosition());
		position.move(positionAdjustment.getBlockX(), 0, positionAdjustment.getBlockZ());
		boolean destinationSolid = true;
		World world = context.getWorld();
		int yAdjustment = 0;
		do {
			boolean posOneSolid = isSolid(world.getBlockAt(position));
			position.moveY(+1);
			boolean posTwoSolid = isSolid(world.getBlockAt(position));
			// Check level above head due to issues using player.translate() in straight tunnels
			position.moveY(+1);
			boolean posThreeSolid = isSolid(world.getBlockAt(position));
			
			if (!posOneSolid && !posTwoSolid && !posThreeSolid) {
				destinationSolid = false;
			} else {
				yAdjustment ++;
				position.moveY(-1);
			}
			
		} while (destinationSolid);
		// Issue with player.teleportTo is that post-teleport, player facing is always SOUTH and can't seem to be changed. 
		// TeleportTo will also put player into a solid block, while translate won't.
		// Translate works with the correct facing but refuses to teleport into a solid block
		// TODO: Some issues observed with using translate in straight tunnels - sometimes need a third block above clear (air) at the target square and jump before teleporting.
		// Adding a small y adjustment up doesn't help with the need to jump.
		Vector3D vector = new Vector3D(positionAdjustment.getBlockX(), yAdjustment, positionAdjustment.getBlockZ());
		player.translate(vector);
		
	    return true;
	}
	
	private boolean isSolid(Block block) {
		
		return (block.getBlockBase().isCollidable());
	}
	
}
