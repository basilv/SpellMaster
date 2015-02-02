package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockFace;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class DecomposeTreeSpell extends Spell {

	public DecomposeTreeSpell() {
		super("Decompose Tree");
		setCastingMinimumLevel(1);
		setCastingFocus("Wooden axe", ItemType.WoodAxe); 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Decompose a tree into its parts. You must use the focus directly on a block of the tree. "
		   + "You can decompose a number of blocks up to your level * 2."
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.DIG_WOOD;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Block blockClicked = context.getBlockClicked();
		if (blockClicked == null || !isTreeBlock(blockClicked.getType())) {
			return false;
		}

		// TODO: Apply max range to block clicked?
		
		Item itemHeld = context.getItemHeld();
		int maxDamage = itemHeld.getBaseItem().getMaxDamage();
		int currentDamage = itemHeld.getDamage();
		int maxTreeBlocksDecomposed = context.getCastingLevel() * 2;
		int treeBlocksDecomposed = 0;

		LinkedList<Block> treeBlocks = new LinkedList<>();
		treeBlocks.add(blockClicked);
		
		BlockFace[] faces = { 
			BlockFace.BOTTOM, BlockFace.TOP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, 
		};

		while (!treeBlocks.isEmpty()) {
			Block currentBlock = treeBlocks.removeFirst();
			if (currentDamage >= maxDamage) {
				break;
			}
			currentDamage++;
			currentBlock.dropBlockAsItem(true);
			treeBlocksDecomposed++;
			if (treeBlocksDecomposed == maxTreeBlocksDecomposed) {
				break;
			}
			
			// Populate neighbors.  
			for (BlockFace face : faces) {
				Block block = currentBlock.getFacingBlock(face);
				// TODO: Add tree blocks first, reduce item damage for leaves or don't disintegrate leaves?
				if (isTreeBlock(block.getType())) {
					treeBlocks.add(block);
				}
			}
		}

		MinecraftUtils.setItemHeldDamage(context.getPlayer(), currentDamage);
		
		return (treeBlocksDecomposed > 0);
	}

	private static Set<BlockType> treeBlocks = new HashSet<>(Arrays.asList(
			BlockType.AcaciaLeaves, BlockType.AcaciaLog,
			BlockType.BirchLeaves, BlockType.BirchLog,
			BlockType.DarkOakLeaves, BlockType.DarkOakLog,
			BlockType.JungleLeaves, BlockType.JungleLog,
			BlockType.OakLeaves, BlockType.OakLog,
			BlockType.PineLeaves, BlockType.PineLog
		));

	private boolean isTreeBlock(BlockType type) {
		return treeBlocks.contains(type);
	}

}
