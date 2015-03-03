package com.basilv.minecraft.spellmaster.spells;

import java.util.concurrent.ConcurrentHashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.HumanCapabilities;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.tasks.ServerTask;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellExhaustionTask;
import com.basilv.minecraft.spellmaster.tomes.AirMagicTome.AirSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public abstract class AbstractFlightSpell extends AirSpell {

	private static final ConcurrentHashMap<String,Boolean> playerUuidFlyingMap = new ConcurrentHashMap<>();

	public AbstractFlightSpell(String spellName) {
		super(spellName);
		setCastingFocus("2 Feathers", ItemType.Feather);
		setCastingComponent("Feather", ItemType.Feather, 2);
		// We don't use a more expensive component: instead the spell has an ongoing exhaustion cost.
	}
	
	@Override
	public final boolean canCastSpellWithHeldItem(Item item) {
		if (!super.canCastSpellWithHeldItem(item)) {
			return false;
		}
		// Ensure holding at least two feathers to cast
		return (item.getAmount() >= 2); 
	}

	@Override
	protected final SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.BAT_TAKEOFF;
	}
	
	@Override
	protected final boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		// Don't allow spell to be recast while in progress to avoid weird outcomes and to enforce 'penalty' of player falling out of the sky.
		if (playerUuidFlyingMap.containsKey(player.getUUIDString())) {
			sendPlayerUnableToCastMessage(player, "Flight spell is already active.");
			return false;
		}
		
		HumanCapabilities capabilities = player.getCapabilities();
		capabilities.setMayFly(true);
		capabilities.setFlying(true);
		
		capabilities.setFlySpeed(getFlightSpeed(context));
		player.updateCapabilities();

		OngoingFlightTask task = new OngoingFlightTask(context, getDurationInSeconds(context));
		
		// Part of the cost of the spell is that it ends without warning, so the player will potentially take damage if still in the air.
    	playerUuidFlyingMap.put(player.getUUIDString(), Boolean.TRUE);
	    Canary.getServer().addSynchronousTask(task);

	    return true;
	}

	protected abstract int getDurationInSeconds(MagicContext context);

	protected abstract float getFlightSpeed(MagicContext context);

	private double getOngoingExhaustionBaseCost() {
		return 3.0;
	}
	private class OngoingFlightTask extends ServerTask {

		private MagicContext context;
		private int durationRemainingInSeconds;

	    public OngoingFlightTask(MagicContext context, int durationInSeconds) {
			super(Canary.getServer(), MinecraftUtils.secondsToTicks(1));
			this.context = context;
			this.durationRemainingInSeconds = durationInSeconds;
		}

	    public void run() {
	    	durationRemainingInSeconds--;
    		Player player = context.getPlayer();
	    	if (durationRemainingInSeconds == 0) {
		    	playerUuidFlyingMap.remove(player.getUUIDString());
		    	player.message(getName() + " spell ending");
		    	HumanCapabilities capabilities = player.getCapabilities();
				capabilities.setMayFly(false);
		    	capabilities.setFlying(false);
		    	float defaultFlySpeed = 0.05f;
		    	capabilities.setFlySpeed(defaultFlySpeed);
		    	player.updateCapabilities();
		    	Canary.getServer().removeSynchronousTask(this);
	    	} else {
		    	float exhaustionCost = (float) (getOngoingExhaustionBaseCost() / (1.0 + context.getSpellboost().getExhaustionReduction()));
	    		SpellExhaustionTask.addSpellExhaustion(player, exhaustionCost);
	    	}
	    }

	}
	
}
