package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class FireMagicTome extends Tome {

	public static abstract class FireSpell extends Spell {
		
		public FireSpell(String name) {
			super(name);
		}

		@Override
		protected void applyCastingCost(MagicContext context) {
			super.applyCastingCost(context);
			// Set player on fire briefly.
			Player player = context.getPlayer();
			player.setFireTicks(player.getFireTicks() + (int)MinecraftUtils.secondsToTicks(1));
		}

		@Override
		protected float getCastingExhaustionCost() {
			return 0;  
		}
		
		@Override
		protected float getCastingHealthCost() {
			return 0.5f;
		}
	}

	public FireMagicTome() {
		super("Fire Magic", 2);
		setCeremonyFocus("Blaze rod", ItemType.BlazeRod);
		setCeremonyComponent("Blaze rod", ItemType.BlazeRod, 1);
		setSpellBoost(new SpellBoost(0, 1, 0, 0));

		// TODO: Fire spells
		// Ignite: convert air block to fire, revert back after duration ends?
		// Lava burst
		// Fireball
		// Flamewall
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Feel the fury of fire."
			, "", "Fire seeks to consume so these spells are not exhausting to cost but do set you on fire briefly."
			, "", "Boost: +1 damage"); 
	}

	@Override
	public String getCeremonyName() {
		return "Fire's Fury";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList( 
			"Requirements: Use the focus while in the nether, on fire, submerged in lava from head to foot. The lava cannot be flowing."  
		));
	}
	
	@Override
	protected int getCeremonyMinimumLevel() {
		return 15; 
	}

	@Override
	protected int getCeremonyLevelCost() {
		return 3; 
	}

	@Override
	protected boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context) {

		Player player = context.getPlayer();
		if (!DimensionType.NETHER.equals(context.getWorld().getType())) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be in the nether.");
			return false;
		}
		
		if (player.getFireTicks() == 0) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be on fire.");
			return false;
		}
		
		if (!MinecraftUtils.isPlayerSurroundedByTypeOfBlock(player, BlockType.Lava, 0, 0, 0)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Must be submerged in lava.");
			return false;
		}
		
		return true;
	}
	
}
