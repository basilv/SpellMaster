package com.basilv.minecraft.spellmaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;

import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

/**
 * Base class representing magical book containing spells, ceremonies, and bonuses to spellcasting.
 * @author Basil
 */
public abstract class Tome extends NamedObject implements Ceremony.CeremonyCapable {

	private static final String TOME_NAME_DATA_TAG = "spellmaster.tomeName"; 
	// TODO: Have id used to identify tome in minecraft item tag different from book display name.

	public static Set<Tome> getTomesInPlayerInventory(Player player) {
		
		Set<Tome> tomes = Arrays.stream(player.getInventory().getContents())
			.filter(item -> item != null 
				&& ItemType.WrittenBook.equals(item.getType())
				&& item.getDataTag().containsKey(TOME_NAME_DATA_TAG) )
			.map(item -> { 
				String tomeName = item.getDataTag().getString(TOME_NAME_DATA_TAG);
				return TomeRegistry.getTomeForName(tomeName); 
			})
			.collect(Collectors.toSet());
		
		return tomes;
	}

	public static List<Spell> getSpellsFromTomes(Collection<Tome> tomes) {
		return tomes.stream()
			.flatMap(tome -> tome.getSpells().stream())
			.collect(Collectors.toList());
	}
	
	private final int tomeLevel;
	private List<Spell> spells = new ArrayList<>();
	private List<Ceremony> ceremonies = new ArrayList<>();
	private SpellBoost spellBoost = new SpellBoost();

	private MagicComponent ceremonyComponent;
	private MagicFocus ceremonyFocus;
	
	public Tome(String name, int tomeLevel) {
		super(name);
		this.tomeLevel = tomeLevel;
	}

	public int getTomeLevel() {
		return tomeLevel;
	}

	public Ceremony createCeremony() {
		return new Ceremony(this);
	}
	
	public List<Ceremony> getCeremonies() {
		return ceremonies;
	}

	private void addCeremony(Ceremony ceremony) {
		ceremonies.add(ceremony);
	}
	
	public SpellBoost getSpellBoost() {
		return spellBoost;
	}

	public void setSpellBoost(SpellBoost spellBoost) {
		this.spellBoost = spellBoost;
	}

	public List<Spell> getSpells() {
		return spells;
	}
	
	public final void addSpell(Spell spell) {
		spells.add(spell);
	}
	
	public final void giveTomeToPlayer(Player player) {
		Item item = createBook();
		MinecraftUtils.giveItemToPlayer(player, item);
	}

	
	protected final Item createBook() {
		List<List<String>> pages = new ArrayList<>();
		
		List<String> intro = getBookIntroduction();
		if (intro != null) {
			pages.add(intro);
		}
		
		spells.forEach(spell -> pages.add(spell.createBookPage()));

		ceremonies.forEach(ceremony -> pages.add(ceremony.createBookPage())); 
		
		Item item = MinecraftUtils.createWrittenBookWithContent(getName(), pages, null);
		item.getDataTag().put(TOME_NAME_DATA_TAG, getName());
		
		return item;
	}

	/**
	 * @return lines of text for the introduction page(s) of this tome, or null if there is no introduction.
	 */
	protected abstract List<String> getBookIntroduction();

	/**
	 * Add the specified tome to this tome as a ceremony and register the tome.
	 * @param tome
	 */
	public final void addTome(Tome tome) {
		addCeremony(tome.createCeremony());
		TomeRegistry.addTome(tome);
	}

	/**
	 * @param itemDescription Must not be null.
	 * @param itemType Must not be null.
	 * @param numberConsumed May be null to indicate that the component is not consumed.
	 */
	public final void setCeremonyComponent(String itemDescription, ItemType itemType, Integer numberConsumedPerCast) {
		this.ceremonyComponent = new MagicComponent(itemDescription, itemType, numberConsumedPerCast); 
	}

