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

public class CurseHeavy extends ConstantTriggerEnchantment{
    private int duration;
    private int amplifier_slow;
    private int amplifier_lv_slow;
    private int amplifier_fatigue;
    private int amplifier_lv_fatigue;

    public CurseHeavy(){
        this.enchantType = CustomEnchantEnum.CURSE_HEAVY;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.curse_heavy";
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-curse-heavy")){
                return;
            }
        }
        int final_amplifier_slow = (level <= 1) ? this.amplifier_slow : (this.amplifier_slow + ((level - 1) * amplifier_lv_slow));
        int final_amplifier_fatigue = (level <= 1) ? this.amplifier_fatigue : (this.amplifier_fatigue + ((level - 1) * amplifier_lv_fatigue));
        if (compatibleItems.contains(stack.getType())){
            if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW)){
                if (e.getPlayer().getPotionEffect(PotionEffectType.SLOW).getAmplifier() <= final_amplifier_slow){
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, final_amplifier_slow), true);
                }
            } else {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, final_amplifier_slow), true);
            }
            if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)){
                if (e.getPlayer().getPotionEffect(PotionEffectType.SLOW_DIGGING).getAmplifier() <= final_amplifier_fatigue){
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, final_amplifier_fatigue), true);
                }
            } else {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, final_amplifier_fatigue), true);
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.curse_heavy.enchant_name");
        this.duration = config.getInt("enchantment_configuration.curse_heavy.duration");
        this.amplifier_slow = config.getInt("enchantment_configuration.curse_heavy.amplifier_slow");
        this.amplifier_lv_slow = config.getInt("enchantment_configuration.curse_heavy.amplifier_lv_slow");
        this.amplifier_fatigue = config.getInt("enchantment_configuration.curse_heavy.amplifier_fatigue");
        this.amplifier_lv_fatigue = config.getInt("enchantment_configuration.curse_heavy.amplifier_lv_fatigue");
        this.enabled = config.getBoolean("enchantment_configuration.curse_heavy.enabled");
        this.weight = config.getInt("enchantment_configuration.curse_heavy.weight");
        this.book_only = config.getBoolean("enchantment_configuration.curse_heavy.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.curse_heavy.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.curse_heavy.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.curse_heavy.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.curse_heavy.compatible_with");
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
