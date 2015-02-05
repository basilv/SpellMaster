package com.basilv.minecraft.spellmaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;

import org.junit.Ignore;
import org.junit.Test;

import com.basilv.minecraft.spellmaster.tomes.EarthMagicTome;
import com.basilv.minecraft.spellmaster.tomes.IntroductorySpellcastingTome;

public class MagicContextTest {

	@Test
	public void noTomesInInventory() {
		Player player = MockHelper.mockPlayerWithInventory(MockHelper.mockItem(ItemType.Stick, 1));
		MagicContext context = new MagicContext(player, null);

		assertTrue(context.getTomes().isEmpty());
		assertTrue(context.getSpells().isEmpty());
		assertFalse(context.doesPlayerHaveTome(IntroductorySpellcastingTome.class));
		assertEquals(0, context.countTomesPlayerHas(IntroductorySpellcastingTome.class));
		assertEquals(0, context.getSpellboost().getCasterLevel());
	}
	
	@Test @Ignore // TODO: Cannot create book due to Canary.factory instantiation problem. 
	public void oneTomeInInventory() {
		Tome tome = new IntroductorySpellcastingTome();
		Item item = tome.createBook();
		Player player = MockHelper.mockPlayerWithInventory(item);
		MagicContext context = new MagicContext(player, null);

		assertEquals(1, context.getTomes().size());
		assertTrue(context.doesPlayerHaveTome(IntroductorySpellcastingTome.class));
		assertEquals(1, context.countTomesPlayerHas(IntroductorySpellcastingTome.class));
		assertEquals(0, context.countTomesPlayerHas(EarthMagicTome.class));
	}
}
