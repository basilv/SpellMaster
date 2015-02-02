package com.basilv.minecraft.spellmaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.nbt.BaseTag;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.properties.BlockProperty;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.hook.player.PlayerRespawnedHook;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

import com.basilv.minecraft.spellmaster.spells.AcidErosionSpell;
import com.basilv.minecraft.spellmaster.spells.DecomposeTreeSpell;
import com.basilv.minecraft.spellmaster.spells.DisintegrateEarthSpell;
import com.basilv.minecraft.spellmaster.spells.FertileFieldSpell;
import com.basilv.minecraft.spellmaster.spells.FlightSpell;
import com.basilv.minecraft.spellmaster.spells.LightningBoltSpell;
import com.basilv.minecraft.spellmaster.spells.SenseOreSpell;
import com.basilv.minecraft.spellmaster.spells.WallOfStoneSpell;
import com.basilv.minecraft.spellmaster.spells.WallOfWaterSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.ControlWeatherSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.CreateSnowGolemSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.GreaterTeleportSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.LightSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.SummonZombieSpell;
import com.basilv.minecraft.spellmaster.spells.experimental.TeleportSpell;
import com.basilv.minecraft.spellmaster.tomes.AirMagicTome;
import com.basilv.minecraft.spellmaster.tomes.ArchmageTome;
import com.basilv.minecraft.spellmaster.tomes.EarthMagicTome;
import com.basilv.minecraft.spellmaster.tomes.FireMagicTome;
import com.basilv.minecraft.spellmaster.tomes.IntroductorySpellcastingTome;
import com.basilv.minecraft.spellmaster.tomes.NatureMagicTome;
import com.basilv.minecraft.spellmaster.tomes.WaterMagicTome;
import com.basilv.minecraft.spellmaster.tomes.WizardTome;

// TODO: Put into GitHub
// TODO: Set up Maven build?

public class SpellMasterPlugin extends Plugin implements CommandListener, PluginListener {

	public Logman logger;

	public SpellMasterPlugin() {
		logger = getLogman();
	}

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(this, this);
		try {
			Canary.commands().registerCommands(this, this, false);
		} catch (CommandDependencyException e) {
			logger.error("Duplicate command name", e);
		}
		
		IntroductorySpellcastingTome introTome = registerTome(new IntroductorySpellcastingTome());
		
		// Create light spell with focus: sunflower or stick (YellowFlower item type)
		// Higher level books reuse stick, but with more powerful spell?
		
		// TODO: Move elsewhere
		introTome.addSpell(new LightSpell()); // TODO: Probably remove
		introTome.addSpell(new SummonZombieSpell());
		introTome.addSpell(new CreateSnowGolemSpell());

		NatureMagicTome natureTome = registerTome(new NatureMagicTome());
		introTome.addTome(natureTome);
		natureTome.addSpell(new DecomposeTreeSpell());
		natureTome.addSpell(new FertileFieldSpell());

		// TODO: animal-related spell like clone animal, summon animal (within a large area)

		// Foci: 
//		YellowFlower (Dandelion), Poppy, BlueOrchid, Allium, AzureBluet, RedTulip, OrangeTulip, WhiteTulip, PinkTulip, OxeyeDaisy,
//		Apple,, Egg, FlowerPot, Mushrooms, Ice, Large Fern, Lilac, Lilypad, Grass, 
		//  PackedIce, Peony,  , Reed, Rosebush
		// Seeds, Shrub, Snow, Soil, Tallfern, Vines, Wheat, YellowFlower (Sunflower)

		EarthMagicTome earthTome = registerTome(new EarthMagicTome());
		introTome.addTome(earthTome);
		earthTome.addSpell(new DisintegrateEarthSpell());
		earthTome.addSpell(new WallOfStoneSpell());
		earthTome.addSpell(new SenseOreSpell());
		// TODO: Add spells
		// Flatten area
		// Create bridge
		
		// lava burst
		// use armor for different effects: chestplate - invulnerability, boots - teleport, etc.

		WaterMagicTome waterTome = registerTome(new WaterMagicTome());
		introTome.addTome(waterTome);
		waterTome.addSpell(new AcidErosionSpell());
		waterTome.addSpell(new WallOfWaterSpell());
		// Water wave
		// Flood
		// Earth to mud
		// Absorb Water
//		ItemType.ClownFish
//		ItemType.FishingRod
//		ItemType.Ice
//		ItemType.Lilypad
//		ItemType.PufferFish
//		ItemType.RabbitFoot
//		ItemType.RawFish
//		ItemType.RawSalmon;
//		ItemType.String
//		ItemType.SpiderEye
//		ItemType.LapisLazuli
		
