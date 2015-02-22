package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.BlockIterator;
import net.canarymod.Canary;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.FireMagicTome.FireSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

public class FireballSpell extends FireSpell {

	public FireballSpell() {
		super("Fireball");
		setCastingMinimumLevel(20);
		setCastingFocus("Blaze rod", ItemType.BlazeRod); 
		setCastingComponent("Coal", ItemType.Coal, 2);  
	}
	

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: 1 second per 15 levels",
		   "Create a fireball in front of you in the direction you are pointing. You must hit the fireball to launch it forward. "
		   + "The fireball will only remain in existance for the spell's duration."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.GHAST_FIREBALL;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {
		boolean includeAir = true;
		BlockIterator sightIterator = new BlockIterator(context.getPlayer(), includeAir);
    	Block block = sightIterator.next();
    	if (!block.getType().equals(BlockType.Air)) {
    		return false;
    	}
	    Position startPosition = block.getPosition();

		Entity entity = Canary.factory().getEntityFactory().newEntity(EntityType.LARGEFIREBALL);
		entity.setX(startPosition.getX());
		entity.setY(startPosition.getY());
		entity.setZ(startPosition.getZ());
		entity.spawn();

		double duration = context.getCastingLevel() / 15.0 + context.getSpellboost().getDurationInSeconds();
		Canary.getServer().addSynchronousTask(new EndFireballTask(duration, entity));
		
		return true;
	}
	
	private static class EndFireballTask extends OneTimeServerTask {

		private Entity fireball;
		
		public EndFireballTask(double durationInSeconds, Entity fireball) {
			super(MinecraftUtils.secondsToTicks(durationInSeconds));
			this.fireball = fireball;
		}

		@Override
		public void doTask() {
			if (!fireball.isDead()) {
				fireball.destroy();
			}
		}
		
	}
	
}
