package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
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

import java.util.Arrays;

public class Stunning extends AttackEnchantment{
    private int duration;
    private int duration_lv;
    private double apply_chance;
    private double apply_chance_lv;
    private boolean buffed_axe_potency;

    private String message;

    public Stunning(){
        this.enchantType = CustomEnchantType.STUNNING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.stunning";
        loadFunctionalItemStrings(Arrays.asList("SWORDS", "AXES", "BOWS", "CROSSBOWS", "PICKAXES", "HOES", "SHOVELS", "TRIDENTS", "SHEARS"));
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (!damager.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-stunning")){
                return;
            }
        }
        if (victim == null) return;

        double final_apply_chance = (level <= 1) ? this.apply_chance : this.apply_chance + ((level - 1) * this.apply_chance_lv);
        if (buffed_axe_potency) {
            if (ItemMaterialManager.getInstance().getAxes().contains(i.getType())){
                final_apply_chance *= 2;
            }
        }
        if (RandomNumberGenerator.getRandom().nextDouble() <= final_apply_chance){
            int final_duration = (level <= 1) ? this.duration : this.duration + ((level - 1) * this.duration_lv);

            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, final_duration, 9, true, false), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, final_duration, 9, true, false), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, final_duration, 9, true, false), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, final_duration, 9, true, false), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, final_duration, 128, true, false), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, final_duration, 9, true, false), true);

            if (damager instanceof Player){
                if (!message.equals("")){
                    ((Player) damager).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(message)));
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.stunning.enchant_name");
        this.apply_chance = config.getDouble("enchantment_configuration.stunning.apply_chance");
        this.apply_chance_lv = config.getDouble("enchantment_configuration.stunning.apply_chance_lv");
        this.duration = config.getInt("enchantment_configuration.stunning.duration");
        this.duration_lv = config.getInt("enchantment_configuration.stunning.duration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.stunning.enabled");
        this.weight = config.getInt("enchantment_configuration.stunning.weight");
        this.book_only = config.getBoolean("enchantment_configuration.stunning.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.stunning.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.stunning.max_level");
        this.buffed_axe_potency = config.getBoolean("enchantment_configuration.stunning.buffed_axe_potency");
        this.enchantDescription = config.getString("enchantment_configuration.stunning.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.stunning.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.stunning.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.stunning.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.stunning.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.stunning.trade_enabled");

        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("enchant_notifications.application_stunning");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.stunning.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:stunning is not valid, please correct it");
            }
        }
    }
}
