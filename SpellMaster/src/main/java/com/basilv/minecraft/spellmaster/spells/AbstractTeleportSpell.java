package com.basilv.minecraft.spellmaster.spells;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.Spell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.basilv.minecraft.spellmaster.util.OneTimeServerTask;

public abstract class AbstractTeleportSpell extends Spell {

	public AbstractTeleportSpell(String name) {
		super(name);
	}
	
	@Override
	protected final float getCastingHealthCost() {
		return 1.0f;
	}

	@Override
	protected final SoundEffect.Type getSpellSoundEffect() {
		return SoundEffect.Type.ENDERMAN_PORTAL;
	}

	@Override
	protected final boolean createCastingGameEffect(MagicContext context) {

		Position position = getTargetPosition(context);
		if (position == null) {
			return false;
		} else {
			Player player = context.getPlayer();
			Canary.getServer().addSynchronousTask(new OneTimeServerTask(MinecraftUtils.secondsToTicks(MinecraftUtils.randomNumberWithinRange(1, 2))) {
				@Override
				public void doTask() {
					teleportPlayerToPosition(player, position);
				}
			});
			MinecraftUtils.setItemHeldDamage(player, player.getItemHeld().getDamage() + 2);
			return true;
		}
	}

	/**
	 * @param context
	 * @return the position to teleport to or null if there is no such valid position. 
	 */
	protected abstract Position getTargetPosition(MagicContext context); 

	protected final Position getInitialTargetPosition(Player player, int rangeInBlocks) {
		Position positionAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position position = player.getPosition().copy();
		position.move(positionAdjustment.getBlockX() * rangeInBlocks, 0, positionAdjustment.getBlockZ() * rangeInBlocks);
		
		// Ensure that player ends up centered on block to avoid getting covered by surrounding blocks. 
		position = new Position(0.5 + position.getBlockX(), position.getBlockY(), 0.5 + position.getBlockZ());
		return position;
	}
	
	private void teleportPlayerToPosition(Player player, Position position) {
		player.teleportTo(position.getX(), position.getY(), position.getZ(), player.getPitch(), player.getRotation());
	}

	protected final boolean canPlayerTeleportToPosition(World world, Position positionToCheck) {
		Position position = positionToCheck.copy();
		if (isSolid(world.getBlockAt(position))) {
			return false;
		}
		position.moveY(+1);
		return !isSolid(world.getBlockAt(position)); 
	}
	
	
	private boolean isSolid(Block block) {
		// Water is treated as non-solid.
		return block.getBlockMaterial().isSolid();
	}

}
