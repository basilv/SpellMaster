package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

public class LightSpell extends Spell {

/* Problems with this spell:
 * Light doesn't appear until player disconnects and reconnects. Calling block.update() doesn't work
 * Reseting to original light level not necessarily valid if player changes light level in the meantime - can easily make a permanent light.
 */
	
	public LightSpell() {
		super("Light");
		setCastingMinimumLevel(0);
		setCastingFocus("Stick", ItemType.Stick); 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: 5 + level seconds", 
		   "Create a temporary light at your location."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.FIRE_IGNITE;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {
		
		World world = context.getWorld();
		Position position = new Position(context.getPlayer().getPosition());
		int originalLightLevel = world.getLightLevelAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
		
		int newLightLevel = 12; // Not as bright as torch
		world.setLightLevelOnBlockMap(position.getBlockX(), position.getBlockY(), position.getBlockZ(), newLightLevel);
		world.getBlockAt(position).update();

		int durationInSeconds  = 5 + context.getCastingLevel() + context.getSpellboost().getDurationInSeconds();
	    Canary.getServer().addSynchronousTask(new SetLightLevelTask(world, position, newLightLevel, 0));
	    Canary.getServer().addSynchronousTask(new SetLightLevelTask(world, position, originalLightLevel, durationInSeconds));
		
	    return true;
	}

	private class SetLightLevelTask extends OneTimeServerTask {
	
		private World world;
		private Position position;
		private int lightLevel;
		
	    public SetLightLevelTask(World world, Position position, int lightLevel, long delayInSeconds) {
			super(MinecraftUtils.secondsToTicks(delayInSeconds));
			this.world = world;
			this.position = position;
			this.lightLevel = lightLevel;
		}

	    public void doTask() {
			world.setLightLevelOnBlockMap(position.getBlockX(), position.getBlockY(), position.getBlockZ(), lightLevel);
			log("Light level to " + lightLevel); // TODO:REMOVE
			world.getBlockAt(position).update();
	    }
	}
	
}
