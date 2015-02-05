package com.basilv.minecraft.spellmaster;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.PlayerInventory;

import org.mockito.Mockito;

public class MockHelper {

	public static Item mockItem(ItemType itemType, int amount) {
		Item item = mock(Item.class);
		when(item.getType()).thenReturn(itemType);
		when(item.getAmount()).thenReturn(amount);

		return item;		
	}
	
	public static Player mockPlayerWithInventory(Item... items) {
		
		PlayerInventory inventory = mock(PlayerInventory.class);
		when(inventory.getItem((ItemType)Mockito.anyObject())).then(answer -> 
			Arrays.stream(items)
				.filter(i -> i.getType().equals(answer.getArgumentAt(0, ItemType.class)))
				.findFirst()
				.get() 
		);
		when(inventory.getContents()).thenReturn(items);

		Player player = mock(Player.class);
		when(player.getInventory()).thenReturn(inventory);

		return player;
	}
	
}
