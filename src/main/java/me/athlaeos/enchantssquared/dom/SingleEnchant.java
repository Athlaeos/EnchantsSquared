package me.athlaeos.enchantssquared.dom;

public class SingleEnchant {
    private CustomEnchant enchantment;
    private int level;

    public SingleEnchant(CustomEnchant enchantment, int level){
        this.enchantment = enchantment;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public CustomEnchant getEnchantment() {
        return enchantment;
    }
}
