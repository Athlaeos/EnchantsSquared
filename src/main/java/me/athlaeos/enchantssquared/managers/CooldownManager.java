package me.athlaeos.enchantssquared.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private static CooldownManager manager = null;

    private Map<String, Map<UUID, Long>> allCooldowns = new HashMap<>();
    private Map<String, Map<UUID, Integer>> staticTimers = new HashMap<>();
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

    public Map<UUID, Long> getCooldowns(String cooldownKey){
        if (allCooldowns.containsKey(cooldownKey)){
            return allCooldowns.get(cooldownKey);
        }
        return new HashMap<>();
    }

    public boolean canPlayerUseItem(UUID player, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        if (allCooldowns.get(cooldownKey).containsKey(player)){
            return allCooldowns.get(cooldownKey).get(player) <= System.currentTimeMillis();
        }
        return true;
    }

    public void setStaticTimer(UUID entity, int value, String key){
        if (!staticTimers.containsKey(key)) staticTimers.put(key, new HashMap<>());
        Map<UUID, Integer> timers = staticTimers.get(key);
        timers.put(entity, value);
        staticTimers.put(key, timers);
    }

    public void incrementStaticTimer(UUID entity, int amount, String key){
        if (!staticTimers.containsKey(key)) staticTimers.put(key, new HashMap<>());
        Map<UUID, Integer> timers = staticTimers.get(key);
        timers.put(entity, getStaticTimer(entity, key) + amount);
        staticTimers.put(key, timers);
    }

    public int getStaticTimer(UUID entity, String key){
        if (!staticTimers.containsKey(key)) staticTimers.put(key, new HashMap<>());
        if (!staticTimers.get(key).containsKey(entity)) return 0;
        return staticTimers.get(key).get(entity);
    }

    public Map<UUID, Integer> getStaticTimers(String key) {
        if (!staticTimers.containsKey(key)) staticTimers.put(key, new HashMap<>());
        return staticTimers.get(key);
    }
}
