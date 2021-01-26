package me.athlaeos.enchantssquared.enchantments.interactenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class AutoReplant extends InteractEnchantment{
    public AutoReplant(){
        this.enchantType = CustomEnchantEnum.AUTO_REPLANT;
        this.max_level_table = 0;
        this.max_level = 0;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.autoreplant";
        loadConfig();
    }

    @Override
    public void execute(PlayerInteractEvent e, ItemStack tool, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-auto-replant")){
                return;
            }
        }
        if (e.getClickedBlock() == null) return;
        if (Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA)
                .contains(e.getClickedBlock().getType())){
            if (e.getClickedBlock().getBlockData() instanceof Ageable){
                Ageable crop = (Ageable) e.getClickedBlock().getBlockData();
                if (crop.getAge() >= crop.getMaximumAge()){
                    BlockBreakEvent breakEvent = new BlockBreakEvent(e.getClickedBlock(), e.getPlayer());
                    Main.getPlugin().getServer().getPluginManager().callEvent(breakEvent);
                    if (!breakEvent.isCancelled()){
                        JobsHook.getJobsHook().performBlockBreakAction(e.getPlayer(), e.getClickedBlock());
                        Collection<ItemStack> drops =  e.getClickedBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand());

                        crop.setAge(0);
                        e.getClickedBlock().setBlockData(crop);
                        for (ItemStack drop : drops){
                            e.getClickedBlock().getWorld().dropItem(e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), drop);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.auto_replant.enchant_name");
        this.enabled = config.getBoolean("enchantment_configuration.auto_replant.enabled");
        this.weight = config.getInt("enchantment_configuration.auto_replant.weight");
        this.book_only = config.getBoolean("enchantment_configuration.auto_replant.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.auto_replant.description");

        compatibleItems.addAll(ItemMaterialManager.getInstance().getHoes());
    }
}
