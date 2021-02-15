package me.athlaeos.enchantssquared.managers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.enchantments.attackenchantments.*;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.*;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.Shielding;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.Steady;
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
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

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
                                "the enchant " + entry.object.getEnchantLore() + " is higher than max_enchants." +
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
    public boolean combineItems(ItemStack item1, ItemStack item2, ItemStack output){
        if (item2 == null || output == null) return true;
        Map<CustomEnchant, Integer> item1enchants = getItemsEnchantsFromLore(item1, item1);
        Map<CustomEnchant, Integer> item2enchants = getItemsEnchantsFromLore(item2, item1);
        Map<CustomEnchant, Integer> resultEnchants = new HashMap<>();

        if (output.hasItemMeta()){
            if (Objects.requireNonNull(output.getItemMeta()).hasLore()){
                Objects.requireNonNull(output.getItemMeta().getLore()).clear();
            }
        } else {
            return true;
        }

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

        if (output.getType() != Material.ENCHANTED_BOOK && resultEnchants.size() > maxEnchants){
            return false;
        } else {
            setItemEnchants(output, resultEnchants);
            return true;
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
            }
        } else {
            //updating PersistentDataContainer to be accurate with enchantments
            List<String> stringEnchants = new ArrayList<>();
            for (CustomEnchant e : enchantments.keySet()){
                stringEnchants.add(allEnchants.inverse().get(e) + ":" + enchantments.get(e));
            }
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
        for (String l : lore){
            if (isLoreAnEnchant(l)){
                if (firstEnchantIndex == 0) firstEnchantIndex = lore.indexOf(l);
            } else {
                finalLore.add(l);
            }
        }
        if (finalLore.size() == 0){
            for (CustomEnchant e : enchantments.keySet()){
                finalLore.add(Utils.chat(e.getEnchantLore().replace("%lv_number%", "" + enchantments.get(e))
                        .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
            }
        } else {
            if (firstEnchantIndex >= finalLore.size()){
                for (CustomEnchant e : enchantments.keySet()){
                    finalLore.add(Utils.chat(e.getEnchantLore().replace("%lv_number%", "" + enchantments.get(e))
                            .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
                }
            } else {
                for (CustomEnchant e : enchantments.keySet()){
                    finalLore.add(firstEnchantIndex, Utils.chat(e.getEnchantLore().replace("%lv_number%", "" + enchantments.get(e))
                            .replace("%lv_roman%", Utils.toRoman(enchantments.get(e)))));
                }
            }
        }
        meta.setLore(finalLore);
        item.setItemMeta(meta);
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
                if (l.contains(extractEnchantString(e.getEnchantLore()))) {
                    if (e.getEnchantLore().contains("%lv_roman%")) {
                        String[] splitLine = l.split(" ");
                        int level = Utils.translateRomanToLevel(splitLine[splitLine.length - 1]);
                        itemEnchants.put(e, level);
                    } else if (e.getEnchantLore().contains("%lv_number%")) {
                        String[] splitLine = l.split(" ");
                        int level = Integer.parseInt(splitLine[splitLine.length - 1]);
                        itemEnchants.put(e, level);
                    } else {
                        itemEnchants.put(e, 0);
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

    /*
    Removes an enchantment from ItemStack enchantedItem
     */
    public boolean removeEnchant(ItemStack enchantedItem, CustomEnchantType enchant){
        if (enchantedItem == null) return false;
        Map<CustomEnchant, Integer> itemsEnchants = getItemsEnchantsFromPDC(enchantedItem);
        if (itemsEnchants.remove(allEnchants.get(getEnchantID(enchant))) == null) return false;
        setItemEnchants(enchantedItem, itemsEnchants);
        return true;
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

    private void insertEnchants(){
        allEnchants.put(1, new Excavation());
        allEnchants.put(2, new Sunforged());
        allEnchants.put(3, new Kinship());

        allEnchants.put(4, new Flight());
        allEnchants.put(5, new Rejuvenation());
        allEnchants.put(6, new LavaWalker());
        allEnchants.put(7, new SpeedBoost());
        allEnchants.put(8, new JumpBoost());
        allEnchants.put(9, new NightVision());
        allEnchants.put(10, new WaterBreathing());
        allEnchants.put(11, new Haste());
        allEnchants.put(12, new Metabolism());
        allEnchants.put(13, new Strength());
        allEnchants.put(14, new Vigorous());
        allEnchants.put(15, new Luck());
        allEnchants.put(16, new CurseBrittle());
        allEnchants.put(17, new CurseHeavy());
        allEnchants.put(18, new CurseHunger());

        allEnchants.put(19, new Withering());
        allEnchants.put(20, new Stunning());
        allEnchants.put(21, new Slowness());
        allEnchants.put(22, new Nausea());
        allEnchants.put(23, new Weakening());
        allEnchants.put(24, new Poisoning());
        allEnchants.put(25, new Blinding());
        allEnchants.put(26, new Crushing());
        allEnchants.put(27, new AOEArrows());
        allEnchants.put(28, new Toxic());

        allEnchants.put(29, new Shielding());
        allEnchants.put(30, new Steady());

        allEnchants.put(31, new Vitality());

        allEnchants.put(32, new PlaceTorch());
        allEnchants.put(33, new AutoReplant());
        allEnchants.put(34, new Shockwave());

        allEnchants.put(35, new Sapping());
        allEnchants.put(36, new Vampiric());
        allEnchants.put(37, new Beheading());
        allEnchants.put(38, new Soulbound());

        allEnchants.put(39, new SplashPotionBlock());
        allEnchants.put(40, new IncreasePotionPotency());

        allEnchants.put(41, new CurseBerserk());

        for (CustomEnchant c : allEnchants.values()){
            stringEnchantments.add(extractEnchantString(c.getEnchantLore()));
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
        for (CustomEnchant e : getItemsEnchantsFromPDC(item).keySet()){
            if (e.getEnchantType() == enchant){
                return true;
            }
        }
        return false;
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


