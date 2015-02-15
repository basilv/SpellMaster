package com.basilv.minecraft.spellmaster;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.hook.player.PlayerRespawnedHook;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

import com.basilv.minecraft.spellmaster.spells.experimental.CreateSnowGolemSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.LightSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.SummonZombieSpell;
import com.basilv.minecraft.spellmaster.tomes.IntroductorySpellcastingTome;

public class SpellMasterPlugin extends Plugin implements PluginListener {

	private final Logman logger;
	private SpellExhaustionTask spellExhaustionTask;

	public SpellMasterPlugin() {
		logger = getLogman();
	}

	private String getJarVersion() {
		return getClass().getPackage().getImplementationVersion();
	}
	
	@Override
	public boolean enable() {
		logger.info("Enabling SpellMaster plugin. Plugin version " + getVersion() + " JAR version " + getJarVersion());
		Canary.hooks().registerListener(this, this);
		try {
			Canary.commands().registerCommands(new SpellMasterCommands(logger), this, false);
		} catch (CommandDependencyException e) {
			logger.error("Duplicate command name", e);
		}

		spellExhaustionTask = new SpellExhaustionTask();
		Canary.getServer().addSynchronousTask(spellExhaustionTask);
		
		IntroductorySpellcastingTome introTome = new IntroductorySpellcastingTome();
		TomeRegistry.addTome(introTome);
		// TODO: Experimental spells - remove
		// Create light spell with focus: sunflower or stick (YellowFlower item type)
		// Higher level books reuse stick, but with more powerful spell?
		introTome.addSpell(new LightSpell()); 
		introTome.addSpell(new SummonZombieSpell());
		introTome.addSpell(new CreateSnowGolemSpell());

		return true;
	}

	@Override
	public void disable() {
		Canary.getServer().removeSynchronousTask(spellExhaustionTask);
	}

	@HookHandler // TODO: Test
	public void onPlayerConnection(ConnectionHook hook) {
		if (hook.isFirstConnection()) {
			welcomePlayer(hook.getPlayer());
		}
	}
	
	@HookHandler
	public void onPlayerSpawn(PlayerRespawnedHook hook) {
		welcomePlayer(hook.getPlayer());
	}

	private void welcomePlayer(Player player) {
		String tomeName = IntroductorySpellcastingTome.NAME;
		TomeRegistry.getTomeForName(tomeName).giveTomeToPlayer(player);

		player.chat("Welcome, apprentice.");
		player.chat("Here is your tome ' " + tomeName + "'.");
		player.chat("You would be well advised to read it.");
	}

	@HookHandler
	public void onInteract(ItemUseHook event) {
		Player player = event.getPlayer();
		Item itemHeld = player.getItemHeld();
		
		if (isDuplicateEvent(event)) {
			return;
		}
		
		MagicContext context = new MagicContext(player, event.getBlockClicked());

		// First try to cast known spells
		Optional<Spell> spellOptional = context.getSpells().stream()
			.filter(spell -> spell.canCastSpellWithHeldItem(itemHeld))
			.findFirst();
		if (spellOptional.isPresent()) {
			spellOptional.get().tryCastSpell(context);
			return;
		}

//		for (Spell spell : context.getSpells()) {
//			// If have focus for spell, then try to cast and stop trying anything else.
//			if (spell.canCastSpellWithHeldItem(itemHeld)) {
//				spell.tryCastSpell(context);
//				return;
//			}
//		}
		
		// If no spell was castable, try invoking known ceremonies
		Optional<Ceremony> ceremonyOptional = context.getTomes().stream()
			.flatMap(tome -> tome.getCeremonies().stream())
			.filter(ceremony -> ceremony.tryPerformCeremony(context))
			.findFirst();
		if (ceremonyOptional.isPresent()) {
			player.chat("Performed ceremony " + ceremonyOptional.get().getName());
			return;
		}

		TomeRegistry.getTomeForName(IntroductorySpellcastingTome.NAME).tryPerformCeremony(context);
		
//		for (Tome book : context.getTomes()) {
//			for (Ceremony ceremony : book.getCeremonies()) {
//				if (ceremony.tryPerformCeremony(context)) {
//					player.chat("Performed ceremony " + ceremony.getName());
//					return;
//				}
//			}
//		}
//		TomeRegistry.getTomeForName(IntroductorySpellcastingTome.NAME).tryPerformCeremony(context);

	}
	

	private static final ConcurrentHashMap<String,LocalTime> playerUuidLastEventMap = new ConcurrentHashMap<>();

	// Workaround for issue with two ItemUseHook events being invoked by a single right-click of a wooden or stone hoe.
	// Also might help prevent super-fast clicking from casting a spell multiple times.
	private boolean isDuplicateEvent(ItemUseHook event) {
		String key = event.getPlayer().getUUIDString();
		
		LocalTime lastEvent = playerUuidLastEventMap.get(key);
		LocalTime now = LocalTime.now();
		playerUuidLastEventMap.put(key, now);
		
		if (lastEvent != null) {
			Duration duration = Duration.between(lastEvent, now);
			if (duration.toMillis() < 100) {
				logger.info("Duplicate item use event for player " + event.getPlayer().getName() + " using item " + event.getItem().getDisplayName());
				return true;
			}
		}
		
		return false;
	}
	
}
