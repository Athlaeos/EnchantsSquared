package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LavaWalker extends ConstantTriggerEnchantment{
    private double durability_degeneration;
    private Material transform_into;

    public LavaWalker(){
        this.enchantType = CustomEnchantEnum.LAVA_WALKER;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.lavawalker";
        loadConfig();
    }

    @Override
    public void execute(PlayerMoveEvent e, ItemStack stack, int level) {
        if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-lava-walker")) {
                return;
            }
        }
        if (compatibleItems.contains(stack.getType())) {
            List<Block> nearbyBlocks = Utils.getNearbyBlocks2D(e.getPlayer().getLocation().subtract(0, 1, 0), level - 1, Material.LAVA);
            for (Block b : nearbyBlocks){
                BlockBreakEvent event = new BlockBreakEvent(b, e.getPlayer());
                Main.getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()){
                    b.setType(transform_into);

                    if (RandomNumberGenerator.getRandom().nextDouble() < durability_degeneration){
                        if (stack.getItemMeta() instanceof Damageable){
                            PlayerItemDamageEvent breakEvent = new PlayerItemDamageEvent(e.getPlayer(), stack, 1);
                            Main.getPlugin().getServer().getPluginManager().callEvent(breakEvent);
                            if (!breakEvent.isCancelled()){
                                Damageable toolMeta = (Damageable) stack.getItemMeta();
                                toolMeta.setDamage(toolMeta.getDamage() + breakEvent.getDamage());
                                stack.setItemMeta((ItemMeta) toolMeta);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.lava_walker.enchant_name");
        this.durability_degeneration = config.getDouble("enchantment_configuration.lava_walker.durability_degeneration");
        this.enabled = config.getBoolean("enchantment_configuration.lava_walker.enabled");
        this.weight = config.getInt("enchantment_configuration.lava_walker.weight");
        this.book_only = config.getBoolean("enchantment_configuration.lava_walker.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.lava_walker.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.lava_walker.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.lava_walker.description");
        try{
            this.transform_into = Material.valueOf(config.getString("enchantment_configuration.lava_walker.transform_into"));
        } catch (IllegalArgumentException e){
            System.out.println("Material for transform_into for the lava walker enchant is not valid, please correct it");
        }

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.lava_walker.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:lava_walker is not valid, please correct it");
            }
        }

        if (transform_into == Material.LAVA) transform_into = Material.OBSIDIAN;
    }
}
