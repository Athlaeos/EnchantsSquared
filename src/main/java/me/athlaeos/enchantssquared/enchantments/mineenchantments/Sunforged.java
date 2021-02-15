package me.athlaeos.enchantssquared.enchantments.mineenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.utils.MineUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Sunforged extends BreakBlockEnchantment{
    private static boolean drop_exp;

    public Sunforged(){
        this.enchantType = CustomEnchantType.SUNFORGED;
        this.max_level_table = 0;
        this.max_level = 0;
        this.conflictsWith.add(Enchantment.SILK_TOUCH);
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        loadFunctionalItemStrings(Arrays.asList("SWORDS", "AXES", "PICKAXES", "HOES", "SHOVELS", "SHEARS"));
        this.requiredPermission = "es.enchant.sunforged";
        loadConfig();
    }

    @Override
    public void execute(BlockBreakEvent e, ItemStack item, int level) {
        if (!e.isCancelled()){
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getBlock().getLocation(), "es-deny-smelting")){
                    return;
                }
            }
            if (this.functionalItems.contains(item.getType())){
                boolean hasExcavation = CustomEnchantManager.getInstance().doesItemHaveEnchant(e.getPlayer().getInventory().getItemInMainHand(), CustomEnchantType.EXCAVATION);
                if (!hasExcavation || e.getPlayer().isSneaking()){ //if player is sneaking excavation is disabled, so it smelts single blocks
                    if (e.getBlock().getDrops(item).isEmpty()) return;
                    for (ItemStack i : MineUtils.cookBlock(e.getPlayer().getInventory().getItemInMainHand(), e.getBlock())){
                        if (i != null){
                            e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5, 0.5, 0.5), i);
                        }
                    }
                    if (Sunforged.isDrop_exp()){
                        if (!MineUtils.cookBlock(e.getPlayer().getInventory().getItemInMainHand(), e.getBlock()).equals(e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand()))){
                            ExperienceOrb orb = (ExperienceOrb) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
                            orb.setExperience(1);
                        }
                    }

                    JobsHook.getJobsHook().performBlockBreakAction(e.getPlayer(), e.getBlock());
                    e.getBlock().setType(Material.AIR);
                    if (item.getItemMeta() instanceof Damageable){
                        Damageable toolMeta = (Damageable) item.getItemMeta();
                        int unBreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
                        double breakChance = 1D/(unBreakingLevel + 1D) * 100;
                        if ((RandomNumberGenerator.getRandom().nextInt(100) + 1) < breakChance){
                            PlayerItemDamageEvent event = new PlayerItemDamageEvent(e.getPlayer(), item, 1);
                            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                toolMeta.setDamage(toolMeta.getDamage() + event.getDamage());
                                item.setItemMeta((ItemMeta) toolMeta);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.sunforged.enchant_name");
        this.enabled = config.getBoolean("enchantment_configuration.sunforged.enabled");
        this.weight = config.getInt("enchantment_configuration.sunforged.weight");
        this.book_only = config.getBoolean("enchantment_configuration.sunforged.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.sunforged.description");
        drop_exp = config.getBoolean("enchantment_configuration.sunforged.drop_exp");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.sunforged.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:sunforged is not valid, please correct it");
            }
        }
    }

    public static boolean isDrop_exp(){
        return drop_exp;
    }
}
