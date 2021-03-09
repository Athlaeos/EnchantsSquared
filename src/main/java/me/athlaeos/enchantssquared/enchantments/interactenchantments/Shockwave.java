package me.athlaeos.enchantssquared.enchantments.interactenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CooldownManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Shockwave extends BlockInteractEnchantment {
    private int cooldown;
    private double force_base;
    private double force_lv;
    private double radius_base;
    private double radius_lv;
    private String cooldown_message;
    private boolean explode;

    public Shockwave(){
        this.enchantType = CustomEnchantType.SHOCKWAVE;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.shockwave";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(PlayerInteractEvent e, ItemStack tool, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-shockwave")){
                return;
            }
        }

        if (this.functionalItems.contains(tool.getType())){
            if (!CooldownManager.getInstance().canPlayerUseItem(e.getPlayer().getUniqueId(), "shockwave_cooldown")) {
                if (!cooldown_message.equals("")){
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(cooldown_message
                            .replace("{cooldown}", String.format("%.2f", CooldownManager.getInstance().getItemCooldown(e.getPlayer().getUniqueId(), "shockwave_cooldown")/1000D)))));
                }
                return;
            }

            if (e.getClickedBlock() == null) return;
            if (e.getBlockFace() != BlockFace.UP) return;

            Location playerLocation = e.getPlayer().getLocation();
            double final_radius = (level <= 1) ? this.radius_base : this.radius_base + ((level - 1) * this.radius_lv);
            Collection<Entity> entitiesInRadius = e.getPlayer().getWorld().getNearbyEntities(playerLocation,
                    final_radius, final_radius, final_radius);
            entitiesInRadius.remove(e.getPlayer());
            double final_force = (level <= 1) ? this.force_base : this.force_base + ((level - 1) * this.force_lv);

            for (Entity entity : entitiesInRadius){
                if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)){
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(e.getPlayer(), entity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, 0);
                    EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()){
                        entity.setVelocity(new Vector(
                                (entity.getLocation().getX() - playerLocation.getX()) * final_force,
                                final_force,
                                (entity.getLocation().getZ() - playerLocation.getZ()) * final_force));
                    }
                }
            }
            if (explode) {
                playerLocation.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, playerLocation, 0);
                playerLocation.getWorld().playSound(playerLocation, Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F);
            }
            if (!e.getPlayer().hasPermission("es.nocooldowns")){
                CooldownManager.getInstance().setItemCooldown(e.getPlayer().getUniqueId(), cooldown * 50, "shockwave_cooldown");
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.shockwave.enchant_name");
        this.cooldown = config.getInt("enchantment_configuration.shockwave.cooldown");
        this.cooldown_message = config.getString("enchantment_configuration.shockwave.cooldown_message");
        this.force_base = config.getDouble("enchantment_configuration.shockwave.force_base");
        this.force_lv = config.getDouble("enchantment_configuration.shockwave.force_lv");
        this.radius_base = config.getDouble("enchantment_configuration.shockwave.radius_base");
        this.radius_lv = config.getDouble("enchantment_configuration.shockwave.radius_lv");
        this.enabled = config.getBoolean("enchantment_configuration.shockwave.enabled");
        this.weight = config.getInt("enchantment_configuration.shockwave.weight");
        this.book_only = config.getBoolean("enchantment_configuration.shockwave.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.shockwave.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.shockwave.max_level");
        this.explode = config.getBoolean("enchantment_configuration.shockwave.explode");
        this.enchantDescription = config.getString("enchantment_configuration.shockwave.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.shockwave.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.shockwave.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.shockwave.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.shockwave.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.shockwave.trade_enabled");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.shockwave.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:shockwave is not valid, please correct it");
            }
        }
    }
}
