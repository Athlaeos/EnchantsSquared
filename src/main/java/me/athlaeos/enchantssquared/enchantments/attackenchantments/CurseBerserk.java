package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class CurseBerserk extends AttackEnchantment{
    private double damage_taken_base;
    private double damage_taken_lv;
    private double damage_dealt_base;
    private double damage_dealt_lv;

    public CurseBerserk(){
        this.enchantType = CustomEnchantType.CURSE_BERSERK;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.curse_berserk";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        // this enchant needs buff damage dealt and nerf damage taken on the same person, so it can't be coded here
        // I know it's ugly, but this is an exception enchant which can be found in the EntityAttackEntityListener
    }

    public double getDamageTakenMultiplier(Entity defender, int level){
        if (!defender.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(defender.getLocation(), "es-deny-curse-berserk")){
                return 1;
            }
        }
        return ((level <= 1) ? this.damage_taken_base : (this.damage_taken_base + ((level - 1) * damage_taken_lv)));
    }

    public double getDamageDealtMultiplier(Entity attacker, int level){
        if (!attacker.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(attacker.getLocation(), "es-deny-curse-berserk")){
                return 1;
            }
        }
        return ((level <= 1) ? this.damage_dealt_base : (this.damage_dealt_base + ((level - 1) * damage_dealt_lv)));
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.curse_berserk.enchant_name");
        this.damage_taken_base = config.getDouble("enchantment_configuration.curse_berserk.damage_taken_base");
        this.damage_taken_lv = config.getDouble("enchantment_configuration.curse_berserk.damage_taken_lv");
        this.damage_dealt_base = config.getDouble("enchantment_configuration.curse_berserk.damage_dealt_base");
        this.damage_dealt_lv = config.getDouble("enchantment_configuration.curse_berserk.damage_dealt_lv");
        this.enabled = config.getBoolean("enchantment_configuration.curse_berserk.enabled");
        this.weight = config.getInt("enchantment_configuration.curse_berserk.weight");
        this.book_only = config.getBoolean("enchantment_configuration.curse_berserk.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.curse_berserk.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.curse_berserk.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.curse_berserk.description");
        this.compatibleItemStrings = config.getStringList("enchantment_configuration.curse_berserk.compatible_with");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.curse_berserk.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.curse_berserk.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.curse_berserk.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.curse_berserk.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.curse_berserk.trade_enabled");
        setIcon(config.getString("enchantment_configuration.curse_berserk.icon"));

        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:curse_berserk is not valid, please correct it");
            }
        }
    }
}
