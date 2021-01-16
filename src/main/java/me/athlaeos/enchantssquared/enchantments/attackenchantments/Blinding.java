package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Blinding extends AttackEnchantment{
    private int duration;
    private int duration_lv;
    private double apply_chance;
    private double apply_chance_lv;

    private String message;

    public Blinding(){
        this.enchantType = CustomEnchantEnum.BLINDING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.blinding";
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (!damager.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-blinding")){
                return;
            }
        }

        if (compatibleItems.contains(i.getType())){
            double final_apply_chance = (level <= 1) ? this.apply_chance : this.apply_chance + ((level - 1) * this.apply_chance_lv);
            if (RandomNumberGenerator.getRandom().nextDouble() <= final_apply_chance){
                int final_duration = (level <= 1) ? this.duration : this.duration + ((level - 1) * this.duration_lv);
                if (victim.hasPotionEffect(PotionEffectType.BLINDNESS)){
                    if (victim.getPotionEffect(PotionEffectType.BLINDNESS).getAmplifier() <= 0){
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, final_duration, 0, false, true), true);
                    }
                } else {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, final_duration, 0, false, true), true);
                }
                if (damager instanceof Player){
                    if (!message.equals("")){
                        ((Player) damager).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(message)));
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.blinding.enchant_name");
        this.apply_chance = config.getDouble("enchantment_configuration.blinding.apply_chance");
        this.apply_chance_lv = config.getDouble("enchantment_configuration.blinding.apply_chance_lv");
        this.duration = config.getInt("enchantment_configuration.blinding.duration");
        this.duration_lv = config.getInt("enchantment_configuration.blinding.duration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.blinding.enabled");
        this.weight = config.getInt("enchantment_configuration.blinding.weight");
        this.book_only = config.getBoolean("enchantment_configuration.blinding.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.blinding.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.blinding.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.blinding.description");

        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_notifications.application_blinding");

        for (String s : config.getStringList("enchantment_configuration.blinding.compatible_with")){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:blinding is not valid, please correct it");
            }
        }
    }
}
