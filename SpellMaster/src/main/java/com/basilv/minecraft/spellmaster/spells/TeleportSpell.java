package com.basilv.minecraft.spellmaster.spells;

import java.util.Arrays;
import java.util.List;

import net.canarymod.api.DamageType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class TeleportSpell extends AbstractTeleportSpell {

	public TeleportSpell() {
		super("Teleport");
		setCastingMinimumLevel(20); 
		setCastingFocus("Gold boots", ItemType.GoldBoots);
	}
	
	@Override
	protected float getCastingExhaustionCost() {
		return 6;
	}

	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 1 chunk per 5 levels (1 chunk = 16 blocks)", 
		   "Costs more health and is more exhausting than normal.",
		   "Teleport horizontally in the direction you are facing after a short delay. ",
		   "The spell will not function and cause a little damage if your end point is occupied by solid material. "
		   + "Teleporting into a narrow space can cause a little damage as well."
		));
	}
	
	@Override
	protected Position getTargetPosition(MagicContext context) {
		Player player = context.getPlayer();
		int rangeInChunks = context.getCastingRange(0, 5);

		Position position = getInitialTargetPosition(player, MinecraftUtils.chunksToBlocks(rangeInChunks));
		
		if (!canPlayerTeleportToPosition(context.getWorld(), position)) {
			player.message("Destination is occupied by solid material.");
			player.dealDamage(DamageType.SUFFOCATION, 1);
			return null;
		} else {
			return position;
		}
	}

}
