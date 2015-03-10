package com.basilv.minecraft.spellmaster.spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.effects.SoundEffect.Type;
import net.canarymod.api.world.position.Position;

import com.basilv.minecraft.spellmaster.MagicContext;
import com.basilv.minecraft.spellmaster.tomes.FireMagicTome.FireSpell;
import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

public class DragonsBreathSpell extends FireSpell {

	public DragonsBreathSpell() {
		super("Dragon's Breath");
		setCastingMinimumLevel(30);
		setCastingFocus("Blaze Powder", ItemType.BlazePowder);
		setCastingComponent("Blaze powder", ItemType.BlazePowder, 1);
	}
	
	@Override
	protected void populateSpellSpecificCastingInformation(List<String> lines) {
		lines.addAll(Arrays.asList(
		   "Range: 1 square per 2 levels", 
		   "Damage: 1 to 4 + 1 per 2 levels. +2 damage if already on fire",
		   "Breathe fire in a cone extending in the direction you are facing, dealing heavy damage and setting creatures and blocks on fire. "
		));
	}
	
	@Override
	protected Type getSpellSoundEffect() {
		return SoundEffect.Type.ENDERDRAGON_GROWL;
	}

	@Override
	protected boolean createCastingGameEffect(MagicContext context) {

		Player player = context.getPlayer();
		// Calculate direction to breathe fire in
		Position positionFacingAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerFacing(player);
		Position positionLeftSideAdjustment = MinecraftUtils.getPositionAdjustmentForDirectionPlayerLeftSide(player);

		int range = context.getCastingRange(0, 2);
		List<Block> blocksAffected = new ArrayList<Block>();
		for (int height = -1; height <= 1; height++) {
			Position centerPosition = player.getPosition().copy();
			centerPosition.moveY(height);
			int coneWidth = -1; // First square will have coneWidth = 0 which is just the center square. Next will have width = 1 which is 3 squares across.
			for (int distance = 2; distance <= range; distance++) {
				centerPosition.move(positionFacingAdjustment.getBlockX(), 0, positionFacingAdjustment.getBlockZ());
				coneWidth++;
				for (int widthIndex = -coneWidth; widthIndex <= coneWidth; widthIndex++) {
					Position sidePosition = centerPosition.copy();
					sidePosition.move(positionLeftSideAdjustment.getBlockX()*widthIndex, 0, positionLeftSideAdjustment.getBlockZ()*widthIndex);
					breathFireAt(context, sidePosition).ifPresent(block -> blocksAffected.add(block));
				}
			}
		}
		
		blocksAffected.stream().forEach(block -> block.update());
		
		int damage = MinecraftUtils.randomNumberWithinRange(1, 4) + context.getCastingLevel() / 2 + context.getSpellboost().getDamage(); 
		context.getWorld().getEntityLivingList().stream().forEach(entity -> damageEntityIfInSpellArea(entity, player, blocksAffected, damage)); 
		context.getWorld().getPlayerList().stream().forEach(entity -> damageEntityIfInSpellArea(entity, player, blocksAffected, damage)); 
		
		return true;
	}

	private void damageEntityIfInSpellArea(LivingBase entity, Player player, List<Block> blocksAffected, int damage) {
		Position entityPosition = entity.getPosition();
		if (blocksAffected.stream().anyMatch(block -> MinecraftUtils.getSeparationInBlocks(block.getPosition(), entityPosition) == 0)) {
			player.attackEntity(entity, damage);
		}
	}
	
	
	private Optional<Block> breathFireAt(MagicContext context, Position position) {
		World world = context.getWorld();
		Block block = world.getBlockAt(position);
		if (block.isAir()) {
			block.setType(BlockType.FireBlock);
			return Optional.of(block);
		} else if (block.getType().equals(BlockType.Water) || block.getType().equals(BlockType.WaterFlowing)) {
			block.setType(BlockType.Air); // With any volume of water this does basically nothing
			context.spawnParticle(block.getLocation(), Particle.Type.SMOKE_NORMAL);
			return Optional.of(block);
		} else if (block.getType().equals(BlockType.LavaFlowing)) {
			block.setType(BlockType.Lava); // TODO: Doesn't seem to work
			return Optional.of(block);
		} else {
			return Optional.empty();
		}
	}

}
