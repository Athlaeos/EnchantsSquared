package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class Flight extends ConstantTriggerEnchantment{
    private double durability_decay;

    public Flight(){
        this.enchantType = CustomEnchantType.FLIGHT;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.flight";
        loadConfig();
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        this.max_level_table = 0;
        this.max_level = 0;
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-flight")
                    || WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-all")){
                if (!(e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR
                        || e.getPlayer().hasPermission("essentials.fly"))){
                    if (e.getPlayer().isFlying() || e.getPlayer().getAllowFlight()){
                        e.getPlayer().setFlying(false);
                        e.getPlayer().setAllowFlight(false);
                    }
                }
                return;
            }
        }
        if (stack.getType() != Material.ENCHANTED_BOOK){
            e.getPlayer().setAllowFlight(true);
            if (e.getPlayer().isFlying()){
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getGameMode() != GameMode.SPECTATOR){
                    if (stack.getItemMeta() instanceof Damageable){
                        double break_chance = durability_decay * (1D/(stack.getEnchantmentLevel(Enchantment.DURABILITY) + 1D));
                        double randomDouble = RandomNumberGenerator.getRandom().nextDouble();
                        if (randomDouble < break_chance){
                            Damageable itemMeta = (Damageable) stack.getItemMeta();
                            itemMeta.setDamage(itemMeta.getDamage() + 1);
                            stack.setItemMeta((ItemMeta) itemMeta);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.flight.enchant_name");
        this.durability_decay = config.getDouble("enchantment_configuration.flight.durability_decay");
        this.enabled = config.getBoolean("enchantment_configuration.flight.enabled");
        this.weight = config.getInt("enchantment_configuration.flight.weight");
        this.book_only = config.getBoolean("enchantment_configuration.flight.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.flight.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.flight.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.flight.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.flight.trade_enabled");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.flight.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:flight is not valid, please correct it");
            }
        }
    }
}
