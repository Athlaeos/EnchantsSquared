package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CooldownManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Flight extends ConstantTriggerEnchantment{
    private Set<UUID> flyingPlayers = new HashSet<>();

    private double durability_decay;

    private int duration_base;
    private int duration_lv;
    private int slowfall_duration;
    private boolean flight_bar;
    private String flight_bar_display;
    private String colorPresentFuel;
    private String colorAbsentFuel;
    private int regeneration_base;
    private int regeneration_lv;

    public Flight(){
        this.enchantType = CustomEnchantType.FLIGHT;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.flight";
        loadConfig();
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
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
                        if (stack.getItemMeta().isUnbreakable()) return;
                        double break_chance = durability_decay * (1D/(stack.getEnchantmentLevel(Enchantment.DURABILITY) + 1D));
                        double randomDouble = RandomNumberGenerator.getRandom().nextDouble();
                        if (randomDouble < break_chance){
                            Damageable itemMeta = (Damageable) stack.getItemMeta();
                            PlayerItemDamageEvent event = new PlayerItemDamageEvent(e.getPlayer(), stack, 1);
                            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                itemMeta.setDamage(itemMeta.getDamage() + event.getDamage());
                                stack.setItemMeta((ItemMeta) itemMeta);
                            }
                        }
                    }
                }
            }
        }

        if (max_level > 0){
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                CooldownManager cooldownManager = CooldownManager.getInstance();
                int maxFlightDuration = (level == 1) ? duration_base : duration_base + (duration_lv * (level - 1));
                int flightRegeneration = (level == 1) ? regeneration_base : regeneration_base + (regeneration_lv * (level - 1));
                if (!cooldownManager.getStaticTimers("player_in_flight").containsKey(e.getPlayer().getUniqueId())) {
                    cooldownManager.setStaticTimer(e.getPlayer().getUniqueId(), maxFlightDuration, "player_in_flight");
                }
                if (e.getPlayer().isFlying()) {
                    flyingPlayers.add(e.getPlayer().getUniqueId());
                    if (cooldownManager.getStaticTimer(e.getPlayer().getUniqueId(), "player_in_flight") < 0) {
                        cooldownManager.setStaticTimer(e.getPlayer().getUniqueId(), 0, "player_in_flight");
                        e.getPlayer().setFlying(false);
                        e.getPlayer().setAllowFlight(false);
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, slowfall_duration, 0, false, false));
                    } else {
                        cooldownManager.incrementStaticTimer(e.getPlayer().getUniqueId(), -500, "player_in_flight");
                    }
                } else if (e.getPlayer().isOnGround()) {
                    if (cooldownManager.getStaticTimer(e.getPlayer().getUniqueId(), "player_in_flight") > maxFlightDuration) {
                        cooldownManager.setStaticTimer(e.getPlayer().getUniqueId(), maxFlightDuration, "player_in_flight");
                    } else {
                        cooldownManager.incrementStaticTimer(e.getPlayer().getUniqueId(), flightRegeneration, "player_in_flight");
                    }
                }
                if (flight_bar){
                    if (flyingPlayers.contains(e.getPlayer().getUniqueId())){
                        if (!flight_bar_display.equals("")){
                            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(flight_bar_display.replace("%fuel%", fuelBarBuilder(e, level)))));
                        }
                        if (!(cooldownManager.getStaticTimer(e.getPlayer().getUniqueId(), "player_in_flight") < maxFlightDuration)){
                            flyingPlayers.remove(e.getPlayer().getUniqueId());
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
        this.max_level = config.getInt("enchantment_configuration.flight.max_level");
        this.max_level_table = config.getInt("enchantment_configuration.flight.max_level_table");
        this.book_only = config.getBoolean("enchantment_configuration.flight.book_only");
        this.enchantDescription = config.getString("enchantment_configuration.flight.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.flight.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.flight.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.flight.trade_enabled");
        this.duration_base = config.getInt("enchantment_configuration.flight.flight_duration_base");
        this.duration_lv = config.getInt("enchantment_configuration.flight.flight_duration_lv");
        this.regeneration_base = config.getInt("enchantment_configuration.flight.regeneration_base");
        this.regeneration_lv = config.getInt("enchantment_configuration.flight.regeneration_lv");
        this.slowfall_duration = config.getInt("enchantment_configuration.flight.slowfall_duration");
        this.flight_bar = config.getBoolean("enchantment_configuration.flight.flight_bar");
        this.flight_bar_display = config.getString("enchantment_configuration.flight.flight_bar_display");
        this.colorPresentFuel = config.getString("enchantment_configuration.flight.flight_bar_present");
        this.colorAbsentFuel = config.getString("enchantment_configuration.flight.flight_bar_absent");

        setIcon(config.getString("enchantment_configuration.flight.icon"));

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

    private String fuelBarBuilder(PlayerMoveEvent e, int level){
        StringBuilder fuelBar = new StringBuilder();
        int maxFlightDuration = (level == 1) ? duration_base : duration_base + (duration_lv * (level - 1));
        int currentFlightDuration = CooldownManager.getInstance().getStaticTimer(e.getPlayer().getUniqueId(), "player_in_flight");
        double fuelPerBar = (double) maxFlightDuration / 40D;
        for (int i = 0; i < 40; i++){
            if (i * fuelPerBar < currentFlightDuration){
                fuelBar.append(colorPresentFuel).append("|");
            } else {
                fuelBar.append(colorAbsentFuel).append("|");
            }
        }
        return fuelBar.toString();
    }
}
