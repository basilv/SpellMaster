package com.basilv.minecraft.spellmaster.spells;

import java.util.HashMap;
import java.util.Map;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.EarthMagicTome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

/**
 * Common base class for spells that find ore.
 * Focus is a block of the material you are looking for.
 */
public abstract class AbstractFindOreSpell extends EarthMagicTome.EarthSpell {

	private Map<ItemType,BlockType> blockTypeForItemHeld = new HashMap<>();

	/**
	 * Register an ore block that can be found by holding the corresponding material as an item.   
	 * @param itemType
	 * @param blockType
	 */
	protected final void registerOre(ItemType itemType, BlockType blockType) {
		blockTypeForItemHeld.put(itemType, blockType);
	}
	
	public AbstractFindOreSpell(String name) {
		super(name);
	}

	@SuppressWarnings("deprecation")
	protected final void registerCommonOres() {
		registerOre(ItemType.CoalBlock, BlockType.CoalOre);
		registerOre(ItemType.IronBlock, BlockType.IronOre);
		registerOre(ItemType.RedstoneBlock, BlockType.RedstoneOre);
		registerOre(ItemType.LapisBlock, BlockType.LapislazuliOre); // LapisOre causes exception
	}

	/**
	 * Requires that all ores have been registered.
	 */
	protected final void initializeCastingFocus() {
		setCastingFocus("Block of material obtained from ore (e.g. coal, iron)", blockTypeForItemHeld.keySet().toArray(new ItemType[] {})); 
	}

	@Override
	protected final SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.NOTE_PLING;
	}

	@Override
	protected final boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		BlockType blockType = blockTypeForItemHeld.get(player.getItemHeld().getType());
		Position nearestOrePosition = getNearestOrePosition(context, blockType);

		if (nearestOrePosition == null) {
			player.message("No ore " + getPastTenseActionVerb());
			return true;
		}

		Position playerPosition = player.getPosition();
		int xDiff = nearestOrePosition.getBlockX() - playerPosition.getBlockX();
		int yDiff = nearestOrePosition.getBlockY() - playerPosition.getBlockY();
		int zDiff = nearestOrePosition.getBlockZ() - playerPosition.getBlockZ();
		sendOreMessage(player, xDiff, yDiff, zDiff);
		
	    return true;
	}

	/**
	 * Send the message related to finding ore to the specified player. 
	 * @param player
	 * @param xDiff  Distance ore is from player on X axis.
	 * @param yDiff  Distance ore is from player on Y axis.
	 * @param zDiff  Distance ore is from player on Z axis.
	 */
	protected abstract void sendOreMessage(Player player, int xDiff, int yDiff, int zDiff);

	
	protected final void sendOreClosenessMessage(Player player, int xDiff, int yDiff, int zDiff) {
		
		int maxDistance = Math.max(Math.abs(xDiff), Math.max(Math.abs(yDiff), Math.abs(zDiff)));

		String message;
		
		if (maxDistance < 3) {
			message = "very close to you";
		} else if (maxDistance < 6) {
			message = "close to you";
		} else if (maxDistance < 9) {
			message = "a short distance away";
		} else if (maxDistance < 12) {
			message = "a medium distance away";
		} else if (maxDistance < 15) {
			message = "a long distance away";
		} else {
			message = "very far away";
		}

		player.message("Ore " + getPastTenseActionVerb() + " " + message);
		
	}

	/**
	 * @return the past-tense action the spell performs (e.g. sensed or detected).
	 */
	protected abstract String getPastTenseActionVerb();
	
	protected final void sendOreDirectionMessage(Player player, int xDiff, int yDiff,
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
		
		player.message("Ore " + getPastTenseActionVerb() + " " + message);
	}

	protected final Position getNearestOrePosition(MagicContext context, BlockType blockType) {
		int maxRange = context.getCastingRange(0, 2);

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
