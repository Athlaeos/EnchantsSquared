package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBoost extends ConstantTriggerEnchantment{
    private int amplifier;
    private int duration;
    private int amplifier_lv;

    public SpeedBoost(){
        this.enchantType = CustomEnchantEnum.SPEED_BOOST;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.speed_boost";
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-speed")){
                return;
            }
        }
        int final_amplifier = (level <= 1) ? this.amplifier : (this.amplifier + ((level - 1) * amplifier_lv));
        if (compatibleItems.contains(stack.getType())){
            if (e.getPlayer().hasPotionEffect(PotionEffectType.SPEED)){
                if (e.getPlayer().getPotionEffect(PotionEffectType.SPEED).getAmplifier() <= final_amplifier){
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, final_amplifier), true);
                }
            } else {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, final_amplifier), true);
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.speed_boost.enchant_name");
        this.amplifier = config.getInt("enchantment_configuration.speed_boost.amplifier");
        this.duration = config.getInt("enchantment_configuration.speed_boost.duration");
        this.amplifier_lv = config.getInt("enchantment_configuration.speed_boost.amplifier_lv");
        this.enabled = config.getBoolean("enchantment_configuration.speed_boost.enabled");
        this.weight = config.getInt("enchantment_configuration.speed_boost.weight");
        this.book_only = config.getBoolean("enchantment_configuration.speed_boost.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.speed_boost.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.speed_boost.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.speed_boost.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.speed_boost.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:speed_boost is not valid, please correct it");
            }
        }
    }
}
