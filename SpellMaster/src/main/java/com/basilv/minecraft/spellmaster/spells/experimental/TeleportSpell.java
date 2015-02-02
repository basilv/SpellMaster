package com.basilv.minecraft.spellmaster.spells.experimental;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;
import net.canarymod.api.world.position.Vector3D;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class TeleportSpell extends Spell {

	public TeleportSpell() {
		super("Teleport");
		setCastingMinimumLevel(20); 
		setCastingFocus("Iron boots", ItemType.IronBoots); // TODO: Figure out focus, components 
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: level * 10 squares - 100", 
		   "Teleport horizontally in the direction you are facing. The spell will not function if your end point is solid earth and may cause you extra damage."
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
		// Issue with player.teleportTo is that post-teleport, player facing is always SOUTH and can't seem to be changed. 
		// TeleportTo will also put player into a solid block, while translate won't.
		// Translate works with the correct facing but refuses to teleport into a solid block
		// TODO: Some issues observed with using translate in straight tunnels - sometimes need a third block above clear (air) at the target square and jump before teleporting.
		// Adding a small y adjustment up doesn't help with the need to jump.
		double yAdjustment = 0; 
		Vector3D vector = new Vector3D(positionAdjustment.getBlockX() * range, yAdjustment, positionAdjustment.getBlockZ() * range);
		player.translate(vector);
		
	    return true;
	}
	
}
