package me.athlaeos.enchantssquared.enchantments.fishenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.BlockInteractEnchantment;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CooldownManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Collections;

public class Grappling extends FishingEnchantment {
    private int cooldown;
    private double force_base;
    private double force_lv;
    private boolean require_hooking;
    private String cooldown_message;

    public Grappling(){
        this.enchantType = CustomEnchantType.GRAPPLING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.grappling";
        loadFunctionalItemStrings(Collections.singletonList("FISHINGROD"));
        loadConfig();
    }

    @Override
    public void execute(PlayerFishEvent e, ItemStack tool, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-grappling")){
                return;
            }
        }

        if (this.functionalItems.contains(tool.getType())){
            if (require_hooking){
                if (e.getState() != PlayerFishEvent.State.IN_GROUND) return;
            } else {
                if (e.getState() != PlayerFishEvent.State.REEL_IN) return;
            }
            if (e.getHook().getWorld().getBlockAt(e.getHook().getLocation()).getType() == Material.WATER) return;
            if (!CooldownManager.getInstance().canPlayerUseItem(e.getPlayer().getUniqueId(), "grappling_cooldown")) {
                if (!cooldown_message.equals("")){
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(cooldown_message
                            .replace("%cooldown%", String.format("%.2f", CooldownManager.getInstance().getItemCooldown(e.getPlayer().getUniqueId(), "grappling_cooldown")/1000D)))));
                }
                return;
            }
            double final_force = (level <= 1) ? this.force_base : this.force_base + ((level - 1) * this.force_lv);

            e.getPlayer().setVelocity(e.getPlayer().getVelocity().add(new Vector(
                    e.getHook().getLocation().getX() - e.getPlayer().getLocation().getX(),
                    e.getHook().getLocation().getY() - e.getPlayer().getLocation().getY(),
                    e.getHook().getLocation().getZ() - e.getPlayer().getLocation().getZ())
                    .multiply(final_force)));

            int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
            if (RandomNumberGenerator.getRandom().nextDouble() < (1D / unbreakingLevel + 1)){
                Utils.damageItem(e.getPlayer(), tool, 1);
            }

            if (!e.getPlayer().hasPermission("es.nocooldowns")){
                CooldownManager.getInstance().setItemCooldown(e.getPlayer().getUniqueId(), cooldown * 50, "grappling_cooldown");
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.grappling.enchant_name");
        this.cooldown = config.getInt("enchantment_configuration.grappling.cooldown");
        this.cooldown_message = config.getString("enchantment_configuration.grappling.cooldown_message");
        this.force_base = config.getDouble("enchantment_configuration.grappling.force_base");
        this.force_lv = config.getDouble("enchantment_configuration.grappling.force_lv");
        this.enabled = config.getBoolean("enchantment_configuration.grappling.enabled");
        this.weight = config.getInt("enchantment_configuration.grappling.weight");
        this.book_only = config.getBoolean("enchantment_configuration.grappling.book_only");
        this.require_hooking = config.getBoolean("enchantment_configuration.grappling.require_hooking");
        this.max_level_table = config.getInt("enchantment_configuration.grappling.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.grappling.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.grappling.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.grappling.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.grappling.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.grappling.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.grappling.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.grappling.trade_enabled");
        setIcon(config.getString("enchantment_configuration.grappling.icon"));

        this.compatibleItemStrings = Collections.singletonList("FISHINGROD");
        this.compatibleItems.add(Material.FISHING_ROD);
    }
}
