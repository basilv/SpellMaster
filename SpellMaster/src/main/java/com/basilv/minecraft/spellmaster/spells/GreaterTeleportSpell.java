package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class GreaterTeleportSpell extends AbstractTeleportSpell {

	public GreaterTeleportSpell() {
		super("Greater Teleport");
		setCastingMinimumLevel(30);  
		setCastingFocus("Diamond, gold, or iron boots", ItemType.DiamondBoots, ItemType.GoldBoots, ItemType.IronBoots);  
	}

	@Override
	protected float getCastingExhaustionCost() {
		return 8;
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: varies depending on the focus used:",
		   "Iron boots: 1 block per level",
		   "Gold boots: 1 chunk per 4 levels",
		   "Diamond boots: level - 15 chunks (1 chunk = 16 blocks)",
		   "Costs more health and is twice as exhausting than normal.",
		   "Teleport horizontally in the direction you are facing after a slight delay. "
		   + "If your destination is solid earth, you will be redirected up to the first open space. "
		   + "If your destination is air underneath, you will be redirected down to on top of the first non-air space. "
		   + "Teleporting into a narrow space can cause a little damage. "
		));
	}
	
	@Override
	protected Position getTargetPosition(MagicContext context) {
		Player player = context.getPlayer();
		int rangeInBlocks = getRangeInBlocks(context);
		logger.info("Range in blocks = " + rangeInBlocks);
		Position position = getInitialTargetPosition(player, rangeInBlocks);
		World world = context.getWorld();
		while(!canPlayerTeleportToPosition(world, position)) {
			position.moveY(+1);
			if (position.getBlockY() > 255) {
				// At maximum height so abort teleport
				player.chat("Destination is occupied by solid material.");
				return null;
			}
		}

		// Adjust downwards if in open air.
		while (isPositionUnderneathAir(world, position)) {
			position.moveY(-1);
		}
		
		return position;
	}

	private int getRangeInBlocks(MagicContext context) {
		ItemType itemType = context.getItemHeld().getType();
		if (ItemType.IronBoots.equals(itemType)) {
			return context.getCastingRange(0, 1);
		} else if (ItemType.GoldBoots.equals(itemType)) {
			return MinecraftUtils.chunksToBlocks(context.getCastingRange(0, 4));
		} else {
			return MinecraftUtils.chunksToBlocks(context.getCastingRange(-15, 1));
		}
	}

	private boolean isPositionUnderneathAir(World world, Position positionToCheck) {
		Block blockToCheck = world.getBlockAt(positionToCheck).getRelative(0, -1, 0);
		return blockToCheck.isAir();
	}

}
