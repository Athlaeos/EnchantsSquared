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

public class Metabolism extends ConstantTriggerEnchantment{
    private double hunger_regeneration_lv;
    private CustomEnchantManager manager;
    private int saturation_limit;

    public Metabolism(){
        this.enchantType = CustomEnchantType.METABOLISM;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.metabolism";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (manager == null) manager = CustomEnchantManager.getInstance();
        if (e.getPlayer().getFoodLevel() >= 20 && e.getPlayer().getSaturation() >= saturation_limit) return;
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-metabolism")){
                return;
            }
        }
        int collectiveLevel = 0;
        for (ItemStack item : Utils.getEntityEquipment(e.getPlayer(), true)) {
            if (this.functionalItems.contains(item.getType())) {
                collectiveLevel += manager.getEnchantStrength(item, CustomEnchantType.METABOLISM);
            }
        }
        double hungerRegenerationChance = collectiveLevel * hunger_regeneration_lv;

        if (RandomNumberGenerator.getRandom().nextDouble() < hungerRegenerationChance){
            FoodLevelChangeEvent event = new FoodLevelChangeEvent(e.getPlayer(), 1);
            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()){
                if (e.getPlayer().getFoodLevel() == 20){
                    if (e.getPlayer().getSaturation() < saturation_limit && e.getPlayer().getSaturation() <= 19F){
                        e.getPlayer().setSaturation(e.getPlayer().getSaturation() + Math.max(1, event.getFoodLevel()));
                    }
                } else {
                    e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + Math.max(1, event.getFoodLevel()));
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.metabolism.enchant_name");
        this.hunger_regeneration_lv = config.getDouble("enchantment_configuration.metabolism.hunger_regeneration_lv");
        this.enabled = config.getBoolean("enchantment_configuration.metabolism.enabled");
        this.weight = config.getInt("enchantment_configuration.metabolism.weight");
        this.book_only = config.getBoolean("enchantment_configuration.metabolism.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.metabolism.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.metabolism.max_level");
        this.saturation_limit = config.getInt("enchantment_configuration.metabolism.saturation_limit");
        this.enchantDescription = config.getString("enchantment_configuration.metabolism.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.metabolism.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.metabolism.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.metabolism.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.metabolism.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.metabolism.trade_enabled");
        setIcon(config.getString("enchantment_configuration.metabolism.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.metabolism.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:metabolism is not valid, please correct it");
            }
        }
    }
}
