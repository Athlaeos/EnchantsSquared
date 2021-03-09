package me.athlaeos.enchantssquared.enchantments.interactenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class AutoReplant extends BlockInteractEnchantment {
    public AutoReplant(){
        this.enchantType = CustomEnchantType.AUTO_REPLANT;
        this.max_level_table = 0;
        this.max_level = 0;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.auto_replant";
        loadFunctionalItemStrings(Collections.singletonList("HOES"));
        this.compatibleItemStrings = Collections.singletonList("HOES");
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
        if (this.functionalItems.contains(tool.getType())){
            if (Arrays.asList(Material.WHEAT, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA)
                    .contains(e.getClickedBlock().getType())){
                if (e.getClickedBlock().getBlockData() instanceof Ageable){
                    Ageable crop = (Ageable) e.getClickedBlock().getBlockData();
                    Block blockUnderCrop = e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, -1, 0));
                    EquipmentSlot hand;
                    if (e.getPlayer().getInventory().getItemInMainHand().equals(tool)){
                        hand = EquipmentSlot.HAND;
                    } else {
                        hand = EquipmentSlot.OFF_HAND;
                    }
                    if (crop.getAge() >= crop.getMaximumAge()){
                        BlockBreakEvent breakEvent = new BlockBreakEvent(e.getClickedBlock(), e.getPlayer());
                        EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(breakEvent);
                        if (!breakEvent.isCancelled()){
                            JobsHook.getJobsHook().performBlockBreakAction(e.getPlayer(), e.getClickedBlock());
                            Collection<ItemStack> drops =  e.getClickedBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand());

                            for (ItemStack drop : drops){
                                e.getClickedBlock().getWorld().dropItem(e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), drop);
                            }
                        }
                        BlockPlaceEvent placeEvent = new BlockPlaceEvent(e.getClickedBlock(), e.getClickedBlock().getState()
                                , blockUnderCrop, tool, e.getPlayer(), true, hand);
                        EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(placeEvent);
                        if (!placeEvent.isCancelled()){
                            crop.setAge(0);
                            e.getClickedBlock().setBlockData(crop);
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
        this.tradeMinCostBase = config.getInt("enchantment_configuration.auto_replant.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.auto_replant.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.auto_replant.trade_enabled");

        compatibleItems.addAll(ItemMaterialManager.getInstance().getHoes());
    }
}
