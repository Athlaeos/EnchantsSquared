package me.athlaeos.enchantssquared.enchantments.defendenchantments;

import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantClassification;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Steady extends DefendEnchantment {
    private double knockbacK_reduction_lv = 1D;
    private CustomEnchantManager manager;

    public Steady(){
        this.enchantType = CustomEnchantEnum.KNOCKBACK_PROTECTION;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.steady";
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (manager == null) manager = CustomEnchantManager.getInstance();
        if (!victim.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-knockback-resistance")){
                return;
            }
        }

        int collectiveLevel = 0;
        for (ItemStack item : Utils.getEntityEquipment(victim, true)) {
            if (this.compatibleItems.contains(item.getType())) {
                collectiveLevel += manager.getEnchantStrength(item, CustomEnchantEnum.KNOCKBACK_PROTECTION, CustomEnchantClassification.ON_DAMAGED);
            }
        }

        double final_knockback_reduced = 1 - (collectiveLevel * knockbacK_reduction_lv);
        if (final_knockback_reduced < 0) final_knockback_reduced = 0D;

        double final_knockback_reduced1 = final_knockback_reduced;
        new BukkitRunnable(){
            @Override
            public void run() {
                victim.setVelocity(victim.getVelocity().multiply(final_knockback_reduced1));
            }
        }.runTask(Main.getPlugin());
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.steady.enchant_name");
        this.knockbacK_reduction_lv = config.getDouble("enchantment_configuration.steady.knockback_reduction_lv");
        this.enabled = config.getBoolean("enchantment_configuration.steady.enabled");
        this.weight = config.getInt("enchantment_configuration.steady.weight");
        this.book_only = config.getBoolean("enchantment_configuration.steady.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.steady.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.steady.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.steady.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.steady.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:steady is not valid, please correct it");
            }
        }
    }
}
