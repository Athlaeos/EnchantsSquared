package me.athlaeos.enchantssquared.enchantments.mineenchantments;

import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.hooks.McMMOHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Excavation extends BreakBlockEnchantment{
    private double durability_decay;
    private boolean nerf_excavation_speed;
    private int fatigue_amplifier;
    private int fatigue_duration;
    private final List<Material> pickaxeBreakables = new ArrayList<>();
    private final List<Material> axeBreakables = new ArrayList<>();
    private final List<Material> shovelBreakables = new ArrayList<>();
    private final List<Material> hoeBreakables = new ArrayList<>();
    private Map<Material, BlockExperience> blockExperienceValues = new HashMap<>();

    public Excavation(){
        this.enchantType = CustomEnchantType.EXCAVATION;
        this.max_level_table = 0;
        this.max_level = 0;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.excavation";
        loadFunctionalItemStrings(Arrays.asList("HOES", "AXES", "PICKAXES", "SHOVELS"));
        loadConfig();

        blockExperienceValues.put(Material.SPAWNER, new BlockExperience(15, 43));
        blockExperienceValues.put(Material.COAL_ORE, new BlockExperience(0, 2));
        blockExperienceValues.put(Material.DIAMOND_ORE, new BlockExperience(3, 7));
        blockExperienceValues.put(Material.EMERALD_ORE, new BlockExperience(3, 7));
        blockExperienceValues.put(Material.LAPIS_ORE, new BlockExperience(2, 5));
        blockExperienceValues.put(Material.NETHER_QUARTZ_ORE, new BlockExperience(2, 5));
        blockExperienceValues.put(Material.REDSTONE_ORE, new BlockExperience(1, 5));
        try {
            blockExperienceValues.put(Material.valueOf("NETHER_GOLD_ORE"), new BlockExperience(0, 1));
        } catch (IllegalArgumentException ignored){
        }
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

                int expToDrop = 0;
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
                    int kinshipLevel = CustomEnchantManager.getInstance().getEnchantStrength(heldTool, CustomEnchantType.KINSHIP);
                    for (Location l : blocksToBreak){
                        if (breakableBlocks.contains(blockBroken.getWorld().getBlockAt(l).getType())){
                            Block block = blockBroken.getWorld().getBlockAt(l);
                            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, e.getPlayer());
                            if (blockExperienceValues.containsKey(blockBreakEvent.getBlock().getType())){
                                blockBreakEvent.setExpToDrop(blockExperienceValues.get(blockBreakEvent.getBlock().getType()).getRandomExperience());
                            }
                            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(blockBreakEvent);
                            if (blockBreakEvent.isCancelled()) continue;
                            if (block.getDrops(item).isEmpty()) continue;
                            JobsHook.getJobsHook().performBlockBreakAction(e.getPlayer(), block);
                            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(new BlockBreakEvent(block, e.getPlayer()));
                            if (kinshipLevel > 0){
                                Kinship k = Kinship.getKinship();
                                if (k != null){
                                    k.execute(e, heldTool, kinshipLevel);
                                }
                            }
                            if (McMMOHook.getMcMMOHook().useMcMMO()){
                                McMMOHook.getMcMMOHook().rememberBlock(e.getPlayer(), block);
                            }
                            if (smeltBlocks && smeltingAllowed){
                                for (ItemStack i : MineUtils.cookBlock(heldTool, block)){
                                    if (i != null){
                                        block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), i);
                                    }
                                }
                                if (!MineUtils.cookBlock(heldTool, block).equals(block.getDrops(heldTool))){
                                    if (Sunforged.doesDropEXP()) {
                                        expToDrop++;
                                    }
                                } else {
                                    if (heldTool.getItemMeta() != null){
                                        if (!heldTool.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)){
                                            expToDrop += blockBreakEvent.getExpToDrop();
                                        }
                                    }
                                }
                                block.setType(Material.AIR);
                            } else {
                                if (heldTool.getItemMeta() != null){
                                    if (!heldTool.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)){
                                        expToDrop += blockBreakEvent.getExpToDrop();
                                    }
                                }
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
                        if (item.getItemMeta().isUnbreakable()) return;
                        Damageable toolMeta = (Damageable) item.getItemMeta();
                        int unBreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
                        double breakChance = 1D/(unBreakingLevel + 1D) * 100;
                        if ((RandomNumberGenerator.getRandom().nextInt(100) + 1) < breakChance){
                            PlayerItemDamageEvent event = new PlayerItemDamageEvent(e.getPlayer(), item, durabilityDamage);
                            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                toolMeta.setDamage(toolMeta.getDamage() + event.getDamage());
                                item.setItemMeta((ItemMeta) toolMeta);
                            }
                        }
                    }
                }
                if (expToDrop > 0){
                    ExperienceOrb orb = (ExperienceOrb) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
                    orb.setExperience(expToDrop);
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
        this.tradeMinCostBase = config.getInt("enchantment_configuration.excavation.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.excavation.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.excavation.trade_enabled");
        setIcon(config.getString("enchantment_configuration.excavation.icon"));

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

    private static class BlockExperience{
        private final int lowerBound;
        private final int upperBound;
        BlockExperience(int lowerBound, int upperBound){
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public int getRandomExperience(){
            return RandomNumberGenerator.getRandom().nextInt(((upperBound + 1) - lowerBound)) + lowerBound;
        }
    }
}
