package me.athlaeos.enchantssquared.main;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.configs.ConfigUpdater;
import me.athlaeos.enchantssquared.dom.Version;
import me.athlaeos.enchantssquared.enchantments.StandardGlintEnchantment;
import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.hooks.McMMOHook;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.listeners.*;
import me.athlaeos.enchantssquared.managers.CommandManager;
import me.athlaeos.enchantssquared.managers.MinecraftVersionManager;
import me.athlaeos.enchantssquared.menus.MenuListener;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public final class EnchantsSquared extends JavaPlugin {
    private static EnchantsSquared plugin = null;
    private static AnvilListener anvilListener = null;
    private static EnchantListener enchantListener = null;
    private static VillagerClickListener villagerListener = null;
    private static PlayerFishListener fishListener = null;
    private static ChunkGenListener chunkListener = null;

    @Override
    public void onEnable() {
        plugin = this;
        if (!(new File(this.getDataFolder(), "config.yml").exists())){
            this.saveResource("config.yml", false);
        }
        if (!(new File(this.getDataFolder(), "excavationblocks.yml").exists())){
            this.saveResource("excavationblocks.yml", false);
        }
        if (!(new File(this.getDataFolder(), "smeltblocksrecipes.yml").exists())){
            this.saveResource("smeltblocksrecipes.yml", false);
        }
        if (!(new File(this.getDataFolder(), "translations.yml").exists())){
            this.saveResource("translations.yml", false);
        }
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(plugin, "config.yml", configFile, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }

        WorldguardHook.getWorldguardHook().registerWorldGuard();
        JobsHook.getJobsHook().registerJobs();
        McMMOHook.getMcMMOHook().registerMcMMO();
        if (WorldguardHook.getWorldguardHook().useWorldGuard()){
            WorldguardHook.getWorldguardHook().registerFlags();
        }

        if (MinecraftVersionManager.getInstance().getServerVersion() == Version.INCOMPATIBLE){
            this.getServer().getConsoleSender().sendMessage(Utils.chat("&c[EnchantsSquared] Plugin not compatible with this version, unloading..."));
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        baguetteBoat();

        CommandManager.getInstance();
        StandardGlintEnchantment.register();
        registerListeners();
    }

    private void baguetteBoat(){
        if (ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("baguette")){
            ItemStack baguetteBoat = new ItemStack(Material.OAK_BOAT);
            ItemMeta baguetteMeta = baguetteBoat.getItemMeta();
            assert baguetteMeta != null;
            baguetteMeta.setDisplayName(Utils.chat("&fBaguette Boat"));
            baguetteBoat.setItemMeta(baguetteMeta);
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "baguette"), baguetteBoat);
            recipe.shape("B B", "BBB");
            recipe.setIngredient('B', Material.BREAD);
            this.getServer().addRecipe(recipe);
        }
    }

    private void registerListeners(){
        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(Version.MINECRAFT_1_14)){
            this.getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);
        }

        anvilListener = new AnvilListener();
        enchantListener = new EnchantListener();
        villagerListener = new VillagerClickListener();
        fishListener = new PlayerFishListener();
        chunkListener = new ChunkGenListener();
        if (!ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("disable_anvil")){
            this.getServer().getPluginManager().registerEvents(anvilListener, this);
        }
        if (!ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("disable_enchanting")) {
            this.getServer().getPluginManager().registerEvents(enchantListener, this);
        }
        if (!ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("disable_trading")) {
            this.getServer().getPluginManager().registerEvents(villagerListener, this);
        }
        if (!ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("disable_fishing")) {
            this.getServer().getPluginManager().registerEvents(fishListener, this);
        }
//        if (!ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("disable_dungeonlootgen")) {
//            this.getServer().getPluginManager().registerEvents(chunkListener, this);
//        }
        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        this.getServer().getPluginManager().registerEvents(new InteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new HealthRegenerationListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityAttackEntityListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PotionEffectListener(), this);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
    }

    public static AnvilListener getAnvilListener() {
        return anvilListener;
    }

    public static EnchantListener getEnchantListener() {
        return enchantListener;
    }

    public static VillagerClickListener getVillagerListener() {
        return villagerListener;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EnchantsSquared getPlugin(){
        return plugin;
    }
}
