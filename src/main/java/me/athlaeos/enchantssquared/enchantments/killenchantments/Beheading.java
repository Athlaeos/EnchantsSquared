package me.athlaeos.enchantssquared.enchantments.killenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class Beheading extends KillEnchantment{
    private double beheading_base;
    private double beheading_lv;
    private double axe_buff;

    public Beheading(){
        this.enchantType = CustomEnchantType.DECAPITATION;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.beheading";
        loadFunctionalItemStrings(Arrays.asList("SWORDS", "AXES", "PICKAXES", "HOES", "SHOVELS", "SHEARS", "BOWS", "CROSSBOWS", "TRIDENTS"));
        loadConfig();
    }

    @Override
    public void execute(EntityDeathEvent e, ItemStack stack, int level, LivingEntity killer, LivingEntity killed) {
        if (!killer.hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-beheading")){
                return;
            }
        }
        double final_beheading_chance =  (level <= 1) ? this.beheading_base : this.beheading_base + ((level - 1) * this.beheading_lv);
        if (ItemMaterialManager.getInstance().getAxes().contains(stack.getType())){
            final_beheading_chance *= axe_buff;
        }

        if (RandomNumberGenerator.getRandom().nextDouble() < final_beheading_chance){
            ItemStack head = null;
            if (e.getEntity() instanceof Zombie){
                head = new ItemStack(Material.ZOMBIE_HEAD, 1);
            } else if (e.getEntity() instanceof WitherSkeleton){
                head = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
            } else if (e.getEntity() instanceof Skeleton){
                head = new ItemStack(Material.SKELETON_SKULL, 1);
            } else if (e.getEntity() instanceof Creeper){
                head = new ItemStack(Material.CREEPER_HEAD, 1);
            } else if (e.getEntity() instanceof HumanEntity){
                head = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                assert meta != null;
                meta.setOwningPlayer((Player)e.getEntity());
                head.setItemMeta(meta);
            }

            if (head != null){
                e.getDrops().add(head);
            }
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.beheading.enchant_name");
        this.beheading_base = config.getDouble("enchantment_configuration.beheading.beheading_base");
        this.beheading_lv = config.getDouble("enchantment_configuration.beheading.beheading_lv");
        this.axe_buff = config.getDouble("enchantment_configuration.beheading.axe_buff");
        this.enabled = config.getBoolean("enchantment_configuration.beheading.enabled");
        this.weight = config.getInt("enchantment_configuration.beheading.weight");
        this.book_only = config.getBoolean("enchantment_configuration.beheading.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.beheading.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.beheading.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.beheading.description");

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.beheading.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:beheading is not valid, please correct it");
            }
        }
    }
}
