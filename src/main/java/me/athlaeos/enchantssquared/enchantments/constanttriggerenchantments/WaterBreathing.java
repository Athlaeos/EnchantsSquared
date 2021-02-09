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

public class WaterBreathing extends ConstantTriggerEnchantment{
    private int duration;

    public WaterBreathing(){
        this.enchantType = CustomEnchantType.WATER_BREATHING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.water_breathing";
        loadConfig();
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        this.max_level_table = 0;
        this.max_level = 0;
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-water-breathing")){
                return;
            }
        }
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0), true);
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.water_breathing.enchant_name");
        this.duration = config.getInt("enchantment_configuration.water_breathing.duration");
        this.enabled = config.getBoolean("enchantment_configuration.water_breathing.enabled");
        this.weight = config.getInt("enchantment_configuration.water_breathing.weight");
        this.book_only = config.getBoolean("enchantment_configuration.water_breathing.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.water_breathing.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.water_breathing.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:water_breathing is not valid, please correct it");
            }
        }
    }
}
