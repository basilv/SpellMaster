package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;
import net.canarymod.tasks.ServerTask;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.SpellExhaustionTask;
import com.basilv.minecraft.spellmaster.tomes.AirMagicTome.AirSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class WindGustsSpell extends AirSpell {

	private static final ConcurrentHashMap<String,Boolean> playerUuidGustOfWindMap = new ConcurrentHashMap<>();

	public WindGustsSpell() {
		super("Wind Gusts");
		setCastingMinimumLevel(10);
		setCastingFocus("Lilypad", ItemType.Lilypad);
		// No component - spell is extra tiring.
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Duration: 1 second per level", 
		   "Range: 1 square per level", 
		   "Create gusts of wind that push nearby creatures away in the direction the player is facing. " +
		   "The strenth of the push increases with casting level. " +
		   "Maintaining this spell requires that effort be spent throughout the duration."
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.BREATH;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		
		if (playerUuidGustOfWindMap.containsKey(player.getUUIDString())) {
			sendPlayerUnableToCastMessage(player, "Spell is already active.");
			return false;
		}
		
		int durationInSeconds = context.getCastingLevel() + context.getSpellboost().getDurationInSeconds();
		GustOfWindTask task = new GustOfWindTask(context, durationInSeconds);
	    Canary.getServer().addSynchronousTask(task);
		playerUuidGustOfWindMap.put(player.getUUIDString(), Boolean.TRUE);

		// Perform initial gust since task won't trigger for 1 second.
		knockBackEntities(context);
		
	    return true;
	}

	private class GustOfWindTask extends ServerTask {
	
		private MagicContext context;
		private int durationRemainingInSeconds;
	
	    public GustOfWindTask(MagicContext context, int durationInSeconds) {
			super(Canary.getServer(), MinecraftUtils.secondsToTicks(1.5), true);
			this.context = context;
			this.durationRemainingInSeconds = durationInSeconds;
		}
	
	    public void run() {
	    	Player player = context.getPlayer();
	    
	    	durationRemainingInSeconds--;
	    	if (durationRemainingInSeconds == 0) {
	    		playerUuidGustOfWindMap.remove(player.getUUIDString());
	    		player.message(getName() + " spell ending");
	    		Canary.getServer().removeSynchronousTask(this);
	    		return;
	    	}

	    	float exhaustionCost = (float) (1.0 / (1.0 + context.getSpellboost().getExhaustionReduction()));
	    	SpellExhaustionTask.addSpellExhaustion(player, exhaustionCost);

	    	playSpellSound(player);
			knockBackEntities(context);
	    }

	}

	private void knockBackEntities(MagicContext context) {
		knockBackMembersOfStream(context, context.getWorld().getEntityLivingList().stream());
		knockBackMembersOfStream(context, context.getWorld().getPlayerList().stream());
	}

	private void knockBackMembersOfStream(MagicContext context, Stream<? extends LivingBase> stream) {
    	Player player = context.getPlayer();
		int range = context.getCastingRange(0, 1);
		
		// 'Damage' = strength of push
		// From 0.5 at minimum level (10) - barely slows down spiders, limited slowdown of zombies
		// to 1.0 at level 30 - somewhat slows down spiders, creepers/zombies almost entirely slowed down.
		// 0.025 = 0.5 / 20
		float strength = (context.getCastingLevel() + context.getSpellboost().getDamage() - 10) * 0.025f + 0.5f;
		
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);

		stream
			.filter(entity -> MinecraftUtils.getSeparationInBlocks(player.getPosition(), entity.getPosition()) < range)
			.filter(entity -> !(entity.equals(context.getPlayer())) )
			.forEach(entity -> {
				entity.moveEntity(positionAdjustment.getX() * strength, 0, positionAdjustment.getZ() * strength);
			});
		
	}
	
}
