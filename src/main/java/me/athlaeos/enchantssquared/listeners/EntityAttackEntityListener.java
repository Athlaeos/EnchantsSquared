package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.enchantments.attackenchantments.AttackEnchantment;
import me.athlaeos.enchantssquared.enchantments.attackenchantments.CurseBerserk;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.DefendEnchantment;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.Shielding;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityAttackEntityListener implements Listener {
    private CustomEnchantManager manager = null;

    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityAttack(EntityDamageByEntityEvent e){
        if (manager == null) manager = CustomEnchantManager.getInstance();
        if (!e.isCancelled()){
            Shielding s = null;

            Entity damager = e.getDamager();
            Entity defender = e.getEntity();
            if (damager instanceof Projectile){
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Entity){
                    damager = (Entity) projectile.getShooter();
                }
            }
            LivingEntity attacker = null;
            LivingEntity victim = null;
            if (damager instanceof LivingEntity) {
                attacker = (LivingEntity) damager;
            }
            if (defender instanceof LivingEntity) {
                victim = (LivingEntity) defender;
            }


            List<ItemStack> attackerEquipment = new ArrayList<>(Utils.getEntityEquipment(attacker, true));
            List<ItemStack> victimEquipment = new ArrayList<>(Utils.getEntityEquipment(victim, true));

            if (attacker != null){
                if (!attacker.hasPermission("es.noregionrestrictions")){
                    if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-all")) {
                        attackerEquipment.clear();
                    }
                }
            }
            CurseBerserk attackerCurseBerserk = null;
            int attackerCurseBerserkLevel = 0;
            for (ItemStack i : attackerEquipment){
                if (i.getType() == Material.ENCHANTED_BOOK) continue;
                Map<CustomEnchant, Integer> enchants = manager.getItemsEnchantsFromPDC(i);
                for (CustomEnchant en : enchants.keySet()){
                    if (en instanceof CurseBerserk){
                        attackerCurseBerserk = (CurseBerserk) en;
                        attackerCurseBerserkLevel = enchants.get(en);
                    } else if (en instanceof AttackEnchantment){
                        ((AttackEnchantment) en).execute(e, i, enchants.get(en), attacker, victim);
                    }
                }
            }
            if (attacker != null){
                if (attackerCurseBerserk != null) {
                    e.setDamage(e.getDamage() * attackerCurseBerserk.getDamageDealtMultiplier(attacker, attackerCurseBerserkLevel));
                }
            }

            if (victim != null){
                if (!victim.hasPermission("es.noregionrestrictions")){
                    if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-all")) {
                        attackerEquipment.clear();
                    }
                }
            }
            CurseBerserk victimCurseBerserk = null;
            int victimCurseBerserkLevel = 0;
            for (ItemStack i : victimEquipment){
                if (i.getType() == Material.ENCHANTED_BOOK) continue;
                Map<CustomEnchant, Integer> enchants = manager.getItemsEnchantsFromPDC(i);
                for (CustomEnchant en : enchants.keySet()){
                    if (en instanceof CurseBerserk){
                        victimCurseBerserk = (CurseBerserk) en;
                        victimCurseBerserkLevel = enchants.get(en);
                    } else if (en instanceof Shielding){
                        s = (Shielding) en;
                    } else if (en instanceof DefendEnchantment){
                        ((DefendEnchantment) en).execute(e, i, enchants.get(en), attacker, victim);
                    }
                }
            }
            if (victim != null){
                if (victimCurseBerserk != null) {
                    e.setDamage(e.getDamage() * victimCurseBerserk.getDamageTakenMultiplier(victim, victimCurseBerserkLevel));
                }
            }

            if (s != null){
                s.execute(e, null, 0, attacker, victim);
            }
        }
    }
}
