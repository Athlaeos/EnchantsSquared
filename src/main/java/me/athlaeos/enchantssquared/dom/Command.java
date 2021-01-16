package me.athlaeos.enchantssquared.dom;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Command {
	public boolean execute(CommandSender sender, String[] args);
	public String[] getRequiredPermission();
	public String getFailureMessage();
	public String[] getHelpEntry();
	public List<String> getSubcommandArgs(CommandSender sender, String[] args);
}
