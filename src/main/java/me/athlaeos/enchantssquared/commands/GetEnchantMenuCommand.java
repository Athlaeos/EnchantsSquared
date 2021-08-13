package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.menus.EnchantmentOverviewMenu;
import me.athlaeos.enchantssquared.menus.PlayerMenuUtilManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetEnchantMenuCommand implements Command {
	private String menu_description;

	public GetEnchantMenuCommand(){
		menu_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("menu_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		new EnchantmentOverviewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();

		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.menu"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/es menu";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es menu"),
				Utils.chat("&7" + menu_description),
				Utils.chat("&7> &des.menu")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
