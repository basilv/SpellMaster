package com.basilv.minecraft.spellmaster.tomes;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellBoost;
import com.basilv.minecraft.spellmaster.Tome;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class EarthMagicTome extends Tome {

	public EarthMagicTome() {
		super("Earth Magic", 1);
		setCeremonyFocus("Stone pickaxe that is heavily damaged (red level)", ItemType.StonePickaxe);
		setCeremonyComponent("Stone pickaxe", ItemType.StonePickaxe, 9);
		setSpellBoost(new SpellBoost(0, 0, 0, 1));
	}

	@Override
	protected List<String> getBookIntroduction() {
		return Arrays.asList("Evoke the power of the earth.", "", "Boost: +1 second duration"); 
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
			
			log("Block at " + i + " pos " + position + " is " + blockType.getMachineName());
			if (!expectedBlockSequence[i].equals(blockType)) {
				sendPlayerUnableToPerformCeremonyMessage(player, "Block # " + (i+1) + " in front of you is not the correct type.");
				return false;
			}
		}
		
		return true;		
	}

	
}
