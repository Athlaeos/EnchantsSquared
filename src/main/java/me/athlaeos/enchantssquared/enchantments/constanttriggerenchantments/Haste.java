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

public class Haste extends ConstantTriggerEnchantment{
    private int amplifier;
    private int duration;
    private int amplifier_lv;

    public Haste(){
        this.enchantType = CustomEnchantType.HASTE;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.haste";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-haste")){
                return;
            }
        }
        int final_amplifier = (level <= 1) ? this.amplifier : (this.amplifier + ((level - 1) * amplifier_lv));
        if (e.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING)){
            if (e.getPlayer().getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier() <= final_amplifier){
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, final_amplifier), true);
            }
        } else {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, final_amplifier), true);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.haste.enchant_name");
        this.amplifier = config.getInt("enchantment_configuration.haste.amplifier");
        this.duration = config.getInt("enchantment_configuration.haste.duration");
        this.amplifier_lv = config.getInt("enchantment_configuration.haste.amplifier_lv");
        this.enabled = config.getBoolean("enchantment_configuration.haste.enabled");
        this.weight = config.getInt("enchantment_configuration.haste.weight");
        this.book_only = config.getBoolean("enchantment_configuration.haste.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.haste.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.haste.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.haste.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.haste.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.haste.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.haste.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.haste.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.haste.trade_enabled");
        setIcon(config.getString("enchantment_configuration.haste.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.haste.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:haste is not valid, please correct it");
            }
        }
    }
}
