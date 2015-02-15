package com.basilv.minecraft.spellmaster;

import java.util.concurrent.ConcurrentHashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.tasks.ServerTask;

import com.basilv.minecraft.spellmaster.util.MinecraftUtils;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * This task applies exhaustion from casting spells to players.
 *
 */
public class SpellExhaustionTask extends ServerTask {

	private static final ConcurrentHashMap<String,AtomicDouble> playerUuidSpellExhaustionMap = new ConcurrentHashMap<>();

	public static void addSpellExhaustion(Player player, float exhaustionToAdd) {

		AtomicDouble exhaustion = new AtomicDouble(exhaustionToAdd);
		applyExhaustionToPlayer(player, exhaustion);
		double exhaustionRemainingToAdd = exhaustion.get();
		if (exhaustionRemainingToAdd > 0) {
			AtomicDouble spellExhaustion = playerUuidSpellExhaustionMap.getOrDefault(player.getUUIDString(), new AtomicDouble(0));
			spellExhaustion.addAndGet(exhaustionRemainingToAdd);
			playerUuidSpellExhaustionMap.put(player.getUUIDString(), spellExhaustion);
		}
	}
	
	public SpellExhaustionTask() {
		super(Canary.getServer(), MinecraftUtils.secondsToTicks(1)/2, true); // Run every 0.5 seconds
	}
	
	@Override
	public void run() {
		playerUuidSpellExhaustionMap.entrySet().stream().forEach(entry -> {
			String playerUuid = entry.getKey();
			Player player = Canary.getServer().getPlayerFromUUID(playerUuid);
			if (entry.getValue().get() > 0 && player != null) {
				applyExhaustionToPlayer(player, entry.getValue());
			} else {
				playerUuidSpellExhaustionMap.remove(playerUuid);
			}
		});
	}

	private static void applyExhaustionToPlayer(Player player, AtomicDouble spellExhaustion) {
		float amountToApply = (float) Math.min(4.0 - player.getExhaustionLevel(), spellExhaustion.get());

		player.setExhaustion(player.getExhaustionLevel() + amountToApply);
		spellExhaustion.addAndGet(-1 * amountToApply);
	}

}
