package me.athlaeos.enchantssquared.hooks;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.player.UserManager;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.JobsManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.gmail.nossr50.mcMMO;
import org.bukkit.scheduler.BukkitRunnable;

public class McMMOHook {
    private static McMMOHook hook = null;
    private boolean useMcMMO = false;

    public static McMMOHook getMcMMOHook(){
        if (hook == null){
            hook = new McMMOHook();
        }
        return hook;
    }

    public void registerMcMMO(){
        if (EnchantsSquared.getPlugin().getServer().getPluginManager().getPlugin("McMMO") == null){
            useMcMMO = false;
        } else {
            useMcMMO = true;
        }
    }

    public boolean useMcMMO(){
        return useMcMMO;
    }

    public void rememberBlock(Player p, Block b){
//        if (useMcMMO){
//            new BukkitRunnable(){
//                @Override
//                public void run() {
//                    McMMOPlayer mcMMOPlayer = UserManager.getPlayer(p);
//                    if(mcMMOPlayer == null) {
//                        mcMMO.getPlaceStore().setFalse(b.getState());
//                        return;
//                    }
//                    mcMMOPlayer.getMiningManager().miningBlockCheck(b.getState());
//                    mcMMO.getPlaceStore().setTrue(b.getState());
//
//                }
//            }.runTaskLater(EnchantsSquared.getPlugin(), 1L);
//            if (BlockUtils.affectedBySuperBreaker(b.getState()) && ItemUtils.isPickaxe(p.getInventory().getItemInMainHand()) && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(p, PrimarySkillType.MINING) && !mcMMO.getPlaceStore().isTrue(b)) {
//            }
//            com.gmail.nossr50.util.player.UserManager.getPlayer("").getMiningManager().;
//        } else {
//            System.out.println("not using mmo");
//        }
    }
}
