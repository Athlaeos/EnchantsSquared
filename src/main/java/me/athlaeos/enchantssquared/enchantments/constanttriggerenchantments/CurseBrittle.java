package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class CurseBrittle extends ConstantTriggerEnchantment{
    private double durability_degeneration_base;
    private double durability_degeneration_lv;

    public CurseBrittle(){
        this.enchantType = CustomEnchantType.CURSE_DURABILITY;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.curse_durability";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-curse-brittle")){
                return;
            }
        }
        if (functionalItems.contains(stack.getType())){
            double durability_degen_chance = (level <= 1) ? durability_degeneration_base : (durability_degeneration_base + ((level - 1) * durability_degeneration_lv));
            assert stack.getItemMeta() != null;
            if (stack.getItemMeta().isUnbreakable()) return;
            if (RandomNumberGenerator.getRandom().nextDouble() < durability_degen_chance){
                if (stack.getItemMeta() instanceof Damageable){
                    PlayerItemDamageEvent event = new PlayerItemDamageEvent(e.getPlayer(), stack, 1);
                    EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()){
                        Damageable toolMeta = (Damageable) stack.getItemMeta();
                        toolMeta.setDamage(toolMeta.getDamage() + event.getDamage());
                        stack.setItemMeta((ItemMeta) toolMeta);
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.curse_brittle.enchant_name");
        this.durability_degeneration_base = config.getDouble("enchantment_configuration.curse_brittle.durability_degeneration_base");
        this.durability_degeneration_lv = config.getDouble("enchantment_configuration.curse_brittle.durability_degeneration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.curse_brittle.enabled");
        this.weight = config.getInt("enchantment_configuration.curse_brittle.weight");
        this.book_only = config.getBoolean("enchantment_configuration.curse_brittle.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.curse_brittle.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.curse_brittle.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.curse_brittle.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.curse_brittle.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.curse_brittle.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.curse_brittle.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.curse_brittle.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.curse_brittle.trade_enabled");
        setIcon(config.getString("enchantment_configuration.curse_brittle.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.curse_brittle.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:curse_brittle is not valid, please correct it");
            }
        }
    }
}
