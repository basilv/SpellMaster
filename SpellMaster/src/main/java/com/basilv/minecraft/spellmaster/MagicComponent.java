package com.basilv.minecraft.spellmaster;

import java.util.Arrays;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.PlayerInventory;

import com.basilv.minecraft.spellmaster.util.ChangeInventoryTask;

public class MagicComponent {

	private ItemType itemType;
	private String itemDescription;
	private Integer numberConsumed;
	
	/**
	 * @param itemDescription Must not be null.
	 * @param itemType Must not be null.
	 * @param numberConsumed May be null to indicate that the component is not consumed.
	 */
	public MagicComponent(String itemDescription, ItemType itemType, Integer numberConsumed) {
		this.itemDescription = itemDescription; // Need this because no easy way to get a good description from ItemType
		this.itemType = itemType;
		this.numberConsumed = numberConsumed;
	}

	public boolean isPossessedByPlayer(Player player) {
		PlayerInventory inventory = player.getInventory();
		if (inventory.getItem(itemType) == null) {
			return false;
		}
		if (numberConsumed == null || numberConsumed == 1) {
			return true;
		}
					
		int itemCount = Arrays.stream(inventory.getContents())
			.filter(item -> item != null && item.getType().equals(itemType))
			.mapToInt(Item::getAmount)
			.sum();
		return itemCount >= numberConsumed;
		
	}

	public void consumeForUse(Player player) {
		if (numberConsumed == null || numberConsumed == 0) {
			return;
		}
        Canary.getServer().addSynchronousTask(new ChangeInventoryTask() {
			@Override public void doTask() {
				PlayerInventory inventory = player.getInventory();
				int numberRemainingToConsume = numberConsumed;
				while (numberRemainingToConsume > 0) {
					Item item = inventory.getItem(itemType);
					if (item == null) {
						throw new RuntimeException("Unable to consume item of type " + itemType.getMachineName() + ": player has none in their inventory.");
					}
					if (numberRemainingToConsume < item.getAmount()) {
						item.setAmount(item.getAmount() - numberRemainingToConsume);
						numberRemainingToConsume = 0;
					} else {
						numberRemainingToConsume -= item.getAmount();
						inventory.setSlot(item.getSlot(), null);
					}
				}
				inventory.update();
			}
        });
	}
	
	@Override
	public String toString() {
		if (numberConsumed != null) {
			return numberConsumed.toString() + " " + itemDescription;
		} else {
			return itemDescription + " (Not consumed)";
		}
	}

}