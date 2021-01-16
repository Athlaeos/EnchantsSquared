package me.athlaeos.enchantssquared.managers.enchantmanagers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToxicHealingReductionManager {

    private static ToxicHealingReductionManager manager = null;

    private Map<UUID, Map<Integer, Long>> afflictedEntities = new HashMap<>();
    public ToxicHealingReductionManager(){
    }

    public static ToxicHealingReductionManager getInstance(){
        if (manager == null){
            manager = new ToxicHealingReductionManager();
        }
        return manager;
    }

    public void afflictEntity(UUID entity, int level, int timeMS){
        if (afflictedEntities.containsKey(entity)){
            afflictedEntities.get(entity).put(level, System.currentTimeMillis() + timeMS);
        } else {
            Map<Integer, Long> debuff = new HashMap<>();
            debuff.put(level, System.currentTimeMillis() + timeMS);
            afflictedEntities.put(entity, debuff);
        }
    }

    public int getHealingReductionLevel(UUID player){
        if (afflictedEntities.containsKey(player)){
            int level = 0;
            for (Integer i : afflictedEntities.get(player).keySet()){
                if (afflictedEntities.get(player).get(i) - System.currentTimeMillis() > 0){
                    if (i > 0) level = i;
                }
            }
            return level;
        }
        return 0;
    }
}
