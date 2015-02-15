package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.entity.living.EntityLiving;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.EntityFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class SummonZombieSpell extends Spell {

	public SummonZombieSpell() {
		super("Summon Zombie");
		setCastingMinimumLevel(5);
		setCastingFocus("Rotten flesh", ItemType.RottenFlesh); 
		setCastingComponent("Rotten Flesh", ItemType.RottenFlesh, 2); 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		    "Summon a zombie warrior nearby."
		    // TODO: Range???
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.ZOMBIE_SAY;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		// TODO: Or summon at block clicked?
		Player player = context.getPlayer();

		int summonDistance = 7;
		Location summonLocation = player.getLocation();
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		summonLocation.move(summonDistance * positionAdjustment.getBlockX(), 0, summonDistance * positionAdjustment.getBlockZ());
	    EntityFactory factory = Canary.factory().getEntityFactory();

	    EntityLiving pig = factory.newEntityLiving(EntityType.PIG, summonLocation);
	    EntityLiving zombie = factory.newEntityLiving(EntityType.PIGZOMBIE, summonLocation);
	    zombie.setDisplayName("BOB");
	    pig.spawn(zombie);
	    
	    
//	    EntityMob zombie = factory.newEntityMob(EntityType.ZOMBIE, summonLocation);
//	    zombie.setDisplayName("BOB");

//	    Set an item to a given slot.
//	    0 = item in hand
//	    1 = boots
//	    2 = leggings
//	    3 = chestplate
//	    4 = helmet
/*
	    ItemFactory itemFactory = Canary.factory().getItemFactory();
		Item sword = itemFactory.newItem(ItemType.DiamondSword);
		sword.addEnchantments(new CanaryEnchantment(Enchantment.Type.Sharpness, (short) 5));
		zombie.setEquipment(sword, 0); 
		Item helmet = itemFactory.newItem(ItemType.DiamondHelmet);
		helmet.addEnchantments(new CanaryEnchantment(Enchantment.Type.Protection, (short) 5));
		zombie.setEquipment(helmet, 4); // Allow to live in sun
		Item chestPlate = itemFactory.newItem(ItemType.DiamondChestplate);
		chestPlate.addEnchantments(new CanaryEnchantment(Enchantment.Type.Thorns, (short) 3));
		zombie.setEquipment(chestPlate, 3);
		zombie.setEquipment(itemFactory.newItem(ItemType.DiamondLeggings), 2);
		zombie.setEquipment(itemFactory.newItem(ItemType.DiamondBoots), 1);
		EntityLiving target = null;
		for (EntityLiving entity : player.getWorld().getEntityLivingList()) {
			int separation = MinecraftUtils.getSeparationInBlocks(entity.getPosition(), blockClicked.getPosition());
//			log("Separation " + separation + " for entity " + entity.getDisplayName());
			if (separation <= 6) {
				target = entity;
				break;
			}
		}
		if (target != null) {
			log("Setting target for zombie to attack with name " + target.getDisplayName()); 
			// TODO: Not working!!!
			zombie.setAttackTarget(target);
			zombie.attackEntity(target, 5);
		}
//		zombie.getAITargetTaskManager().addTask
	    zombie.spawn();
	    TODO: See http://canarymod.net/books/api-reference/custom-artificial-intelligenceai-living-entities
*/		
	    return true;
	}

}
