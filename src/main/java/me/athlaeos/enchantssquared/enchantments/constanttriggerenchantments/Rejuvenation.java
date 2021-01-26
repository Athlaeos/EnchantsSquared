package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Rejuvenation extends ConstantTriggerEnchantment{
    private double durability_regeneration_base;
    private double durability_regeneration_lv;

    public Rejuvenation(){
        this.enchantType = CustomEnchantEnum.REJUVENATION;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.rejuvenation";
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-rejuvenation")) {
                return;
            }
        }
        if (!(stack.getItemMeta() instanceof Damageable)) {
            return;
        }
        if (compatibleItems.contains(stack.getType())) {
            double durability_regen_chance = (level <= 1) ? durability_regeneration_base : (durability_regeneration_base + ((level - 1) * durability_regeneration_lv));
            if (RandomNumberGenerator.getRandom().nextDouble() < durability_regen_chance) {
                PlayerItemDamageEvent event = new PlayerItemDamageEvent(e.getPlayer(), stack, -1);
                Main.getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()){
                    Damageable toolMeta = (Damageable) stack.getItemMeta();
                    toolMeta.setDamage(toolMeta.getDamage() + event.getDamage());
                    stack.setItemMeta((ItemMeta) toolMeta);
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.rejuvenation.enchant_name");
        this.durability_regeneration_base = config.getDouble("enchantment_configuration.rejuvenation.durability_regeneration_base");
        this.durability_regeneration_lv = config.getDouble("enchantment_configuration.rejuvenation.durability_regeneration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.rejuvenation.enabled");
        this.weight = config.getInt("enchantment_configuration.rejuvenation.weight");
        this.book_only = config.getBoolean("enchantment_configuration.rejuvenation.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.rejuvenation.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.rejuvenation.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.rejuvenation.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.rejuvenation.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:rejuvenation is not valid, please correct it");
            }
        }
    }
}
