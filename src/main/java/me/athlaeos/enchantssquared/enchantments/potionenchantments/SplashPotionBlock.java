package me.athlaeos.enchantssquared.enchantments.potionenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class SplashPotionBlock extends PotionEffectEnchantment{

    public SplashPotionBlock(){
        this.enchantType = CustomEnchantType.CHEMICAL_SHIELD;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.chemical_shield";
        this.max_level_table = 0;
        this.max_level = 0;
        this.compatibleItemStrings = Collections.singletonList("SHIELDS");
        this.compatibleItems.add(Material.SHIELD);
        loadFunctionalItemStrings(Collections.singletonList("SHIELDS"));
        loadConfig();
    }

    @Override
    public void execute(EntityPotionEffectEvent e, ItemStack i, int level) {
        if (!e.getEntity().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-chemical-shield")){
                return;
            }
        }

        if (this.functionalItems.contains(i.getType())){
            if (e.getCause() == EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD || e.getCause() == EntityPotionEffectEvent.Cause.POTION_SPLASH){
                if (e.getEntity() instanceof Player){
                    Player p = (Player) e.getEntity();
                    if (p.isBlocking()){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.chemical_shield.enchant_name");
        this.enabled = config.getBoolean("enchantment_configuration.chemical_shield.enabled");
        this.weight = config.getInt("enchantment_configuration.chemical_shield.weight");
        this.book_only = config.getBoolean("enchantment_configuration.chemical_shield.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.chemical_shield.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.chemical_shield.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.chemical_shield.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.chemical_shield.trade_enabled");
        setIcon(config.getString("enchantment_configuration.chemical_shield.icon"));
    }
}
