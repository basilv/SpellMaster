package com.basilv.minecraft.spellmaster.util;

import java.util.ArrayList;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.PlayerInventory;
import net.canarymod.api.nbt.ListTag;
import net.canarymod.api.nbt.StringTag;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.ChatFormat;

public class MinecraftUtils {

	public static long secondsToTicks(long seconds) {
		// Roughly 20 ticks per second in my testing.
		long ticks = (long)(seconds * Canary.getServer().getTicksPerSecond());
		return ticks;
	}

	public static Item createWrittenBookWithContent(String title, List<List<String>> pagesOfTextLines, List<String> loreTextLines) {
		Item item = Canary.factory().getItemFactory().newItem(ItemType.WrittenBook);
		item.setDisplayName(title); // This must be first, before populating the data tag, since it creates the data tag.

		item.getDataTag().put("pages", constructPages(title, pagesOfTextLines));
	    item.getDataTag().put("author", ""); // Necessary to have page of text display properly, but can be blank.
	    item.getDataTag().put("title", title);
	    item.getDataTag().put("resolved",  (byte) 1);
	
	    // Format lines of text for the item's lore, wrapping long lines at spaces.
	    if (loreTextLines != null) {
		    List<String> loreWrappedLines = constructLore(loreTextLines);
			item.setLore(loreWrappedLines.toArray(new String[] {}));
	    }
	    
		return item;
	}

	private static List<String> constructLore(List<String> loreTextLines) {
		final int maxLoreLineLength = 39;
		List<String> loreWrappedLines = new ArrayList<>();
		for (String line : loreTextLines) {
			loreWrappedLines.addAll(wrapText(line, maxLoreLineLength));
		}
		return loreWrappedLines;
	}

	private static List<String> wrapText(String line, int maxLineLength) {
		// TODO: This does not handle lines containing \n characters
		List<String> wrappedLines = new ArrayList<>();
		while (line.length() >= maxLineLength) {
			int lastSpaceIndex = line.substring(0, maxLineLength).lastIndexOf(" ");
			if (lastSpaceIndex == -1) {
				break;
			}
			wrappedLines.add(line.substring(0, lastSpaceIndex));
			line = line.substring(lastSpaceIndex+1, line.length());
		}
		wrappedLines.add(line);
		return wrappedLines;
	}
	
	private static ListTag<StringTag> constructPages(String title,
			List<List<String>> pagesOfTextLines) {
    	final int maxLineCountPerPage = 13;
    	final int maxLineLength = 22;

		ListTag<StringTag> listTag = Canary.factory().getNBTFactory().newListTag();
	    boolean firstPage = true;
	    for (List<String> page : pagesOfTextLines) {
			StringBuffer stringData = new StringBuffer();
	    	if (firstPage) {
	    		firstPage = false;
	    		// Add initial line with title
	    		List<String> existingPage = page;
	    		page = new ArrayList<String>();
	    		page.add(0, ChatFormat.BOLD + title + ChatFormat.RESET);
	    		page.add("");
	    		page.addAll(existingPage);
	    	}
	    	
	    	int lineCount = 0; 
	    	for (String line : page) {
	    		List<String> wrappedLines = wrapText(line, maxLineLength);
	    		for (String wrappedLine : wrappedLines) {
	    			if (lineCount >= maxLineCountPerPage) {
		    			// Store current page and start new page
		    		    listTag.add(Canary.factory().getNBTFactory().newStringTag(stringData.toString()));
		    		    stringData = new StringBuffer();
		    		    lineCount = 0;
	    			}
	    			lineCount++;
	    			
		    		if (stringData.length() > 0) {
		    			stringData.append("\n"); 
		    		}
		    		stringData.append(wrappedLine);
	    		}
	    	}
    		listTag.add(Canary.factory().getNBTFactory().newStringTag(stringData.toString()));
	    }
		return listTag;
	}

	/**
	 * Add the specified item to the specified player's inventory.
	 * @param player
	 * @param item
	 */
	public static void giveItemToPlayer(Player player, Item item) {
		// TODO: This will add a non-equippable item like a book to an equippable slot (e.g. helmet) if all other slots are full.
		// Fancy logic necessary to get the inventory to update properly when this is invoked from a hook like ItemUseHook
		Canary.getServer().addSynchronousTask(new ChangeInventoryTask() {
			@Override public void doTask() {
				PlayerInventory inventory = player.getInventory();
				if (inventory.getEmptySlot() == -1) {
					player.chat("No space in inventory for item " + item.getDisplayName());
					return;
				}
				inventory.addItem(item);
				inventory.update();
			}
	    });
	}

