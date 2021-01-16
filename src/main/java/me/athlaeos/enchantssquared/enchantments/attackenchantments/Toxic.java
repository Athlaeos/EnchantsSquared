package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.enchantmanagers.ToxicHealingReductionManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Toxic extends AttackEnchantment{
    private int duration_base;
    private int duration_lv;

    public Toxic(){
        this.enchantType = CustomEnchantEnum.TOXIC;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.toxic";
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (!damager.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-toxic")){
                return;
            }
        }
        if (compatibleItems.contains(i.getType())){
            int finalDuration = 50 * ((level <= 1) ? this.duration_base : (this.duration_base + ((level - 1) * duration_lv)));

            ToxicHealingReductionManager.getInstance().afflictEntity(victim.getUniqueId(), level, finalDuration);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.toxic.enchant_name");
        this.duration_base = config.getInt("enchantment_configuration.toxic.duration_base");
        this.duration_lv = config.getInt("enchantment_configuration.toxic.duration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.toxic.enabled");
        this.weight = config.getInt("enchantment_configuration.toxic.weight");
        this.book_only = config.getBoolean("enchantment_configuration.toxic.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.toxic.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.toxic.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.toxic.description");

        for (String s : config.getStringList("enchantment_configuration.toxic.compatible_with")){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:toxic is not valid, please correct it");
            }
        }
    }
}
