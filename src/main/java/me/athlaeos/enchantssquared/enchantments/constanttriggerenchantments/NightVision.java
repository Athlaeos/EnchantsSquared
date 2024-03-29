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

public class NightVision extends ConstantTriggerEnchantment{
    private int duration;

    public NightVision(){
        this.enchantType = CustomEnchantType.NIGHT_VISION;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.night_vision";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
        this.max_level_table = 0;
        this.max_level = 0;
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-night-vision")){
                return;
            }
        }
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0), true);
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.night_vision.enchant_name");
        this.duration = config.getInt("enchantment_configuration.night_vision.duration");
        this.enabled = config.getBoolean("enchantment_configuration.night_vision.enabled");
        this.weight = config.getInt("enchantment_configuration.night_vision.weight");
        this.book_only = config.getBoolean("enchantment_configuration.night_vision.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.night_vision.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.night_vision.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.night_vision.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.night_vision.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.night_vision.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.night_vision.trade_enabled");
        setIcon(config.getString("enchantment_configuration.night_vision.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.night_vision.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:night_vision is not valid, please correct it");
            }
        }
    }
}
