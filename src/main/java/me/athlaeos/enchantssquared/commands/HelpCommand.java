package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command {
	private String invalid_number;
	private String help_description;

	public HelpCommand(){
		invalid_number = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_invalid_number");
		help_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("help_description");
	}

	private List<Command> commands = new ArrayList<>();

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Map<Integer, ArrayList<String>> helpCommandList;
		List<String[]> helpEntries = new ArrayList<>();
		List<String> helpLines = new ArrayList<>();
		
		for (Command c : commands) {
			for (String permission : c.getRequiredPermission()) {
				if (sender.hasPermission(permission)) {
					helpEntries.add(c.getHelpEntry());
					break;
				}
			}
		}
		for (String[] commandHelp : helpEntries) {
			helpLines.addAll(Arrays.asList(commandHelp));
		}

		helpCommandList = Utils.paginateTextList(12, helpLines);
		
		if (helpCommandList.size() == 0) {
			return true;
		}
		
		// args[0] is "help" and args.length > 0
		if (args.length == 1) {
			for (String line : helpCommandList.get(0)) {
				sender.sendMessage(Utils.chat(line));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&51&8/&5%s&8]", helpCommandList.size())));
			return true;
		}

		if (args.length == 2) {
			try {
				Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				sender.sendMessage(Utils.chat(invalid_number));
				return true;
			}

			int pageNumber = Integer.parseInt(args[1]);
			if (pageNumber < 1) {
				pageNumber = 1;
			}
			if (pageNumber > helpCommandList.size()) {
				pageNumber = helpCommandList.size();
			}
			
			for (String entry : helpCommandList.get(pageNumber - 1)) {
				sender.sendMessage(Utils.chat(entry));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&5%s&8/&5%s&8]", pageNumber, helpCommandList.size())));
			return true;
		}

		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.help"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/es help";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es help <page>"),
				Utils.chat("&7" + help_description),
				Utils.chat("&7> &des.help")
		};
	}

	public void giveCommandMap(Map<String, Command> commandMap) {
		commands = new ArrayList<>();
		for (String key : commandMap.keySet()) {
			commands.add(commandMap.get(key));
		}
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) {
			List<String> subargs = new ArrayList<String>();
			subargs.add("1");
			subargs.add("2");
			subargs.add("3");
			subargs.add("...");
			return subargs;
		}
		List<String> subargs = new ArrayList<String>();
		subargs.add(" ");
		return subargs;
	}
}
