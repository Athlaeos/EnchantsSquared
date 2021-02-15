package me.athlaeos.enchantssquared.hooks;

import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.JobsManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JobsHook {

    private static JobsHook hook = null;
    private boolean useJobs = false;

    public static JobsHook getJobsHook(){
        if (hook == null){
            hook = new JobsHook();
        }
        return hook;
    }

    public void registerJobs(){
        if (EnchantsSquared.getPlugin().getServer().getPluginManager().getPlugin("Jobs") == null){
            useJobs = false;
        } else {
            useJobs = true;
        }
    }

    public boolean useJobs(){
        return useJobs;
    }

    public void performBlockBreakAction(Player p, Block b){
        if (useJobs){
            JobsManager.getInstance().performBlockBreakAction(p, b);
        }
    }
}
