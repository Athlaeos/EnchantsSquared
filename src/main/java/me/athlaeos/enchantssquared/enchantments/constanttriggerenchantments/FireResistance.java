package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class FireResistance extends ConstantTriggerEnchantment{
    private int duration;
    private boolean slow_nerf;

    public FireResistance(){
        this.enchantType = CustomEnchantType.LAVA_RESISTANCE;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.fire_resistance";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-fire-resistance")){
                return;
            }
        }
        if (e.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)){
            if (e.getPlayer().getPotionEffect(PotionEffectType.FIRE_RESISTANCE).getDuration() <= duration){
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0), true);
            }
        } else {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0), true);
        }
        if (slow_nerf){
            if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW)){
                if (e.getPlayer().getPotionEffect(PotionEffectType.SLOW).getAmplifier() == 0){
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 0), true);
                }
            } else {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 0), true);
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.fire_resistance.enchant_name");
        this.duration = config.getInt("enchantment_configuration.fire_resistance.duration");
        this.enabled = config.getBoolean("enchantment_configuration.fire_resistance.enabled");
        this.weight = config.getInt("enchantment_configuration.fire_resistance.weight");
        this.book_only = config.getBoolean("enchantment_configuration.fire_resistance.book_only");
        this.slow_nerf = config.getBoolean("enchantment_configuration.fire_resistance.slow_nerf");
        this.max_level_table = config.getInt("enchantment_configuration.fire_resistance.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.fire_resistance.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.fire_resistance.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.fire_resistance.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.fire_resistance.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.fire_resistance.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.fire_resistance.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.fire_resistance.trade_enabled");
        setIcon(config.getString("enchantment_configuration.fire_resistance.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.fire_resistance.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:fire_resistance is not valid, please correct it");
            }
        }
    }
}
