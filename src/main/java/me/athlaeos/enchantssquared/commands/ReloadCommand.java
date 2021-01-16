package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.managers.CommandManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.MineUtils;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReloadCommand implements Command {
	private String reload_successful;
	private String reload_description;

	public ReloadCommand(){
		reload_successful = ConfigManager.getInstance().getConfig("translations.yml").get().getString("reload_successful");
		reload_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("reload_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		for (String config : ConfigManager.getInstance().getConfigs().keySet()){
			ConfigManager.getInstance().getConfigs().get(config).reload();
		}
		CustomEnchantManager.getInstance().reload();
		CommandManager.getInstance().reload();
		MineUtils.reload();
		sender.sendMessage(Utils.chat(reload_successful));
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.reload"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/es reload";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es reload"),
				Utils.chat("&7" + reload_description),
				Utils.chat("&7> &des.reload")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
