package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.enchantments.attackenchantments.AttackEnchantment;
import me.athlaeos.enchantssquared.enchantments.singletriggerenchantments.SingleTriggerEnchantment;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemAttributesManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TridentSharpness extends AttackEnchantment {
    private double damage_base;
    private double damage_lv;

    public TridentSharpness(){
        this.enchantType = CustomEnchantType.TRIDENT_SHARPNESS;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.trident_sharpness";
        loadFunctionalItemStrings(Collections.singletonList("TRIDENTS"));
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {

        if (victim == null) return;
        if (functionalItems.contains(i.getType())){
            double finalDamage = (level <= 1) ? this.damage_base : (this.damage_base + ((level - 1) * damage_lv));

            double currentDamage = e.getDamage();

            e.setDamage(currentDamage + finalDamage);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.trident_sharpness.enchant_name");
        this.damage_base = config.getDouble("enchantment_configuration.trident_sharpness.damage_base");
        this.damage_lv = config.getDouble("enchantment_configuration.trident_sharpness.damage_lv");
        this.enabled = config.getBoolean("enchantment_configuration.trident_sharpness.enabled");
        this.weight = config.getInt("enchantment_configuration.trident_sharpness.weight");
        this.book_only = config.getBoolean("enchantment_configuration.trident_sharpness.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.trident_sharpness.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.trident_sharpness.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.trident_sharpness.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.trident_sharpness.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.trident_sharpness.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.trident_sharpness.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.trident_sharpness.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.trident_sharpness.trade_enabled");
        setIcon(config.getString("enchantment_configuration.trident_sharpness.icon"));

        this.compatibleItemStrings = Collections.singletonList("TRIDENTS");
        this.compatibleItems.add(Material.TRIDENT);
    }
}
