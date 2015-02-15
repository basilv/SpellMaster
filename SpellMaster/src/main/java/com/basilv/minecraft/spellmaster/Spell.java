package com.basilv.minecraft.spellmaster;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.ChatFormat;

import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

/**
 * Base class for spells.
 * Spells are typically cast by right-clicking a spell-casting item.
 * Spells typically have requirements to cast (e.g. minimum level, valid target, available spell components) and a cost to cast (e.g. increased hunger).
 * 
 * @author Basil
 */
public abstract class Spell extends NamedObject {

	protected final Logger logger = Logger.getLogger(getClass().getName());

	private MagicFocus castingFocus;
	private MagicComponent castingComponent;
	private int castingMinimumLevel = 1;

	public Spell(String name) {
		super(name);
	}

	public MagicFocus getCastingFocus() {
		return castingFocus;
	}

	public final void setCastingFocus(String itemDescription, ItemType... itemTypes) {
		castingFocus = new MagicFocus(itemDescription, itemTypes);
	}
	
	public MagicComponent getCastingComponent() {
		return castingComponent;
	}

	/**
	 * @param itemDescription Must not be null.
	 * @param itemType Must not be null.
	 * @param numberConsumed May be null to indicate that the component is not consumed.
	 */
	public final void setCastingComponent(String itemDescription, ItemType itemType, Integer numberConsumedPerCast) {
		this.castingComponent = new MagicComponent(itemDescription, itemType, numberConsumedPerCast); 
	}
	
	public final int getCastingMinimumLevel() {
		return castingMinimumLevel;
	}

	public final void setCastingMinimumLevel(int castingMinimumLevel) {
		this.castingMinimumLevel = castingMinimumLevel;
	}

	/**
	 * @param item May be null if player is not holding an item
	 * @return true if the spell can be cast with the specified held item.
	 */
	public boolean canCastSpellWithHeldItem(Item item) {
		return castingFocus.isItemAFocus(item);
	}

	public final List<String> createBookPage() {
		List<String> spellPage = new ArrayList<String>();
		spellPage.add(ChatFormat.BOLD + getName() + " Spell" + ChatFormat.RESET);
		spellPage.add(""); // Blank line
		populateCastingInformation(spellPage);
		return spellPage;
	}

	public final void populateCastingInformation(List<String> lines) {
		lines.add("Focus: " + getCastingFocus());
		lines.add("Minimum level: " + getCastingMinimumLevel());
		if (castingComponent != null) {
			lines.add("Component: " + castingComponent);
		}
		populateSpellSpecificCastingInformation(lines);
	}

	/**
	 * Populate casting information about the spell beyond that provided by populateCastingInformation(), organized into lines of text.
	 * Each line of text will be wrapped as necessary.
	 * @param lines
	 */
	protected abstract void populateSpellSpecificCastingInformation(List<String> lines);

	/**
	 * Create the game effect of casting the spell. This does NOT include visual/sound effects or paying the spell cost.
	 * @param context Cannot be null.
	 * @return true if the spell was cast and the cost should be paid, false otherwise.
	 */
	protected abstract boolean createCastingGameEffect(MagicContext context);

	/**
	 * Default implementation subtracts one health and adds 4 to exhaustion level.
	 * @param player Cannot be null
	 */
	protected void applyCastingCost(MagicContext context) {
		
		// The idea behind this cost is to have restrictions on repeated castings, especially in combat when taking damage
		// Using lots of spells will require more food, another balancing point to prevent overuse at the start of the game before
		// the player has amassed significant food resources.
		
		Player player = context.getPlayer();
		float exhaustionCost = Math.max(getCastingExhaustionCost() - context.getSpellboost().getExhaustionReduction(), 0);
		SpellExhaustionTask.addSpellExhaustion(player, exhaustionCost);

		player.setHealth(player.getHealth() - getCastingHealthCost());

		if (castingComponent != null) {
			castingComponent.consumeForUse(player);
		}
	}

	/**
	 * @return cost in health for casting the spell. Regenerating each health lost will cost almost a full level of saturation / hunger 
	 * and regeneration only happens if hunger level is > 18. Default cost 0.7 health
	 */
	protected float getCastingHealthCost() {
		return 0.7f;
	}

	/**
	 * @return cost in exhaustion for casting the spell. Level 4 (the normal max) is equaly to one level of saturation / hunger. Levels higher 
	 * than 4 can be returned - the cost will be applied over time as needed to ensure the full cost is paid. Default cost 4 exhaustion.
	 */
	protected float getCastingExhaustionCost() {
		return 4;
	}

	/**
	 * If requirements are not met, a helpful message is displayed to the player.
	 * @param context
	 * @return true if the requirements for casting the spell are met. 
	 */
	private boolean isCastingRequirementsMet(MagicContext context) {

		// Don't check whether player has a book with the spell in their inventory:
		// this is how the spell is invoked in SpellMasterPlugin.
		Player player = context.getPlayer();
		
		if (context.getCastingLevel() < castingMinimumLevel) {
			sendPlayerUnableToCastMessage(player, " Minimum level to cast = " + castingMinimumLevel);
			return false;
		}
		
		if (player.getHealth() <= 1) {
			sendPlayerUnableToCastMessage(player, " Health is too low.");
			return false;
		}
		
		if (player.getHunger() <= 1) {
			sendPlayerUnableToCastMessage(player, " Energy level (hunger) is too low.");
			return false;
		}
		
		if (castingComponent != null) {
			if (!castingComponent.isPossessedByPlayer(player)) {
				sendPlayerUnableToCastMessage(player, " Lacking spell component " + castingComponent);
				return false;
			}
		}
		
		return true;
	}
	

	public final boolean tryCastSpell(MagicContext context) {
		
		Item itemHeld = context.getItemHeld();
		if (!canCastSpellWithHeldItem(itemHeld)) {
			return false;
		}
		
		if (!isCastingRequirementsMet(context)) {
			return false;
		}
		
		if (!createCastingGameEffect(context)) {
			return false;
		}
		logger.info("Casting spell " + getName());
		createCastingVisualAndSoundEffects(context.getPlayer());
		applyCastingCost(context);
		return true;
	}

	protected final void sendPlayerUnableToCastMessage(Player player, String reason) {
		player.chat("Unable to cast " + getName() + ": " + reason);
	}

	private void createCastingVisualAndSoundEffects(Player player) {

		MinecraftUtils.spawnParticleInFrontOfPlayer(player, Particle.Type.SPELL);
		
		Location playerLocation = player.getLocation();
		float volume = 1.0f;
		float pitch = 1.0f;
		player.getWorld().playSound(new SoundEffect(getSpellSoundEffect(), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), volume, pitch));
	}

	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.ORB;
	}

	protected final int randomNumberWithinRange(int low, int high) {
		int result = (int) (Math.random() * (high - low)) + low;
		return result;
	}
	
}
