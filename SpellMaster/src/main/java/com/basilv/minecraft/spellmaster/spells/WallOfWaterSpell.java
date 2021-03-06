package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.effects.SoundEffect.Type;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.WaterMagicTome.WaterSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class WallOfWaterSpell extends WaterSpell {

	public WallOfWaterSpell() {
		super("Wall of Water");
		setCastingMinimumLevel(1); 
		setCastingFocus("Wooden Shovel", ItemType.WoodSpade); 
		// No component: spell isn't that powerful so casting it over and over is okay.
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
			"Range: 1 square + 1 / 5 levels.",
			"Duration: 5 + level * 2 seconds",
			"Conjure a wall of water directly in front of you, extending to each side up to the maximum range. "
			+ "The wall is conjured out of thin air and does not affect existing blocks. "
			+ "The wall is three blocks high, starting from the level that you are standing on top of."
			+ "The bottom level of the wall is permanent, while the upper two levels last only for the duration of the spell."
		));
	}

	
	@Override
	protected Type getSpellSoundEffect() {
		return SoundEffect.Type.WATER;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int maxRange = context.getCastingRange(1, 5);
		int durationInSeconds = 5 + context.getCastingLevel() * 2 + context.getSpellboost().getDurationInSeconds();

		TemporaryWallHelper wallHelper = new TemporaryWallHelper(BlockType.Water);
		wallHelper.createTemporaryWall(context, maxRange, durationInSeconds);

	    // Damage shovel once each time spell is cast.
		MinecraftUtils.setItemHeldDamage(player, player.getItemHeld().getDamage()+1);
	    
		return true;
	}

}
