package me.athlaeos.enchantssquared.managers;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JobsManager {
    private static JobsManager manager = null;

    public JobsManager(){

    }

    public static JobsManager getInstance(){
        if (manager == null){
            manager = new JobsManager();
        }
        return manager;
    }

    public void performBlockBreakAction(Player p, Block b){
        BlockActionInfo bInfo = new BlockActionInfo(b, ActionType.BREAK);
        Jobs.action(Jobs.getPlayerManager().getJobsPlayer(p), bInfo, b);
    }
}
