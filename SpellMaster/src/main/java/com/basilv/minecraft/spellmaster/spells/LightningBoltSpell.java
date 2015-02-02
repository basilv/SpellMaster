package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.BlockIterator;
import net.canarymod.LineTracer;
import net.canarymod.api.DamageType;
import net.canarymod.api.entity.living.EntityLiving;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.effects.SoundEffect;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class LightningBoltSpell extends Spell {

	public LightningBoltSpell() {
		super("Lightning Bolt");
		setCastingMinimumLevel(10);
		setCastingFocus("Nether quartz", ItemType.NetherQuartz); // TODO: Figure out different focus. Nether quartz?
		setCastingComponent(ItemType.RedStone, "Redstone", 3);
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		// Increasing damage in rain/storm is designed to synergize with Control Weather spell.
		lines.addAll(Arrays.asList(
		   "Range: 2 squares + 1 per 2 levels",
 		   "Damage: 5 to 10 + 1 per 5 levels. +3 damage when raining, +7 damage in a thunderstorm",
		   "Blast target block with lightning. Deals full damage to entities at the targetted block and decreasing damage to entities further away."
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.AMBIENCE_THUNDER;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		boolean includeAir = false;
		BlockIterator sightItr = new BlockIterator(new LineTracer(player),
				includeAir);
		if (!sightItr.hasNext()) {
			sendPlayerUnableToCastMessage(player,
					"Only targetting air - must target a non-air block");
			return false;
		}

		Block block = sightItr.next();
		int maxRange = context.getCastingRange(2, 2);
		int range = MinecraftUtils.getSeparationInBlocks(block.getPosition(),
				player.getPosition());
		if (range > maxRange) {
			sendPlayerUnableToCastMessage(player,
					"Out of range. Maximum range = " + maxRange);
			return false;
		}

		World world = player.getWorld();
		world.makeLightningBolt(block.getPosition());

		int originDamage = randomNumberWithinRange(5, 10) + context.getCastingLevel() / 5 + context.getSpellboost().getDamage();
		if (world.isThundering()) {
			originDamage += 7;
		} else if (world.isRaining()) {
			originDamage += 3;
		}

		List<EntityLiving> entities = world.getEntityLivingList();
		for (EntityLiving entity : entities) {
			int separation = MinecraftUtils.getSeparationInBlocks(
					entity.getPosition(), block.getPosition());
			int damage = originDamage;
			// Separation	Damage with Origin = 10
			// 1			7
			// 2			4
			// 3			1
			if (separation > 0) {
				damage -= (3 * separation);
			}
			if (damage > 0) {
				// TODO: Entities killed by this spell are not dropping experience. Maybe need to specify attacker?
//				entity.setLastAssailant(player); // TODO: Try this. Not working, made mob jump towards player (one of the 3 commands did)
				entity.setRevengeTarget(player);
//				entity.setAttackTarget(player);
				entity.dealDamage(DamageType.LIGHTNINGBOLT, damage);

			}
		}

		return true;
	}
	
}