	/**
	 * Set the amount of damage for the item held by the player. If the damage is >= the maximum possible damage, destroy the item.  
	 * @param player
	 * @param damageAmount
	 */
	public static void setItemHeldDamage(Player player, int damageAmount) {
		if (damageAmount == 0) {
			return;
		}
        Canary.getServer().addSynchronousTask(new ChangeInventoryTask() {
			@Override public void doTask() {
				Item itemHeld = player.getItemHeld();
				if (damageAmount >= itemHeld.getBaseItem().getMaxDamage()) {
					player.getInventory().setSlot(itemHeld.getSlot(), null);
				} else {
					itemHeld.setDamage(damageAmount);
				}
				player.getInventory().update();
			}
        });
	}
	
	public static Position getPositionAdjustmentForDirectionPlayerLeftSide(Player player) {
		Position positionAdjustment = new Position(0, 0, 0);
		switch (player.getCardinalDirection()) {
		case NORTH:
			positionAdjustment.setX(-1);
			break;
		case SOUTH:
			positionAdjustment.setX(+1);
			break;
		case WEST:
			positionAdjustment.setZ(+1);
			break;
		case EAST:
			positionAdjustment.setZ(-1);
			break;
		case NORTHWEST:
			positionAdjustment.move(-1, 0, +1);
			break;
		case NORTHEAST:
			positionAdjustment.move(-1, 0, -1);
			break;
		case SOUTHWEST:
			positionAdjustment.move(+1, 0, +1);
			break;
		case SOUTHEAST:
			positionAdjustment.move(+1, 0, -1);
			break;
		default:
			break;
		}
		return positionAdjustment;
	}

	public static Position getPositionAdjustmentForDirectionPlayerFacing(Player player) {
		// TODO: Check out Ideas.fling() logic
		Position positionAdjustment = new Position(0, 0, 0);
		switch (player.getCardinalDirection()) {
		case NORTH:
			positionAdjustment.setZ(-1);
			break;
		case SOUTH:
			positionAdjustment.setZ(+1);
			break;
		case WEST:
			positionAdjustment.setX(-1);
			break;
		case EAST:
			positionAdjustment.setX(+1);
			break;
		case NORTHWEST:
			positionAdjustment.move(-1, 0, -1);
			break;
		case NORTHEAST:
			positionAdjustment.move(+1, 0, -1);
			break;
		case SOUTHWEST:
			positionAdjustment.move(-1, 0, +1);
			break;
		case SOUTHEAST:
			positionAdjustment.move(+1, 0, +1);
			break;
		default:
			break;
		}
		return positionAdjustment;
	}

	public static int getSeparationInBlocks(Position first, Position second) {
		int separation = Math.abs(first.getBlockX() - second.getBlockX()) +
			Math.abs(first.getBlockY() - second.getBlockY()) +
			Math.abs(first.getBlockZ() - second.getBlockZ());
		return separation;
	}

	public static void spawnParticleInFrontOfPlayer(Player player, Particle.Type particleType) {
		// Place particle in front of player's eyes.
		Location location = new Location(player.getLocation());
		Position positionAdjustment = getPositionAdjustmentForDirectionPlayerFacing(player);
		location.move(positionAdjustment.getBlockX(), 1, positionAdjustment.getBlockZ());
		player.getWorld().spawnParticle(new Particle(location.getX(), location.getY(), location.getZ(), particleType));
	}

	public static int countItemsOfType(Player player, ItemType itemType) {
		int count = 0;
		for (Item item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType().equals(itemType)) {
				count += item.getAmount();
			}
		}
		return count;
	}

	public static boolean isPlayerSurroundedByTypeOfBlock(Player player, BlockType blockType, int xBeyond, int yBeyond, int zBeyond) {
		World world = player.getWorld();
		Position centerPosition = new Position(player.getPosition());
		for (int y = -1 * yBeyond; y <= 1 + yBeyond; y++) {
			for (int x = -1 * xBeyond; x <= xBeyond; x++) {
				for (int z = -1 * zBeyond; z <= zBeyond; z++) {
					Position position = new Position(centerPosition);
					position.move(x, y, z);
					Block block = world.getBlockAt(position);
					if (!block.getType().equals(blockType)) {
						return false;
					}
				}
			}
		}
	
		return true;
	}

	public static int chunksToBlocks(int numChunks) {
		return numChunks * 16;
	}

}
