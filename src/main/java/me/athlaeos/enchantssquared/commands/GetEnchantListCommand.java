package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetEnchantListCommand implements Command {
	private String invalid_number;
	private String list_description;

	public GetEnchantListCommand(){
		invalid_number = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_invalid_number");
		list_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("list_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Map<Integer, ArrayList<String>> enchantPagesMap;
		List<String> helpLines = new ArrayList<>();
		
		for (CustomEnchant c : CustomEnchantManager.getInstance().getAllEnchants().values()) {
			helpLines.add(Utils.chat("&7" + CustomEnchantManager.getInstance().extractEnchantString(c.getEnchantLore())));
			helpLines.add(Utils.chat(c.getEnchantDescription()));
			helpLines.add(Utils.chat("&8&m                                             "));
		}

		enchantPagesMap = Utils.paginateTextList(12, helpLines);
		
		if (enchantPagesMap.size() == 0) {
			return true;
		}
		
		// args[0] is "help" and args.length > 0
		if (args.length == 1) {
			for (String line : enchantPagesMap.get(0)) {
				sender.sendMessage(Utils.chat(line));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&e1&8/&e%s&8]", enchantPagesMap.size())));
			return true;
		}

		if (args.length == 2) {
			try {
				Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				try {
					CustomEnchantEnum type = CustomEnchantEnum.valueOf(args[1].toUpperCase());
					CustomEnchant enchant = CustomEnchantManager.getInstance().getEnchant(type);
					sender.sendMessage(Utils.chat("&8&m                                             "));
					sender.sendMessage(Utils.chat("&7" + CustomEnchantManager.getInstance().extractEnchantString(enchant.getEnchantLore())));
					sender.sendMessage(Utils.chat(enchant.getEnchantDescription()));
					sender.sendMessage(Utils.chat("&8&m                                             "));
					return true;
				} catch (IllegalArgumentException ignored){
				}
				sender.sendMessage(Utils.chat(invalid_number));
				return true;
			}

			int pageNumber = Integer.parseInt(args[1]);
			if (pageNumber < 1) {
				pageNumber = 1;
			}
			if (pageNumber > enchantPagesMap.size()) {
				pageNumber = enchantPagesMap.size();
			}
			
			for (String entry : enchantPagesMap.get(pageNumber - 1)) {
				sender.sendMessage(Utils.chat(entry));
			}
			sender.sendMessage(Utils.chat(String.format("&8[&e%s&8/&e%s&8]", pageNumber, enchantPagesMap.size())));
			return true;
		}

		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.list"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/es list";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es list <page/enchant>"),
				Utils.chat("&7" + list_description),
				Utils.chat("&7> &des.list")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) {
			List<String> subargs = new ArrayList<String>();
			for (CustomEnchantEnum e : CustomEnchantEnum.values()){
				subargs.add(e.toString().toLowerCase());
			}
			return subargs;
		}
		List<String> subargs = new ArrayList<String>();
		subargs.add(" ");
		return subargs;
	}
}
