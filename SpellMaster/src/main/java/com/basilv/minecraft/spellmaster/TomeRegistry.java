package com.basilv.minecraft.spellmaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

}
