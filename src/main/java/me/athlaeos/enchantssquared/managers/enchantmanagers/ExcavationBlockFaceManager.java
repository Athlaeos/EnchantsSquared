package me.athlaeos.enchantssquared.managers.enchantmanagers;

import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExcavationBlockFaceManager {
    private static ExcavationBlockFaceManager manager = null;
    private Map<UUID, BlockFace> blockFaceMap = new HashMap<>();

    public ExcavationBlockFaceManager(){

    }

    public static ExcavationBlockFaceManager getInstance(){
        if (manager == null){
            manager = new ExcavationBlockFaceManager();
        }
        return manager;
    }

    public Map<UUID, BlockFace> getBlockFaceMap() {
        return blockFaceMap;
    }
}
