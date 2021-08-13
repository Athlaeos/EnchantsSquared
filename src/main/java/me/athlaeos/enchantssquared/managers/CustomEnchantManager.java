package me.athlaeos.enchantssquared.managers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.enchantssquared.Debug;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.*;
import me.athlaeos.enchantssquared.enchantments.StandardGlintEnchantment;
import me.athlaeos.enchantssquared.enchantments.attackenchantments.*;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.*;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.Shielding;
import me.athlaeos.enchantssquared.enchantments.fishenchantments.Grappling;
import me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments.Vitality;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.AutoReplant;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.PlaceTorch;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.Shockwave;
import me.athlaeos.enchantssquared.enchantments.killenchantments.*;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.Excavation;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.Kinship;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.Sunforged;
import me.athlaeos.enchantssquared.enchantments.potionenchantments.IncreasePotionPotency;
import me.athlaeos.enchantssquared.enchantments.potionenchantments.SplashPotionBlock;
import me.athlaeos.enchantssquared.enchantments.singletriggerenchantments.*;
import me.athlaeos.enchantssquared.enchantments.singletriggerenchantments.Vigorous;
import me.athlaeos.enchantssquared.events.EnchantingEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class CustomEnchantManager {

    private static CustomEnchantManager manager;

    private BiMap<Integer, CustomEnchant> allEnchants;
    private List<String> stringEnchantments = new ArrayList<>();

    private final NamespacedKey enchantmentsKey = new NamespacedKey(EnchantsSquared.getPlugin(), "es_enchantments");

    private int maxEnchants;
    private int maxEnchantsFromTable;

    private boolean requirePermissions;

    public CustomEnchantManager(){
        allEnchants = HashBiMap.create();

        this.requirePermissions = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("permission_required");

        maxEnchants = ConfigOptionsManager.getInstance().getMaxEnchants();
        maxEnchantsFromTable = ConfigOptionsManager.getInstance().getMaxEnchantsFromTable();
        insertEnchants();
    }

    public static CustomEnchantManager getInstance(){
        if (manager == null) {
            manager = new CustomEnchantManager();
        }
        return manager;
    }

    /*
    What follows after a player enchants something with an enchantment table.
    Rolls an amount of times randomly for new enchantments and adds them onto an item.
     */
    public void enchantForPlayer(ItemStack item, Player p){
        if (item == null) return;
        List<CustomEnchant> possibleEnchants = getCompatibleEnchants(item);
        List<Entry> entries = new ArrayList<>();
        Map<CustomEnchant, Integer> obtainedEnchantments = getItemsEnchantsFromPDC(item);
        double accumulatedWeight = 0.0;
        Debug.log(p, "&denchantForPlayer() &fpossible/compatible enchantments size: " + possibleEnchants.size());
        for (CustomEnchant c : possibleEnchants){
            if (requirePermissions){
                if (!p.hasPermission(c.getRequiredPermission())){
                    continue;
                }
            }
            if (c.isEnabled()){
                if (c.isBook_only()){
                    if (item.getType() != Material.ENCHANTED_BOOK){
                        continue;
                    }
                }
                accumulatedWeight += c.getWeight();
                Entry e = new Entry();
                e.object = c;
                e.accumulatedWeight = accumulatedWeight;
                entries.add(e);
            }
        }
        int rolls = RandomNumberGenerator.getRandom().nextInt(maxEnchantsFromTable) + 1;
        for (int i = 0; i < rolls; i++){
            double r = RandomNumberGenerator.getRandom().nextDouble() * accumulatedWeight;

            entries:
            for (Entry entry : entries) {
                if (entry.accumulatedWeight >= r) {
                    int designatedLevel;
                    if (entry.object.getMax_level() < entry.object.getMax_level_table()){
                        System.out.println("[EnchantsSquared] A player attempted to enchant something, but max_enchants_table for" +
                                "the enchant " + entry.object.getDisplayName() + " is higher than max_enchants." +
                                "This cannot be the case, so for this instance the value of max_enchants_table was set to the same " +
                                "value as max_enchants. Be sure to correct this mistake");
                        entry.object.setMax_level_table(entry.object.getMax_level());
                    }
                    if (entry.object.getMax_level_table() > 0){
                        designatedLevel = RandomNumberGenerator.getRandom().nextInt(entry.object.getMax_level_table()) + 1;
                    } else {
                        designatedLevel = 1;
                    }
                    if (obtainedEnchantments.size() < maxEnchants){
                        obtainedEnchantments.put(entry.object, designatedLevel);
                    } else {
                        break;
                    }
                    break;
                }
            }
        }
        setItemEnchants(item, obtainedEnchantments);
    }

    public SingleEnchant pickRandomEnchant(List<CustomEnchant> enchantments){
        double accumulatedWeight = 0.0;
        List<Entry> entries = new ArrayList<>();

        for (CustomEnchant c : enchantments){
            if (c.isEnabled()){
                accumulatedWeight += c.getWeight();
                Entry e = new Entry();
                e.object = c;
                e.accumulatedWeight = accumulatedWeight;
                entries.add(e);
            }
        }
        double r = RandomNumberGenerator.getRandom().nextDouble() * accumulatedWeight;
        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= r) {
                int designatedLevel;
                if (entry.object.getMax_level() < entry.object.getMax_level_table()){
                    System.out.println("[EnchantsSquared] An enchanted book was attempted to be made, but max_enchants_table for" +
                            "the enchant " + entry.object.getDisplayName() + " is higher than max_enchants." +
                            "This cannot be the case, so for this instance the value of max_enchants_table was set to the same " +
                            "value as max_enchants. Be sure to correct this mistake");
                    entry.object.setMax_level_table(entry.object.getMax_level());
                }
                if (entry.object.getMax_level_table() > 0){
                    designatedLevel = RandomNumberGenerator.getRandom().nextInt(entry.object.getMax_level()) + 1;
                } else {
                    designatedLevel = 1;
                }
                return new SingleEnchant(entry.object, designatedLevel);
            }
        }
        return null;
    }

    public List<CustomEnchant> getTradableEnchants(){
        return allEnchants.values().stream().filter(CustomEnchant::isAvailableForTrade).collect(Collectors.toList());
    }

    /*
    Returns a list of all CustomEnchants if they are compatible with the item given
     */
    public List<CustomEnchant> getCompatibleEnchants(ItemStack item){
        List<CustomEnchant> possibleEnchants = new ArrayList<>();
        if (item == null) return new ArrayList<>(allEnchants.values());
        enchantsLoop:
        for (CustomEnchant e : allEnchants.values()){
            if (e.isEnabled()){
                for (Enchantment conflict : e.getConflictsWith()){
                    if (item.containsEnchantment(conflict)) {
                        continue enchantsLoop;
                    }
                }
                if (item.getType() == Material.ENCHANTED_BOOK){
                    possibleEnchants.add(e);
                } else {
                    if (!(item.getItemMeta() instanceof Damageable)){
                        possibleEnchants.add(e);
                    } else if (e.getCompatibleItems().contains(item.getType()) && !e.isBook_only()){
                        possibleEnchants.add(e);
                    }
                }
            }
        }
        return possibleEnchants;
    }

    /*
    Attempts to combine the enchantments from item1 and item2 into output, both in lore and in PersistentDataContainer
    Returns false if the total amount of enchantments exceeds maxEnchants
     */
    public AnvilRecipeOutcome combineItems(ItemStack item1, ItemStack item2, ItemStack output){
        ItemStack tempItem2;
        boolean combiningWithBook = false;
        if (item2 != null){
            tempItem2 = item2.clone();
            if (item2.getType() == Material.ENCHANTED_BOOK){
                if (item2.getItemMeta() != null){
                    if (output == null){
                        combiningWithBook = true;
                        if (item2.getItemMeta() instanceof EnchantmentStorageMeta){
                            EnchantmentStorageMeta item2meta = (EnchantmentStorageMeta) tempItem2.getItemMeta();
                            assert item2meta != null;
                            item2meta.removeStoredEnchant(StandardGlintEnchantment.getEnsquaredGlint());
                            tempItem2.setItemMeta(item2meta);
                        }
                    } else if (output.getType() == Material.AIR){
                        combiningWithBook = true;
                        output.setType(item1.getType());
                        if (item2.getItemMeta() instanceof EnchantmentStorageMeta){
                            EnchantmentStorageMeta item2meta = (EnchantmentStorageMeta) tempItem2.getItemMeta();
                            assert item2meta != null;
                            item2meta.removeStoredEnchant(StandardGlintEnchantment.getEnsquaredGlint());
                            tempItem2.setItemMeta(item2meta);
                        }
                    }
                }
            }
        }
        boolean outputNull = false;
        boolean item2Null = false;
        if (item2 == null){
            item2Null = true;
        } else {
            if (item2.getType() == Material.AIR){
                item2Null = true;
            }
        }
        if (output == null){
            outputNull = true;
        } else {
            if (output.getType() == Material.AIR){
                outputNull = true;
            }
        }
        if ((item2Null || outputNull) && !combiningWithBook) {
            Debug.log(EnchantsSquared.getPlugin().getServer().getConsoleSender(), "&dcombineItems() &fitem2 = null = " + (item2 == null) + ", output = null = " + (output==null) + ", combiningWithBook = " + combiningWithBook);
            return new AnvilRecipeOutcome(null, AnvilRecipeOutcomeState.OUTPUT_NULL);
        }
        Map<CustomEnchant, Integer> item1enchants = getItemsEnchantsFromLore(item1, item1);
        Map<CustomEnchant, Integer> item2enchants = getItemsEnchantsFromLore(item2, item1);
        Map<CustomEnchant, Integer> resultEnchants = new HashMap<>();
        if (item2enchants.size() == 0){
            resultEnchants = item1enchants;
        } else {
            for (CustomEnchant e1 : item1enchants.keySet()){
                if (item2enchants.containsKey(e1)){
                    if (item1enchants.get(e1).equals(item2enchants.get(e1))){
                        if (item1enchants.get(e1) >= e1.getMax_level()){
                            resultEnchants.put(e1, item1enchants.get(e1));
                        } else {
                            resultEnchants.put(e1, item1enchants.get(e1) + 1);
                        }
                    } else {
                        if (item1enchants.get(e1) > item2enchants.get(e1)){
                            resultEnchants.put(e1, item1enchants.get(e1));
                        } else {
                            resultEnchants.put(e1, item2enchants.get(e1));
                        }
                    }
                } else {
                    resultEnchants.put(e1, item1enchants.get(e1));
                }
            }

            for (CustomEnchant e2 : item2enchants.keySet()){
                if (!item1enchants.containsKey(e2)){
                    resultEnchants.put(e2, item2enchants.get(e2));
                }
            }
        }

        if (combiningWithBook){
            if (item1 != null){
                output = item1.clone();
            }
        }

        if (output != null){
            if (output.getType() == Material.AIR){
                Debug.log(EnchantsSquared.getPlugin().getServer().getConsoleSender(), "&dcomebineItems() &foutput is null despite all processes");
                return new AnvilRecipeOutcome(null, AnvilRecipeOutcomeState.OUTPUT_NULL);
            } else {
                if (output.getType() != Material.ENCHANTED_BOOK && resultEnchants.size() > maxEnchants){
                    return new AnvilRecipeOutcome(null, AnvilRecipeOutcomeState.MAX_ENCHANTS_EXCEEDED);
                }
            }
        } else {
            Debug.log(EnchantsSquared.getPlugin().getServer().getConsoleSender(), "&dcomebineItems() &foutput is null despite all processes");
            return new AnvilRecipeOutcome(null, AnvilRecipeOutcomeState.OUTPUT_NULL);
        }
        setItemEnchants(output, resultEnchants);
        if (resultEnchants.size() == 0){
            Debug.log(EnchantsSquared.getPlugin().getServer().getConsoleSender(), "&dcomebineItems() &foutput has no custom enchantments, ");
            return new AnvilRecipeOutcome(output, AnvilRecipeOutcomeState.ITEM_NO_CUSTOM_ENCHANTS);
        } else {
            Debug.log(EnchantsSquared.getPlugin().getServer().getConsoleSender(), "&dcomebineItems() &fcombining successful, output has " + resultEnchants.size() + " enchantments");
            return new AnvilRecipeOutcome(output, AnvilRecipeOutcomeState.SUCCESSFUL);
        }
    }

    /*
    getItemsEnchantsFromPDC() takes in an item and returns a HashMap<CustomEnchant, Integer> containing its custom
    enchantments along with their levels. If the item does not contain custom enchants it returns an empty HashMap.

    The method takes these enchants from the item's PersistentDataContainer, from a string where all the enchant's
    details can be found. The string looks like "enchantid:level;enchantid:level;...", of course replacing
    enchantid and level with their respective integer values.
     */
    public Map<CustomEnchant, Integer> getItemsEnchantsFromPDC(ItemStack enchantedItem){
        Map<CustomEnchant, Integer> totalEnchants = new HashMap<>();
        if (enchantedItem == null) return totalEnchants;
        if (enchantedItem.getItemMeta() == null) return totalEnchants;
        PersistentDataContainer container = enchantedItem.getItemMeta().getPersistentDataContainer();
        if (container.has(enchantmentsKey, PersistentDataType.STRING)){
            String enchantmentsString = container.get(enchantmentsKey, PersistentDataType.STRING);
            assert enchantmentsString != null;
            String[] enchantments = enchantmentsString.split(";");
            for (String enchantment : enchantments){
                String[] enchantWithLevel = enchantment.split(":");
                if (enchantWithLevel.length == 2){
                    try {
                        int ID = Integer.parseInt(enchantWithLevel[0]);
                        int level = Integer.parseInt(enchantWithLevel[1]);
                        totalEnchants.put(allEnchants.get(ID), level);
                    } catch (IllegalArgumentException ignored){
                    }
                }
            }
        }
        return totalEnchants;
    }

    /*
    Since the switch from going lore-based to PersistentDataContainer-based affects all old items this method must
    be called at least once on items that haven't had their metadatas updated yet
     */
    public void updateItem(ItemStack i){
        if (i == null) return;
        setItemEnchants(i, getItemsEnchantsFromLore(i, null));
    }

    /*
    setItemEnchants takes in an item and a Map<CustomEnchant, Integer> representing the custom enchantment to
    apply as well as its level. It applies the enchantments as a string to the item's persistent data container.
    The string looks like "enchantid:level;enchantid:level;...", of course replacing enchantid and level with their
    respective integer values.
     */
    public void setItemEnchants(ItemStack item, Map<CustomEnchant, Integer> enchantments){
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (enchantments.isEmpty()){
            if (meta.getPersistentDataContainer().has(enchantmentsKey, PersistentDataType.STRING)){
                meta.getPersistentDataContainer().remove(enchantmentsKey);
                item.setItemMeta(meta);
                meta = item.getItemMeta();
            }
        } else {
            //updating PersistentDataContainer to be accurate with enchantments
            List<String> stringEnchants = new ArrayList<>();
            for (CustomEnchant e : enchantments.keySet()){
                stringEnchants.add(allEnchants.inverse().get(e) + ":" + enchantments.get(e));
                if (item.getType() != Material.ENCHANTED_BOOK){
                    if (e instanceof SingleTriggerEnchantment){
                        EnchantingEnchantmentTriggerEvent event = new EnchantingEnchantmentTriggerEvent(item, enchantments.get(e), e);
                        EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()){
                            ((SingleTriggerEnchantment) e).execute(item, enchantments.get(e));
                        }
                    }
                }
            }
            meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(enchantmentsKey, PersistentDataType.STRING, String.join(";", stringEnchants));
        }

        //updating the lore to be accurate with enchantments
        List<String> lore;
        List<String> finalLore = new ArrayList<>();
        if (meta.hasLore()){
            assert meta.getLore() != null;
            lore = new ArrayList<>(meta.getLore());
        } else {
            lore = new ArrayList<>();
        }
        //if the lore contains an enchantment it's not supposed to have, it is removed
        int firstEnchantIndex = 0; //This'll track where in the lore the last enchant is located,
        // so that once enchants are added back to it no other lore has to be removed
        boolean enchantAtLine0 = false;
        for (String l : lore){
            if (isLoreAnEnchant(l)){
                if (firstEnchantIndex == 0) {
                    firstEnchantIndex = lore.indexOf(l);
                    if (firstEnchantIndex == 0) enchantAtLine0 = true;
                }
            } else {
                finalLore.add(l);
            }
        }
        if (finalLore.size() == 0){
            for (CustomEnchant e : enchantments.keySet()){
                finalLore.add(Utils.chat(e.getDisplayName().replace("%lv_number%", "" + enchantments.get(e))
                        .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
            }
        } else {
            if (firstEnchantIndex >= finalLore.size()){
                for (CustomEnchant e : enchantments.keySet()){
                    finalLore.add(Utils.chat(e.getDisplayName().replace("%lv_number%", "" + enchantments.get(e))
                            .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
                }
            } else if (enchantAtLine0 || firstEnchantIndex != 0){
                for (CustomEnchant e : enchantments.keySet()){
                    finalLore.add(firstEnchantIndex, Utils.chat(e.getDisplayName().replace("%lv_number%", "" + enchantments.get(e))
                            .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
                }
            } else {
                for (CustomEnchant e : enchantments.keySet()){
                    finalLore.add(Utils.chat(e.getDisplayName().replace("%lv_number%", "" + enchantments.get(e))
                            .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
                }
            }
        }
        if (!enchantments.isEmpty()){
            if (meta instanceof EnchantmentStorageMeta){
                EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
                storageMeta.addStoredEnchant(StandardGlintEnchantment.getEnsquaredGlint(), 1, true);
                storageMeta.setLore(finalLore);
                item.setItemMeta(storageMeta);
            } else {
                meta.setLore(finalLore);
                item.setItemMeta(meta);
                item.addUnsafeEnchantment(StandardGlintEnchantment.getEnsquaredGlint(), 1);
            }
        } else {
            meta.setLore(finalLore);
            item.setItemMeta(meta);
        }
    }


    /*
    getItemsEnchantsFromLore() takes in an item and returns a HashMap<CustomEnchant, Integer> containing its custom
    enchantments along with their levels. If the item does not contain custom enchants it returns an empty HashMap.

    The method takes these enchants from the item's lore, this method loops through all the lore and compatible
    enchantments a couple of times so it's not a very fast method. Should be used sparingly.
    */
    public Map<CustomEnchant, Integer> getItemsEnchantsFromLore(ItemStack enchantedItem, ItemStack filterItem){
        //The map is the custom enchant + its level, if the level is 0 the level is not displayed in the lore
        //and has no real max level. ex: "Flight" rather than "Flight I"
        List<String> itemLore;
        Map<CustomEnchant, Integer> itemEnchants = new HashMap<>();
        if (enchantedItem == null) {
            return itemEnchants;
        }
        if (enchantedItem.getItemMeta().hasLore()){
            itemLore = enchantedItem.getItemMeta().getLore();
        } else {
            return itemEnchants;
        }

        for (CustomEnchant e : getCompatibleEnchants(filterItem)){
            for (String l : itemLore) {
                if (l.contains(extractEnchantString(e.getDisplayName()))) {
                    if (e.getDisplayName().contains("%lv_roman%")) {
                        String[] splitLine = l.split(" ");
                        int level = Utils.translateRomanToLevel(splitLine[splitLine.length - 1]);
                        itemEnchants.put(e, level);
                    } else if (e.getDisplayName().contains("%lv_number%")) {
                        String[] splitLine = l.split(" ");
                        int level = Integer.parseInt(splitLine[splitLine.length - 1]);
                        itemEnchants.put(e, level);
                    } else {
                        itemEnchants.put(e, 1);
                    }
                }
            }
        }

        return itemEnchants;
    }

    /*
    Get an enchantment's integer ID based off of the CustomEnchantType enum,
    throws an IllegalArgumentException if the CustomEnchantType given does not yet have an enchantment related to it.
    Returns the integer ID of the enchantment if it exists
     */
    public int getEnchantID(CustomEnchantType enchant) throws IllegalArgumentException{
        for (CustomEnchant e : allEnchants.values()){
            if (e.getEnchantType() == enchant){
                return allEnchants.inverse().get(e);
            }
        }
        throw new IllegalArgumentException("No enchantment was found of this type");
    }

    /*
    Get a CustomEnchant based off of the CustomEnchantType given.
    Returns null if the enchantment does not yet exist, or a CustomEnchant if it does.
     */
    public CustomEnchant getEnchantmentFromType(CustomEnchantType enchant){
        for (CustomEnchant e : allEnchants.values()){
            if (e.getEnchantType() == enchant){
                return e;
            }
        }
        return null;
    }

    /**
     * Removes a given enchant from the given item
     * @param enchantedItem the item to remove the enchantment from
     * @param enchant the enchantment enum of the enchantment to remove
     * @return true if the enchantment was removed from the item. False if the item is null, if the given enchant
     * has no enchantment associated with it, or if the item didn't have the enchantment.
     */
    public boolean removeEnchant(ItemStack enchantedItem, CustomEnchantType enchant){
        if (enchantedItem == null) return false;
        Map<CustomEnchant, Integer> itemsEnchants = getItemsEnchantsFromPDC(enchantedItem);
        int id;
        try {
            id = getEnchantID(enchant);
        } catch (IllegalArgumentException ignored){
            return false;
        }
        if (!allEnchants.containsKey(id)) {
            return false;
        }
        CustomEnchant enchantToRemove = allEnchants.get(id);
        if (itemsEnchants.containsKey(enchantToRemove)) {
            if (enchantToRemove instanceof SingleTriggerEnchantment){
                ((SingleTriggerEnchantment) enchantToRemove).reverse(enchantedItem, itemsEnchants.get(enchantToRemove));
            }
        }
        if (itemsEnchants.remove(enchantToRemove) == null) {
            return false;
        }

        setItemEnchants(enchantedItem, itemsEnchants);
        return true;
    }

    public ItemStack removeAllEnchants(ItemStack enchantedItem){
        if (enchantedItem == null) return null;
        Map<CustomEnchant, Integer> itemsEnchants = getItemsEnchantsFromPDC(enchantedItem);
        Map<CustomEnchant, Integer> itemsEnchantsCopy = new HashMap<>(itemsEnchants);
        for (CustomEnchant enchant : itemsEnchantsCopy.keySet()){
            if (enchant instanceof SingleTriggerEnchantment){
                ((SingleTriggerEnchantment) enchant).reverse(enchantedItem, itemsEnchants.get(enchant));
            }
            itemsEnchants.remove(enchant);
        }

        setItemEnchants(enchantedItem, itemsEnchants);
        return enchantedItem;
    }

    /*
    Adds an enchantment to ItemStack item, with a given level.
    */
    public void addEnchant(ItemStack item, CustomEnchantType enchant, int level){
        if (item == null) return;
        Map<CustomEnchant, Integer> itemsEnchants = getItemsEnchantsFromPDC(item);
        CustomEnchant newEnchant = getEnchant(enchant);
        if (newEnchant != null){
            itemsEnchants.put(newEnchant, level);
        }
        setItemEnchants(item, itemsEnchants);
    }

    public int getEnchantStrength(ItemStack item, CustomEnchantType enchant){
        if (item == null) return 0;
        CustomEnchant e = getEnchant(enchant);
        if (e != null){
            Map<CustomEnchant, Integer> itemEnchants = getItemsEnchantsFromPDC(item);
            if (itemEnchants.containsKey(e)){
                return itemEnchants.get(e);
            }
        }
        return 0;
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    private void registerEnchant(int id, CustomEnchant enchant){
        if (enchant.isEnabled()){
            allEnchants.put(id, enchant);
        }
    }

    private void insertEnchants(){
        registerEnchant(1, new Excavation());
        registerEnchant(2, new Sunforged());
        registerEnchant(3, new Kinship());

        registerEnchant(4, new Flight());
        registerEnchant(5, new Rejuvenation());
        registerEnchant(6, new LavaWalker());
        registerEnchant(7, new SpeedBoost());
        registerEnchant(8, new JumpBoost());
        registerEnchant(9, new NightVision());
        registerEnchant(10, new WaterBreathing());
        registerEnchant(11, new Haste());
        registerEnchant(12, new Metabolism());
        registerEnchant(13, new Strength());
        registerEnchant(14, new Vigorous());
        registerEnchant(15, new Luck());
        registerEnchant(16, new CurseBrittle());
        registerEnchant(17, new CurseHeavy());
        registerEnchant(18, new CurseHunger());

        registerEnchant(19, new Withering());
        registerEnchant(20, new Stunning());
        registerEnchant(21, new Slowness());
        registerEnchant(22, new Nausea());
        registerEnchant(23, new Weakening());
        registerEnchant(24, new Poisoning());
        registerEnchant(25, new Blinding());
        registerEnchant(26, new Crushing());
        registerEnchant(27, new AOEArrows());
        registerEnchant(28, new Toxic());

        registerEnchant(29, new Shielding());
        registerEnchant(30, new Steady());

        registerEnchant(31, new Vitality());

        registerEnchant(32, new PlaceTorch());
        registerEnchant(33, new AutoReplant());
        registerEnchant(34, new Shockwave());

        registerEnchant(35, new Sapping());
        registerEnchant(36, new Vampiric());
        registerEnchant(37, new Beheading());
        registerEnchant(38, new Soulbound());

        registerEnchant(39, new SplashPotionBlock());
        registerEnchant(40, new IncreasePotionPotency());

        registerEnchant(41, new CurseBerserk());

        registerEnchant(42, new ReinforcedPlating());

        registerEnchant(43, new TridentSharpness());
        registerEnchant(44, new Grappling());

        registerEnchant(45, new FireResistance());

//        allEnchants.put(42, new ElytraFireworkBoost());

        for (CustomEnchant c : allEnchants.values()){
            stringEnchantments.add(extractEnchantString(c.getDisplayName()));
        }
    }

    public String extractEnchantString(String s){
        if (s == null) return null;
        return ChatColor.stripColor(Utils.chat(s)
                .replace("%lv_roman%", "")
                .replace("%lv_number%", ""));
    }

    private boolean isLoreAnEnchant(String lore){
        for (String s : stringEnchantments){
            if (lore.contains(s)) return true;
        }
        return false;
    }

    public Map<Integer, CustomEnchant> getAllEnchants() {
        return allEnchants;
    }

    /*
    Returns true if the item given has the enchant of the matching type, false otherwise
     */
    public boolean doesItemHaveEnchant(ItemStack item, CustomEnchantType enchant){
        if (item == null) return false;
        if (enchant == null) return false;
        for (CustomEnchant e : getItemsEnchantsFromPDC(item).keySet()){
            if (e.getEnchantType() == enchant){
                return true;
            }
        }
        return false;
    }

    public boolean doesItemHaveEnchants(ItemStack item){
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(enchantmentsKey, PersistentDataType.STRING);
    }

    private static class Entry{
        double accumulatedWeight;
        CustomEnchant object;
    }

    public CustomEnchant getEnchant(CustomEnchantType c){
        for (CustomEnchant e : allEnchants.values()){
            if (e.getEnchantType() == c){
                return e;
            }
        }
        return null;
    }
}


