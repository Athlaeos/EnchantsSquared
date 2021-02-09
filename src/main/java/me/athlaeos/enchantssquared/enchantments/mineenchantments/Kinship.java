package me.athlaeos.enchantssquared.enchantments.mineenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Kinship extends BreakBlockEnchantment{
    private int durability_netherite;
    private int durability_diamond;
    private int durability_iron;
    private int durability_stone;
    private Map<Material, Material> pickaxeBreakables = new HashMap<>();

    public Kinship(){
        this.enchantType = CustomEnchantType.KINSHIP;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.kinship";
        loadFunctionalItemStrings(Collections.singletonList("PICKAXES"));
        this.compatibleItemStrings = Collections.singletonList("PICKAXES");
        this.functionalItems.remove(Material.GOLDEN_PICKAXE);
        loadConfig();
        try {
            pickaxeBreakables.put(Material.valueOf("NETHERITE_PICKAXE"), Material.valueOf("NETHERITE_ORE"));
        } catch (IllegalArgumentException ignored){
        }
        pickaxeBreakables.put(Material.DIAMOND_PICKAXE, Material.DIAMOND_ORE);
        pickaxeBreakables.put(Material.IRON_PICKAXE, Material.IRON_ORE);
        pickaxeBreakables.put(Material.STONE_PICKAXE, Material.STONE);
    }

    @Override
    public void execute(BlockBreakEvent e, ItemStack item, int level) {
        if (this.functionalItems.contains(item.getType())){
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getPlayer().getLocation(), "es-deny-kinship")){
                    return;
                }
            }

            if (e.getBlock().getType() == pickaxeBreakables.get(item.getType())){
                int durabilityToRepair = 0;
                switch (item.getType().toString()){
                    case "NETHERITE_PICKAXE": durabilityToRepair = durability_netherite * level;
                    break;
                    case "DIAMOND_PICKAXE": durabilityToRepair = durability_diamond * level;
                        break;
                    case "IRON_PICKAXE": durabilityToRepair = durability_iron * level;
                        break;
                    case "STONE_PICKAXE": durabilityToRepair = durability_stone * level;
                        break;
                }

                if (item.getItemMeta() instanceof Damageable){
                    Damageable toolMeta = (Damageable) item.getItemMeta();
                    toolMeta.setDamage(toolMeta.getDamage() - durabilityToRepair);
                    item.setItemMeta((ItemMeta) toolMeta);
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.kinship.enchant_name");
        this.enabled = config.getBoolean("enchantment_configuration.kinship.enabled");
        this.weight = config.getInt("enchantment_configuration.kinship.weight");
        this.book_only = config.getBoolean("enchantment_configuration.kinship.book_only");
        this.durability_netherite = config.getInt("enchantment_configuration.kinship.durability_regen_netherite");
        this.durability_diamond = config.getInt("enchantment_configuration.kinship.durability_regen_diamond");
        this.durability_iron = config.getInt("enchantment_configuration.kinship.durability_regen_iron");
        this.durability_stone = config.getInt("enchantment_configuration.kinship.durability_regen_stone");
        this.enchantDescription = config.getString("enchantment_configuration.kinship.description");

        this.compatibleItems.addAll(ItemMaterialManager.getInstance().getPickaxes());
        this.compatibleItems.remove(Material.GOLDEN_PICKAXE);
    }
}
