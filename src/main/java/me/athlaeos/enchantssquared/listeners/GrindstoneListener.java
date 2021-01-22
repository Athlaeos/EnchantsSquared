package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GrindstoneListener implements Listener {

    private List<HumanEntity> peoplewhowerespammed = new ArrayList<>();
    private String message;

    public GrindstoneListener(){
        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_grindstone_clear_all");
    }

    @EventHandler
    public void onGrindstoneUse(InventoryClickEvent e){
        if (e.getClickedInventory() instanceof GrindstoneInventory){
            if (!peoplewhowerespammed.contains(e.getWhoClicked())){
                e.getWhoClicked().sendMessage(Utils.chat(message));
                peoplewhowerespammed.add(e.getWhoClicked());
            }

            if (e.getSlotType() == InventoryType.SlotType.RESULT){
                ItemStack item = e.getCurrentItem();
                if (item != null){
                    if (item.hasItemMeta()){
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        if (meta.hasLore()){
                            meta.setLore(new ArrayList<>());
                        }
                        item.setItemMeta(meta);
                    }
                }
            }
        }
    }
}