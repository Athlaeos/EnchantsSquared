package me.athlaeos.enchantssquared.managers;

import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.dom.Version;

import java.util.HashMap;
import java.util.Map;

public class MinecraftVersionManager {
    private static MinecraftVersionManager manager = null;
    private Map<Version, Integer> versions = new HashMap<>();
    private Version serverVersion;
    private static EnchantsSquared plugin;

    public MinecraftVersionManager(){
        plugin = EnchantsSquared.getPlugin();
        setServerVersion();
        versions.put(Version.MINECRAFT_1_13, 1);
        versions.put(Version.MINECRAFT_1_14, 2);
        versions.put(Version.MINECRAFT_1_15, 3);
        versions.put(Version.MINECRAFT_1_16, 4);
        versions.put(Version.MINECRAFT_1_17, 5);
    }

    public static MinecraftVersionManager getInstance(){
        if (manager == null){
            manager = new MinecraftVersionManager();
        }
        return manager;
    }

    public boolean currentVersionOlderThan(Version version){
        if (serverVersion == Version.INCOMPATIBLE) return false;
        if (versions.get(serverVersion) <= versions.get(version)){
            return true;
        }
        return false;
    }

    public boolean currentVersionNewerThan(Version version){
        if (serverVersion == Version.INCOMPATIBLE) return false;
        if (versions.get(serverVersion) >= versions.get(version)){
            return true;
        }
        return false;
    }

    private void setServerVersion(){
        String version = plugin.getServer().getVersion();
        if (version.contains("1_13") || version.contains("1.13")) serverVersion = Version.MINECRAFT_1_13;
        else if (version.contains("1_14") || version.contains("1.14")) serverVersion = Version.MINECRAFT_1_14;
        else if (version.contains("1_15") || version.contains("1.15")) serverVersion = Version.MINECRAFT_1_15;
        else if (version.contains("1_16") || version.contains("1.16")) serverVersion = Version.MINECRAFT_1_16;
        else if (version.contains("1_17") || version.contains("1.17")) serverVersion = Version.MINECRAFT_1_17;
        else serverVersion = Version.INCOMPATIBLE;
    }

    public Version getServerVersion() {
        return serverVersion;
    }
}
