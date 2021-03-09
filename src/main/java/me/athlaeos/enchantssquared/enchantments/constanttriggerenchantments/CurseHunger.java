package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class CurseHunger extends ConstantTriggerEnchantment{
    private double hunger_degeneration_lv;
    private CustomEnchantManager manager;

    public CurseHunger(){
        this.enchantType = CustomEnchantType.CURSE_HUNGER;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.curse_hunger";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (manager == null) manager = CustomEnchantManager.getInstance();
        if (e.getPlayer().getFoodLevel() == 0) return;
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-curse-hunger")){
                return;
            }
        }
        int collectiveLevel = 0;
        for (ItemStack item : Utils.getEntityEquipment(e.getPlayer(), true)){
            if (this.functionalItems.contains(item.getType())) {
                collectiveLevel += manager.getEnchantStrength(item, CustomEnchantType.CURSE_HUNGER);
            }
        }
        double hungerDegenerationChance = collectiveLevel * hunger_degeneration_lv;
        if (RandomNumberGenerator.getRandom().nextDouble() < hungerDegenerationChance){
            FoodLevelChangeEvent event = new FoodLevelChangeEvent(e.getPlayer(), -1);
            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()){
                e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + event.getFoodLevel());
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.curse_hunger.enchant_name");
        this.hunger_degeneration_lv = config.getDouble("enchantment_configuration.curse_hunger.hunger_degeneration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.curse_hunger.enabled");
        this.weight = config.getInt("enchantment_configuration.curse_hunger.weight");
        this.book_only = config.getBoolean("enchantment_configuration.curse_hunger.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.curse_hunger.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.curse_hunger.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.curse_hunger.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.curse_hunger.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.curse_hunger.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.curse_hunger.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.curse_hunger.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.curse_hunger.trade_enabled");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.curse_hunger.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:curse_hunger is not valid, please correct it");
            }
        }
    }
}
