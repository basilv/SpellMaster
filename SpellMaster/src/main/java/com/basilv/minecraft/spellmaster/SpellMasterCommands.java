package com.basilv.minecraft.spellmaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.nbt.BaseTag;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.properties.BlockProperty;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.logger.Logman;

public class SpellMasterCommands implements CommandListener {

	private final Logman logger;

	public SpellMasterCommands(Logman logger) {
		this.logger = logger;
	}

	@Command(aliases = { "spellmaster", "sm" }, description = "Spellmaster plugin commands.", 
			permissions = { "spellmaster.command" }, toolTip = "/spellmaster <givetome|castbonus|listtomes> [parameters...] [--help]")
	public void spellMasterBaseCommand(MessageReceiver caller, String[] parameters) {
		// Do nothing - everything is done by subcommands.
	}

	@Command(aliases = { "givetome" },
	        parent = "spellmaster",
	        helpLookup = "spellmaster givetome",
	        description = "Give a player the specified tome.",
	        permissions = { "spellmaster.command.givetome" },
	        toolTip = "/spellmaster givetome <player name|@p> <tome name>",
	        min = 2, 
	        version = 2)
	public void giveTomeCommand(MessageReceiver caller, String[] parameters) {
		LinkedList<String> parameterList = new LinkedList<>(Arrays.asList(parameters));
		parameterList.removeFirst(); // First argument is givetome command
		getPlayerFromFirstArgument(caller, parameterList).ifPresent(player -> {
			String name = getNameFromRestOfArguments(player, parameterList);
			Tome book = TomeRegistry.getTomeForName(name);
			if (book != null) {
				player.chat("Receiving tome " + book.getName());
				book.giveTomeToPlayer(player);
			} else {
				caller.message("Tome " + name + " does not exist.");
			}
		});
	}

	private Optional<Player> getPlayerFromFirstArgument(MessageReceiver caller,
			LinkedList<String> parameterList) {
		String playerString = parameterList.removeFirst();
		Player player = null;
		if ("@p".equals(playerString) && (caller instanceof Player)) {
			player = (Player) caller;
		} else {
			player = Canary.getServer().getPlayer(playerString);
		}
		if (player == null) {
			caller.message("No player of name '" + playerString + "' found.");
			return Optional.empty();
		}
		return Optional.of(player);
	}
	
	@Command(aliases = { "castbonus" },
	        parent = "spellmaster",
	        helpLookup = "spellmaster castbonus",
	        description = "Display the casting bonuses you possess.",
	        permissions = { "spellmaster.command.castbonus" },
	        toolTip = "/spellmaster castbonus",
	        min = 0, 
	        version = 2)
	public void castingStatsCommand(MessageReceiver caller, String[] parameters) {

		if (caller instanceof Player) {
			Player player = (Player) caller;
			MagicContext context = new MagicContext(player, null);
			SpellBoost spellboost = context.getSpellboost();
			player.chat("Casting level " + context.getCastingLevel() + " (bonus +" + spellboost.getCasterLevel() + ")");
			player.chat("Bonus to range +" + spellboost.getRange());
			player.chat("Bonus to damage +" + spellboost.getDamage());
			player.chat("Bonus to duration +" + spellboost.getDurationInSeconds() + " seconds");
			player.chat("Reduction in exhaustion -" + spellboost.getExhaustionReduction());
		}
		
	}

	// TODO: For debugging mostly
	@Command(aliases = { "listtomes" },
	        parent = "spellmaster",
	        helpLookup = "spellmaster listtomes",
	        description = "List all available tomes.",
	        permissions = { "spellmaster.command.castbonus" },
	        toolTip = "/spellmaster listtomes",
	        min = 0, 
	        version = 2)
	public void listTomesCommand(MessageReceiver caller, String[] parameters) {
		caller.message("Available tomes of magic:");
		for (Tome tome : TomeRegistry.getTomes()) {
			caller.message(tome.getName());
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

	// TODO: Commands for debugging - eventually remove or move to another plugin.
	@Command(aliases = { "inspectItem" }, description = "Inspect item player is holding", permissions = { "*" }, toolTip = "/inspectItem")
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

	@Command(aliases = { "inspectBlock" }, description = "Inspect block player is standing in and on.", permissions = { "*" }, toolTip = "/inspectBlock")
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
	
}
