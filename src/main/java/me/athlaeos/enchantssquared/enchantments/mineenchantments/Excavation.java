package me.athlaeos.enchantssquared.enchantments.mineenchantments;

import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import me.athlaeos.enchantssquared.managers.enchantmanagers.ExcavationBlockFaceManager;
import me.athlaeos.enchantssquared.utils.MineUtils;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Excavation extends BreakBlockEnchantment{
    private double durability_decay;
    private boolean nerf_excavation_speed;
    private int fatigue_amplifier;
    private int fatigue_duration;
    private final List<Material> pickaxeBreakables = new ArrayList<>();
    private final List<Material> axeBreakables = new ArrayList<>();
    private final List<Material> shovelBreakables = new ArrayList<>();
    private final List<Material> hoeBreakables = new ArrayList<>();

    public Excavation(){
        this.enchantType = CustomEnchantType.EXCAVATION;
        this.max_level_table = 0;
        this.max_level = 0;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.excavation";
        loadFunctionalItemStrings(Arrays.asList("HOES", "AXES", "PICKAXES", "SHOVELS"));
        loadConfig();
    }

    @Override
    public void execute(BlockBreakEvent e, ItemStack item, int level) {
        if (!e.isCancelled()){
            boolean smeltingAllowed = true;
            if (e.getPlayer().isSneaking()){
                return;
            }
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getBlock().getLocation(), "es-deny-smelting")){
                    smeltingAllowed = false;
                }
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getBlock().getLocation(), "es-deny-smelting")){
                    return;
                }
            }
            if (ExcavationBlockFaceManager.getInstance().getBlockFaceMap().get(e.getPlayer().getUniqueId()) == null){
                return;
            }
            ItemStack heldTool = e.getPlayer().getInventory().getItemInMainHand();

            List<Material> breakableBlocks;
            if (heldTool.getType().toString().contains("_PICKAXE")){
                breakableBlocks = pickaxeBreakables;
            } else if (heldTool.getType().toString().contains("_AXE")){
                breakableBlocks = axeBreakables;
            } else if (heldTool.getType().toString().contains("_SHOVEL") || heldTool.getType().toString().contains("_SPADE")){
                breakableBlocks = shovelBreakables;
            } else if (heldTool.getType().toString().contains("_HOE")){
                breakableBlocks = hoeBreakables;
            } else {
                breakableBlocks = new ArrayList<>();
            }

            if (!breakableBlocks.contains(e.getBlock().getType())){
                return;
            }

            List<Location> blocksToBreak = new ArrayList<>();
            BlockFace face = ExcavationBlockFaceManager.getInstance().getBlockFaceMap().get(e.getPlayer().getUniqueId());
            if (face != null){
                Location blockBroken = e.getBlock().getLocation();
                if (face == BlockFace.NORTH || face == BlockFace.SOUTH){
                    Location l1 = new Location(blockBroken.getWorld(), blockBroken.getX() + 1, blockBroken.getY() + 1, blockBroken.getZ());
                    Location l2 = new Location(blockBroken.getWorld(), blockBroken.getX() - 1, blockBroken.getY() - 1, blockBroken.getZ());
                    blocksToBreak.addAll(Utils.getBlocksInArea(l1, l2));
                } else if (face == BlockFace.EAST || face == BlockFace.WEST){
                    Location l1 = new Location(blockBroken.getWorld(), blockBroken.getX(), blockBroken.getY() + 1, blockBroken.getZ() + 1);
                    Location l2 = new Location(blockBroken.getWorld(), blockBroken.getX(), blockBroken.getY() - 1, blockBroken.getZ() - 1);
                    blocksToBreak.addAll(Utils.getBlocksInArea(l1, l2));
                } else if (face == BlockFace.UP || face == BlockFace.DOWN){
                    Location l1 = new Location(blockBroken.getWorld(), blockBroken.getX() + 1, blockBroken.getY(), blockBroken.getZ() + 1);
                    Location l2 = new Location(blockBroken.getWorld(), blockBroken.getX() - 1, blockBroken.getY(), blockBroken.getZ() - 1);
                    blocksToBreak.addAll(Utils.getBlocksInArea(l1, l2));
                }

                if (e.getPlayer().getGameMode() == GameMode.CREATIVE){
                    for (Location l : blocksToBreak){
                        if (breakableBlocks.contains(blockBroken.getWorld().getBlockAt(l).getType())){
                            blockBroken.getWorld().getBlockAt(l).setType(Material.AIR);
                        }
                    }
                } else {
                    int durabilityDamage = 0;
                    ExcavationBlockFaceManager.getInstance().getBlockFaceMap().remove(e.getPlayer().getUniqueId());
                    boolean smeltBlocks = CustomEnchantManager.getInstance().doesItemHaveEnchant(heldTool, CustomEnchantType.SUNFORGED);
                    for (Location l : blocksToBreak){
                        if (breakableBlocks.contains(blockBroken.getWorld().getBlockAt(l).getType())){
                            Block block = blockBroken.getWorld().getBlockAt(l);
                            if (block.getDrops(item).isEmpty()) continue;
                            JobsHook.getJobsHook().performBlockBreakAction(e.getPlayer(), block);
                            Main.getPlugin().getServer().getPluginManager().callEvent(new BlockBreakEvent(block, e.getPlayer()));
                            if (smeltBlocks && smeltingAllowed){
                                for (ItemStack i : MineUtils.cookBlock(heldTool, block)){
                                    block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), i);
                                }
                                block.setType(Material.AIR);
                            } else {
                                block.breakNaturally(heldTool);
                            }

                            if (RandomNumberGenerator.getRandom().nextDouble() <= durability_decay){
                                durabilityDamage++;
                            }
                        }
                    }
                    if (nerf_excavation_speed){
                        if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)){
                            if (e.getPlayer().getPotionEffect(PotionEffectType.SLOW_DIGGING).getAmplifier() <= fatigue_amplifier){
                                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, fatigue_duration, fatigue_amplifier), true);
                            }
                        } else {
                            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, fatigue_duration, fatigue_amplifier), true);
                        }
                    }
                    if (item.getItemMeta() instanceof Damageable){
                        Damageable toolMeta = (Damageable) item.getItemMeta();
                        int unBreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
                        double breakChance = 1D/(unBreakingLevel + 1D) * 100;
                        if ((RandomNumberGenerator.getRandom().nextInt(100) + 1) < breakChance){
                            PlayerItemDamageEvent event = new PlayerItemDamageEvent(e.getPlayer(), item, durabilityDamage);
                            Main.getPlugin().getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                toolMeta.setDamage(toolMeta.getDamage() + durabilityDamage);
                                item.setItemMeta((ItemMeta) toolMeta);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.excavation.enchant_name");
        this.durability_decay = config.getDouble("enchantment_configuration.excavation.durability_decay");
        this.enabled = config.getBoolean("enchantment_configuration.excavation.enabled");
        this.weight = config.getInt("enchantment_configuration.excavation.weight");
        this.book_only = config.getBoolean("enchantment_configuration.excavation.book_only");
        this.nerf_excavation_speed = config.getBoolean("enchantment_configuration.excavation.nerf_excavation_speed");
        this.fatigue_amplifier = config.getInt("enchantment_configuration.excavation.fatigue_amplifier");
        this.fatigue_duration = config.getInt("enchantment_configuration.excavation.fatigue_duration");
        this.enchantDescription = config.getString("enchantment_configuration.excavation.description");

        YamlConfiguration excavConfig = ConfigManager.getInstance().getConfig("excavationblocks.yml").get();
        for (String s : excavConfig.getStringList("excavation_pickaxe_blocks")){
            try {
                pickaxeBreakables.add(Material.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }
        for (String s : excavConfig.getStringList("excavation_shovel_blocks")){
            try {
                shovelBreakables.add(Material.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }
        for (String s : excavConfig.getStringList("excavation_axe_blocks")){
            try {
                axeBreakables.add(Material.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }
        for (String s : excavConfig.getStringList("excavation_hoe_blocks")){
            try {
                hoeBreakables.add(Material.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.excavation.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:excavation is not valid, please correct it");
            }
        }
    }
}
