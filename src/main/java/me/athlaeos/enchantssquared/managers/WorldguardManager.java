package me.athlaeos.enchantssquared.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import org.bukkit.Location;

public class WorldguardManager {
    private static WorldguardManager manager = null;
    private StateFlag ENCHANTSSQUARED_DENY_ALL;
    private StateFlag ENCHANTSSQUARED_DENY_FLIGHT;
    private StateFlag ENCHANTSSQUARED_DENY_REJUVENATION;
    private StateFlag ENCHANTSSQUARED_DENY_SPEED;
    private StateFlag ENCHANTSSQUARED_DENY_JUMP;
    private StateFlag ENCHANTSSQUARED_DENY_SMELTING;
    private StateFlag ENCHANTSSQUARED_DENY_NIGHT_VISION;
    private StateFlag ENCHANTSSQUARED_DENY_WATER_BREATHING;
    private StateFlag ENCHANTSSQUARED_DENY_KINSHIP;
    private StateFlag ENCHANTSSQUARED_DENY_TORCHES;
    private StateFlag ENCHANTSSQUARED_DENY_HASTE;
    private StateFlag ENCHANTSSQUARED_DENY_VITALITY;
    private StateFlag ENCHANTSSQUARED_DENY_METABOLISM;
    private StateFlag ENCHANTSSQUARED_DENY_DEFLECT_PROJECTILES;
    private StateFlag ENCHANTSSQUARED_DENY_MAGIC_PROTECTION;
    private StateFlag ENCHANTSSQUARED_DENY_KNOCKBACK_RESISTANCE;
    private StateFlag ENCHANTSSQUARED_DENY_CURSE_HEAVY;
    private StateFlag ENCHANTSSQUARED_DENY_CURSE_BRITTLE;
    private StateFlag ENCHANTSSQUARED_DENY_CURSE_HUNGER;
    private StateFlag ENCHANTSSQUARED_DENY_WITHERING;
    private StateFlag ENCHANTSSQUARED_DENY_POISONOUS;
    private StateFlag ENCHANTSSQUARED_DENY_CRUSHING;
    private StateFlag ENCHANTSSQUARED_DENY_BLINDING;
    private StateFlag ENCHANTSSQUARED_DENY_STUNNING;
    private StateFlag ENCHANTSSQUARED_DENY_NAUSEA;
    private StateFlag ENCHANTSSQUARED_DENY_SHOCKWAVE;
    private StateFlag ENCHANTSSQUARED_DENY_AOE_ARROWS;
    private StateFlag ENCHANTSSQUARED_DENY_AOE_MINE;
    private StateFlag ENCHANTSSQUARED_DENY_WEAKENING;
    private StateFlag ENCHANTSSQUARED_DENY_SLOWNESS;
    private StateFlag ENCHANTSSQUARED_DENY_TOXIC;
    private StateFlag ENCHANTSSQUARED_DENY_VAMPIRIC;
    private StateFlag ENCHANTSSQUARED_DENY_SAPPING;
    private StateFlag ENCHANTSSQUARED_DENY_BARBARIAN;
    private StateFlag ENCHANTSSQUARED_DENY_BEHEADING;
    private StateFlag ENCHANTSSQUARED_DENY_SOULBOUND;

    public WorldguardManager(){
    }

    public static WorldguardManager getInstance(){
        if (manager == null){
            manager = new WorldguardManager();
        }
        return manager;
    }

