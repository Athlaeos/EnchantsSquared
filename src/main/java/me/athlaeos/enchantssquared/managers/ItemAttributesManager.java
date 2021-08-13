package me.athlaeos.enchantssquared.managers;

import me.athlaeos.enchantssquared.dom.EquipmentClass;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemAttributesManager {
    private static ItemAttributesManager manager = null;
    private final Map<Material, List<AttributeWrapper>> defaultAttributes = new HashMap<>();
    private final NamespacedKey defaultAttributeKey = new NamespacedKey(EnchantsSquared.getPlugin(), "ensquared_default_attributes");

    public ItemAttributesManager(){
        weaponDamage(Material.WOODEN_SWORD, 4D);
        weaponSpeed(Material.WOODEN_SWORD, 1.6);
        weaponDamage(Material.WOODEN_PICKAXE, 2D);
        weaponSpeed(Material.WOODEN_PICKAXE, 1.2);
        weaponDamage(Material.WOODEN_SHOVEL, 2.5);
        weaponSpeed(Material.WOODEN_SHOVEL, 1D);
        weaponDamage(Material.WOODEN_AXE, 7D);
        weaponSpeed(Material.WOODEN_AXE, 0.8);
        weaponDamage(Material.WOODEN_HOE, 1D);
        weaponSpeed(Material.WOODEN_HOE, 1D);

        weaponDamage(Material.STONE_SWORD, 5D);
        weaponSpeed(Material.STONE_SWORD, 1.6);
        weaponDamage(Material.STONE_PICKAXE, 3D);
        weaponSpeed(Material.STONE_PICKAXE, 1.2);
        weaponDamage(Material.STONE_SHOVEL, 3.5);
        weaponSpeed(Material.STONE_SHOVEL, 1D);
        weaponDamage(Material.STONE_AXE, 9D);
        weaponSpeed(Material.STONE_AXE, 0.8);
        weaponDamage(Material.STONE_HOE, 1D);
        weaponSpeed(Material.STONE_HOE, 2D);

        weaponDamage(Material.GOLDEN_SWORD, 4D);
        weaponSpeed(Material.GOLDEN_SWORD, 1.6);
        weaponDamage(Material.GOLDEN_PICKAXE, 2D);
        weaponSpeed(Material.GOLDEN_PICKAXE, 1.2);
        weaponDamage(Material.GOLDEN_SHOVEL, 2.5);
        weaponSpeed(Material.GOLDEN_SHOVEL, 1D);
        weaponDamage(Material.GOLDEN_AXE, 7D);
        weaponSpeed(Material.GOLDEN_AXE, 1D);
        weaponDamage(Material.GOLDEN_HOE, 1D);
        weaponSpeed(Material.GOLDEN_HOE, 1D);

        weaponDamage(Material.IRON_SWORD, 6D);
        weaponSpeed(Material.IRON_SWORD, 1.6);
        weaponDamage(Material.IRON_PICKAXE, 4D);
        weaponSpeed(Material.IRON_PICKAXE, 1.2);
        weaponDamage(Material.IRON_SHOVEL, 4.5);
        weaponSpeed(Material.IRON_SHOVEL, 1D);
        weaponDamage(Material.IRON_AXE, 9D);
        weaponSpeed(Material.IRON_AXE, 0.9);
        weaponDamage(Material.IRON_HOE, 1D);
        weaponSpeed(Material.IRON_HOE, 3D);

        weaponDamage(Material.DIAMOND_SWORD, 7D);
        weaponSpeed(Material.DIAMOND_SWORD, 1.6);
        weaponDamage(Material.DIAMOND_PICKAXE, 5D);
        weaponSpeed(Material.DIAMOND_PICKAXE, 1.2);
        weaponDamage(Material.DIAMOND_SHOVEL, 5.5);
        weaponSpeed(Material.DIAMOND_SHOVEL, 1D);
        weaponDamage(Material.DIAMOND_AXE, 9D);
        weaponSpeed(Material.DIAMOND_AXE, 1D);
        weaponDamage(Material.DIAMOND_HOE, 1D);
        weaponSpeed(Material.DIAMOND_HOE, 4D);


        weaponDamage("NETHERITE_SWORD", 8D);
        weaponSpeed("NETHERITE_SWORD", 1.6);
        weaponDamage("NETHERITE_PICKAXE", 6D);
        weaponSpeed("NETHERITE_PICKAXE", 1.2);
        weaponDamage("NETHERITE_SHOVEL", 6.5);
        weaponSpeed("NETHERITE_SHOVEL", 1D);
        weaponDamage("NETHERITE_AXE", 10D);
        weaponSpeed("NETHERITE_AXE", 1D);
        weaponDamage("NETHERITE_HOE", 1D);
        weaponSpeed("NETHERITE_HOE", 4D);

        armorRating(Material.LEATHER_HELMET, 1D);
        armorRating(Material.LEATHER_CHESTPLATE, 3D);
        armorRating(Material.LEATHER_LEGGINGS, 2D);
        armorRating(Material.LEATHER_BOOTS, 1D);

        armorRating(Material.CHAINMAIL_HELMET, 2D);
        armorRating(Material.CHAINMAIL_CHESTPLATE, 5D);
        armorRating(Material.CHAINMAIL_LEGGINGS, 4D);
        armorRating(Material.CHAINMAIL_BOOTS, 1D);

        armorRating(Material.GOLDEN_HELMET, 2D);
        armorRating(Material.GOLDEN_CHESTPLATE, 5D);
        armorRating(Material.GOLDEN_LEGGINGS, 3D);
        armorRating(Material.GOLDEN_BOOTS, 1D);

        armorRating(Material.IRON_HELMET, 2D);
        armorRating(Material.IRON_CHESTPLATE, 6D);
        armorRating(Material.IRON_LEGGINGS, 5D);
        armorRating(Material.IRON_BOOTS, 2D);

        armorRating(Material.DIAMOND_HELMET, 3D);
        armorToughnessRating(Material.DIAMOND_HELMET, 2D);
        armorRating(Material.DIAMOND_CHESTPLATE, 8D);
        armorToughnessRating(Material.DIAMOND_CHESTPLATE, 2D);
        armorRating(Material.DIAMOND_LEGGINGS, 6D);
        armorToughnessRating(Material.DIAMOND_LEGGINGS, 2D);
        armorRating(Material.DIAMOND_BOOTS, 3D);
        armorToughnessRating(Material.DIAMOND_BOOTS, 2D);

        armorRating("NETHERITE_HELMET", 3D);
        armorToughnessRating("NETHERITE_HELMET", 3D);
        armorKnockbackResistanceRating("NETHERITE_HELMET", 0.01D);
        armorRating("NETHERITE_CHESTPLATE", 8D);
        armorToughnessRating("NETHERITE_CHESTPLATE", 3D);
        armorKnockbackResistanceRating("NETHERITE_CHESTPLATE", 0.01D);
        armorRating("NETHERITE_LEGGINGS", 6D);
        armorToughnessRating("NETHERITE_LEGGINGS", 3D);
        armorKnockbackResistanceRating("NETHERITE_LEGGINGS", 0.01D);
        armorRating("NETHERITE_BOOTS", 3D);
        armorToughnessRating("NETHERITE_BOOTS", 3D);
        armorKnockbackResistanceRating("NETHERITE_BOOTS", 0.01D);

        armorRating(Material.TURTLE_HELMET, 2D);

        weaponDamage(Material.TRIDENT, 9);
        weaponSpeed(Material.TRIDENT, 1.1);
    }

    /**
     * Stores all the item's vanilla stats to the item's PersistentDataContainer. If the item has no vanilla attributes,
     * nothing happens. Example: Items like a diamond chestplate will be assigned 8 armor and 2 toughness
     * The attribute modifiers will also be applied to the item
     * @param i the item to set vanilla attributes to.
     */
    public void applyVanillaStats(ItemStack i){
        if (i == null) return;
        if (defaultAttributes.containsKey(i.getType())){
            ItemMeta itemMeta = i.getItemMeta();
            if (itemMeta == null) return;
            setDefaultStats(i, defaultAttributes.get(i.getType()));
        }
    }

    public List<AttributeWrapper> getVanillaStats(ItemStack i){
        if (i == null) return new ArrayList<>();
        if (defaultAttributes.containsKey(i.getType())){
            return defaultAttributes.get(i.getType());
        }
        return new ArrayList<>();
    }

    public double getVanillaAttributeStrength(ItemStack i, Attribute a){
        if (i == null) return 0;
        if (defaultAttributes.containsKey(i.getType())){
            for (AttributeWrapper wrapper : defaultAttributes.get(i.getType())){
                if (wrapper.getAttribute() == a){
                    return wrapper.getModifier().getAmount();
                }
            }
        }
        return 0;
    }

    /**
     * Sets all the item's stored default attribute modifiers as actual attributes, and removes any that aren't present.
     * @param i the item to update.
     */
    public void applyDefaultStats(ItemStack i){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        meta.setAttributeModifiers(null);
        i.setItemMeta(meta);
        Map<Attribute, AttributeWrapper> defaultAttributes = getItemDefaultStats(i);
        for (AttributeWrapper wrapper : defaultAttributes.values()){
            setAttributeStrength(i, wrapper.getAttribute(), wrapper.getModifier().getAmount());
        }
    }

    public void addDefaultStat(ItemStack i, Attribute attribute, double strength){
        if (i == null) return;
        Map<Attribute, AttributeWrapper> defaultStats = getItemDefaultStats(i);
        EquipmentSlot slot = getEquipmentSlot(i.getType());
        defaultStats.put(attribute, new AttributeWrapper(attribute,
                new AttributeModifier(UUID.randomUUID(),
                        attribute.toString().toLowerCase().replaceFirst("_", "."),
                        strength,
                        AttributeModifier.Operation.ADD_NUMBER,
                        slot)));
        setDefaultStats(i, defaultStats.values());
    }

    public void removeDefaultStat(ItemStack i, Attribute attribute){
        if (i == null) return;
        Map<Attribute, AttributeWrapper> defaultStats = getItemDefaultStats(i);
        defaultStats.remove(attribute);
        setDefaultStats(i, defaultStats.values());
    }

    public void setDefaultStats(ItemStack i, Collection<AttributeWrapper> attributes){
        if (i == null) return;
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        if (attributes.isEmpty()) {
            meta.getPersistentDataContainer().remove(defaultAttributeKey);
            meta.setAttributeModifiers(null);
        } else {
            Collection<String> stringAttributes = new HashSet<>();
            for (AttributeWrapper wrapper : attributes){
                if (wrapper.getAttribute() == null || wrapper.getModifier() == null) continue;
                stringAttributes.add(wrapper.getAttribute().toString() + ":" + wrapper.getModifier().getAmount());
            }
            String defaultAttributes = String.join(";", stringAttributes);
            meta.getPersistentDataContainer().set(defaultAttributeKey, PersistentDataType.STRING, defaultAttributes);
        }
        i.setItemMeta(meta);
        applyDefaultStats(i);
    }

    public Map<Attribute, AttributeWrapper> getItemDefaultStats(ItemStack i){
        Map<Attribute, AttributeWrapper> attributeModifiers = new HashMap<>();
        if (i == null) return attributeModifiers;
        assert i.getItemMeta() != null;
        if (i.getItemMeta().getPersistentDataContainer().has(defaultAttributeKey, PersistentDataType.STRING)){
            String[] splitDefaultStats = i.getItemMeta().getPersistentDataContainer().get(defaultAttributeKey, PersistentDataType.STRING).split(";");
            for (String modifier : splitDefaultStats){
                String[] splitModifier = modifier.split(":");
                if (splitModifier.length >= 2){
                    try {
                        double strength = Double.parseDouble(splitModifier[1]);
                        Attribute attribute = Attribute.valueOf(splitModifier[0]);
                        EquipmentSlot slot = getEquipmentSlot(i.getType());
                        if (slot == null) throw new IllegalArgumentException();
                        AttributeWrapper dto = new AttributeWrapper(attribute,
                                new AttributeModifier(UUID.randomUUID(),
                                        attribute.toString().toLowerCase().replaceFirst("_", "."),
                                        strength,
                                        AttributeModifier.Operation.ADD_NUMBER,
                                        slot));
                        attributeModifiers.put(attribute, dto);
                    } catch (IllegalArgumentException ignored){
                    }
                }
            }
        }
        return attributeModifiers;
    }

    /**
     * Sets an attribute to an item only if the type of the item has attributes by default
     * The attribute will always be added with Operation.ADD_NUMBER
     * @param i the item to add the attribute to
     * @param attribute the attribute to add to the item
     * @param value the value to give to the item
     */
    public void setAttributeStrength(ItemStack i, Attribute attribute, double value){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        Map<Attribute, AttributeWrapper> defaultStats = getItemDefaultStats(i);
        if (defaultStats.containsKey(attribute)){
            AttributeWrapper dto = defaultStats.get(attribute);
            if (value < 0) {
                value = 0;
            }
            if (attribute == Attribute.GENERIC_ATTACK_SPEED) value -= 4D;
            if (attribute == Attribute.GENERIC_ATTACK_DAMAGE) value -= 1D;
            value = Utils.round(value, 4);
            AttributeModifier modifier = dto.getModifier();
            meta.removeAttributeModifier(attribute);
            meta.addAttributeModifier(attribute,
                    new AttributeModifier(
                            UUID.randomUUID(),
                            modifier.getName(),
                            value,
                            modifier.getOperation(),
                            modifier.getSlot()));

            i.setItemMeta(meta);
        } else {
            System.out.println(i.getType() + " does not have " + attribute + " by default");
        }
    }

    public double getDefaultStat(ItemStack m, Attribute attribute){
        Map<Attribute, AttributeWrapper> defaultStats = getItemDefaultStats(m);
        if (defaultStats.containsKey(attribute)){
            return defaultStats.get(attribute).getModifier().getAmount();
        }
        return 0D;
    }

    private void weaponDamage(Material weapon, double value){
        List<AttributeWrapper> modifiers = new ArrayList<>();
        if (defaultAttributes.containsKey(weapon)){
            modifiers = defaultAttributes.get(weapon);
        }
        modifiers.add(new AttributeWrapper(
                Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(
                        UUID.randomUUID(), "generic.attack_damage", value, AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND)));
        defaultAttributes.put(weapon, modifiers);
    }

    private void weaponDamage(String weapon, double value){
        Material material;
        try {
            material = Material.valueOf(weapon);
        } catch (IllegalArgumentException ignored){
            return;
        }
        List<AttributeWrapper> modifiers = new ArrayList<>();
        if (defaultAttributes.containsKey(material)){
            modifiers = defaultAttributes.get(material);
        }
        modifiers.add(new AttributeWrapper(
                Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(
                UUID.randomUUID(), "generic.attack_damage", value, AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND)));
        defaultAttributes.put(material, modifiers);
    }

    private void weaponSpeed(Material weapon, double value){
        List<AttributeWrapper> modifiers = new ArrayList<>();
        if (defaultAttributes.containsKey(weapon)){
            modifiers = defaultAttributes.get(weapon);
        }
        modifiers.add(new AttributeWrapper(
                Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(
                UUID.randomUUID(), "generic.attack_speed", value, AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND)));
        defaultAttributes.put(weapon, modifiers);
    }

    private void weaponSpeed(String weapon, double value){
        Material material;
        try {
            material = Material.valueOf(weapon);
        } catch (IllegalArgumentException ignored){
            return;
        }
        List<AttributeWrapper> modifiers = new ArrayList<>();
        if (defaultAttributes.containsKey(material)){
            modifiers = defaultAttributes.get(material);
        }
        modifiers.add(new AttributeWrapper(
                Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(
                UUID.randomUUID(), "generic.attack_speed", value, AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND)));
        defaultAttributes.put(material, modifiers);
    }

    private void armorRating(Material armor, double value){
        EquipmentSlot slot = getEquipmentSlot(armor);
        if (slot != null){
            List<AttributeWrapper> modifiers = new ArrayList<>();
            if (defaultAttributes.containsKey(armor)){
                modifiers = defaultAttributes.get(armor);
            }
            modifiers.add(new AttributeWrapper(
                    Attribute.GENERIC_ARMOR, new AttributeModifier(
                    UUID.randomUUID(), "generic.armor", value, AttributeModifier.Operation.ADD_NUMBER,
                    slot)));
            defaultAttributes.put(armor, modifiers);
        }
    }

    private void armorRating(String armor, double value){
        Material material;
        try {
            material = Material.valueOf(armor);
        } catch (IllegalArgumentException ignored){
            return;
        }
        EquipmentSlot slot = getEquipmentSlot(material);
        if (slot != null){
            List<AttributeWrapper> modifiers = new ArrayList<>();
            if (defaultAttributes.containsKey(material)){
                modifiers = defaultAttributes.get(material);
            }
            modifiers.add(new AttributeWrapper(
                    Attribute.GENERIC_ARMOR, new AttributeModifier(
                    UUID.randomUUID(), "generic.armor", value, AttributeModifier.Operation.ADD_NUMBER,
                    slot)));
            defaultAttributes.put(material, modifiers);
        }
    }

    private void armorToughnessRating(String armor, double value){
        Material material;
        try {
            material = Material.valueOf(armor);
        } catch (IllegalArgumentException ignored){
            return;
        }
        EquipmentSlot slot = getEquipmentSlot(material);
        if (slot != null){
            List<AttributeWrapper> modifiers = new ArrayList<>();
            if (defaultAttributes.containsKey(material)){
                modifiers = defaultAttributes.get(material);
            }
            modifiers.add(new AttributeWrapper(
                    Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(
                    UUID.randomUUID(), "generic.armor_toughness", value, AttributeModifier.Operation.ADD_NUMBER,
                    slot)));
            defaultAttributes.put(material, modifiers);
        }
    }

    private void armorToughnessRating(Material armor, double value){
        EquipmentSlot slot = getEquipmentSlot(armor);
        if (slot != null){
            List<AttributeWrapper> modifiers = new ArrayList<>();
            if (defaultAttributes.containsKey(armor)){
                modifiers = defaultAttributes.get(armor);
            }
            modifiers.add(new AttributeWrapper(
                    Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(
                    UUID.randomUUID(), "generic.armor_toughness", value, AttributeModifier.Operation.ADD_NUMBER,
                    slot)));
            defaultAttributes.put(armor, modifiers);
        }
    }

    private void armorKnockbackResistanceRating(String armor, double value){
        Material material;
        try {
            material = Material.valueOf(armor);
        } catch (IllegalArgumentException ignored){
            return;
        }
        EquipmentSlot slot = getEquipmentSlot(material);
        if (slot != null){
            List<AttributeWrapper> modifiers = new ArrayList<>();
            if (defaultAttributes.containsKey(material)){
                modifiers = defaultAttributes.get(material);
            }
            modifiers.add(new AttributeWrapper(
                    Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(
                    UUID.randomUUID(), "generic.knockback_resistance", value, AttributeModifier.Operation.ADD_NUMBER,
                    slot)));
            defaultAttributes.put(material, modifiers);
        }
    }

    private void armorKnockbackResistanceRating(Material armor, double value) {
        EquipmentSlot slot = getEquipmentSlot(armor);
        if (slot != null) {
            List<AttributeWrapper> modifiers = new ArrayList<>();
            if (defaultAttributes.containsKey(armor)) {
                modifiers = defaultAttributes.get(armor);
            }
            modifiers.add(new AttributeWrapper(
                    Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(
                    UUID.randomUUID(), "generic.knockback_resistance", value, AttributeModifier.Operation.ADD_NUMBER,
                    slot)));
            defaultAttributes.put(armor, modifiers);
        }
    }

    private EquipmentSlot getEquipmentSlot(Material m){
        EquipmentClass damageableClass = EquipmentClass.getClass(m);
        if (damageableClass != null){
            switch (damageableClass){
                case BOOTS: return EquipmentSlot.FEET;
                case LEGGINGS: return EquipmentSlot.LEGS;
                case CHESTPLATE:
                case ELYTRA:
                    return EquipmentSlot.CHEST;
                case HELMET: return EquipmentSlot.HEAD;
                default: return EquipmentSlot.HAND;
            }
        }
        return EquipmentSlot.HAND;
    }

    public static ItemAttributesManager getInstance(){
        if (manager == null) manager = new ItemAttributesManager();
        return manager;
    }

    private static class AttributeWrapper {
        private final AttributeModifier modifier;
        private final Attribute attribute;

        public AttributeWrapper(Attribute attribute, AttributeModifier modifier){
            this.attribute = attribute;
            this.modifier = modifier;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public AttributeModifier getModifier() {
            return modifier;
        }
    }
}
