package me.athlaeos.enchantssquared.enchantments.killenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Sapping extends KillEnchantment{
    private int exp_base;
    private int exp_lv;
    private double drop_chance_base;
    private double drop_chance_lv;

    public Sapping(){
        this.enchantType = CustomEnchantType.BONUS_EXP;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.sapping";
        loadFunctionalItemStrings(Arrays.asList("SWORDS", "AXES", "PICKAXES", "HOES", "SHOVELS", "SHEARS", "BOWS", "CROSSBOWS", "TRIDENTS"));
        loadConfig();
    }

    @Override
    public void execute(EntityDeathEvent e, ItemStack stack, int level, LivingEntity killer, LivingEntity killed) {
        if (!killer.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-sapping")){
                return;
            }
        }
        double final_drop_chance = (level <= 1) ? this.drop_chance_base : (this.drop_chance_base + ((level - 1) * drop_chance_lv));

        if (RandomNumberGenerator.getRandom().nextDouble() < final_drop_chance){
            int final_exp_dropped = (level <= 1) ? this.exp_base : (this.exp_base + ((level - 1) * exp_lv));
            e.setDroppedExp(e.getDroppedExp() + final_exp_dropped);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.sapping.enchant_name");
        this.drop_chance_base = config.getDouble("enchantment_configuration.sapping.drop_chance_base");
        this.drop_chance_lv = config.getDouble("enchantment_configuration.sapping.drop_chance_lv");
        this.exp_base = config.getInt("enchantment_configuration.sapping.exp_base");
        this.exp_lv = config.getInt("enchantment_configuration.sapping.exp_lv");
        this.enabled = config.getBoolean("enchantment_configuration.sapping.enabled");
        this.weight = config.getInt("enchantment_configuration.sapping.weight");
        this.book_only = config.getBoolean("enchantment_configuration.sapping.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.sapping.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.sapping.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.sapping.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.sapping.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:sapping is not valid, please correct it");
            }
        }
    }
}
