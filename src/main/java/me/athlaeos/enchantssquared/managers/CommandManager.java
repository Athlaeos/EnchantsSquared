package me.athlaeos.enchantssquared.managers;

import me.athlaeos.enchantssquared.commands.*;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabExecutor {
	
	private EnchantsSquared plugin;
	private Map<String, Command> commands = new HashMap<>();
	private String invalid_command;
	private String warning_no_permission;
	private static CommandManager manager = null;

	public CommandManager(EnchantsSquared plugin) {
		this.plugin = plugin;
		invalid_command = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_invalid_command");
		warning_no_permission = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_no_permission");

		commands.put("help", new HelpCommand());
		commands.put("reload", new ReloadCommand());
		commands.put("enchant", new EnchantCommand());
		commands.put("remove", new RemoveEnchantCommand());
		commands.put("list", new GetEnchantListCommand());
		commands.put("menu", new GetEnchantMenuCommand());
		commands.put("give", new GetEnchantedItemCommand());
		commands.put("debug", new DebugCommand());

	    ((HelpCommand) commands.get("help")).giveCommandMap(commands);
		
		plugin.getCommand("enchantssquared").setExecutor(this);
	}

	public static CommandManager getInstance(){
		if (manager == null){
			manager = new CommandManager(EnchantsSquared.getPlugin());
		}
		return manager;
	}

	public void reload(){
		manager = new CommandManager(EnchantsSquared.getPlugin());
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Utils.chat(String.format("&dEnchants Squared v%s by Athlaeos", plugin.getDescription().getVersion())));
			sender.sendMessage(Utils.chat("&7/es help"));
			return true;
		}
		
		for (String subCommand : commands.keySet()) {
			if (args[0].equalsIgnoreCase(subCommand)) {
				boolean hasPermission = false;
				for (String permission : commands.get(subCommand).getRequiredPermission()){
					if (sender.hasPermission(permission)){
						hasPermission = true;
						break;
					}
				}
				if (!hasPermission){
					sender.sendMessage(Utils.chat(warning_no_permission));
					return true;
				}
				if (!commands.get(subCommand).execute(sender, args)) {
					sender.sendMessage(Utils.chat(commands.get(subCommand).getFailureMessage()));
				}
				return true;
			}
		}
		sender.sendMessage(Utils.chat(invalid_command));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (args.length == 1) {
			List<String> completeList = new ArrayList<>();
			completeList.addAll(commands.keySet());
			return completeList;
		} else if (args.length > 1) {
			for (String arg : commands.keySet()) {
				if (args[0].equalsIgnoreCase(arg)) {
					return commands.get(arg).getSubcommandArgs(sender, args);
				}
			}
		}
		return null;
	}
}