		WizardTome wizardTome = registerTome(new WizardTome());
		introTome.addTome(wizardTome);
		wizardTome.addSpell(new TeleportSpell());
		// Teleport?
		
		AirMagicTome airTome = registerTome(new AirMagicTome());
		wizardTome.addTome(airTome);
		airTome.addSpell(new ControlWeatherSpell());
		airTome.addSpell(new FlightSpell());
		airTome.addSpell(new LightningBoltSpell());
		// TODO: Wind wall, focus: large fern
		// Gust of wind: push all mobs away from you.
		
		FireMagicTome fireTome = registerTome(new FireMagicTome());
		wizardTome.addTome(fireTome);
		// Ignite: convert air block to fire, revert back after duration ends?
		// Lava burst
		// Fireball
		// Flamewall
		
		ArchmageTome archmageTome = new ArchmageTome();
		wizardTome.addTome(archmageTome);
		
		archmageTome.addSpell(new GreaterTeleportSpell());
		
		
//		logger.info(ItemType.BlueOrchid.getMachineName());
//		logger.info(ItemType.LightBlueDye.getMachineName());
//		logger.info(ItemType.Banner);
		
		return true;
	}

	private static <T extends Tome> T registerTome(T tome) {
		TomeRegistry.addTome(tome);
		return tome;
	}
	
	@Override
	public void disable() {
	}

	@HookHandler
	public void onPlayerSpawn(PlayerRespawnedHook hook) {
		Player player = hook.getPlayer();
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
		MagicContext context = new MagicContext(player, event.getBlockClicked());
		
		// First try to cast known spells
		for (Spell spell : context.getSpells()) {
			// If have focus for spell, then try to cast and stop trying anything else.
			if (spell.canCastSpellWithHeldItem(itemHeld)) {
				spell.tryCastSpell(context);
				return;
			}
		}
		
		// If no spell was castable, try invoking known ceremonies
		boolean invokedCeremony = false;
		for (Tome book : context.getTomes()) {
			for (Ceremony ceremony : book.getCeremonies()) {
				// TODO: Pass context into spells and ceremonies.
				if (ceremony.tryPerformCeremony(context)) {
					player.chat("Performed ceremony " + ceremony.getName());
					invokedCeremony = true;
					break;
				}
			}
			if (invokedCeremony) {
				break;
			}
		}
		
		if (!invokedCeremony) {
			invokedCeremony = TomeRegistry.getTomeForName(IntroductorySpellcastingTome.NAME).tryPerformCeremony(context);
		}
	}
	

	// TODO: For debugging.
	@Command(aliases = { "inspectItem" }, description = "spellmaster plugin inspect item", permissions = { "*" }, toolTip = "/spellmaster")
	public void inspectItemCommand(MessageReceiver caller, String[] parameters) {
		if (caller instanceof Player) {
			Player player = (Player) caller;

			Item item = player.getItemHeld();
			logger.info("Item type = " + item.getType().getMachineName());
			logger.info("Item damage = " + item.getDamage());
			logger.info("Item max damage = " + item.getBaseItem().getMaxDamage());
			for (String key : item.getAttributes().keySet()) {
				logger.info("item attribute " + key + " "
						+ item.getAttributes().get(key));
			}
			if (item.getDataTag() != null) {
				for (String key : item.getDataTag().keySet()) {
					@SuppressWarnings("rawtypes")
					BaseTag baseTag = item.getDataTag().get(key);
					logger.info("data tag key " + key + " class "
							+ baseTag.getClass() + " value " + baseTag.toString());
				}
			}
		}
	}

	@Command(aliases = { "inspectBlock" }, description = "spellmaster plugin inspect block", permissions = { "*" }, toolTip = "/spellmaster")
	public void inspectBlockCommand(MessageReceiver caller, String[] parameters) {
		if (caller instanceof Player) {
			Player player = (Player) caller;
			Position pos = new Position(player.getPosition());
			logBlockInfo(player, pos, "Standing in with lower body");
			pos.moveY(-1); // Get block that player is standing on
			logBlockInfo(player, pos, "Standing on");
		}
	}

	@SuppressWarnings("deprecation")
	private void logBlockInfo(Player player, Position pos, String description) {
		Block block = player.getWorld().getBlockAt(pos);
		logger.info("Position " + pos + " " + description);
		logger.info("Block type = " + block.getType().getMachineName() + " id = " + block.getType().getId() + " data = " + block.getType().getData());
		if (block.getBlockBase() != null) {
			logger.info("block base localized name = " + block.getBlockBase().getLocalizedName());
			logger.info("block base collidable?" + block.getBlockBase().isCollidable());
		}
		logger.info("Block data = " + block.getData());
		for (BlockProperty key : block.getPropertyKeys()) {
			String msg = "Block property key : " + key.getName();

			@SuppressWarnings("rawtypes")
			Comparable c = block.getProperties().get(key);
			String desc = "<null>";
			if (c != null) {
				desc = c.toString();
			}
			logger.info(msg + " value = " + desc);
		}
		if (block.getTileEntity() != null) {
			logger.info("Tile entity = " + block.getTileEntity().toString());
		}
	}

	@Command(aliases = { "spellmaster", "s" }, description = "Spellmaster plugin commands. Execute without arguments for help.", 
		permissions = { "*" }, toolTip = "/spellmaster")
	public void spellMasterCommands(MessageReceiver caller, String[] parameters) {
		if (caller instanceof Player) {
			Player player = (Player) caller;
			LinkedList<String> parameterList = new LinkedList<>(Arrays.asList(parameters));
			parameterList.removeFirst(); // First argument is command name
		
			String command = ""; // Must be non-null for switch statement
			if (!parameterList.isEmpty()) {
				command = parameterList.removeFirst();
			}
		
			switch (command) {
			case "obtainBook":
				executeObtainBookCommand(player, parameterList);
				break;
			case "listSpells":
				executeListSpellsCommand(player, parameterList);
				break;
			case "castingStats":
				executeCastingStatsCommand(player);
				break;
			case "bookInfo":
				executeBookInfoCommand(player, parameterList);
				break;
			default: // Help
				player.chat("SpellMaster Commands:");
				player.chat("help : Displays this help");
				player.chat("obtainBook <book> : Obtain the specified book of magic.");
				player.chat("listSpells : List the spells that you have available to cast.");
				player.chat("castingStats : Show player statistics related to spellcasting.");
				player.chat("bookInfo <book> : Log information on the ceremony to obtain the specified book.");
				break;
			}
		}
	}

	private void executeCastingStatsCommand(Player player) {
		MagicContext context = new MagicContext(player, null);
		SpellBoost spellboost = context.getSpellboost();
		player.chat("Casting level " + context.getCastingLevel() + " (bonus +" + spellboost.getCasterLevel() + ")");
		player.chat("Bonus to range +" + spellboost.getRange());
		player.chat("Bonus to damage +" + spellboost.getDamage());
		player.chat("Bonus to duration +" + spellboost.getDurationInSeconds() + " seconds");
	}

	private void executeListSpellsCommand(Player player, LinkedList<String> commandArguments) {
		List<Spell> spells = new ArrayList<>();
		for (Tome tome : TomeRegistry.getTomes()) {
			spells.addAll(tome.getSpells());
		}
		player.chat("Spells available: " + spells.size());
		for (Spell spell : spells) {
			player.chat(spell.getName());
		}
	}
	
	private void executeBookInfoCommand(Player player, LinkedList<String> commandArguments) {
		String name = getNameFromRestOfArguments(player, commandArguments);
		Tome book = TomeRegistry.getTomeForName(name);
		if (book != null) {
			logger.info("Ceremony information for book " + book.getName());
			List<String> lines = new ArrayList<>();
			book.populateCeremonyInformation(lines);
			for (String infoLine : lines) {
				logger.info(infoLine);
			}
		}	
	}

	private void executeObtainBookCommand(Player player, LinkedList<String> commandArguments) {
		String name = getNameFromRestOfArguments(player, commandArguments);
		Tome book = TomeRegistry.getTomeForName(name);
		if (book != null) {
			player.chat("Obtaining book " + book.getName());
			book.giveTomeToPlayer(player);
		}	
	}

	/**
	 * Displays warning to player if no name was provided.
	 * @param player
	 * @param commandArguments
	 * @return the corresponding name or null.
	 */
	private String getNameFromRestOfArguments(Player player, LinkedList<String> commandArguments) {
		if (commandArguments.isEmpty()) {
			player.chat("Need to specify name");
			return null;
		}
		String name = convertAllArgumentsToName(commandArguments);
		return name;
	}

	private String convertAllArgumentsToName(LinkedList<String> commandArguments) {
		String name = "";
		for (String argument : commandArguments) {
			if (!name.isEmpty()) {
				name += " ";
			}
			name += argument;
		}
		return name;
	}

}