    private StateFlag setFlag(String s){
        StateFlag newFlag = null;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag(s, true);
            registry.register(flag);
            newFlag = flag;
        } catch (Exception e) {
            Flag<?> existing = registry.get(s);
            if (existing instanceof StateFlag) {
                newFlag = (StateFlag) existing;
            } else {
                System.out.println("[EnchantsSquared] Something went wrong with WorldguardHook#setFlag for flag " + s + ", contact the plugin developer!");
            }
        }
        return newFlag;
    }

    public boolean isLocationInFlaggedRegion(Location l, String flag){
        if (WorldguardHook.getWorldguardHook().useWorldGuard()){
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(l.getWorld()));
            if (regions == null) return false;
            for (String region : regions.getRegions().keySet()) {
                if (regions.getRegion(region).contains((int)l.getX(), (int)l.getY(), (int)l.getZ())) {
                    assert regions.getRegion(region) != null;
                    for (Flag f : regions.getRegion(region).getFlags().keySet()) {
                        if (f.getName().equals(flag)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void registerFlags(){
        this.ENCHANTSSQUARED_DENY_ALL = setFlag("es-deny-all");
        this.ENCHANTSSQUARED_DENY_AOE_ARROWS = setFlag("es-deny-aoe-arrows");
        this.ENCHANTSSQUARED_DENY_BARBARIAN = setFlag("es-deny-barbarian");
        this.ENCHANTSSQUARED_DENY_BLINDING = setFlag("es-deny-blinding");
        this.ENCHANTSSQUARED_DENY_CURSE_BRITTLE = setFlag("es-deny-curse-brittle");
        this.ENCHANTSSQUARED_DENY_CURSE_HEAVY = setFlag("es-deny-curse-heavy");
        this.ENCHANTSSQUARED_DENY_CURSE_HUNGER = setFlag("es-deny-curse-hunger");
        this.ENCHANTSSQUARED_DENY_DEFLECT_PROJECTILES = setFlag("es-deny-deflect-projectiles");
        this.ENCHANTSSQUARED_DENY_AOE_MINE = setFlag("es-deny-excavation");
        this.ENCHANTSSQUARED_DENY_FLIGHT = setFlag("es-deny-flight");
        this.ENCHANTSSQUARED_DENY_HASTE = setFlag("es-deny-haste");
        this.ENCHANTSSQUARED_DENY_JUMP = setFlag("es-deny-jump");
        this.ENCHANTSSQUARED_DENY_KINSHIP = setFlag("es-deny-kinship");
        this.ENCHANTSSQUARED_DENY_KNOCKBACK_RESISTANCE = setFlag("es-deny-knockback-resistance");
        this.ENCHANTSSQUARED_DENY_MAGIC_PROTECTION = setFlag("es-deny-luck");
        this.ENCHANTSSQUARED_DENY_METABOLISM = setFlag("es-deny-metabolism");
        this.ENCHANTSSQUARED_DENY_NAUSEA = setFlag("es-deny-nausea");
        this.ENCHANTSSQUARED_DENY_NIGHT_VISION = setFlag("es-deny-night-vision");
        this.ENCHANTSSQUARED_DENY_CRUSHING = setFlag("es-deny-crushing");
        this.ENCHANTSSQUARED_DENY_POISONOUS = setFlag("es-deny-poisoning");
        this.ENCHANTSSQUARED_DENY_REJUVENATION = setFlag("es-deny-rejuvenation");
        this.ENCHANTSSQUARED_DENY_SAPPING = setFlag("es-deny-sapping");
        this.ENCHANTSSQUARED_DENY_SHOCKWAVE = setFlag("es-deny-shockwave");
        this.ENCHANTSSQUARED_DENY_SLOWNESS = setFlag("es-deny-slowing");
        this.ENCHANTSSQUARED_DENY_SMELTING = setFlag("es-deny-smelting");
        this.ENCHANTSSQUARED_DENY_SPEED = setFlag("es-deny-speed");
        this.ENCHANTSSQUARED_DENY_STUNNING = setFlag("es-deny-stunning");
        this.ENCHANTSSQUARED_DENY_TORCHES = setFlag("es-deny-torches");
        this.ENCHANTSSQUARED_DENY_TOXIC = setFlag("es-deny-toxic");
        this.ENCHANTSSQUARED_DENY_VITALITY = setFlag("es-deny-vitality");
        this.ENCHANTSSQUARED_DENY_VAMPIRIC = setFlag("es-deny-vampiric");
        this.ENCHANTSSQUARED_DENY_WATER_BREATHING = setFlag("es-deny-water-breathing");
        this.ENCHANTSSQUARED_DENY_WEAKENING = setFlag("es-deny-weakening");
        this.ENCHANTSSQUARED_DENY_WITHERING = setFlag("es-deny-withering");
        this.ENCHANTSSQUARED_DENY_BEHEADING = setFlag("es-deny-beheading");
        this.ENCHANTSSQUARED_DENY_SOULBOUND = setFlag("es-deny-soulbound");
    }
}
