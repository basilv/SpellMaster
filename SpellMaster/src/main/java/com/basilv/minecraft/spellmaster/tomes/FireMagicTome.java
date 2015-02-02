package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.blocks.BlockType;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class FireMagicTome extends Tome {

	public FireMagicTome() {
		super("Fire Magic", 2);
		setCeremonyFocus("Blaze rod", ItemType.BlazeRod);
		setCeremonyComponent("Blaze rod", ItemType.BlazeRod, 1);
		setSpellBoost(new SpellBoost(0, 1, 0, 0));
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Feel the fury of fire."
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
