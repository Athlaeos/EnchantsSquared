package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class Vigorous extends ConstantTriggerEnchantment{
    private double health_lv;
    private CustomEnchantManager manager = null;

    public Vigorous(){
        this.enchantType = CustomEnchantType.VIGOROUS;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.vigorous";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (manager == null) manager = CustomEnchantManager.getInstance();
        int healthBoostLevel = 0;
        PotionEffect healthBoostBuff = e.getPlayer().getPotionEffect(PotionEffectType.HEALTH_BOOST);
        if (healthBoostBuff != null) healthBoostLevel += healthBoostBuff.getAmplifier() + 1;
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-vigorous")){
                if (e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() != (20 + (healthBoostLevel * 4))){
                    e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + (healthBoostLevel * 4));
                }
                return;
            }
        }
        int collectiveLevel = 0;
        for (ItemStack item : Utils.getEntityEquipment(e.getPlayer(), true)){
            collectiveLevel += manager.getEnchantStrength(item, CustomEnchantType.VIGOROUS);
        }
        double finalBonusHealth = collectiveLevel * health_lv;
        e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + (healthBoostLevel * 4) + finalBonusHealth);
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.vigorous.enchant_name");
        this.health_lv = config.getDouble("enchantment_configuration.vigorous.health_lv");
        this.enabled = config.getBoolean("enchantment_configuration.vigorous.enabled");
        this.weight = config.getInt("enchantment_configuration.vigorous.weight");
        this.book_only = config.getBoolean("enchantment_configuration.vigorous.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.vigorous.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.vigorous.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.vigorous.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.vigorous.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.vigorous.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.vigorous.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.vigorous.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.vigorous.trade_enabled");
        setIcon(config.getString("enchantment_configuration.vigorous.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.vigorous.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:vigorous is not valid, please correct it");
            }
        }
    }
}
