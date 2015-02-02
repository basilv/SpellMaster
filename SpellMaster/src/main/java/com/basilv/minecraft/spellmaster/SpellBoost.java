package com.basilv.minecraft.spellmaster;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an increase in one or more aspects of a spell.
 * 
 * @author Basil
 */
public class SpellBoost {

	private final int casterLevel;
	private final int damage;
	private final int range;
	private final int durationInSeconds;
	
	public SpellBoost() {
		this(0, 0, 0, 0);
	}
	
	public SpellBoost(int casterLevel, int damage, int range, int durationInSeconds) {
		this.casterLevel = casterLevel;
		this.damage = damage;
		this.range = range;
		this.durationInSeconds = durationInSeconds;
	}
	
	public SpellBoost(Collection<Tome> tomes) {
		// Put into a set to avoid getting a bonus multiple times from the same tome.
		Set<Tome> tomeSet = new HashSet<>(tomes);
		int tmpCasterLevel = 0;
		int tmpDamage = 0;
		int tmpRange = 0;
		int tmpDuration = 0;
		for (Tome tome : tomeSet) {
			SpellBoost boost = tome.getSpellBoost();
			tmpCasterLevel += boost.getCasterLevel();
			tmpDamage += boost.getDamage();
			tmpRange += boost.getRange();
			tmpDuration += boost.getDurationInSeconds();
		}

		casterLevel = tmpCasterLevel;
		damage = tmpDamage;
		range = tmpRange;
		durationInSeconds = tmpDuration;
	}

	public int getCasterLevel() {
		return casterLevel;
	}

	public int getDamage() {
		return damage;
	}

	public int getRange() {
		return range;
	}

	public int getDurationInSeconds() {
		return durationInSeconds;
	}
	
}
