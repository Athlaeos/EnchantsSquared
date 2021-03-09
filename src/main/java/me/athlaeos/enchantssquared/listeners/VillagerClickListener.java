package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.SingleEnchant;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillagerClickListener implements Listener {
    public double bookCustomEnchantChance;
    public VillagerClickListener(){
        bookCustomEnchantChance = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("custom_enchant_trade_rate");
    }

    @EventHandler
    public void onVillagerClick(PlayerInteractAtEntityEvent e){
        Entity entity = e.getRightClicked();
        CustomEnchantManager manager = CustomEnchantManager.getInstance();
        if (entity instanceof Villager){
            Villager villager = (Villager) entity;
            if (villager.getProfession() == Villager.Profession.LIBRARIAN){
                List<MerchantRecipe> villagerTrades = new ArrayList<>();
                for (MerchantRecipe trade : villager.getRecipes()){
                    ItemStack tradeItem = trade.getResult();
                    if (tradeItem.getType() == Material.ENCHANTED_BOOK){
                        if (manager.getItemsEnchantsFromPDC(tradeItem).size() == 0){
                            if (RandomNumberGenerator.getRandom().nextDouble() * 100 <= bookCustomEnchantChance){
                                ItemStack newResult = new ItemStack(Material.ENCHANTED_BOOK, 1);
                                SingleEnchant chosenEnchant = manager.pickRandomEnchant(manager.getTradableEnchants());
                                Map<CustomEnchant, Integer> enchantMap = new HashMap<>();
                                enchantMap.put(chosenEnchant.getEnchantment(), chosenEnchant.getLevel());
                                manager.setItemEnchants(newResult, enchantMap);
                                ItemStack book = null;
                                boolean hasEmeralds = false;
                                List<ItemStack> newIngredients = new ArrayList<>();
                                for (ItemStack i : trade.getIngredients()){
                                    if (i.getType() == Material.BOOK) book = i;
                                    if (i.getType() == Material.EMERALD) {
                                        hasEmeralds = true;
                                    }
                                }
                                if (book == null && !hasEmeralds){
                                    newIngredients = trade.getIngredients();
                                }
                                if (hasEmeralds){
                                    int minAmount = (chosenEnchant.getLevel() <= 1) ? chosenEnchant.getEnchantment().getTradeMinCostBase() : chosenEnchant.getEnchantment().getTradeMinCostBase() + (chosenEnchant.getEnchantment().getTradeMinCostLv() * (chosenEnchant.getLevel() - 1));
                                    int maxAmount = (chosenEnchant.getLevel() <= 1) ? chosenEnchant.getEnchantment().getTradeMaxCostBase() : chosenEnchant.getEnchantment().getTradeMaxCostBase() + (chosenEnchant.getEnchantment().getTradeMaxCostLv() * (chosenEnchant.getLevel() - 1));
                                    int emeraldAmount = RandomNumberGenerator.getRandom().nextInt((maxAmount - minAmount) + 1) + minAmount;
                                    newIngredients.add(new ItemStack(Material.EMERALD, emeraldAmount));
                                }
                                if (book != null) {
                                    newIngredients.add(book);
                                }
                                MerchantRecipe newTrade = new MerchantRecipe(newResult, trade.getUses(), trade.getMaxUses(), trade.hasExperienceReward(), trade.getVillagerExperience(), trade.getPriceMultiplier());
                                newTrade.setIngredients(newIngredients);
                                villagerTrades.add(newTrade);
                            } else {
                                villagerTrades.add(trade);
                            }
                        } else {
                            villagerTrades.add(trade);
                        }
                    } else {
                        villagerTrades.add(trade);
                    }
                }
                villager.setRecipes(villagerTrades);
            }
        }
    }
}
