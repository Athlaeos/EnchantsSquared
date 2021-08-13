package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.enchantments.StandardGlintEnchantment;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class GetEnchantedItemCommand implements Command {
	private String give_item_successful;
	private String error_invalid_item;
	private String give_item_description;
	private String error_player_not_found;
	private String error_invalid_syntax;
	private String reason_invalid_level;
	private String reason_invalid_enchant;
	private String warning_invalid_number;

	public GetEnchantedItemCommand(){
		give_item_successful = ConfigManager.getInstance().getConfig("translations.yml").get().getString("give_item_successful");
		give_item_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("give_item_description");
		error_invalid_item = ConfigManager.getInstance().getConfig("translations.yml").get().getString("error_invalid_item");
		error_invalid_syntax = ConfigManager.getInstance().getConfig("translations.yml").get().getString("error_invalid_syntax");
		reason_invalid_level = ConfigManager.getInstance().getConfig("translations.yml").get().getString("reason_invalid_level");
		error_player_not_found = ConfigManager.getInstance().getConfig("translations.yml").get().getString("error_player_not_found");
		reason_invalid_enchant = ConfigManager.getInstance().getConfig("translations.yml").get().getString("reason_invalid_enchant");
		warning_invalid_number = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_invalid_number");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length <= 3) return false;
		Player target = EnchantsSquared.getPlugin().getServer().getPlayer(args[1]);
		if (target == null) {
			sender.sendMessage(Utils.chat(error_player_not_found));
			return true;
		}
		Material itemType;
		try {
			itemType = Material.valueOf(args[2].toUpperCase());
		} catch (IllegalArgumentException ignored){
			sender.sendMessage(Utils.chat(error_invalid_item));
			return true;
		}
		int amount;
		try {
			amount = Integer.parseInt(args[3]);
		} catch (IllegalArgumentException ignored){
			sender.sendMessage(Utils.chat(warning_invalid_number));
			return true;
		}

		if (args.length == 4){
			Map<Integer, ItemStack> itemsLeft = target.getInventory().addItem(new ItemStack(itemType, amount));
			if (!itemsLeft.isEmpty()){
				for (ItemStack i : itemsLeft.values()){
					Item item = (Item) target.getWorld().spawnEntity(target.getLocation(), EntityType.DROPPED_ITEM);
					item.setItemStack(i);
				}
			}
			sender.sendMessage(Utils.chat(give_item_successful));
			return true;
		}

		Map<Enchantment, Integer> vanillaEnchantments = new HashMap<>();
		Map<CustomEnchant, Integer> customEnchantments = new HashMap<>();
		List<String> lore = new ArrayList<>();
		String displayName = null;

		String[] customDataArgs = Arrays.copyOfRange(args, 4, args.length);
		for (String arg : customDataArgs){
			if (arg.contains("custom=")){
				String finalArg = arg.replace("custom=", "");
				String[] enchantArgs = finalArg.split(",");
				for (String enchantment : enchantArgs){
					String[] enchantDetails = enchantment.split(":");
					if (enchantDetails.length != 2){
						continue;
					}
					CustomEnchant enchant;
					try {
						enchant = CustomEnchantManager.getInstance().getEnchant(CustomEnchantType.valueOf(enchantDetails[0].toUpperCase()));
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_invalid_syntax.replace("%reason%", reason_invalid_enchant)));
						return true;
					}
					int level;
					try {
						level = Integer.parseInt(enchantDetails[1]);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_invalid_syntax.replace("%reason%", reason_invalid_level)));
						return true;
					}
					customEnchantments.put(enchant, level);
				}
			} else if (arg.contains("name=")){
				String finalArg = arg.replace("name=", "");
				displayName = Utils.chat(finalArg.replace("_", " "));
			} else if (arg.contains("lore=")){
				String finalArg = arg.replace("lore=", "");
				String[] loreArgs = finalArg.split("/n");
				for (String line : loreArgs){
					lore.add(Utils.chat(line.replace("_", " ")));
				}
			} else if (arg.contains("enchants=")){
				String finalArg = arg.replace("enchants=", "");
				String[] enchantArgs = finalArg.split(",");
				for (String enchantment : enchantArgs){
					String[] enchantDetails = enchantment.split(":");
					if (enchantDetails.length != 2){

						continue;
					}
					Enchantment enchant;
					try {
						enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantDetails[0]));
						if (enchant == null) throw new IllegalArgumentException();
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_invalid_syntax.replace("%reason%", reason_invalid_enchant)));
						return true;
					}
					int level;
					try {
						level = Integer.parseInt(enchantDetails[1]);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_invalid_syntax.replace("%reason%", reason_invalid_level)));
						return true;
					}
					vanillaEnchantments.put(enchant, level);
				}
			}
		}

		ItemStack giveItem = new ItemStack(itemType, amount);
		ItemMeta itemMeta = giveItem.getItemMeta();
		assert itemMeta != null;
		if (displayName != null){
			itemMeta.setDisplayName(Utils.chat(displayName));
		}

		if (!lore.isEmpty()){
			itemMeta.setLore(lore);
		}

		giveItem.setItemMeta(itemMeta);

		if (!customEnchantments.isEmpty()){
			CustomEnchantManager.getInstance().setItemEnchants(giveItem, customEnchantments);
		}

		for (Enchantment e : vanillaEnchantments.keySet()){
			giveItem.addUnsafeEnchantment(e, vanillaEnchantments.get(e));
		}

		Map<Integer, ItemStack> itemsLeft = target.getInventory().addItem(giveItem);
		if (!itemsLeft.isEmpty()){
			for (ItemStack i : itemsLeft.values()){
				Item item = (Item) target.getWorld().spawnEntity(target.getLocation(), EntityType.DROPPED_ITEM);
				item.setItemStack(i);
			}
		}
		sender.sendMessage(Utils.chat(give_item_successful));

		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.enchant"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/es give [player] [item] [amount] <custom=enchant:level,enchant:level...> <name=&8Example_name> <lore=&7Loreline_1/n&7Loreline_2> <enchants=enchant:level,enchant:level...>";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es give [player] [item] [amount] <custom=enchant:level,enchant:level...> <name=&8Example_name> <lore=&7Loreline_1/n&7Loreline_2> <enchants=enchant:level,enchant:level...>"),
				Utils.chat("&7" + give_item_description),
				Utils.chat("&7> &des.enchant")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return null;
		}
		if (args.length == 3){
			return new ArrayList<>(Arrays.stream(Material.values()).map(Material::toString).map(String::toLowerCase).collect(Collectors.toList()));
		}
		if (args.length == 4) {
			return Arrays.asList(
					"1",
					"2",
					"3",
					"4",
					"5",
					"...");
		}
		if (args.length >= 5) {
			String currentArg = args[args.length - 1];
			if (currentArg.contains("custom=")) {
				currentArg = currentArg.replace("custom=", "");
				if (currentArg.length() > 0) {
					if (currentArg.contains(":")) {
						String[] currentEnchantArgs = currentArg.split(":");
						if (currentEnchantArgs.length % 2 == 1) {
							return Arrays.asList(
									"custom=" + currentEnchantArgs[0] + "1,",
									"custom=" + currentEnchantArgs[0] + "2,",
									"custom=" + currentEnchantArgs[0] + "3,",
									"custom=" + currentEnchantArgs[0] + "4,",
									"custom=" + currentEnchantArgs[0] + "5,",
									"...");
						}
					}
				}
				List<String> returns = new ArrayList<>();
				for (CustomEnchantType c : CustomEnchantManager.getInstance().getAllEnchants().values().stream().map(CustomEnchant::getEnchantType).collect(Collectors
						.toList())) {
					returns.add("custom=" + c.toString().toLowerCase() + ":");
				}
				return returns;
			}
			if (currentArg.contains("enchants=")) {
				currentArg = currentArg.replace("enchants=", "");
				if (currentArg.length() > 0) {
					if (currentArg.contains(":")) {
						String[] currentEnchantArgs = currentArg.split(":");
						if (currentEnchantArgs.length % 2 == 1) {
							return Arrays.asList(
									"enchants=" + currentEnchantArgs[0] + "1,",
									"enchants=" + currentEnchantArgs[0] + "2,",
									"enchants=" + currentEnchantArgs[0] + "3,",
									"enchants=" + currentEnchantArgs[0] + "4,",
									"enchants=" + currentEnchantArgs[0] + "5,",
									"...");
						}
					}
				}
				List<String> returns = new ArrayList<>();
				for (Enchantment c : Enchantment.values()) {
					returns.add("enchants=" + c.getKey().getKey() + ":");
				}
				return returns;
			} else if (currentArg.contains("name=") || currentArg.contains("lore=")) {
				return Collections.singletonList(currentArg);
			} else {
				return Arrays.asList("custom=", "enchants=", "name=", "lore=");
			}
		} else {
			return Arrays.asList("custom=", "enchants=", "name=", "lore=");
		}
	}
}
