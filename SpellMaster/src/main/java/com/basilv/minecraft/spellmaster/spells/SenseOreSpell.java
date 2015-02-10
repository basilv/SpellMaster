package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class SenseOreSpell extends Spell {

	private Map<ItemType,BlockType> blockTypeForItemHeld = new HashMap<>();

	public SenseOreSpell() {
		super("Sense Ore");
		setCastingMinimumLevel(5);

		blockTypeForItemHeld.put(ItemType.CoalBlock, BlockType.CoalOre);
		blockTypeForItemHeld.put(ItemType.IronBlock, BlockType.IronOre);
		blockTypeForItemHeld.put(ItemType.RedstoneBlock, BlockType.RedstoneOre);
		blockTypeForItemHeld.put(ItemType.LapisBlock, BlockType.LapislazuliOre);
		blockTypeForItemHeld.put(ItemType.EmeraldBlock, BlockType.EmeraldOre);
		blockTypeForItemHeld.put(ItemType.GoldBlock, BlockType.GoldOre);
		blockTypeForItemHeld.put(ItemType.DiamondBlock, BlockType.DiamondOre);

		setCastingFocus("Block of material obtained from ore (e.g. coal, iron, diamond)", blockTypeForItemHeld.keySet().toArray(new ItemType[] {})); 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 1 square per level", 
		   "Use the focus without placing the block. You will sense the direction to the nearest ore corresponding to the material you are using as a focus "
		   + "if it is within the spell's range. "
		   + "At higher levels the sense of direction grows more accurate."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.NOTE_PLING;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();

		BlockType blockType = blockTypeForItemHeld.get(player.getItemHeld().getType());
		
		Position nearestOrePosition = getNearestOrePosition(context, blockType);

		if (nearestOrePosition == null) {
			player.chat("No ore sensed");
			return true;
		}
		
		int castingLevel = context.getCastingLevel();
		if (castingLevel < 10) {
			player.chat("Ore sensed");
			return true;
		} 
		
		Position playerPosition = player.getPosition();
		int xDiff = nearestOrePosition.getBlockX() - playerPosition.getBlockX();
		int yDiff = nearestOrePosition.getBlockY() - playerPosition.getBlockY();
		int zDiff = nearestOrePosition.getBlockZ() - playerPosition.getBlockZ();

		if (castingLevel < 20) {
			sendOreClosenessMessage(player, xDiff, yDiff, zDiff);
		} else if (castingLevel < 30) {
			sendOreDirectionMessage(player, xDiff, yDiff, zDiff);
		} else {
			sendOreClosenessMessage(player, xDiff, yDiff, zDiff);
			sendOreDirectionMessage(player, xDiff, yDiff, zDiff);
		}

	    return true;
	}

	private void sendOreClosenessMessage(Player player, int xDiff, int yDiff, int zDiff) {
		
		int maxDistance = Math.max(Math.abs(xDiff), Math.max(Math.abs(yDiff), Math.abs(zDiff)));

		String message;
		
		if (maxDistance < 4) {
			message = "very close to you";
		} else if (maxDistance < 8) {
			message = "close to you";
		} else if (maxDistance < 12) {
			message = "a short distance away";
		} else if (maxDistance < 16) {
			message = "a medium distance away";
		} else if (maxDistance < 20) {
			message = "a long distance away";
		} else {
			message = "very far away";
		}

		player.chat("Ore sensed " + message);
		
	}

	private void sendOreDirectionMessage(Player player, int xDiff, int yDiff,
			int zDiff) {
		Position positionAdjustmentFacing = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position positionAdjustmentLeft = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);
		String message = "";
		int playerFacing;
		int nearestDiff;
		int playerLeft;
		if (Math.abs(xDiff) >= Math.abs(zDiff)) {
			playerFacing = positionAdjustmentFacing.getBlockX();
			nearestDiff = xDiff;
			playerLeft = positionAdjustmentLeft.getBlockX();
		} else {
			playerFacing = positionAdjustmentFacing.getBlockZ();
			nearestDiff = zDiff;
			playerLeft = positionAdjustmentLeft.getBlockZ();
		}
		if (playerFacing != 0) {
			if ((playerFacing > 0 && nearestDiff > 0) || (playerFacing < 0 && nearestDiff < 0)) {
				message = "ahead of you";
			} else if (nearestDiff != 0) {
				message = "behind you";
			} else {
				message = "";
			}
		} else {
			if ((playerLeft > 0 && nearestDiff > 0) || (playerLeft < 0 && nearestDiff < 0)) {
				message = "left of you";
			} else if (nearestDiff != 0) {
				message = "right of you";
			} else {
				message = "";
			}
		}
		
		if (!message.isEmpty()) {
			message += " and ";
		}
		
		if (yDiff > 1) {
			message += " above you";
		} else if (yDiff < 0) {
			message += " below you";
		} else {
			message += " at your level";
		}
		
		player.chat("Ore sensed " + message);
	}

	private Position getNearestOrePosition(MagicContext context, BlockType blockType) {
		int maxRange = context.getCastingRange(0, 1);

		World world = context.getWorld();
		Position playerPosition = context.getPlayer().getPosition();
		
		Position nearestOrePosition = null;
		for (int x = -maxRange; x <= maxRange; x++) {
			for (int z = -maxRange; z <= maxRange; z++) {
				for (int y = -maxRange; y <= maxRange + 1; y++) { // Add one for player height of two blocks
					Position position = new Position(playerPosition);
					position.move(x, y, z);
					if (world.getBlockAt(position).getType().equals(blockType)) {
						if (nearestOrePosition == null) {
							nearestOrePosition = position; 
						} else {
							int nearestSeparation = MinecraftUtils.getSeparationInBlocks(nearestOrePosition, playerPosition);
							int currentSeparation = MinecraftUtils.getSeparationInBlocks(position, playerPosition);
							if (currentSeparation < nearestSeparation) {
								nearestOrePosition = position;
							}
						}
					}
				}
			}
		}
		return nearestOrePosition;
	}

}
