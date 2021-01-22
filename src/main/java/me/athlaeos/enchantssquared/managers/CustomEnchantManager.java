package me.athlaeos.enchantssquared.managers;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantClassification;
import me.athlaeos.enchantssquared.dom.CustomEnchantEnum;
import me.athlaeos.enchantssquared.enchantments.attackenchantments.*;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.*;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.DefendEnchantment;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.Shielding;
import me.athlaeos.enchantssquared.enchantments.defendenchantments.Steady;
import me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments.HealthRegenerationEnchantment;
import me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments.Vitality;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.AutoReplant;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.InteractEnchantment;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.PlaceTorch;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.Shockwave;
import me.athlaeos.enchantssquared.enchantments.killenchantments.*;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.BreakBlockEnchantment;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.Excavation;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.Kinship;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.Sunforged;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomEnchantManager {

    private static CustomEnchantManager manager;

    private Map<String, CustomEnchant> allEnchants = new HashMap<>();

    private Map<String, BreakBlockEnchantment> breakBlockEnchantments;
    private Map<String, AttackEnchantment> attackEnchantments;
    private Map<String, DefendEnchantment> defendEnchantments;
    private Map<String, HealthRegenerationEnchantment> healthRegenerationEnchantments;
    private Map<String, InteractEnchantment> interactEnchantments;
    private Map<String, KillEnchantment> killEnchantments;
    private Map<String, ConstantTriggerEnchantment> constantTriggerEnchantments;

    private int maxEnchants;
    private int maxEnchantsFromTable;

    private boolean requirePermissions;

    public CustomEnchantManager(){
        allEnchants = new HashMap<>();
        breakBlockEnchantments = new HashMap<>();
        attackEnchantments = new HashMap<>();
        healthRegenerationEnchantments = new HashMap<>();
        interactEnchantments = new HashMap<>();
        killEnchantments = new HashMap<>();
        defendEnchantments = new HashMap<>();
        constantTriggerEnchantments = new HashMap<>();

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

    public void applyEnchant(ItemStack item, CustomEnchantEnum enchant, int level){
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()){
            lore = meta.getLore();
        }
        for (String s : allEnchants.keySet()){
            CustomEnchant e = allEnchants.get(s);
            if (e.getEnchantType() == enchant){
                String enchantApplied = e.getEnchantLore()
                        .replace("%lv_number%", "" + level)
                        .replace("%lv_roman%", Utils.toRoman(level));
                lore.add(Utils.chat(enchantApplied));
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void applyCustomEnchants(ItemStack item, Player p){
        List<CustomEnchant> possibleEnchants = getCompatibleEnchants(item);
        List<Entry> entries = new ArrayList<>();
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
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < rolls; i++){
            double r = RandomNumberGenerator.getRandom().nextDouble() * accumulatedWeight;

            entries:
            for (Entry entry : entries) {
                if (entry.accumulatedWeight >= r) {
                    for (String l : lore){
                        if (l.contains(Utils.chat(entry.object.getEnchantLore()
                                .replace("%lv_number%", "")
                                .replace("%lv_roman%", "")))){
                            break entries;
                        }
                    }
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
                    String enchantApplied = entry.object.getEnchantLore()
                            .replace("%lv_number%", "" + designatedLevel)
                            .replace("%lv_roman%", Utils.toRoman(designatedLevel));
                    if (lore.size() < maxEnchants){
                        lore.add(Utils.chat(enchantApplied));
                    } else {
                        break;
                    }
                    break;
                }
            }
        }

        assert meta != null;
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public List<CustomEnchant> getCompatibleEnchants(ItemStack item){
        List<CustomEnchant> possibleEnchants = new ArrayList<>();
        enchantsLoop:
        for (String s : allEnchants.keySet()){
            CustomEnchant e = allEnchants.get(s);
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

    public boolean combineItems(ItemStack item1, ItemStack item2, ItemStack output){
        if (item2 == null || output == null) return true;
        Map<CustomEnchant, Integer> item1enchants = getItemsEnchants(item1, item1);
        Map<CustomEnchant, Integer> item2enchants = getItemsEnchants(item2, item1);
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
            ItemMeta meta = output.getItemMeta();
            List<String> lore = new ArrayList<>();
            for (CustomEnchant e : resultEnchants.keySet()){
                lore.add(Utils.chat(e.getEnchantLore().replace("%lv_number%", "" + resultEnchants.get(e))
                        .replace("%lv_roman%", Utils.toRoman(resultEnchants.get(e)))));
            }
            meta.setLore(lore);
            output.setItemMeta(meta);
            return true;
        }
    }

    public Map<CustomEnchant, Integer> getItemsEnchants(ItemStack enchantedItem, ItemStack filterItem){
        //The map is the custom enchant + its level, if the level is 0 the level is not displayed in the lore
        //and has no real max level. ex: "Flight" rather than "Flight I"
        List<String> itemLore;
        Map<CustomEnchant, Integer> itemEnchants = new HashMap<>();
        if (enchantedItem == null){
            return new HashMap<>();
        }
        if (Objects.requireNonNull(enchantedItem.getItemMeta()).hasLore()){
            itemLore = enchantedItem.getItemMeta().getLore();
        } else {
            return new HashMap<>();
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

    public Map<CustomEnchant, Integer> getItemsEnchants(ItemStack enchantedItem, CustomEnchantClassification c){
        //The map is the custom enchant + its level, if the level is 0 the level is not displayed in the lore
        //and has no real max level. ex: "Flight" rather than "Flight I"
        List<String> itemLore;
        Map<CustomEnchant, Integer> itemEnchants = new HashMap<>();
        if (enchantedItem == null){
            return new HashMap<>();
        }
        if (!enchantedItem.hasItemMeta()){
            return new HashMap<>();
        }
        if (enchantedItem.getItemMeta().hasLore()){
            itemLore = enchantedItem.getItemMeta().getLore();
        } else {
            return new HashMap<>();
        }
        loreLoop:
        for (String l : itemLore) {
            Map<String, CustomEnchant> classEnchants = getEnchantsByClassification(c);
            for (String s : classEnchants.keySet()){
                CustomEnchant e = classEnchants.get(s);
                if (l.contains(s)) {
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
                    continue loreLoop;
                }
            }
        }
        return itemEnchants;
    }

    public boolean removeEnchant(ItemStack enchantedItem, CustomEnchantEnum enchant, CustomEnchantClassification c){
        //The map is the custom enchant + its level, if the level is 0 the level is not displayed in the lore
        //and has no real max level. ex: "Flight" rather than "Flight I"
        List<String> itemLore;
        Map<CustomEnchant, Integer> itemEnchants = new HashMap<>();
        if (enchantedItem == null){
            return false;
        }
        if (!enchantedItem.hasItemMeta()){
            return false;
        }
        if (enchantedItem.getItemMeta().hasLore()){
            itemLore = enchantedItem.getItemMeta().getLore();
        } else {
            return false;
        }
        for (String l : itemLore) {
            Map<String, CustomEnchant> classEnchants = getEnchantsByClassification(c);
            for (String s : classEnchants.keySet()) {
                CustomEnchant e = classEnchants.get(s);
                if (e.getEnchantType() == enchant) {
                    if (l.contains(ChatColor.stripColor(Utils.chat(
                            e.getEnchantLore()
                                    .replace("%lv_number%", ""))
                            .replace("%lv_roman%", "")))) {
                        List<String> newLore = new ArrayList<>(itemLore);
                        newLore.remove(l);
                        ItemMeta meta = enchantedItem.getItemMeta();
                        meta.setLore(newLore);
                        enchantedItem.setItemMeta(meta);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getEnchantStrength(ItemStack item, CustomEnchantEnum enchant, CustomEnchantClassification c){
        Map<CustomEnchant, Integer> enchants = getItemsEnchants(item, c);
        for (CustomEnchant e : enchants.keySet()){
            if (e.getEnchantType() == enchant){
                return enchants.get(e);
            }
        }
        return 0;
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    private void insertEnchants(){
        CustomEnchant e = new Excavation();
        breakBlockEnchantments.put(extractEnchantString(e.getEnchantLore()), (BreakBlockEnchantment) e);
        e = new Sunforged();
        breakBlockEnchantments.put(extractEnchantString(e.getEnchantLore()), (BreakBlockEnchantment) e);
        e = new Kinship();
        breakBlockEnchantments.put(extractEnchantString(e.getEnchantLore()), (BreakBlockEnchantment) e);

        e = new Flight();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new Rejuvenation();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new LavaWalker();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new SpeedBoost();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new JumpBoost();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new NightVision();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new WaterBreathing();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new Haste();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new Metabolism();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new Strength();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new Vigorous();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new Luck();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new CurseBrittle();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new CurseHeavy();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);
        e = new CurseHunger();
        constantTriggerEnchantments.put(extractEnchantString(e.getEnchantLore()), (ConstantTriggerEnchantment) e);

        e = new Withering();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Stunning();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Slowness();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Nausea();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Weakening();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Poisoning();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Blinding();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Crushing();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new AOEArrows();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);
        e = new Toxic();
        attackEnchantments.put(extractEnchantString(e.getEnchantLore()), (AttackEnchantment) e);

        e = new Shielding();
        defendEnchantments.put(extractEnchantString(e.getEnchantLore()), (DefendEnchantment) e);
        e = new Steady();
        defendEnchantments.put(extractEnchantString(e.getEnchantLore()), (DefendEnchantment) e);

        e = new Vitality();
        healthRegenerationEnchantments.put(extractEnchantString(e.getEnchantLore()), (HealthRegenerationEnchantment) e);

        e = new PlaceTorch();
        interactEnchantments.put(extractEnchantString(e.getEnchantLore()), (InteractEnchantment) e);
        e = new AutoReplant();
        interactEnchantments.put(extractEnchantString(e.getEnchantLore()), (InteractEnchantment) e);
        e = new Shockwave();
        interactEnchantments.put(extractEnchantString(e.getEnchantLore()), (InteractEnchantment) e);

        e = new Sapping();
        killEnchantments.put(extractEnchantString(e.getEnchantLore()), (KillEnchantment) e);
        e = new Vampiric();
        killEnchantments.put(extractEnchantString(e.getEnchantLore()), (KillEnchantment) e);
        e = new Beheading();
        killEnchantments.put(extractEnchantString(e.getEnchantLore()), (KillEnchantment) e);
        e = new Soulbound();
        killEnchantments.put(extractEnchantString(e.getEnchantLore()), (KillEnchantment) e);

        allEnchants.putAll(killEnchantments);
        allEnchants.putAll(interactEnchantments);
        allEnchants.putAll(breakBlockEnchantments);
        allEnchants.putAll(constantTriggerEnchantments);
        allEnchants.putAll(healthRegenerationEnchantments);
        allEnchants.putAll(attackEnchantments);
        allEnchants.putAll(defendEnchantments);
    }

    public Map<String, CustomEnchant> getAllEnchants() {
        return allEnchants;
    }

    public boolean doesItemHaveEnchant(ItemStack item, CustomEnchantEnum enchant, CustomEnchantClassification c){
        for (CustomEnchant e : getItemsEnchants(item, c).keySet()){
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

    private Map<String, CustomEnchant> getEnchantsByClassification(CustomEnchantClassification c){
        if (c == null) return allEnchants;
        switch (c) {
            case CONSTANT_TRIGGER: return new HashMap<>(constantTriggerEnchantments);
            case ON_KILL: return new HashMap<>(killEnchantments);
            case ON_ATTACK: return new HashMap<>(attackEnchantments);
            case ON_BLOCK_BREAK: return new HashMap<>(breakBlockEnchantments);
            case ON_INTERACT: return new HashMap<>(interactEnchantments);
            case ON_HEALTH_REGEN: return new HashMap<>(healthRegenerationEnchantments);
            case ON_DAMAGED: return new HashMap<>(defendEnchantments);
            default: return allEnchants;
        }
    }

    public CustomEnchant getEnchant(CustomEnchantEnum c){
        for (CustomEnchant e : allEnchants.values()){
            if (e.getEnchantType() == c){
                return e;
            }
        }
        return null;
    }

    public String extractEnchantString(String s){
        if (s != null) {
            return ChatColor.stripColor(Utils.chat(s
                    .replace("%lv_number%", ""))
                    .replace("%lv_roman%", ""));
        }
        return null;
    }
}


