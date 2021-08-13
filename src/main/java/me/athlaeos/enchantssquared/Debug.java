package me.athlaeos.enchantssquared;

import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Debug {
    private static final Map<CommandSender, Boolean> debugPlayers = new HashMap<>();
    private static boolean consoleDebugMode = false;

    public static boolean toggleDebug(CommandSender sender){
        if (sender instanceof Entity){
            if (debugPlayers.containsKey(sender)){
                debugPlayers.put(sender, !debugPlayers.get(sender));
            } else {
                debugPlayers.put(sender, true);
            }
            return debugPlayers.get(sender);
        } else {
            consoleDebugMode = !consoleDebugMode;
            return consoleDebugMode;
        }
    }

    public static void log(CommandSender sender, String message){
        if (sender instanceof Player){
            if (debugPlayers.containsKey(sender)){
                if (debugPlayers.get(sender)){
                    sender.sendMessage(Utils.chat(message));
                }
            }
        } else {
            if (consoleDebugMode){
                sender.sendMessage(Utils.chat(message));
            }
        }
    }
}
