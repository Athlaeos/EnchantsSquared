package me.athlaeos.enchantssquared.enchantments.attackenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class AOEArrows extends AttackEnchantment{
    private double aoe_damage_base;
    private double aoe_damage_lv;
    private double radius_base;
    private double radius_lv;
    private boolean explosion;

    public AOEArrows(){
        this.enchantType = CustomEnchantType.ARROW_AOE;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.aoe_arrows";
        loadFunctionalItemStrings(Arrays.asList("BOWS", "CROSSBOWS"));
        loadConfig();
    }

    @Override
    public void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim) {
        if (!damager.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-aoe-arrows")){
                return;
            }
        }
        if (!(e.getDamager() instanceof Projectile)) return;
        if (victim == null) return;
        if (functionalItems.contains(i.getType())){
            double finalRadius = (level <= 1) ? this.radius_base : (this.radius_base + ((level - 1) * radius_lv));
            double finalDamage = (level <= 1) ? this.aoe_damage_base : (this.aoe_damage_base + ((level - 1) * aoe_damage_lv));
            double damage = e.getDamage();
            if (explosion){
                damager.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, victim.getLocation(), 0);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F);
            }
            Collection<Entity> surroundingEntities = victim.getWorld().getNearbyEntities(victim.getLocation(), finalRadius, finalRadius, finalRadius);
            surroundingEntities.remove(e.getEntity());
            surroundingEntities.remove(damager);
            for (Entity entity : surroundingEntities){
                if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)){
                    ((LivingEntity) entity).damage(Utils.applyNaturalDamageMitigations(entity, damage * finalDamage, EntityDamageEvent.DamageCause.PROJECTILE), damager);
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.aoe_arrows.enchant_name");
        this.aoe_damage_base = config.getDouble("enchantment_configuration.aoe_arrows.aoe_damage_base");
        this.aoe_damage_lv = config.getDouble("enchantment_configuration.aoe_arrows.aoe_damage_lv");
        this.radius_base = config.getDouble("enchantment_configuration.aoe_arrows.radius_base");
        this.radius_lv = config.getDouble("enchantment_configuration.aoe_arrows.radius_lv");
        this.enabled = config.getBoolean("enchantment_configuration.aoe_arrows.enabled");
        this.weight = config.getInt("enchantment_configuration.aoe_arrows.weight");
        this.book_only = config.getBoolean("enchantment_configuration.aoe_arrows.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.aoe_arrows.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.aoe_arrows.max_level");
        this.explosion = config.getBoolean("enchantment_configuration.aoe_arrows.explosion");
        this.enchantDescription = config.getString("enchantment_configuration.aoe_arrows.description");
        this.compatibleItemStrings = config.getStringList("enchantment_configuration.aoe_arrows.compatible_with");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.aoe_arrows.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.aoe_arrows.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.aoe_arrows.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.aoe_arrows.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.aoe_arrows.trade_enabled");
        setIcon(config.getString("enchantment_configuration.aoe_arrows.icon"));

        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:aoe_arrows is not valid, please correct it");
            }
        }
    }
}
