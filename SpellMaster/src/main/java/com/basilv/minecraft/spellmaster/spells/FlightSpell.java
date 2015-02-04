package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.HumanCapabilities;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

public class FlightSpell extends Spell {

	private static final ConcurrentHashMap<String,Boolean> playerUuidFlyingMap = new ConcurrentHashMap<>();

	public FlightSpell() {
		super("Flight");
		setCastingMinimumLevel(20);
		setCastingFocus("2 Feathers", ItemType.Feather);
		setCastingComponent("Feather", ItemType.Feather, 2); // TODO: Consider using more exotic item?
		// TODO: Should there be a greater spell cost?
	}
	
	@Override
	public boolean canCastSpellWithHeldItem(Item item) {
		if (!super.canCastSpellWithHeldItem(item)) {
			return false;
		}
		// Ensure holding at least two feathers to cast
		return (item.getAmount() >= 2); 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: level * 2 - 30 seconds",
		   "Fly speed: 0.01 / 4 levels",
		   "Allows you to fly for a limited time. The spell ends without warning and you can take falling damage if still in the air."
//		   "In addition to the regular casting cost, this costs one level to cast" // TODO: Evaluate if this should be done. Too easy to fight monsters from the air, surmount End start, etc.
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.BAT_TAKEOFF;
	}
	
	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		// Don't allow spell to be recast while in progress to avoid weird outcomes and to enforce 'penalty' of player falling out of the sky.
		if (playerUuidFlyingMap.containsKey(player.getUUIDString())) {
			sendPlayerUnableToCastMessage(player, "Flight spell is already active.");
			return false;
		}
		
		HumanCapabilities capabilities = player.getCapabilities();
		capabilities.setMayFly(true);
		capabilities.setFlying(true);
		
		// Default fly speed is 0.05, which is used at minimum casting level
		float flySpeed = (float) (0.01 * context.getCastingLevel() / 4); 
		capabilities.setFlySpeed(flySpeed);
		player.updateCapabilities();

		long durationInSeconds = context.getCastingLevel() * 2 - 30 + context.getSpellboost().getDurationInSeconds();
		EndFlightTask task = new EndFlightTask(player, durationInSeconds);
		
		// Part of the cost of the spell is that it ends without warning, so the player will potentially take damage if still in the air.
    	playerUuidFlyingMap.put(player.getUUIDString(), Boolean.TRUE);
	    Canary.getServer().addSynchronousTask(task);

	    return true;
	}

	private class EndFlightTask extends OneTimeServerTask {

		private Player player;

	    public EndFlightTask(Player player, long durationInSeconds) {
			super(MinecraftUtils.secondsToTicks(durationInSeconds));
			this.player = player;
		}

	    public void doTask() {
	    	playerUuidFlyingMap.remove(player.getUUIDString());
	    	player.chat(getName() + " spell ending");
	    	HumanCapabilities capabilities = player.getCapabilities();
			capabilities.setMayFly(false);
	    	capabilities.setFlying(false);
	    	float defaultFlySpeed = 0.05f;
	    	capabilities.setFlySpeed(defaultFlySpeed);
	    	player.updateCapabilities();
	        Canary.getServer().removeSynchronousTask(this);
	    }
	}
	
}
