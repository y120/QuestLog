package com.github.y120.bukkit.questlog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.github.y120.bukkit.Common;
import com.github.y120.bukkit.Common.Debug;
import com.github.y120.bukkit.Common.Utils;
import com.github.y120.bukkit.InventorySerializer;
import com.github.y120.bukkit.questlog.quest.PlayerObjective;

public class PlayerProgress {
    private static QuestLogPlugin pl;
    private static File progFile;
    private static FileConfiguration prog;
    
    public static HashMap<String, PlayerObjective> progress;
    public static HashMap<String, Inventory> archives = new HashMap<String, Inventory>();
    
    public static void init() {
        PlayerProgress.pl = (QuestLogPlugin) Common.Plugin;
        PlayerProgress.progFile = new File(PlayerProgress.pl.getDataFolder(), "progress.yml");
    }
    
    public static void load() {
        PlayerProgress.progress = new HashMap<String, PlayerObjective>();
        
        PlayerProgress.prog = YamlConfiguration.loadConfiguration(PlayerProgress.progFile);
        
        for (String key : PlayerProgress.prog.getKeys(false))
            PlayerProgress.progress.put(key, new PlayerObjective(PlayerProgress.prog.getInt(key + ".objective"),
                    PlayerProgress.prog.getInt(key + ".progress")));
        
        for (Player p : Bukkit.getServer().getOnlinePlayers())
            PlayerProgress.loadArchives(p.getName());
    }
    
    public static void loadArchives(String name) {
        File f = new File(Utils.subFile("archives/" + name + ".yml"));
        if (!f.isFile())
            return;
        FileConfiguration archive = YamlConfiguration.loadConfiguration(f);
        Inventory i = Bukkit.getServer().createInventory(null, Constants.ARCHIVES_SIZE, "");
        try {
            i.addItem(InventorySerializer.deserialize(archive.getString("serialized")));
        } catch (InvalidConfigurationException e) {
            Common.Logger.warning("Failed to deserialize archive");
            e.printStackTrace();
        }
        PlayerProgress.archives.put(name, i);
    }
    
    public static void save() {
        PlayerProgress.prog = YamlConfiguration.loadConfiguration(PlayerProgress.progFile);
        
        for (Entry<String, PlayerObjective> e : PlayerProgress.progress.entrySet()) {
            PlayerProgress.prog.set(e.getKey() + ".objective", e.getValue().objectiveID);
            PlayerProgress.prog.set(e.getKey() + ".progress", e.getValue().progress);
        }
        
        for (String s : PlayerProgress.archives.keySet()) {
            Debug.log("Save a " + s);
            PlayerProgress.saveArchives(s);
        }
        
        try {
            PlayerProgress.prog.save(PlayerProgress.progFile);
        } catch (IOException e) {
            Common.Logger.warning("Could not save player progress!");
        }
    }
    
    public static void saveArchives(String name) {
        File f = new File(Utils.subFile("archives/" + name + ".yml"));
        FileConfiguration arch = YamlConfiguration.loadConfiguration(f);
        
        arch.set("serialized", InventorySerializer.serialize(PlayerProgress.archives.get(name)));
        
        try {
            arch.save(f);
        } catch (IOException e) {
            Common.Logger.warning("Could not save " + name + "'s archived progress!");
        }
    }
}
