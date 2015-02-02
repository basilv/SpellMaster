package com.basilv.minecraft.spellmaster;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;

public class MagicFocus {
	
	private List<ItemType> itemTypes = Collections.emptyList();
	private String itemDescription;
	
	/**
	 * @param itemDescription Must not be null.
	 * @param itemTypes Must specify at least one item type.
	 */
	public MagicFocus(String itemDescription, ItemType... itemTypes) {
		this.itemDescription = itemDescription; // Need this because no easy way that I found to get a good description from ItemType
		this.itemTypes = Arrays.asList(itemTypes);
	}

	public boolean isItemAFocus(Item item) {
		if (item == null) { 
			return false;
		}
		return itemTypes.contains(item.getType());
	}

	public List<ItemType> getItemTypes() {
		return itemTypes;
	}
	
	@Override
	public String toString() {
		return itemDescription;
	}

}