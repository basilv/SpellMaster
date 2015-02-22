package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.EarthMagicTome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

@SuppressWarnings("deprecation") // For deprecated Canary item types
public class DisintegrateEarthSpell extends EarthMagicTome.EarthSpell {

	public DisintegrateEarthSpell() {
		super("Disintegrate Earth");
		setCastingMinimumLevel(1);
		setCastingFocus("Stone pickaxe", ItemType.StonePickaxe);
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 2 squares + 1 per 4 levels",
		   "Remove a horizontal tunnel 2 blocks high in the direction you are facing. "
		   + "To disintegrate each block requires having the matching dropped item in your inventory. "
		));
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		// Calculate direction to disintegrate in
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		if (positionAdjustment.getBlockX() != 0 && positionAdjustment.getBlockZ() != 0) {
			// On a diagonal this spell is not allowed
			return false;
		}

		Position disintegratePosition = new Position(player.getPosition());
		
		// Disintegrate range: minimum 2 at level 0, up to 9 at level 30.
		int disintegrateRange = context.getCastingRange(2, 4);
		Item itemHeld = player.getItemHeld();
		int maxDamage = itemHeld.getBaseItem().getMaxDamage();
		int currentDamage = itemHeld.getDamage();
		
		for (int blockIndex = 0; blockIndex < disintegrateRange * 2; blockIndex++) {
			if (blockIndex % 2 == 0) {
				disintegratePosition.move(positionAdjustment.getBlockX(), 1, positionAdjustment.getBlockZ()); // Square at height of eyes
			} else {
				disintegratePosition.move(0, -1, 0); // Square at height of feet
			}
			
			Block block = player.getWorld().getBlockAt(disintegratePosition);
			BlockType blockType = block.getType();

			if (currentDamage >= maxDamage) {
				break;
			}
			
			if (BlockType.Air.equals(blockType) || BlockType.Torch.equals(blockType)) {
				// Skip over air blocks and torch blocks
				continue; 
			}
			
			if (!canDisintegrateBlock(player, blockType)) {
				// Encountering block that cannot be disintegrated stops further disintegration
				// player.message("Cannot disintegrate " + blockType.getMachineName());
				break;
			}

			block.dropBlockAsItem(true);
			context.spawnParticle(block.getLocation(), Particle.Type.SPELL_INSTANT);
			
			// Cause damage to tool
			currentDamage++;
		}

		MinecraftUtils.setItemHeldDamage(player, currentDamage);

		return true;
	}


	private boolean canDisintegrateBlock(Player player, BlockType blockType) {
		
		ItemType itemTypeNeeded = itemTypeNeededToDisintegrateBlockMap.get(blockType);
		if (itemTypeNeeded == null) {
			return false;
		} else {
			return player.getInventory().hasItem(itemTypeNeeded);
		}
	}

	private static Map<BlockType,ItemType> itemTypeNeededToDisintegrateBlockMap = new HashMap<>();
	static {
		// Don't disintegrate any ores/blocks requiring better-than-stone pickaxe or anything constructed (e.g. cobblestone, polished stone, stained clay, red sand, etc.)
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Andesite, ItemType.Andesite);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Clay, ItemType.Clay);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.CoalOre, ItemType.Coal);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.CoarseDirt, ItemType.Dirt);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Diorite, ItemType.Diorite);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Dirt, ItemType.Dirt);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.EndStone, ItemType.EndStone);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.GlowStone, ItemType.GlowstoneDust);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Granite, ItemType.Granite);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Gravel, ItemType.Gravel);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.HardenedClay, ItemType.Clay);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.IronOre, ItemType.IronOre);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.NetherQuartzOre, ItemType.NetherQuartz);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Netherrack, ItemType.Netherrack);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Podzol, ItemType.Dirt);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Sand, ItemType.Sand);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Sandstone, ItemType.Sandstone);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Soil, ItemType.Dirt);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.SoulSand, ItemType.SoulSand);
		itemTypeNeededToDisintegrateBlockMap.put(BlockType.Stone, ItemType.Cobble);
	}

}
