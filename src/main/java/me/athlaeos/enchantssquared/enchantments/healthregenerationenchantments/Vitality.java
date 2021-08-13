package me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class Vitality extends HealthRegenerationEnchantment{
    private int extra_healing_lv;
    private int max_bonus_healing;
    private CustomEnchantManager manager;

    public Vitality(){
        this.enchantType = CustomEnchantType.VITALITY;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.vitality";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(EntityRegainHealthEvent e) {
        if (manager == null) manager = CustomEnchantManager.getInstance();
        if (!e.getEntity().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-vitality")){
                return;
            }
        }

        assert e.getEntity() instanceof LivingEntity;
        int collectiveLevel = 0;
        for (ItemStack item : Utils.getEntityEquipment(e.getEntity(), true)){
            collectiveLevel += manager.getEnchantStrength(item, CustomEnchantType.VITALITY);
        }

        int healBonus = extra_healing_lv * collectiveLevel;
        if (healBonus > max_bonus_healing) healBonus = max_bonus_healing;

        e.setAmount(e.getAmount() * (1D + (healBonus/100D)));
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.vitality.enchant_name");
        this.extra_healing_lv = config.getInt("enchantment_configuration.vitality.extra_healing_lv");
        this.max_bonus_healing = config.getInt("enchantment_configuration.vitality.max_bonus_healing");
        this.enabled = config.getBoolean("enchantment_configuration.vitality.enabled");
        this.weight = config.getInt("enchantment_configuration.vitality.weight");
        this.book_only = config.getBoolean("enchantment_configuration.vitality.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.vitality.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.vitality.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.vitality.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.vitality.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.vitality.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.vitality.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.vitality.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.vitality.trade_enabled");
        setIcon(config.getString("enchantment_configuration.vitality.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.vitality.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:vitality is not valid, please correct it");
            }
        }
    }
}
