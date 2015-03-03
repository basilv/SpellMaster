package com.basilv.minecraft.spellmaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

import net.canarymod.Canary;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.nbt.BaseTag;
import net.canarymod.api.nbt.CompoundTag;
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
				player.message("Receiving tome " + book.getName());
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
			player.message("Casting level " + context.getCastingLevel() + " (bonus +" + spellboost.getCasterLevel() + ")");
			player.message("Bonus to range +" + spellboost.getRange());
			player.message("Bonus to damage +" + spellboost.getDamage());
			player.message("Bonus to duration +" + spellboost.getDurationInSeconds() + " seconds");
			player.message("Reduction in exhaustion -" + spellboost.getExhaustionReduction());
		}
		
	}

	@Command(aliases = { "listtomes" },
	        parent = "spellmaster",
	        helpLookup = "spellmaster listtomes",
	        description = "List all available tomes.",
	        permissions = { "spellmaster.command.listtomes" },
	        toolTip = "/spellmaster listtomes",
	        min = 0, 
	        version = 2)
	public void listTomesCommand(MessageReceiver caller, String[] parameters) {
		caller.message("Available tomes of magic:");
		caller.message("Legend: LN = level needed for ceremony, LC = cost in levels, F = focus, C = component");
		TomeRegistry.getTomes().stream()
			.sorted((first, second) -> first.getName().compareTo(second.getName()))
			.forEach(tome -> {
				String message = tome.getName() + "  LN:" + tome.getCeremonyMinimumLevel() + " LC:" + tome.getCeremonyLevelCost() + " F:" + tome.getCeremonyFocus();
				if (tome.getCeremonyComponent() != null) {
					message += " C:" + tome.getCeremonyComponent();
				}
				caller.message(message);
			});
	}

	@Command(aliases = { "listspells" },
	        parent = "spellmaster",
	        helpLookup = "spellmaster listspells",
	        description = "List available spells by page #.",
	        permissions = { "spellmaster.command.listspells" },
	        toolTip = "/spellmaster listspells < page number>",
	        min = 2, 
	        version = 2)
	public void listSpellsCommand(MessageReceiver caller, String[] parameters) {
		int page = Integer.parseInt(parameters[1]);
		int spellsPerPage = 9;
		int spellsToSkip = (page -1) * spellsPerPage;
		int maxSpells = (int) TomeRegistry.getTomes().stream().flatMap(tome -> tome.getSpells().stream()).count();
		int maxPages = (int)Math.ceil((double)maxSpells / (double)spellsPerPage);
		caller.message("Spell List (page " + page + " / " + maxPages + "):");
		caller.message("Legend: L = level, H = health cost, E = exhaustion cost, F = focus, C = component");
		TomeRegistry.getTomes().stream().flatMap(tome -> tome.getSpells().stream())
			.sorted((first, second) -> first.getName().compareTo(second.getName()))
			.skip(spellsToSkip)
			.limit(spellsPerPage)
			.forEach(spell -> {
				String message = spell.getName() + "  L:" + spell.getCastingMinimumLevel() + 
					" H:" + spell.getCastingHealthCost() + " E:" + spell.getCastingExhaustionCost() + " F:" + spell.getCastingFocus();
				if (spell.getCastingComponent() != null) {
					message += " C:" + spell.getCastingComponent();
				}
				caller.message(message);
			});
	}
	
	
	/**
	 * Displays warning to player if no name was provided.
	 * @param player
	 * @param commandArguments
	 * @return the corresponding name or null.
	 */
	private String getNameFromRestOfArguments(Player player, LinkedList<String> commandArguments) {
		if (commandArguments.isEmpty()) {
			player.message("Need to specify name");
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
	@Command(aliases = { "stock" }, description = "Stock inventory of player", permissions = { "*" }, toolTip = "/stock")
	public void stockCommand(MessageReceiver caller, String[] parameters) {
		if (caller instanceof Player) {
			Player player = (Player) caller;
			ItemFactory itemFactory = Canary.factory().getItemFactory();
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.DiamondSword));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Bow));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Torch, 0, 64));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Steak, 0, 64));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Cobble, 0, 64));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Sand, 0, 64));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.IronBoots));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.StoneSpade));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.StonePickaxe, 0, 3));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Stick, 0, 64));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.DiamondBoots));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.DiamondChestplate));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.DiamondHelmet));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.DiamondLeggings));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.IronPickaxe));

			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Bed));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Compass));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Arrow, 0, 64));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.Workbench));
			giveItemIfNeeded(player, itemFactory.newItem(ItemType.WaterBucket));
			
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Earth Magic"});
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Water Magic"});
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Nature Magic"});
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Wizard Magic"});
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Air Magic"});
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Fire Magic"});
			giveTomeCommand(caller, new String[] {"givetome", "@p", "Archmage Magic"});
		}
	}
	
	private void giveItemIfNeeded(Player player, Item item) {
		Item itemPossessed = player.getInventory().getItem(item.getType());
		if (itemPossessed == null) {
			player.giveItem(item);
		} else {
			itemPossessed.setAmount(item.getAmount());
		}
	}
	
	
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

	@Command(aliases = { "inspectEntity" }, description = "Inspect entity player is pointing at.", permissions = { "*" }, toolTip = "/inspectEntity")
	public void inspectEntityCommand(MessageReceiver caller, String[] parameters) {
		if (caller instanceof Player) {
			Player player = (Player) caller;
			Position playerPosition = player.getPosition();
			player.getWorld().getTrackedEntities().stream().forEach(entity -> inspectEntity(playerPosition, entity)); 			
		}
	}

	private void inspectEntity(Position playerPosition, Entity entity) {
		int separation = MinecraftUtils.getSeparationInBlocks(entity.getPosition(), playerPosition);
		if (separation > 10) {
			return;
		}

		logger.info("Entity type = " + entity.getEntityType() + " name = " + entity.getDisplayName());
		CompoundTag nbt = entity.getNBT();
		if (nbt != null) {
			logger.info("  NBT data:");
			nbt.keySet().stream().forEach(key -> {
				logger.info("    " + key + " = " + nbt.get(key));
			});
		}

		if (entity.getEntityType().equals(EntityType.XPORB)) {
			logger.info("Value value class = " + nbt.get("Value").getClass().getCanonicalName());
			logger.info("Value value as short = " + nbt.getShort("Value"));

			CompoundTag metaData = entity.getMetaData();
			if (metaData != null) {
				logger.info("  Meta data:");
				metaData.keySet().stream().forEach(key -> {
					logger.info("    " + key + " = " + metaData.get(key));
				});
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
