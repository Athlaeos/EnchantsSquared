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

public class Poisoning extends AttackEnchantment{
    private int amplifier;
    private int amplifier_lv;
    private int duration;
    private int duration_lv;
    private double apply_chance;
    private double apply_chance_lv;

    private String message;

    public Poisoning(){
        this.enchantType = CustomEnchantEnum.POISONING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.poisoning";
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (!damager.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-poisoning")){
                return;
            }
        }
        if (victim == null) return;

        if (compatibleItems.contains(i.getType())){
            double final_apply_chance = (level <= 1) ? this.apply_chance : this.apply_chance + ((level - 1) * this.apply_chance_lv);
            if (RandomNumberGenerator.getRandom().nextDouble() <= final_apply_chance){
                int final_amplifier = (level <= 1) ? this.amplifier : this.amplifier + ((level - 1) * this.amplifier_lv);
                int final_duration = (level <= 1) ? this.duration : this.duration + ((level - 1) * this.duration_lv);
                if (victim.hasPotionEffect(PotionEffectType.POISON)){
                    if (victim.getPotionEffect(PotionEffectType.POISON).getAmplifier() <= final_amplifier){
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, final_duration, final_amplifier, false, true), true);
                    }
                } else {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, final_duration, final_amplifier, false, true), true);
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
        this.enchantLore = config.getString("enchantment_configuration.poisoning.enchant_name");
        this.apply_chance = config.getDouble("enchantment_configuration.poisoning.apply_chance");
        this.apply_chance_lv = config.getDouble("enchantment_configuration.poisoning.apply_chance_lv");
        this.amplifier = config.getInt("enchantment_configuration.poisoning.amplifier");
        this.amplifier_lv = config.getInt("enchantment_configuration.poisoning.amplifier_lv");
        this.duration = config.getInt("enchantment_configuration.poisoning.duration");
        this.duration_lv = config.getInt("enchantment_configuration.poisoning.duration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.poisoning.enabled");
        this.weight = config.getInt("enchantment_configuration.poisoning.weight");
        this.book_only = config.getBoolean("enchantment_configuration.poisoning.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.poisoning.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.poisoning.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.poisoning.description");

        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_notifications.application_poisoning");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.poisoning.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:poisoning is not valid, please correct it");
            }
        }
    }
}
