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

public class CreateSnowGolemSpell extends Spell {

	public CreateSnowGolemSpell() {
		super("Create Snow Golem");
		setCastingMinimumLevel(5);
		setCastingFocus("Snow block", ItemType.SnowBlock); // TODO: Figure out focus
//		setCastingComponent(ItemType.RottenFlesh, "Rotten Flesh", 6); // TODO: Consider using more exotic item?
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Create a snow golem nearby."
		));
	}

	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.IRONGOLEM_WALK;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		// TODO: Or summon at block clicked?
		Player player = context.getPlayer();
		int summonDistance = 5;
		Location summonLocation = player.getLocation();
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		summonLocation.move(summonDistance * positionAdjustment.getBlockX(), 0, summonDistance * positionAdjustment.getBlockZ());
	    EntityFactory factory = Canary.factory().getEntityFactory();
	    EntityLiving snowGolem = factory.newEntityLiving(EntityType.SNOWMAN, summonLocation);
	    snowGolem.setDisplayName("Created Golem");
	    snowGolem.spawn();
		
	    return true;
	}

}
