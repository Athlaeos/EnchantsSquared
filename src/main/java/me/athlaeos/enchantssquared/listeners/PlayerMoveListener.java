package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.ConstantTriggerEnchantment;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.Metabolism;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.Vigorous;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CooldownManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.GameMode;
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

import java.util.*;

public class PlayerMoveListener implements Listener {
    private final CustomEnchantManager enchantManager;
    private final boolean isVigorEnabled;
    private Set<UUID> playersWhoHadFlight = new HashSet<>();

    public PlayerMoveListener(){
        this.enchantManager = CustomEnchantManager.getInstance();
        Vigorous v = new Vigorous();
        isVigorEnabled = v.isEnabled();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (e.isCancelled()) return;
        if (CooldownManager.getInstance().canPlayerUseItem(e.getPlayer().getUniqueId(), "movement-spam-limiter")){
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-all")) return;
            }
            Metabolism m = null;
            Vigorous v = null;

            List<ItemStack> equipment = Utils.getEntityEquipment(e.getPlayer(), true);

            for (ItemStack i : equipment){
                if (i == null) continue;

                Map<CustomEnchant, Integer> enchants = enchantManager.getItemsEnchantsFromPDC(i);
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
            if (v != null) {
                v.execute(e, null, 0);
            } else {
                if (isVigorEnabled){
                    resetPlayerHealth(e);
                }
            }
            checkFlightConditions(e);
            checkBreakConditions(e);

            CooldownManager.getInstance().setItemCooldown(e.getPlayer().getUniqueId(), 500, "movement-spam-limiter");
        }
    }

    /*
    Resets the player's health back to its vanilla value if the player doesn't have the vigorous enchantment
     */
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
        List<ItemStack> equipment = Utils.getEntityEquipment(e.getPlayer(), true);
        for (ItemStack item : equipment){
            if (CustomEnchantManager.getInstance().doesItemHaveEnchant(item, CustomEnchantType.FLIGHT)) {
                playersWhoHadFlight.add(e.getPlayer().getUniqueId());
                return;
            }
        }
        if (playersWhoHadFlight.contains(e.getPlayer().getUniqueId())){
            playersWhoHadFlight.remove(e.getPlayer().getUniqueId());
            e.getPlayer().setFlying(false);
            e.getPlayer().setAllowFlight(false);
        }
    }
}
