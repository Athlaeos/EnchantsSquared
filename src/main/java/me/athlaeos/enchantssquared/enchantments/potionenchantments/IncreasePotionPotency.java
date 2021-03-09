package me.athlaeos.enchantssquared.enchantments.potionenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class IncreasePotionPotency extends PotionEffectEnchantment{
    private int amplifier_buff_base;
    private int amplifier_buff_lv;
    private double duration_buff_base;
    private double duration_buff_lv;
    private boolean duration_buff_decimals = false;

    public IncreasePotionPotency(){
        this.enchantType = CustomEnchantType.POTION_POTENCY_BUFF;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.potion_potency_buff";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(EntityPotionEffectEvent e, ItemStack i, int level) {
        if (!e.getEntity().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-potion-potency-buff")){
                return;
            }
        }
        if (e.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK){
            if (e.getEntity() instanceof LivingEntity){
                if (e.getModifiedType() == PotionEffectType.HEAL || e.getModifiedType() == PotionEffectType.HARM) return;
                int amplifier = e.getNewEffect().getAmplifier();
                int duration = e.getNewEffect().getDuration();
                amplifier += ((level <= 1) ? this.amplifier_buff_base : (this.amplifier_buff_base + ((level - 1) * amplifier_buff_lv)));
                if (duration_buff_decimals){
                    duration *= ((level <= 1) ? this.duration_buff_base : (this.duration_buff_base + ((level - 1) * duration_buff_lv)));
                } else {
                    duration += (int) ((level <= 1) ? this.duration_buff_base : (this.duration_buff_base + ((level - 1) * duration_buff_lv)));
                }
                if (duration < 0) duration = 0;
                e.setCancelled(true);
                ((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(e.getModifiedType(), duration, amplifier), true);
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.potion_potency_buff.enchant_name");
        this.amplifier_buff_base = config.getInt("enchantment_configuration.potion_potency_buff.amplifier_buff_base");
        this.amplifier_buff_lv = config.getInt("enchantment_configuration.potion_potency_buff.amplifier_buff_lv");
        this.duration_buff_base = config.getDouble("enchantment_configuration.potion_potency_buff.duration_buff_base");
        this.duration_buff_lv = config.getDouble("enchantment_configuration.potion_potency_buff.duration_buff_lv");
        this.enabled = config.getBoolean("enchantment_configuration.potion_potency_buff.enabled");
        this.weight = config.getInt("enchantment_configuration.potion_potency_buff.weight");
        this.book_only = config.getBoolean("enchantment_configuration.potion_potency_buff.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.potion_potency_buff.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.potion_potency_buff.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.potion_potency_buff.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.potion_potency_buff.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.potion_potency_buff.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.potion_potency_buff.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.potion_potency_buff.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.potion_potency_buff.trade_enabled");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.potion_potency_buff.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:potion_potency_buff is not valid, please correct it");
            }
        }

        if (duration_buff_base % 1 != 0 && duration_buff_lv % 1 != 0){
            duration_buff_decimals = true;
        } else if (duration_buff_base % 1 == 0 && duration_buff_lv % 1 == 0){
            duration_buff_decimals = false;
        } else {
            duration_buff_decimals = true;
            duration_buff_base = 0.8;
            duration_buff_lv = -0.2;
            System.out.println("A combination of decimal numbers and whole numbers was used in configuring config:potion_potency_buff duration_buff_base and duration_buff_lv, this does not work and the values have been set to default.");
        }
    }
}
