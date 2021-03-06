package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.spells.DisintegrateEarthSpell;
import com.basilv.minecraft.spellmaster.spells.SenseOreSpell;
import com.basilv.minecraft.spellmaster.spells.WallOfStoneSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class EarthMagicTome extends Tome {

	public static abstract class EarthSpell extends Spell {
		
		public EarthSpell(String name) {
			super(name);
		}

		@Override
		protected float getCastingExhaustionCost() {
			return 6;  
		}
		
	}
	
	public EarthMagicTome() {
		super("Earth Magic", 1);
		setCeremonyFocus("Stone pickaxe that is heavily damaged (red level)", ItemType.StonePickaxe);
		setCeremonyComponent("Stone pickaxe", ItemType.StonePickaxe, 9);
		setSpellBoost(new SpellBoost(0, 0, 0, 1, 0));
		
		addSpell(new DisintegrateEarthSpell());
		addSpell(new WallOfStoneSpell());
		addSpell(new SenseOreSpell());
		
		// Flatten area
		// Create bridge
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Evoke the power of the earth."
			, "", "Due to the solidity and inflexibility of earth these spells are more exhausting than normal to cast. "
			, "", "Boost: +1 second duration"); 
	}

	@Override
	public String getCeremonyName() {
		return "Earth Evocation";
	}
	
	@Override
	protected void populateTomeSpecificCeremonyInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Requirements: Use the focus while facing at eye level nine blocks in a row consisting of 3 sand, then 3 dirt, then 3 stone (regular stone, not cobblestone)."
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

		Player player = context.getPlayer();
		Item itemHeld = player.getItemHeld();
		if (((float)itemHeld.getDamage() / (float)itemHeld.getBaseItem().getMaxDamage() < 0.74)) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Item held is not damaged enough.");
			return false;
		}

		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);

		Position position = new Position(player.getPosition());
		position.move(0, 1, 0); // Square at height of eyes
		BlockType[] expectedBlockSequence = {
			BlockType.Sand, BlockType.Sand, BlockType.Sand,
			BlockType.Dirt, BlockType.Dirt, BlockType.Dirt,
			BlockType.Stone, BlockType.Stone, BlockType.Stone
		};
		for (int i = 0; i < expectedBlockSequence.length; i++) {
			position.move(positionAdjustment.getBlockX(), 0, positionAdjustment.getBlockZ()); 
			BlockType blockType = player.getWorld().getBlockAt(position).getType();
			if (!expectedBlockSequence[i].equals(blockType)) {
				sendPlayerUnableToPerformCeremonyMessage(player, "Block # " + (i+1) + " in front of you is not the correct type.");
				return false;
			}
		}
		
		return true;		
	}

	
}
