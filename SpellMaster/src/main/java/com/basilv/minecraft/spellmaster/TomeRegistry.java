package com.basilv.minecraft.spellmaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of all magic tomes.
 * 
 * @author Basil
 */
public class TomeRegistry {

	private static final Map<String,Tome> tomeRegistryByName = new HashMap<>();

	public static void addTome(Tome tome) {
		tomeRegistryByName.put(tome.getName(), tome);
	}
	
	public static Tome getTomeForName(String name) {
		return tomeRegistryByName.get(name);
	}
	
	public static Collection<Tome> getTomes() {
		return tomeRegistryByName.values();
	}

	// TODO: Not used, remove?
	public static Optional<Spell> getSpellForName(String name) {
		return tomeRegistryByName.values().stream()
			.flatMap(tome -> tome.getSpells().stream())
			.filter(spell -> spell.getName().equals(name))
			.findFirst();
	}
	
}
