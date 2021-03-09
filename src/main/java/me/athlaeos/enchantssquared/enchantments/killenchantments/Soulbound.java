package me.athlaeos.enchantssquared.enchantments.killenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.enchantmanagers.SoulboundItemManager;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class Soulbound extends KillEnchantment{
    private boolean single_use;

    public Soulbound(){
        this.enchantType = CustomEnchantType.SOULBOUND;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.soulbound";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(EntityDeathEvent e, ItemStack stack, int level, LivingEntity killer, LivingEntity killed) {
        if (!killed.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-soulbound")){
                return;
            }
        }
        if (e.getDrops().contains(stack)){
            if (single_use){
                CustomEnchantManager.getInstance().removeEnchant(stack, CustomEnchantType.SOULBOUND);
            }
            SoulboundItemManager.getInstance().executeOnDeath((e.getEntity()).getUniqueId(), stack);
            e.getDrops().remove(stack);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.soulbound.enchant_name");
        this.single_use = config.getBoolean("enchantment_configuration.soulbound.single_use");
        this.enabled = config.getBoolean("enchantment_configuration.soulbound.enabled");
        this.weight = config.getInt("enchantment_configuration.soulbound.weight");
        this.book_only = config.getBoolean("enchantment_configuration.soulbound.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.soulbound.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.soulbound.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.soulbound.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.soulbound.trade_enabled");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.soulbound.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:soulbound is not valid, please correct it");
            }
        }
    }
}
