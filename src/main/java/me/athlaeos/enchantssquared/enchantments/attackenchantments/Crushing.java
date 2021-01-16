package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Crushing extends AttackEnchantment{
    private double damage_base;
    private double damage_lv;

    public Crushing(){
        this.enchantType = CustomEnchantEnum.CRUSHING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.crushing";
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (!damager.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-crushing")){
                return;
            }
        }
        if (compatibleItems.contains(i.getType())){
            int combinedArmorPieces = 0;
            if (victim.getEquipment() != null){
                for (ItemStack item : victim.getEquipment().getArmorContents()){
                    if (item != null){
                        if (item.getType().toString().contains("DIAMOND_") || item.getType().toString().contains("NETHERITE_")){
                            combinedArmorPieces++;
                        }
                    }
                }
            }
            double damageMultiplier = 1 + (combinedArmorPieces * ((level <= 1) ? this.damage_base : this.damage_base + ((level - 1) * damage_lv)) / 100D);
            e.setDamage(e.getDamage() * damageMultiplier);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.crushing.enchant_name");
        this.damage_base = config.getDouble("enchantment_configuration.crushing.damage_base");
        this.damage_lv = config.getDouble("enchantment_configuration.crushing.damage_lv");
        this.enabled = config.getBoolean("enchantment_configuration.crushing.enabled");
        this.weight = config.getInt("enchantment_configuration.crushing.weight");
        this.book_only = config.getBoolean("enchantment_configuration.crushing.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.crushing.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.crushing.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.crushing.description");

        for (String s : config.getStringList("enchantment_configuration.crushing.compatible_with")){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:crushing is not valid, please correct it");
            }
        }
    }
}
