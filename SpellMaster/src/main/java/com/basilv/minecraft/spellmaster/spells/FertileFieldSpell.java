package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.properties.BlockProperty;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicComponent;
import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class FertileFieldSpell extends Spell {

	public FertileFieldSpell() {
		super("Fertile Field");
		setCastingMinimumLevel(3);
		setCastingFocus("Wooden hoe", ItemType.WoodHoe); 
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 2 squares + 1 per level",
		   "Harvest crops (if any) and convert the ground into fertile soil with seeds planted. "
		   + "This is done in the direction you are facing for each block within range at the level you are standing on. "
		   + "Converting blocks to dirt and planting seeds consumes these items from your inventory. "
		   + "The seeds growth is accelerated using bonemeal from your inventory. "
		   + "The spell requires roughly 50% more seeds than what you plant. Extra seeds are sometimes scattered by the spell."
		));
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int playerDirtCount = MinecraftUtils.countItemsOfType(player, ItemType.Dirt);
		int playerDirtConsumed = 0;
		
		int playerSeedsCount = MinecraftUtils.countItemsOfType(player, ItemType.Seeds);
		int playerSeedsConsumed = 0;

		int playerBonemealCount = MinecraftUtils.countItemsOfType(player, ItemType.Bonemeal);
		int playerBonemealConsumed = 0;

		Item itemHeld = player.getItemHeld();
		int maxDamage = itemHeld.getBaseItem().getMaxDamage();
		int currentDamage = itemHeld.getDamage();
		
		// Calculate direction to disintegrate in
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		if (positionAdjustment.getBlockX() != 0 && positionAdjustment.getBlockZ() != 0) {
			// On a diagonal this spell is not allowed
			return false;
		}

		Position position = new Position(player.getPosition());
		
		int maxRange = context.getCastingRange(2, 1);
		World world = player.getWorld();
		
		for (int range = 1; range <= maxRange; range++) {
			position.move(positionAdjustment.getBlockX(), 0, positionAdjustment.getBlockZ());
			Block blockAbove = world.getBlockAt(position);
			BlockType blockAboveType = blockAbove.getType();
			boolean stop = true;
			if (BlockType.Air.equals(blockAboveType)) {
				stop = false;
			}
			if (BlockType.Crops.equals(blockAboveType) || BlockType.Carrots.equals(blockAboveType) || BlockType.Potatoes.equals(blockAboveType)) {
				blockAbove.dropBlockAsItem(true);
				blockAbove.update();
				stop = false;
			}
			
			if (stop) {
				break;
			}
			
			// Convert ground to soil ready to plant
			position.moveY(-1);
			Block groundBlock = world.getBlockAt(position);
			BlockType groundBlockType = groundBlock.getType();

			if (!BlockType.Soil.equals(groundBlockType)) {

				if (groundBlock.getBlockMaterial().isLiquid()) {
					// Spell won't convert water/lava
					break;
				}

				// Only allow spell to replace 'natural' blocks with dirt, to prevent players from easily eliminating obsidian or player-made structures
				List<BlockType> allowedBlockTypes = Arrays.asList(BlockType.Andesite, BlockType.Clay, BlockType.CoalOre, BlockType.CoarseDirt,
					BlockType.Diorite, BlockType.Dirt, BlockType.Granite, BlockType.Gravel, BlockType.HardenedClay, BlockType.Podzol,
					BlockType.Sand, BlockType.Sandstone, BlockType.Stone);
				if (!allowedBlockTypes.contains(groundBlockType)) {
					break;
				}
				
				if (!BlockType.Dirt.equals(groundBlockType) && !BlockType.CoarseDirt.equals(groundBlockType)) {
					if (playerDirtConsumed == playerDirtCount) {
						break;
					}
					playerDirtConsumed++;
				}

				// Damage hoe
				if (currentDamage >= maxDamage) {
					// Item is used up.
					break;
				}
				currentDamage++;
				
				// We replace the existing block rather than drop it to avoid letting the player easily mine through ores or obsidian using this spell.
				world.setBlockAt(position, BlockType.Soil);
			}

			// Plant seeds. 
			// Consume 1.5 seeds because sometimes an extra seed (dropped as an item) is generated for each block that has a seed planted.
			// This happens somewhat randomly (not all the time), so the best compromise seems to be to consume 1.5 seeds to avoid 
			// having the spell generate extra seeds.
			position.moveY(+1);
			int seedsNeeded = randomNumberWithinRange(1, 2);
			if (playerSeedsConsumed + seedsNeeded > playerSeedsCount) {
				break;
			}
			playerSeedsConsumed+=seedsNeeded; 
			world.setBlockAt(position, BlockType.Crops);

			// Grow wheat based on bone meal.
			int maxAge = 7;
			int maxBonemealUsed = 4; // maxAge divided by 2 rounded up
			int bonemealToUse = Math.min(playerBonemealCount - playerBonemealConsumed, maxBonemealUsed);
			int age = Math.min(bonemealToUse * 2, maxAge);
			playerBonemealConsumed += bonemealToUse;
			Block block = world.getBlockAt(position);
			BlockProperty property = block.getPropertyForName("age");
			block.setPropertyValue(property, Integer.valueOf(age));
			block.update();
		}
		
		MinecraftUtils.setItemHeldDamage(player, currentDamage);
		
		// Consume components
		MagicComponent dirtComponent = new MagicComponent("Dirt", ItemType.Dirt, playerDirtConsumed);
		dirtComponent.consumeForUse(player);

		MagicComponent seedsComponent = new MagicComponent("Seeds", ItemType.Seeds, playerSeedsConsumed);
		seedsComponent.consumeForUse(player);

		MagicComponent bonemealComponent = new MagicComponent("Bone meal", ItemType.Bonemeal, playerBonemealConsumed);
		bonemealComponent.consumeForUse(player);

		return true;
	}

}
