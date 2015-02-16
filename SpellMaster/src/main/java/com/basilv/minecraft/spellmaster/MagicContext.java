package com.basilv.minecraft.spellmaster;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.position.Location;

/**
 * Context for performing magic: casting a spell or performing a ceremony. 
 * @author Basil
 */
public class MagicContext {

	private final Player player;
	private final Block blockClicked; 
	private final List<Tome> tomes;
	private final List<Spell> spells;
	private final SpellBoost spellboost;
	
	public MagicContext(Player player, Block blockClicked) {
		this.player = player;
		this.blockClicked = blockClicked;
		
		tomes = Tome.getTomesInPlayerInventory(player).stream()
			.sorted((first, second) -> -1 * Integer.valueOf(first.getTomeLevel()).compareTo(Integer.valueOf(second.getTomeLevel())))
			.collect(Collectors.toList());
		spells = Tome.getSpellsFromTomes(tomes);
		spellboost = new SpellBoost(tomes);
	}
	
	public Item getItemHeld() {
		return player.getItemHeld();
	}
	
	public int getCastingLevel() {
		return player.getLevel() + spellboost.getCasterLevel();
	}
	
	public int getCastingRange(int minimumRange, double levelsPerIncreaseInRange) {
		return minimumRange + (int)((double)getCastingLevel() / levelsPerIncreaseInRange) + spellboost.getRange();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public World getWorld() {
		return player.getWorld();
	}
	
	public Block getBlockClicked() {
		return blockClicked;
	}

	public List<Tome> getTomes() {
		return tomes;
	}

	public List<Spell> getSpells() {
		return spells;
	}

	public SpellBoost getSpellboost() {
		return spellboost;
	}

	@SuppressWarnings("rawtypes") 
	public long countTomesPlayerHas(Class... tomeClasses) {
		return Arrays.stream(tomeClasses).filter(clazz -> doesPlayerHaveTome(clazz)).count();
	}
	
	public boolean doesPlayerHaveTome(@SuppressWarnings("rawtypes") Class clazz) {
		return tomes.stream().anyMatch(tome -> clazz.getCanonicalName().equals(tome.getClass().getCanonicalName()));
	}
	
	public void spawnParticle(Location location, Particle.Type type) {
		getWorld().spawnParticle(new Particle(location.getX(), location.getY(), location.getZ(), type));
	}
	
}
