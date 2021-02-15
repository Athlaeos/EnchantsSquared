package me.athlaeos.enchantssquared.enchantments.interactenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
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
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class PlaceTorch extends InteractEnchantment{
    private int durability_cost;
    private String cooldown_message;
    private int cooldown;
    private boolean use_unbreaking;

    public PlaceTorch(){
        this.enchantType = CustomEnchantType.ILLUMINATED;
        this.max_level_table = 0;
        this.max_level = 0;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.illuminated";
        loadFunctionalItemStrings(Collections.singletonList("PICKAXES"));
        this.compatibleItemStrings = Collections.singletonList("PICKAXES");
        loadConfig();
    }

    @Override
    public void execute(PlayerInteractEvent e, ItemStack tool, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-torches")){
                return;
            }
        }
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().isPassable()) return;
        if (!e.getPlayer().isSneaking()) return;
        if (!CooldownManager.getInstance().canPlayerUseItem(e.getPlayer().getUniqueId(), "illuminated_cooldown")) {
            if (!cooldown_message.equals("")){
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(cooldown_message
                        .replace("{cooldown}", String.format("%.2f", CooldownManager.getInstance().getItemCooldown(e.getPlayer().getUniqueId(), "illuminated_cooldown")/1000D)))));
            }
            return;
        }

        Location clickedBlock = e.getClickedBlock().getLocation();
        switch(e.getBlockFace()){
            case NORTH: clickedBlock.setZ(clickedBlock.getZ() - 1);
                break;
            case SOUTH: clickedBlock.setZ(clickedBlock.getZ() + 1);
                break;
            case UP: clickedBlock.setY(clickedBlock.getY() + 1);
                break;
            case DOWN: clickedBlock.setY(clickedBlock.getY() - 1);
                break;
            case EAST: clickedBlock.setX(clickedBlock.getX() + 1);
                break;
            case WEST: clickedBlock.setX(clickedBlock.getX() - 1);
                break;
        }
        if (!Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR).contains(clickedBlock.getWorld().getBlockAt(clickedBlock).getType())){
            return;
        }

        if (clickedBlock.getWorld().getBlockAt(clickedBlock).getType().isInteractable()) return;
        boolean placedTorch = false;
        switch (e.getBlockFace()){
            case EAST:
            case NORTH:
            case WEST:
            case SOUTH:
                Block torchBlock = clickedBlock.getWorld().getBlockAt(clickedBlock);
                Material originalBlockType = torchBlock.getType();
                BlockData data = torchBlock.getBlockData().clone();
                torchBlock.setType(Material.WALL_TORCH);
                BlockData torchDirection = torchBlock.getBlockData();
                Directional directional;
                if (torchDirection instanceof Directional) {
                    directional = (Directional) torchDirection;
                } else {
                    return;
                }
                directional.setFacing(e.getBlockFace());
                torchBlock.setBlockData(directional);
                BlockPlaceEvent event = new BlockPlaceEvent(torchBlock, torchBlock.getState(), e.getClickedBlock(), tool, e.getPlayer(), true, EquipmentSlot.HAND);
                EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()){
                    placedTorch = true;
                } else {
                    torchBlock.setBlockData(data);
                    torchBlock.setType(originalBlockType);
                }
                break;
            case UP:
            case DOWN:
                Location floorBlock = clickedBlock.clone();
                floorBlock.setY(floorBlock.getY() - 1);
                Block torchBlock1 = clickedBlock.getWorld().getBlockAt(clickedBlock);
                Material m = torchBlock1.getType();
                if (floorBlock.getWorld().getBlockAt(floorBlock).isPassable()){
                    return;
                } else {
                    clickedBlock.getWorld().getBlockAt(clickedBlock).setType(Material.TORCH);
                    BlockPlaceEvent event1 = new BlockPlaceEvent(torchBlock1, torchBlock1.getState(), e.getClickedBlock(), tool, e.getPlayer(), true, EquipmentSlot.HAND);
                    EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event1);
                    if (!event1.isCancelled()){
                        placedTorch = true;
                    } else {
                        torchBlock1.setType(m);
                    }
                }
                break;
        }

        if (placedTorch){
            if (!e.getPlayer().hasPermission("es.nocooldowns")){
                CooldownManager.getInstance().setItemCooldown(e.getPlayer().getUniqueId(), cooldown * 50, "illuminated_cooldown");
            }
            if (tool.getItemMeta() instanceof Damageable){
                int unBreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
                double breakChance = 1D/(unBreakingLevel + 1D);
                if (use_unbreaking){
                    if (RandomNumberGenerator.getRandom().nextDouble() < breakChance){
                        Damageable toolMeta = (Damageable) tool.getItemMeta();
                        toolMeta.setDamage(toolMeta.getDamage() + durability_cost);
                        tool.setItemMeta((ItemMeta) toolMeta);
                    }
                } else {
                    Damageable toolMeta = (Damageable) tool.getItemMeta();
                    toolMeta.setDamage(toolMeta.getDamage() + durability_cost);
                    tool.setItemMeta((ItemMeta) toolMeta);
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.illuminated.enchant_name");
        this.durability_cost = config.getInt("enchantment_configuration.illuminated.durability_cost");
        this.enabled = config.getBoolean("enchantment_configuration.illuminated.enabled");
        this.weight = config.getInt("enchantment_configuration.illuminated.weight");
        this.book_only = config.getBoolean("enchantment_configuration.illuminated.book_only");
        this.use_unbreaking = config.getBoolean("enchantment_configuration.illuminated.use_unbreaking");
        this.cooldown = config.getInt("enchantment_configuration.illuminated.cooldown");
        this.cooldown_message = config.getString("enchantment_configuration.illuminated.cooldown_message");
        this.enchantDescription = config.getString("enchantment_configuration.illuminated.description");

        compatibleItems.addAll(ItemMaterialManager.getInstance().getPickaxes());
    }
}
