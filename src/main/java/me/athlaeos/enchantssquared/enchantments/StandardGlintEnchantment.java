package me.athlaeos.enchantssquared.enchantments;

import me.athlaeos.enchantssquared.main.EnchantsSquared;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StandardGlintEnchantment extends Enchantment {
    private static NamespacedKey enchantmentKey = new NamespacedKey(EnchantsSquared.getPlugin(), "glint_enchantment");
    private static StandardGlintEnchantment ENSQUARED_GLINT = new StandardGlintEnchantment();

    public StandardGlintEnchantment() {
        super(enchantmentKey);
    }

    public static Enchantment getEnsquaredGlint() {
        return ENSQUARED_GLINT;
    }

    @Override
    public String getName() {
        return "Glint";
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true;
    }

    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNew.setAccessible(true);
            acceptingNew.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception ex) {
            registered = false;
            ex.printStackTrace();
        }
        if (registered){
            // do stuff
        }
    }

    public static void register(){
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(ENSQUARED_GLINT);
        if (!registered) registerEnchantment(ENSQUARED_GLINT);
    }
}
