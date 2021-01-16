package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantClassification;
import me.athlaeos.enchantssquared.dom.Version;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.ConstantTriggerEnchantment;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.Flight;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.Metabolism;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.Vigorous;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.BreakBlockEnchantment;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.MinecraftVersionManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerMoveListener implements Listener {
    private final CustomEnchantManager enchantManager;

    public PlayerMoveListener(){
        this.enchantManager = CustomEnchantManager.getInstance();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (!e.isCancelled()){
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-all")) return;
            }
            Metabolism m = null;
            Vigorous v = null;

            List<ItemStack> equipment = new ArrayList<>();
            if (e.getPlayer().getEquipment() == null) return;
            if (e.getPlayer().getEquipment().getHelmet() != null){
                equipment.add(e.getPlayer().getEquipment().getHelmet());
            }
            if (e.getPlayer().getEquipment().getChestplate() != null){
                equipment.add(e.getPlayer().getEquipment().getChestplate());
            }
            if (e.getPlayer().getEquipment().getLeggings() != null){
                equipment.add(e.getPlayer().getEquipment().getLeggings());
            }
            if (e.getPlayer().getEquipment().getBoots() != null){
                equipment.add(e.getPlayer().getEquipment().getBoots());
            }
            if (e.getPlayer().getEquipment().getItemInMainHand().getType() != Material.AIR){
                equipment.add(e.getPlayer().getEquipment().getItemInMainHand());
            }
            if (e.getPlayer().getEquipment().getItemInOffHand().getType() != Material.AIR){
                equipment.add(e.getPlayer().getEquipment().getItemInOffHand());
            }

            for (ItemStack i : equipment){
                if (i == null) continue;
                if (!(i.getItemMeta() instanceof Damageable)){
                    continue;
                }

                Map<CustomEnchant, Integer> enchants = enchantManager.getItemsEnchants(i, CustomEnchantClassification.CONSTANT_TRIGGER);
                for (CustomEnchant enchant : enchants.keySet()){
                    if (enchant instanceof Metabolism){
                        m = (Metabolism) enchant;
                    } else if (enchant instanceof Vigorous) {
                        v = (Vigorous) enchant;
                    } else if (enchant instanceof ConstantTriggerEnchantment){
                        ((ConstantTriggerEnchantment) enchant).execute(e, i, enchants.get(enchant));
                    }
                }
            }
            if (m != null) m.execute(e, null, 0);
            if (v != null) { v.execute(e, null, 0); } else { resetPlayerHealth(e); }
            checkFlightConditions(e);
            checkBreakConditions(e);
        }
    }

    private void resetPlayerHealth(PlayerMoveEvent e){
        int healthBoostLevel = 0;
        PotionEffect healthBoostBuff = e.getPlayer().getPotionEffect(PotionEffectType.HEALTH_BOOST);
        if (healthBoostBuff != null) healthBoostLevel += healthBoostBuff.getAmplifier() + 1;

        if (e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() != (20 + (healthBoostLevel * 4))){
            e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + (healthBoostLevel * 4));
        }
    }

    private void checkBreakConditions(PlayerMoveEvent e){
        Player p = e.getPlayer();
        ItemStack playerHelmet = e.getPlayer().getInventory().getHelmet();
        if (playerHelmet != null){
            if (ItemMaterialManager.getInstance().getHelmets().contains(playerHelmet.getType())){
                if (playerHelmet.getItemMeta() instanceof Damageable){
                    if (((Damageable)playerHelmet.getItemMeta()).getDamage() >= playerHelmet.getType().getMaxDurability()){
                        p.getInventory().setHelmet(null);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    }
                }
            }
        }
        ItemStack playerChestPlate = e.getPlayer().getInventory().getChestplate();
        if (playerChestPlate != null){
            if (ItemMaterialManager.getInstance().getChestPlates().contains(playerChestPlate.getType())){
                if (playerChestPlate.getItemMeta() instanceof Damageable) {
                    if (((Damageable) playerChestPlate.getItemMeta()).getDamage() >= playerChestPlate.getType().getMaxDurability()) {
                        p.getInventory().setChestplate(null);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    }
                }
            }
        }
        ItemStack playerLeggings = e.getPlayer().getInventory().getLeggings();
        if (playerLeggings != null) {
            if (ItemMaterialManager.getInstance().getLeggings().contains(playerLeggings.getType())) {
                if (playerLeggings.getItemMeta() instanceof Damageable) {
                    if (((Damageable) playerLeggings.getItemMeta()).getDamage() >= playerLeggings.getType().getMaxDurability()) {
                        p.getInventory().setLeggings(null);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    }
                }
            }
        }
        ItemStack playerBoots = e.getPlayer().getInventory().getBoots();
        if (playerBoots != null) {
            if (ItemMaterialManager.getInstance().getBoots().contains(playerBoots.getType())) {
                if (playerBoots.getItemMeta() instanceof Damageable) {
                    if (((Damageable) playerBoots.getItemMeta()).getDamage() >= playerBoots.getType().getMaxDurability()) {
                        p.getInventory().setBoots(null);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    }
                }
            }
        }
        ItemStack playerMainHand = e.getPlayer().getInventory().getItemInMainHand();
        if (ItemMaterialManager.getInstance().getAll().contains(playerMainHand.getType())){
            if (playerMainHand.getItemMeta() instanceof Damageable){
                if (((Damageable)playerMainHand.getItemMeta()).getDamage() >= playerMainHand.getType().getMaxDurability()){
                    p.getInventory().setItemInMainHand(null);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                }
            }
        }
        ItemStack playerOffHand = e.getPlayer().getInventory().getItemInOffHand();
        if (ItemMaterialManager.getInstance().getAll().contains(playerOffHand.getType())) {
            if (playerOffHand.getItemMeta() instanceof Damageable) {
                if (((Damageable) playerOffHand.getItemMeta()).getDamage() >= playerOffHand.getType().getMaxDurability()) {
                    p.getInventory().setItemInOffHand(null);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                }
            }
        }
    }

    private void checkFlightConditions(PlayerMoveEvent e){
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR
        || e.getPlayer().hasPermission("essentials.fly")){
            return;
        }
        for (CustomEnchant enchant : CustomEnchantManager.getInstance().getItemsEnchants(e.getPlayer().getInventory().getBoots(), CustomEnchantClassification.CONSTANT_TRIGGER).keySet()){
            if (enchant instanceof Flight){
                return;
            }
        }
        e.getPlayer().setFlying(false);
        e.getPlayer().setAllowFlight(false);
    }
}
