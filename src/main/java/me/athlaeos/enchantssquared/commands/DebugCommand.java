package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.Debug;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.managers.CommandManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.MineUtils;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DebugCommand implements Command {
	private String debug_description;

	public DebugCommand(){
		debug_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("debug_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		boolean debugMode = Debug.toggleDebug(sender);
		if (debugMode){
			sender.sendMessage(Utils.chat("&fDebug mode: &atrue"));
		} else {
			sender.sendMessage(Utils.chat("&fDebug mode: &cfalse"));
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"es.debug"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/es debug";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/es debug"),
				Utils.chat("&7" + debug_description),
				Utils.chat("&7> &des.debug")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
