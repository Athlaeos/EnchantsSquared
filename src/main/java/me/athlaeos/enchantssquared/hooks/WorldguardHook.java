package me.athlaeos.enchantssquared.hooks;

import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.managers.WorldguardManager;
import org.bukkit.Location;

public class WorldguardHook {
    private static WorldguardHook hook = null;
    private boolean useWorldGuard = false;

    public static WorldguardHook getWorldguardHook(){
        if (hook == null){
            hook = new WorldguardHook();
        }
        return hook;
    }

    public void registerWorldGuard(){
        if (Main.getPlugin().getServer().getPluginManager().getPlugin("WorldGuard") == null){
            useWorldGuard = false;
        } else {
            useWorldGuard = true;
        }
    }

    public boolean useWorldGuard(){
        return useWorldGuard;
    }

    public boolean isLocationInRegionWithFlag(Location l, String flag){
        if (useWorldGuard){
            return WorldguardManager.getInstance().isLocationInFlaggedRegion(l, flag);
        } else {
            return false;
        }
    }

    public void registerFlags(){
        if (useWorldGuard){
            WorldguardManager.getInstance().registerFlags();
        }
    }
}
