package me.athlaeos.enchantssquared.managers;

import me.athlaeos.enchantssquared.dom.MaterialClassType;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemMaterialManager {
    private static ItemMaterialManager manager = null;
    private final List<Material> pickaxes = new ArrayList<>();
    private final List<Material> axes = new ArrayList<>();
    private final List<Material> shovels = new ArrayList<>();
    private final List<Material> hoes = new ArrayList<>();
    private final List<Material> swords = new ArrayList<>();
    private final List<Material> boots = new ArrayList<>();
    private final List<Material> leggings = new ArrayList<>();
    private final List<Material> chestPlates = new ArrayList<>();
    private final List<Material> helmets = new ArrayList<>();
    private final Material shears;
    private final Material flintAndSteel;
    private final Material fishingRod;
    private Material elytra;
    private Material bow;
    private Material crossbow;
    private Material trident;
    private final List<Material> all = new ArrayList<>();

    public ItemMaterialManager(){
        for (Material m : Material.values()){
            if (m.toString().contains("_PICKAXE")){
                pickaxes.add(m);
            } else if (m.toString().contains("_AXE")){
                axes.add(m);
            } else if (m.toString().contains("_SHOVEL") || m.toString().contains("_SPADE")){
                shovels.add(m);
            } else if (m.toString().contains("_HOE")){
                hoes.add(m);
            } else if (m.toString().contains("_SWORD")){
                swords.add(m);
            } else if (m.toString().contains("_BOOTS")){
                boots.add(m);
            } else if (m.toString().contains("_LEGGINGS")){
                leggings.add(m);
            } else if (m.toString().contains("_CHESTPLATE")){
                chestPlates.add(m);
            } else if (m.toString().contains("_HELMET")){
                helmets.add(m);
            }
        }

        shears = Material.SHEARS;
        flintAndSteel = Material.FLINT_AND_STEEL;
        fishingRod = Material.FISHING_ROD;
        elytra = Material.ELYTRA;
        bow = Material.BOW;
        trident = Material.TRIDENT;
        try{
            crossbow = Material.valueOf("CROSSBOW");
        } catch (IllegalArgumentException e){
            crossbow = null;
        }

        all.addAll(getMaterialsFromType(MaterialClassType.ALL));
    }

    public List<Material> getMaterialsFromType(MaterialClassType t){
        List<Material> materials = new ArrayList<>();

        switch (t){
            case AXES: materials.addAll(axes);
                break;
            case PICKAXES: materials.addAll(pickaxes);
                break;
            case SHOVELS: materials.addAll(shovels);
                break;
            case HOES: materials.addAll(hoes);
                break;
            case SWORDS: materials.addAll(swords);
                break;
            case BOWS: materials.add(bow);
                break;
            case CROSSBOWS: if (crossbow != null) materials.add(crossbow);
                break;
            case TRIDENTS: materials.add(trident);
                break;
            case HELMETS: materials.addAll(helmets);
                break;
            case CHESTPLATES: materials.addAll(chestPlates);
                break;
            case LEGGINGS: materials.addAll(leggings);
                break;
            case BOOTS: materials.addAll(boots);
                break;
            case ELYTRA: materials.add(elytra);
                break;
            case SHEARS: materials.add(shears);
                break;
            case FISHINGROD: materials.add(fishingRod);
                break;
            case FLINTANDSTEEL: materials.add(flintAndSteel);
                break;
            case ALL:{
                materials.addAll(axes);
                materials.addAll(pickaxes);
                materials.addAll(shovels);
                materials.addAll(hoes);
                materials.addAll(swords);
                materials.add(bow);
                if (crossbow != null) materials.add(crossbow);
                materials.add(trident);
                materials.addAll(helmets);
                materials.addAll(chestPlates);
                materials.addAll(leggings);
                materials.addAll(boots);
                materials.add(elytra);
                materials.add(shears);
                materials.add(fishingRod);
                materials.add(flintAndSteel);
                break;
            }
        }

        return materials;
    }

    public static ItemMaterialManager getInstance(){
        if (manager == null){
            manager = new ItemMaterialManager();
        }
        return manager;
    }

    public List<Material> getAxes() {
        return axes;
    }

    public List<Material> getBoots() {
        return boots;
    }

    public List<Material> getChestPlates() {
        return chestPlates;
    }

    public List<Material> getHelmets() {
        return helmets;
    }

    public List<Material> getHoes() {
        return hoes;
    }

    public List<Material> getLeggings() {
        return leggings;
    }

    public List<Material> getPickaxes() {
        return pickaxes;
    }

    public List<Material> getShovels() {
        return shovels;
    }

    public List<Material> getSwords() {
        return swords;
    }

    public Material getShears() {
        return shears;
    }

    public Material getElytra() {
        return elytra;
    }

    public Material getFlintAndSteel() {
        return flintAndSteel;
    }

    public Material getFishingRod() {
        return fishingRod;
    }

    public Material getBow() {
        return bow;
    }

    public Material getCrossbow() {
        return crossbow;
    }

    public List<Material> getAll() {
        return all;
    }
}
