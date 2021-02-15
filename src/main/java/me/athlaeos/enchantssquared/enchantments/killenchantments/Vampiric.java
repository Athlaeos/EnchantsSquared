package me.athlaeos.enchantssquared.enchantments.killenchantments;

import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Vampiric extends KillEnchantment{
    private double healing_base;
    private double healing_lv;

    public Vampiric(){
        this.enchantType = CustomEnchantType.VAMPIRIC;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.vampiric";
        loadFunctionalItemStrings(Arrays.asList("SWORDS", "AXES", "PICKAXES", "HOES", "SHOVELS", "SHEARS", "BOWS", "CROSSBOWS", "TRIDENTS"));
        loadConfig();
    }

    @Override
    public void execute(EntityDeathEvent e, ItemStack stack, int level, LivingEntity killer, LivingEntity killed) {
        if (!killer.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-vampiric")){
                return;
            }
        }
        double final_amount_healed = (level <= 1) ? this.healing_base : (this.healing_base + ((level - 1) * healing_lv));
        double killer_max_health = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(killer, final_amount_healed, EntityRegainHealthEvent.RegainReason.CUSTOM);
        EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
        if (killer.getHealth() + event.getAmount() > killer_max_health){
            killer.setHealth(killer_max_health);
        } else {
            killer.setHealth(killer.getHealth() + event.getAmount());
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.vampiric.enchant_name");
        this.healing_base = config.getDouble("enchantment_configuration.vampiric.healing_base");
        this.healing_lv = config.getDouble("enchantment_configuration.vampiric.healing_lv");
        this.enabled = config.getBoolean("enchantment_configuration.vampiric.enabled");
        this.weight = config.getInt("enchantment_configuration.vampiric.weight");
        this.book_only = config.getBoolean("enchantment_configuration.vampiric.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.vampiric.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.vampiric.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.vampiric.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.vampiric.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:vampiric is not valid, please correct it");
            }
        }
    }
}
