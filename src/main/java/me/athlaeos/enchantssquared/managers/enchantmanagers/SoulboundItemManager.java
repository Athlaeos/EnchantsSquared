package me.athlaeos.enchantssquared.managers.enchantmanagers;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SoulboundItemManager {

    private static SoulboundItemManager manager = null;
    private Map<UUID, List<ItemStack>> deadPeoplesStuff = new HashMap<>();

    public static SoulboundItemManager getInstance(){
        if (manager == null){
            manager = new SoulboundItemManager();
        }
        return manager;
    }

    public void executeOnDeath(UUID player, ItemStack item){
        if (!deadPeoplesStuff.containsKey(player)){
            deadPeoplesStuff.put(player, new ArrayList<>());
        }
        List<ItemStack> items = deadPeoplesStuff.get(player);
        items.add(item);
        deadPeoplesStuff.put(player, items);
    }

    public List<ItemStack> executeOnRespawn(UUID player){
        if (!deadPeoplesStuff.containsKey(player)) return new ArrayList<>();
        List<ItemStack> items = new ArrayList<>(deadPeoplesStuff.get(player));
        deadPeoplesStuff.remove(player);
        return items;
    }
}
