package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.enchantments.StandardGlintEnchantment;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantCommand implements Command {
	private String enchant_success;
	private String enchant_failed;
	private String invalid_number;
	private String enchant_warning;
	private String enchant_description;

	public EnchantCommand(){
		enchant_success = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_successful");
		enchant_failed = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_failed");
		invalid_number = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_invalid_number");
		enchant_warning = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_warning");
		enchant_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length <= 1) return false;
		CustomEnchantType chosenEnchant;
		int chosenLevel = 1;
		try {
			chosenEnchant = CustomEnchantType.valueOf(args[1].toUpperCase());
		} catch (IllegalArgumentException e){
			return false;
		}
		if (args.length >= 3){
			try {
				chosenLevel = Integer.parseInt(args[2]);
				if (chosenLevel > 20){
					sender.sendMessage(Utils.chat(enchant_warning));
				}
			} catch (IllegalArgumentException e){
				sender.sendMessage(Utils.chat(invalid_number));
				return true;
			}
		}

		ItemStack inHandItem = ((Player) sender).getInventory().getItemInMainHand();
		if (inHandItem.getType() != Material.AIR) {
			CustomEnchantManager.getInstance().removeEnchant(inHandItem, chosenEnchant);
			CustomEnchantManager.getInstance().addEnchant(inHandItem, chosenEnchant, chosenLevel);
			sender.sendMessage(Utils.chat(enchant_success));
			if (inHandItem.getType() == Material.BOOK){
				inHandItem.setType(Material.ENCHANTED_BOOK);
			}
			if (inHandItem.getType() == Material.ENCHANTED_BOOK){
				if (inHandItem.getItemMeta() instanceof EnchantmentStorageMeta){
					EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) inHandItem.getItemMeta();
					storageMeta.addStoredEnchant(StandardGlintEnchantment.getEnsquaredGlint(), 1, true);
					inHandItem.setItemMeta(storageMeta);
				}
			} else {
				inHandItem.addUnsafeEnchantment(StandardGlintEnchantment.getEnsquaredGlint(), 1);
			}
		} else {
			sender.sendMessage(Utils.chat(enchant_failed));
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.enchant"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/es enchant [enchantment] <level>";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es enchant [enchantment] <level>"),
				Utils.chat("&7" + enchant_description),
				Utils.chat("&7> &des.enchant")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			List<String> returns = new ArrayList<>();
			for (CustomEnchantType c : CustomEnchantManager.getInstance().getAllEnchants().values().stream().map(CustomEnchant::getEnchantType).collect(Collectors
					.toList())){
				returns.add(c.toString().toLowerCase());
			}
			return returns;
		}
		if (args.length == 3){
			return Arrays.asList(
					"1",
					"2",
					"3",
					"...");
		}
		return null;
	}
}
