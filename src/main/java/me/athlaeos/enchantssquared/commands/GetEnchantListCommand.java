package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetEnchantListCommand implements Command {
	private String list_description;
	private boolean eslist_include_weight;
	private boolean eslist_include_max_level;
	private boolean eslist_include_compatible_items;
	private String weight_translation;
	private String max_level_translation;
	private String compatible_item_translation;
	private int extraAdditionalLines = 0;

	public GetEnchantListCommand(){
		list_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("list_description");
		weight_translation = ConfigManager.getInstance().getConfig("translations.yml").get().getString("eslist_weight");
		max_level_translation = ConfigManager.getInstance().getConfig("translations.yml").get().getString("max_level_translation");
		compatible_item_translation = ConfigManager.getInstance().getConfig("translations.yml").get().getString("compatible_item_translation");
		eslist_include_weight = EnchantsSquared.getPlugin().getConfig().getBoolean("eslist_include_weight");
		eslist_include_compatible_items = EnchantsSquared.getPlugin().getConfig().getBoolean("eslist_include_compatible_items");
		eslist_include_max_level = EnchantsSquared.getPlugin().getConfig().getBoolean("eslist_include_max_level");
		if (eslist_include_max_level) extraAdditionalLines++;
		if (eslist_include_weight) extraAdditionalLines++;
		if (eslist_include_compatible_items) extraAdditionalLines++;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Map<Integer, ArrayList<String>> enchantPagesMap;
		List<String> helpLines = new ArrayList<>();

		for (CustomEnchant c : CustomEnchantManager.getInstance().getAllEnchants().values()) {
			if (c.isEnabled()){
				helpLines.add(Utils.chat("&7" + CustomEnchantManager.getInstance().extractEnchantString(c.getEnchantLore())));
				helpLines.add(Utils.chat(c.getEnchantDescription()));
				if (eslist_include_weight){
					helpLines.add(Utils.chat(weight_translation + c.getWeight()));
				}
				if (eslist_include_max_level){
					helpLines.add(Utils.chat(max_level_translation
							.replace("%lv_roman%", Utils.toRoman(c.getMax_level())
									.replace("%lv_number%", "" + c.getMax_level()))));
				}
				if (eslist_include_compatible_items){
					helpLines.add(Utils.chat(compatible_item_translation + String.join(", ", c.getCompatibleItemStrings()).toLowerCase()));
				}
				helpLines.add(Utils.chat("&8&m                                             "));
			}
		}

		enchantPagesMap = Utils.paginateTextList(9 + (extraAdditionalLines * 3), helpLines);

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

		if (args.length >= 2) {
			int pageNumber;
			try {
				pageNumber = Integer.parseInt(args[1]);

				if (pageNumber < 1) {
					pageNumber = 1;
				}
				if (pageNumber > enchantPagesMap.size()) {
					pageNumber = enchantPagesMap.size();
				}
			} catch (NumberFormatException nfe) {
				try {
					CustomEnchantType type = CustomEnchantType.valueOf(args[1].toUpperCase());
					CustomEnchant enchant = CustomEnchantManager.getInstance().getEnchant(type);
					sender.sendMessage(Utils.chat("&8&m                                             "));
					sender.sendMessage(Utils.chat("&7" + CustomEnchantManager.getInstance().extractEnchantString(enchant.getEnchantLore())));
					sender.sendMessage(Utils.chat(enchant.getEnchantDescription()));
					if (eslist_include_weight){
						sender.sendMessage(Utils.chat(weight_translation + enchant.getWeight()));
					}
					if (eslist_include_max_level){
						sender.sendMessage(Utils.chat(max_level_translation
								.replace("%lv_roman%", Utils.toRoman(enchant.getMax_level())
										.replace("%lv_number%", "" + enchant.getMax_level()))));
					}
					if (eslist_include_compatible_items){
						sender.sendMessage(Utils.chat(compatible_item_translation + String.join(", ", enchant.getCompatibleItemStrings()).toLowerCase()));
					}
					sender.sendMessage(Utils.chat("&8&m                                             "));
					return true;
				} catch (IllegalArgumentException ignored){
					helpLines.clear();
					pageNumber = 1;
					for (CustomEnchant e : CustomEnchantManager.getInstance().getAllEnchants().values()){
						if (e.isEnabled()){
							if (e.getEnchantLore().toLowerCase().contains(args[1].toLowerCase())){
								helpLines.add(Utils.chat("&7" + CustomEnchantManager.getInstance().extractEnchantString(e.getEnchantLore())));
								helpLines.add(Utils.chat(e.getEnchantDescription()));
								if (eslist_include_weight){
									helpLines.add(Utils.chat(weight_translation + e.getWeight()));
								}
								if (eslist_include_max_level){
									helpLines.add(Utils.chat(max_level_translation
											.replace("%lv_roman%", Utils.toRoman(e.getMax_level())
													.replace("%lv_number%", "" + e.getMax_level()))));
								}
								if (eslist_include_compatible_items){
									helpLines.add(Utils.chat(compatible_item_translation + String.join(", ", e.getCompatibleItemStrings()).toLowerCase()));
								}
								helpLines.add(Utils.chat("&8&m                                             "));
							}
						}
					}
					if (args.length == 3){
						try {
							pageNumber = Integer.parseInt(args[2]);

							if (pageNumber < 1) {
								pageNumber = 1;
							}
							if (pageNumber > enchantPagesMap.size()) {
								pageNumber = enchantPagesMap.size();
							}
						} catch (NumberFormatException ex) {
						}
					}
					enchantPagesMap = Utils.paginateTextList(9 + (extraAdditionalLines * 3), helpLines);

					if (enchantPagesMap.size() == 0) {
						return true;
					}
					for (String entry : enchantPagesMap.get(pageNumber - 1)) {
						sender.sendMessage(Utils.chat(entry));
					}
					sender.sendMessage(Utils.chat(String.format("&8[&e%s&8/&e%s&8]", pageNumber, enchantPagesMap.size())));
					return true;
				}
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
				Utils.chat("&d/es list <page/enchant/search term>"),
				Utils.chat("&7" + list_description),
				Utils.chat("&7> &des.list")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) {
			List<String> subargs = new ArrayList<String>();
			for (CustomEnchantType e : CustomEnchantType.values()){
				if (e != CustomEnchantType.UNASSIGNED){
					subargs.add(e.toString().toLowerCase());
				}
			}
			return subargs;
		}
		List<String> subargs = new ArrayList<String>();
		subargs.add(" ");
		return subargs;
	}
}
