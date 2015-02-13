package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.spells.FlightSpell;
import com.basilv.minecraft.spellmaster.spells.LightningBoltSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.ControlWeatherSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class AirMagicTome extends Tome {

	public static abstract class AirSpell extends Spell {
		
		public AirSpell(String name) {
			super(name);
		}

		@Override
		protected float getCastingExhaustionCost() {
			return 6;  
		}

		@Override
		protected float getCastingHealthCost() {
			return 0.5f;
		}
	}

	public AirMagicTome() {
		super("Air Magic", 2);
		setCeremonyFocus("Feather", ItemType.Feather); 
		setCeremonyComponent("Feather", ItemType.Feather, 32);
		setSpellBoost(new SpellBoost(0, 0, 1, 0));
		
		addSpell(new ControlWeatherSpell());
		addSpell(new LightningBoltSpell());
		addSpell(new FlightSpell());
		
		// TODO: Wind wall, focus: large fern (what then is control weather focus? Maybe sunflower?). Problem is sunflower and large fern are hard to get.
		// Gust of wind: push all mobs away from you.
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Become one with the air."
			, "", "Due to the chaotic, ever-changing nature of air these spells are more exhausting to cast but cost less health."
			, "", "Boost: +1 range"); 
	}

	@Override
	public String getCeremonyName() {
		return "Air Ally";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: Use the focus while falling. You must be surrounded by nothing but air from well below your feet to well above your head." 
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 10;
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 2;
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {

		Player player = context.getPlayer();
		if (!MinecraftUtils.isPlayerSurroundedByTypeOfBlock(player, BlockType.Air, 1, 2, 1)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be surrounded by air.");
			return false;
		}
		return true;
	}

	
}
