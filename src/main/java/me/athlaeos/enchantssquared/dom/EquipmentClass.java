package me.athlaeos.enchantssquared.dom;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum EquipmentClass {
    SWORD(Arrays.asList(Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD, Material.DIAMOND_SWORD, Material.IRON_SWORD), Collections.singletonList("NETHERITE_SWORD")),
    BOW(Collections.singletonList(Material.BOW), null),
    CROSSBOW(Collections.singletonList(Material.CROSSBOW), null),
    TRIDENT(Collections.singletonList(Material.TRIDENT), null),
    HELMET(Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.TURTLE_HELMET), Collections.singletonList("NETHERITE_HELMET")),
    CHESTPLATE(Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE), Collections.singletonList("NETHERITE_CHESTPLATE")),
    LEGGINGS(Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS), Collections.singletonList("NETHERITE_LEGGINGS")),
    BOOTS(Arrays.asList(Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS), Collections.singletonList("NETHERITE_BOOTS")),
    SHEARS(Collections.singletonList(Material.SHEARS), null),
    FLINT_AND_STEEL(Collections.singletonList(Material.FLINT_AND_STEEL), null),
    FISHING_ROD(Collections.singletonList(Material.FISHING_ROD), null),
    ELYTRA(Collections.singletonList(Material.ELYTRA), null),
    PICKAXE(Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE), Collections.singletonList("NETHERITE_PICKAXE")),
    AXE(Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.DIAMOND_AXE), Collections.singletonList("NETHERITE_AXE")),
    SHOVEL(Arrays.asList(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.GOLDEN_SHOVEL, Material.IRON_SHOVEL, Material.DIAMOND_SHOVEL), Collections.singletonList("NETHERITE_SHOVEL")),
    HOE(Arrays.asList(Material.WOODEN_HOE, Material.STONE_HOE, Material.GOLDEN_HOE, Material.IRON_HOE, Material.DIAMOND_HOE), Collections.singletonList("Material.NETHERITE_HOE")),
    SHIELD(Collections.singletonList(Material.SHIELD), null);

    private final List<Material> matches;
    private final List<String> stringMatches;

    EquipmentClass(List<Material> matches, List<String> stringMatches){
        this.matches = matches;
        this.stringMatches = stringMatches;
    }

    public List<Material> getMatches() {
        return matches;
    }

    public List<String> getStringMatches() {
        return stringMatches;
    }

    public static EquipmentClass getClass(Material m){
        for (EquipmentClass tc : EquipmentClass.values()){
            List<Material> matches = new ArrayList<>(tc.getMatches());
            if (tc.getStringMatches() != null){
                for (String s : tc.getStringMatches()){
                    try {
                        matches.add(Material.valueOf(s));
                    } catch (IllegalArgumentException ignored){
                    }
                }
            }
            if (matches.contains(m)){
                return tc;
            }
        }
        return null;
    }
}
