package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.DamageType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class TeleportSpell extends Spell {

	public TeleportSpell() {
		super("Teleport");
		setCastingMinimumLevel(20); 
		setCastingFocus("Iron boots", ItemType.IronBoots); // TODO: Figure out focus, components. Gold boots?
	}
	
	@Override
	protected float getCastingHealthCost() {
		return 1.0f;
	}

	@Override
	protected float getCastingExhaustionCost() {
		return 6;
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: level * 10 squares - 100", 
		   "Costs more health and is more exhausting than normal.",
		   "Teleport horizontally in the direction you are facing. ",
		   "The spell will not function and cause a little damage if your end point is occupied by solid material. "
		   + "Teleporting into a narrow space can cause a little damage as well."
		));
	}
	
	@Override
	protected SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.ENDERMAN_PORTAL;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		int range = context.getCastingRange(-100, 0.10);
		
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position position = player.getPosition().copy();
		position.move(positionAdjustment.getBlockX() * range, 0, positionAdjustment.getBlockZ() * range);
		World world = context.getWorld();
		boolean targetLowerSolid = world.getBlockAt(position).getBlockMaterial().isSolid();
		position.moveY(+1);
		boolean targetUpperSolid = world.getBlockAt(position).getBlockMaterial().isSolid();
		
		if (targetLowerSolid || targetUpperSolid) {
			player.chat("Destination is occupied by solid material.");
			player.dealDamage(DamageType.SUFFOCATION, 1);
			return false;
		}
		
		player.teleportTo(position.getBlockX(), position.getBlockY()-1, position.getBlockZ(), player.getPitch(), player.getRotation());
	    return true;
	}
	
}
