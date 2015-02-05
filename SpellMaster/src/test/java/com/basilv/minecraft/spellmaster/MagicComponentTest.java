package com.basilv.minecraft.spellmaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;

import org.junit.Test;

public class MagicComponentTest {

	private MagicComponent threeStickComponent = new MagicComponent("Sticks", ItemType.Stick, 3);

	@Test
	public void isPossessedByPlayer_OneItemInventoryJustEnough() {
		Item item = MockHelper.mockItem(ItemType.Stick, 3);
		Player player = MockHelper.mockPlayerWithInventory(item);
		assertTrue(threeStickComponent.isPossessedByPlayer(player));
	}

	@Test
	public void isPossessedByPlayer_OneItemInventoryNotEnough() {
		Item item = MockHelper.mockItem(ItemType.Stick, 2);
		Player player = MockHelper.mockPlayerWithInventory(item);
		assertFalse(threeStickComponent.isPossessedByPlayer(player));
	}
	
	@Test
	public void toString_ShouldReturnAmountAndDescription() {
		assertEquals("3 Sticks", threeStickComponent.toString());
	}
	
	// TODO: Test consumeForUser
}
