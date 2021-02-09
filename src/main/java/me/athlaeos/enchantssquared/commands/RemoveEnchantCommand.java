package me.athlaeos.enchantssquared.commands;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.Command;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class RemoveEnchantCommand implements Command {
    private String invalid_number;
    private String remove_enchant_description;
    private String remove_enchant_successful;
    private String remove_enchant_failed;

    public RemoveEnchantCommand(){
        invalid_number = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_invalid_number");
        remove_enchant_description = ConfigManager.getInstance().getConfig("translations.yml").get().getString("remove_enchant_description");
        remove_enchant_successful = ConfigManager.getInstance().getConfig("translations.yml").get().getString("remove_enchant_successful");
        remove_enchant_failed = ConfigManager.getInstance().getConfig("translations.yml").get().getString("remove_enchant_failed");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length <= 1) return false;
        int lineNumber = 1;
        try {
            lineNumber = Integer.parseInt(args[1]);
            if (lineNumber < 1) {
                sender.sendMessage(Utils.chat(invalid_number));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(Utils.chat(invalid_number));
            return true;
        }
        ItemStack inhandItem = ((Player) sender).getInventory().getItemInMainHand();
        if (inhandItem.getType() != Material.AIR) {
            ItemMeta meta = inhandItem.getItemMeta();
            List<String> itemlore;
            if (meta.hasLore()) {
                assert inhandItem.getItemMeta().getLore() != null;
                itemlore = inhandItem.getItemMeta().getLore();
                if (lineNumber > itemlore.size()) {
                    sender.sendMessage(Utils.chat(invalid_number));
                    return true;
                }
                itemlore.remove(lineNumber - 1);
                meta.setLore(itemlore);
                inhandItem.setItemMeta(meta);
                CustomEnchantManager.getInstance().updateItem(inhandItem);
                sender.sendMessage(Utils.chat(remove_enchant_successful));
            } else {
                sender.sendMessage(Utils.chat(remove_enchant_failed));
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"es.removeenchant"};
    }

    @Override
    public String getFailureMessage() {
        return "&c/es remove <line number>";
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                Utils.chat("&8&m                                             "),
                Utils.chat("&d/es remove <line number>"),
                Utils.chat("&7" + remove_enchant_description),
                Utils.chat("&7> &des.removeenchant")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2){
            return Arrays.asList(
                    "1",
                    "2",
                    "3",
                    "...");
        }
        return null;
    }
}
