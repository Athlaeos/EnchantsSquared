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

public class Strength extends ConstantTriggerEnchantment{
    private int amplifier;
    private int duration;
    private int amplifier_lv;

    public Strength(){
        this.enchantType = CustomEnchantType.STRENGTH;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.strength";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-barbarian")){
                return;
            }
        }
        int final_amplifier = (level <= 1) ? this.amplifier : (this.amplifier + ((level - 1) * amplifier_lv));
        if (e.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
            if (e.getPlayer().getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() <= final_amplifier){
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, final_amplifier), true);
            }
        } else {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, final_amplifier), true);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.strength.enchant_name");
        this.amplifier = config.getInt("enchantment_configuration.strength.amplifier");
        this.duration = config.getInt("enchantment_configuration.strength.duration");
        this.amplifier_lv = config.getInt("enchantment_configuration.strength.amplifier_lv");
        this.enabled = config.getBoolean("enchantment_configuration.strength.enabled");
        this.weight = config.getInt("enchantment_configuration.strength.weight");
        this.book_only = config.getBoolean("enchantment_configuration.strength.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.strength.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.strength.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.strength.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.strength.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:strength is not valid, please correct it");
            }
        }
    }
}
