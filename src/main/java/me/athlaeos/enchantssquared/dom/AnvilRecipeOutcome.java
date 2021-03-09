package me.athlaeos.enchantssquared.dom;

import org.bukkit.inventory.ItemStack;

public class AnvilRecipeOutcome {
    private ItemStack output;
    private AnvilRecipeOutcomeState state;

    public AnvilRecipeOutcome(ItemStack output, AnvilRecipeOutcomeState state){
        this.output = output;
        this.state = state;
    }

    public AnvilRecipeOutcomeState getState() {
        return state;
    }

    public ItemStack getOutput() {
        return output;
    }
}
