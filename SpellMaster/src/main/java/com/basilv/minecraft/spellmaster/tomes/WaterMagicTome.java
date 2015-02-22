package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.spells.AcidErosionSpell;
import com.basilv.minecraft.spellmaster.spells.WallOfWaterSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class WaterMagicTome extends Tome {

	public static abstract class WaterSpell extends Spell {
		
		public WaterSpell(String name) {
			super(name);
		}
		
		@Override
		protected float getCastingExhaustionCost() {
			return 3;  
		}

		@Override
		protected float getCastingHealthCost() {
			return 0.5f;
		}
		
	}
	
	
	public WaterMagicTome() {
		super("Water Magic", 1);
		setCeremonyFocus("Water bucket", ItemType.WaterBucket);
		setSpellBoost(new SpellBoost(0, 0, 0, 1, 0));
		
		addSpell(new WallOfWaterSpell());
		addSpell(new AcidErosionSpell());

		// TODO: Other options
		// Water wave
		// Flood
		// Earth to mud
		// Absorb Water (or should this be in fire magic - vaporize water)
//		ItemType.ClownFish
//		ItemType.FishingRod
//		ItemType.Ice
//		ItemType.Lilypad
//		ItemType.PufferFish
//		ItemType.RabbitFoot
//		ItemType.RawFish
//		ItemType.RawSalmon;
//		ItemType.String
//		ItemType.SpiderEye
//		ItemType.LapisLazuli
		
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Learn the ways of water."
			, "", "Water easily adjusts to its conditions so casting these spells is slightly less exhausting and costs slightly less health."
			, "", "Boost: +1 second duration"); 
	}

	@Override
	public String getCeremonyName() {
		return "Water Wizardry";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: You must be surrounded by water on all sides, from well below your feet to well above your head."
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
		if (!MinecraftUtils.isPlayerSurroundedByTypeOfBlock(context.getPlayer(), BlockType.Water, 1, 2, 1)) {
			sendPlayerUnableToPerformCeremonyMessage(context.getPlayer(), "Must be surrounded by water.");
			return false;
		}
		return true;
	}

	
}
