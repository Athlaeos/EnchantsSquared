package me.athlaeos.enchantssquared.main;

import me.athlaeos.enchantssquared.dom.Version;
import me.athlaeos.enchantssquared.hooks.JobsHook;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.listeners.*;
import me.athlaeos.enchantssquared.managers.CommandManager;
import me.athlaeos.enchantssquared.managers.MinecraftVersionManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EnchantsSquared extends JavaPlugin {
    private static EnchantsSquared plugin = null;
    private static AnvilListener anvilListener = null;
    private static EnchantListener enchantListener = null;

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
        WorldguardHook.getWorldguardHook().registerWorldGuard();
        JobsHook.getJobsHook().registerJobs();
        if (WorldguardHook.getWorldguardHook().useWorldGuard()){
            WorldguardHook.getWorldguardHook().registerFlags();
        }

        if (MinecraftVersionManager.getInstance().getServerVersion() == Version.INCOMPATIBLE){
            this.getServer().getConsoleSender().sendMessage(Utils.chat("&c[EnchantsSquared] Plugin not compatible with this version, unloading..."));
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        CommandManager.getInstance();

        registerListeners();
    }

    private void registerListeners(){


        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(Version.MINECRAFT_1_14)){
            this.getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);
        }

        anvilListener = new AnvilListener();
        enchantListener = new EnchantListener();
        this.getServer().getPluginManager().registerEvents(anvilListener, this);
        this.getServer().getPluginManager().registerEvents(enchantListener, this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockInteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new HealthRegenerationListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityAttackEntityListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new PotionEffectListener(), this);
    }

    public static AnvilListener getAnvilListener() {
        return anvilListener;
    }

    public static EnchantListener getEnchantListener() {
        return enchantListener;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EnchantsSquared getPlugin(){
        return plugin;
    }
}
