package me.athlaeos.enchantssquared.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private static CooldownManager manager = null;

    private Map<String, Map<UUID, Long>> allCooldowns = new HashMap<>();
    public CooldownManager(){
        allCooldowns.put("illuminated_cooldown", new HashMap<>());
        allCooldowns.put("shockwave_cooldown", new HashMap<>());
    }

    public static CooldownManager getInstance(){
        if (manager == null){
            manager = new CooldownManager();
        }
        return manager;
    }

    public void setItemCooldown(UUID player, int timems, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        allCooldowns.get(cooldownKey).put(player, System.currentTimeMillis() + timems);
    }

    public long getItemCooldown(UUID player, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        if (allCooldowns.get(cooldownKey).containsKey(player)){
            return allCooldowns.get(cooldownKey).get(player) - System.currentTimeMillis();
        }
        return 0;
    }

    public boolean canPlayerUseItem(UUID player, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        if (allCooldowns.get(cooldownKey).containsKey(player)){
            return allCooldowns.get(cooldownKey).get(player) <= System.currentTimeMillis();
        }
        return true;
    }
}
