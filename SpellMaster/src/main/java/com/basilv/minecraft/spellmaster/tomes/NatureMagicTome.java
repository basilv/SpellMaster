package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;

public class NatureMagicTome extends Tome {

	public NatureMagicTome() {
		super("Nature Magic", 1);
		setCeremonyFocus("One of the flowers in the arrangement", 
			ItemType.YellowFlower, ItemType.Poppy, ItemType.BlueOrchid, ItemType.Allium, ItemType.AzureBluet, ItemType.Lilac, ItemType.OxeyeDaisy,
			ItemType.Peony, ItemType.RoseBush, ItemType.RedTulip, ItemType.OrangeTulip, ItemType.WhiteTulip, ItemType.PinkTulip);
		setSpellBoost(new SpellBoost(0, 0, 0, 1));
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Harness the power of nature.", "", "Boost: +1 second duration"); 
	}

	@Override
	public String getCeremonyName() {
		return "Nature's Nurture";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Components: A flower 'arrangement' consisting of five different flowers from the following list: "
			+ "Allium, Azure Bluet, Blue Orchid, Dandelion, Lilac, Oxeye Daisy, Peony, Poppy, Rose Bush, and Tulip (either Red, Orange, White or Pink)",
			"Requirements: Stand in grass with your flower arrangement, look at the sky, and use the focus. "
			+ "The components are not consumed when the ceremony is performed."
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 1;
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 1;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {
		List<ItemType> flowers = getCeremonyFocus().getItemTypes();

		final int flowersForCeremonyCount = 5;
		
		Player player = context.getPlayer();
		Item itemHeld = player.getItemHeld();
		if (itemHeld == null || !flowers.contains(itemHeld.getType())) {
			return false;
		}
		
		Set<ItemType> flowersInInventory = Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null && flowers.contains(item.getType()))
			.map(item -> item.getType())
			.limit(flowersForCeremonyCount)
			.collect(Collectors.toSet());
		
		if (flowersInInventory.size() < flowersForCeremonyCount) {
			sendPlayerUnableToPerformCeremonyMessage(player, "You only have " + flowersInInventory.size() + " types of flowers.");
			return false;
		}
		
		// Check that player is standing in grass.
		Block block = context.getWorld().getBlockAt(player.getPosition());
		// Don't compare BlockType instance directly because it appears that it can have different values for data in different circumstances, which changes its identity.
		if (BlockType.TallGrass.getId() != block.getType().getId()) {
			sendPlayerUnableToPerformCeremonyMessage(player, "You must be standing in grass.");
			return false;
		} 

		return true;
	}

	@Override
	protected boolean createCeremonyGameEffect(MagicContext context) {
		// Not sure why, but this turns the grass brown. Not quite what we were planning, but it is a cool effect.
		context.getWorld().setBlockAt(context.getPlayer().getPosition(), BlockType.TallGrass);
		return true;
	}

	
}
