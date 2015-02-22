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
	private final int exhaustionReduction;
	
	public SpellBoost() {
		this(0, 0, 0, 0, 0);
	}
	
	public SpellBoost(int casterLevel, int damage, int range, int durationInSeconds, int exhaustionReduction) {
		this.casterLevel = casterLevel;
		this.damage = damage;
		this.range = range;
		this.durationInSeconds = durationInSeconds;
		this.exhaustionReduction = exhaustionReduction;
	}
	
	public SpellBoost(Collection<Tome> tomes) {
		// Put into a set to avoid getting a bonus multiple times from the same tome.
		Set<Tome> tomeSet = new HashSet<>(tomes);
		int tmpCasterLevel = 0;
		int tmpDamage = 0;
		int tmpRange = 0;
		int tmpDuration = 0;
		int tmpExhaustionReduction = 0;
		for (Tome tome : tomeSet) {
			SpellBoost boost = tome.getSpellBoost();
			tmpCasterLevel += boost.getCasterLevel();
			tmpDamage += boost.getDamage();
			tmpRange += boost.getRange();
			tmpDuration += boost.getDurationInSeconds();
			tmpExhaustionReduction += boost.getExhaustionReduction();
		}

		casterLevel = tmpCasterLevel;
		damage = tmpDamage;
		range = tmpRange;
		durationInSeconds = tmpDuration;
		exhaustionReduction = tmpExhaustionReduction;
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

	public int getExhaustionReduction() {
		return exhaustionReduction;
	}

	// TODO: Have description of spellboost, use in Tome class as part of description? and in list tomes command
}
