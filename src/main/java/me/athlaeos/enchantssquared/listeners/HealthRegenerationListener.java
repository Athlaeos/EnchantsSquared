package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantClassification;
import me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments.HealthRegenerationEnchantment;
import me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments.Vitality;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.enchantmanagers.ToxicHealingReductionManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HealthRegenerationListener implements Listener {

    private double healingReductionBase;
    private double healingReductionLv;

    public HealthRegenerationListener(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();

        this.healingReductionBase = config.getDouble("enchantment_configuration.toxic.healing_reduction_base");
        this.healingReductionLv = config.getDouble("enchantment_configuration.toxic.healing_reduction_lv");
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent e){
        if (!e.isCancelled()){
            if (!e.getEntity().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-all")) return;
            }
            if (e.getEntity() instanceof LivingEntity){
                Vitality v = null;

                LivingEntity entity = (LivingEntity) e.getEntity();
                List<ItemStack> equipment = new ArrayList<>();
                if (entity.getEquipment() == null) return;
                if (entity.getEquipment().getHelmet() != null) equipment.add(entity.getEquipment().getHelmet());
                if (entity.getEquipment().getChestplate() != null) equipment.add(entity.getEquipment().getChestplate());
                if (entity.getEquipment().getLeggings() != null) equipment.add(entity.getEquipment().getLeggings());
                if (entity.getEquipment().getBoots() != null) equipment.add(entity.getEquipment().getBoots());
                for (ItemStack i : equipment){
                    for (CustomEnchant en : CustomEnchantManager.getInstance().getItemsEnchants(i, CustomEnchantClassification.ON_HEALTH_REGEN).keySet()){
                        if (en instanceof HealthRegenerationEnchantment){
                            if (en instanceof Vitality){ //vitality is an exception enchantment that may only execute
                                //once per event instead of once for each piece of armor
                                v = (Vitality) en;
                            } else {
                                ((HealthRegenerationEnchantment) en).execute(e);
                            }
                        }
                    }
                }
                if (v != null){
                    v.execute(e);
                }
                int healingReductionLevel = ToxicHealingReductionManager.getInstance().getHealingReductionLevel(entity.getUniqueId());
                if (healingReductionLevel != 0) {
                    double finalHealingReduction = 1 - ((healingReductionLevel == 1) ? this.healingReductionBase : (this.healingReductionBase + ((healingReductionLevel - 1) * healingReductionLv)));
                    e.setAmount(e.getAmount() * Math.max(finalHealingReduction, 0D));
                }
            }
        }
    }
}
