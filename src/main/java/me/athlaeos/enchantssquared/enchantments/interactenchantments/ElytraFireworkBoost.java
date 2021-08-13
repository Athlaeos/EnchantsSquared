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
import org.bukkit.Material;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Collections;

public class ElytraFireworkBoost extends ItemInteractEnchantment {
    private double potency_buff_base;
    private double potency_buff_lv;

    public ElytraFireworkBoost(){
        this.enchantType = CustomEnchantType.ELYTRA_FIREWORK_BUFF;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.elytra_firework_buff";
        loadFunctionalItemStrings(Collections.singletonList("ELYTRA"));
        this.compatibleItemStrings = Collections.singletonList("ELYTRA");
        loadConfig();
    }

    @Override
    public void execute(PlayerInteractEvent e, ItemStack item, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-elytra-firework-buff")){
                return;
            }
        }
        if (functionalItems.contains(item.getType())){
            double final_force = (level <= 1) ? this.potency_buff_base : this.potency_buff_base + ((level - 1) * this.potency_buff_lv);
            System.out.println("velocity before: " + e.getPlayer().getVelocity().getZ());

            if (e.getPlayer().isGliding()){
                if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET){
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            e.getPlayer().setVelocity(e.getPlayer().getVelocity().multiply(final_force));
                            System.out.println("velocity after: " + e.getPlayer().getVelocity().getZ());
                        }
                    }.runTaskLater(EnchantsSquared.getPlugin(), 10L);
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.elytra_firework_buff.enchant_name");
        this.potency_buff_base = config.getDouble("enchantment_configuration.elytra_firework_buff.potency_buff_base");
        this.potency_buff_lv = config.getDouble("enchantment_configuration.elytra_firework_buff.potency_buff_lv");
        this.enabled = config.getBoolean("enchantment_configuration.elytra_firework_buff.enabled");
        this.weight = config.getInt("enchantment_configuration.elytra_firework_buff.weight");
        this.book_only = config.getBoolean("enchantment_configuration.elytra_firework_buff.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.elytra_firework_buff.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.elytra_firework_buff.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.elytra_firework_buff.description");
        setIcon(config.getString("enchantment_configuration.elytra_firework_buff.icon"));

        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:elytra_firework_buff is not valid, please correct it");
            }
        }
    }
}
