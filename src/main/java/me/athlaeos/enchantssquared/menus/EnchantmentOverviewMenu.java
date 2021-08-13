package me.athlaeos.enchantssquared.menus;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantmentOverviewMenu extends Menu{
    private final YamlConfiguration translationConfig = ConfigManager.getInstance().getConfig("translations.yml").get();
    private final String menuTitle = Utils.chat(translationConfig.getString("enchantment_menu.title"));
    private final String iconName = translationConfig.getString("enchantment_menu.name");
    private final List<String> iconLore = translationConfig.getStringList("enchantment_menu.lore");
    private final int loreLength = translationConfig.getInt("enchantment_menu.max_length");
    private final ItemStack nextPageButton = createItemStack(
            Material.valueOf(translationConfig.getString("enchantment_menu.button_nextpage.icon")),
            Utils.chat(translationConfig.getString("enchantment_menu.button_nextpage.name")),
            translationConfig.getStringList("enchantment_menu.button_nextpage.lore")
    );
    private final ItemStack prevPageButton = createItemStack(
            Material.valueOf(translationConfig.getString("enchantment_menu.button_prevpage.icon")),
            Utils.chat(translationConfig.getString("enchantment_menu.button_prevpage.name")),
            translationConfig.getStringList("enchantment_menu.button_prevpage.lore")
    );

    private int currentPage = 1;

    public EnchantmentOverviewMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return menuTitle;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null){
            setMenuItems();
            return;
        }
        if (e.getCurrentItem().equals(prevPageButton)){
            currentPage--;
        } else if (e.getCurrentItem().equals(nextPageButton)){
            currentPage++;
        }
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        List<ItemStack> totalEnchantIcons = new ArrayList<>();
        CustomEnchantManager manager = CustomEnchantManager.getInstance();
        for (Integer enchantID : manager.getAllEnchants().keySet()){
            CustomEnchant enchant = manager.getAllEnchants().get(enchantID);
            List<String> lore = new ArrayList<>();
            for (String line : iconLore){
                String processedLine = line
                        .replace("%description%", enchant.getEnchantDescription())
                        .replace("%weight%", "" + enchant.getWeight())
                        .replace("%lv_roman%", Utils.toRoman(enchant.getMax_level()))
                        .replace("%lv_number%", "" + enchant.getMax_level())
                        .replace("%name%", "" + enchant.getDisplayName()
                                .replace("%lv_roman%", "")
                                .replace("%lv_number%", ""))
                        .replace("%compatible_with%", String.join(", ", enchant.getCompatibleItemStrings()).toLowerCase());
                lore.addAll(Utils.seperateStringIntoLines(processedLine, loreLength));
            }
            assert iconName != null;
            ItemStack icon = createItemStack(
                    enchant.getIcon(),
                    Utils.chat(iconName.replace("%name%", enchant.getDisplayName()
                            .replace("%lv_roman%", "")
                            .replace("%lv_number%", ""))),
                    lore);
            totalEnchantIcons.add(icon);
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, totalEnchantIcons);
        if (currentPage < 1) currentPage = 1;
        if (currentPage > pages.size()) currentPage = pages.size();
        for (ItemStack icon : pages.get(currentPage - 1)){
            inventory.addItem(icon);
        }
        updatePageButtons(prevPageButton);
        updatePageButtons(nextPageButton);
        inventory.setItem(45, prevPageButton);
        inventory.setItem(53, nextPageButton);
    }

    private ItemStack createItemStack(Material material, String displayname, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayname);
        if (lore != null){
            List<String> coloredLore = new ArrayList<>();
            for (String l : lore){
                coloredLore.add(Utils.chat(l));
            }
            meta.setLore(coloredLore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private void updatePageButtons(ItemStack item) {
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        if (itemMeta.hasLore()){
            assert itemMeta.getLore() != null;
            List<String> newLore = new ArrayList<>();
            for (String l : itemMeta.getLore()){
                newLore.add(l.replace("%page%", "" + currentPage));
            }
            itemMeta.setLore(newLore);
            item.setItemMeta(itemMeta);
        }
    }
}