	public final void setCeremonyFocus(String itemDescription, ItemType... itemTypes) {
		this.ceremonyFocus = new MagicFocus(itemDescription, itemTypes); 
	}
	
	protected final MagicFocus getCeremonyFocus() {
		return ceremonyFocus;
	}

	protected final MagicComponent getCeremonyComponent() {
		return ceremonyComponent;
	}

	protected abstract int getCeremonyMinimumLevel();
	
	public final void populateCeremonyInformation(List<String> lines) {
		lines.add("Benefit: Obtain tome " + getName());
		lines.add("Focus: " + ceremonyFocus.toString());
		lines.add("Min level: " + getCeremonyMinimumLevel());
		lines.add("Cost in levels: " + getCeremonyLevelCost());
		if (ceremonyComponent != null) {
			lines.add("Component: " + ceremonyComponent);
		}
		populateTomeSpecificCeremonyInformation(lines);
	}

	/**
	 * Populate additional information about the ceremony beyond that provided by populateCeremonyInformation(), organized into lines of text.
	 * Each line of text will be wrapped as necessary.
	 * @param lines 
	 */
	protected abstract void populateTomeSpecificCeremonyInformation(List<String> lines);

	
	protected boolean canPerformCeremonyWithHeldItem(Item item) {
		return ceremonyFocus.isItemAFocus(item);
	}

	/**
	 * Try to perform the ceremony to learn this spell.  
	 * @param context Cannot be null.
	 * @return true if the ceremony was performed, false otherwise
	 */
	public final boolean tryPerformCeremony(MagicContext context) {
		
		Player player = context.getPlayer();
		if (!canPerformCeremonyWithHeldItem(player.getItemHeld())) {
			return false;
		}
		
		if (context.doesPlayerHaveTome(this.getClass())) {
			// No warning, as a different ceremony might end up triggering.
			return false;
		}

		if (!isCeremonyRequirementsMet(context)) {
			return false;
		}
		
		if (!createCeremonyGameEffect(context)) {
			return false;
		}

		log("Performning ceremony " + getCeremonyName());
		giveTomeToPlayer(player);
		
		applyCeremonyCost(context);
		return true;
	}

	protected boolean createCeremonyGameEffect(MagicContext context) {
		// Default implementation does nothing
		return true;
	}

	protected void applyCeremonyCost(MagicContext context) {
		Player player = context.getPlayer();
		if (ceremonyComponent != null) {
			ceremonyComponent.consumeForUse(player);
		}
		player.removeLevel(getCeremonyLevelCost());
	}

	/**
	 * @return cost in player levels to perform ceremony.
	 */
	protected abstract int getCeremonyLevelCost();

	/**
	 * @param context
	 * @return true if the requirements for the ceremony are met. 
	 */
	protected final boolean isCeremonyRequirementsMet(MagicContext context) {

		Player player = context.getPlayer();
		if (context.getCastingLevel() < getCeremonyMinimumLevel()) {
			sendPlayerUnableToPerformCeremonyMessage(player, "Need a casting level of " + getCeremonyMinimumLevel());
			return false;
		}
		
		if (ceremonyComponent != null) {
			if (!ceremonyComponent.isPossessedByPlayer(player)) {
				sendPlayerUnableToPerformCeremonyMessage(player, "Missing required components.");
				return false;
			}
		}

		if (!isCeremonyConditionsSpecificToTomeMet(context)) {
			return false;
		}
		
		return true;
	}

	/**
	 * @param context
	 * @return true if requirements specific to this tome are met (not including common requirements checked in isCeremonyRequirementsMet).
	 */
	protected abstract boolean isCeremonyConditionsSpecificToTomeMet(MagicContext context);
	
	protected final void sendPlayerUnableToPerformCeremonyMessage(Player player, String reason) {
		player.chat("Unable to perform ceremony: " + reason);
	}
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	protected final void log(String message) {
		logger.info(message);
	}

}
