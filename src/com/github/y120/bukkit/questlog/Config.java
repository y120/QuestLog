package com.github.y120.bukkit.questlog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.github.y120.bukkit.Common;
import com.github.y120.bukkit.Common.CC;
import com.github.y120.bukkit.Common.Debug;
import com.github.y120.bukkit.Common.Utils;
import com.github.y120.bukkit.questlog.quest.ApproachObjective;
import com.github.y120.bukkit.questlog.quest.CollectObjective;
import com.github.y120.bukkit.questlog.quest.KillObjective;
import com.github.y120.bukkit.questlog.quest.Objective;

public class Config {
    private static final int configVersion = 2;
    private static final int lastBreak = 0;
    
    public static String QUEST_LOG_DISPLAY_NAME = CC.G + "Quest Log";
    public static List<Objective> objectives;
    
    private static QuestLogPlugin pl;
    private static File cfgFile;
    private static FileConfiguration cfg;
    
    private static void check(FileConfiguration cfg, String key) {
        if (!cfg.contains(key))
            Common.Logger.warning("Objective key " + key + " is expected but not present");
    }
    
    public static void init() {
        Config.pl = (QuestLogPlugin) Common.Plugin;
        Config.pl.saveDefaultConfig();
        Config.cfgFile = new File(Config.pl.getDataFolder(), "config.yml");
    }
    
    public static void load() {
        Config.cfg = YamlConfiguration.loadConfiguration(Config.cfgFile);
        
        if (Config.cfg.getInt("config-version", Config.configVersion) > Config.configVersion
                || Config.cfg.getInt("config-version", Config.configVersion) < Config.configVersion
                && Config.cfg.getInt("config-version", Config.configVersion) < Config.lastBreak) {
            Common.Logger.severe("Your configuration file is not compatible with this version of the plugin.");
            Common.Logger.severe("Your version: " + Config.cfg.getInt("config-version") + "; this version: "
                    + Config.configVersion);
            Common.Logger.severe("Please see the plugin page for more details. QuestLog will now be disabled.");
            Bukkit.getPluginManager().disablePlugin(Common.Plugin);
        } else if (Config.cfg.getInt("config-version", Config.configVersion) < Config.configVersion) {
            Common.Logger.warning("Your configuration file is outdated but is still backwards-compatible.");
            Common.Logger.warning("Your version: " + Config.cfg.getInt("config-version") + "; this version: "
                    + Config.configVersion);
            Common.Logger.warning("New features may have been added, or functionality may have changed minorly. "
                    + "Please see the plugin page for more details.");
        }
        
        Config.QUEST_LOG_DISPLAY_NAME = Config.cfg.getString("questLogName", Config.QUEST_LOG_DISPLAY_NAME);
        
        Config.objectives = new ArrayList<Objective>();
        for (int i = 0; Config.cfg.contains("objectives." + i); i++) {
            String obji = "objectives." + i + ".";
            
            Config.check(Config.cfg, obji + "type");
            Config.check(Config.cfg, obji + "text");
            String s = Config.cfg.getString(obji + "type").toLowerCase();
            String t = Utils.join(Config.cfg.getStringList(obji + "text"), "\n");
            Debug.log("> " + t);
            switch (s) {
                case "approach":
                    Config.check(Config.cfg, obji + "x");
                    Config.check(Config.cfg, obji + "y");
                    Config.check(Config.cfg, obji + "z");
                    Config.check(Config.cfg, obji + "r");
                    Config.objectives.add(new ApproachObjective(Config.cfg.getInt(obji + "x"), Config.cfg.getInt(obji
                            + "y"), Config.cfg.getInt(obji + "z"), Config.cfg.getInt(obji + "r"), t));
                    break;
                case "kill":
                    Config.check(Config.cfg, obji + "entity");
                    Config.check(Config.cfg, obji + "amount");
                    Config.objectives.add(new KillObjective(EntityType.fromName(Config.cfg.getString(obji + "entity")),
                            Config.cfg.getInt(obji + "amount"), t));
                    break;
                case "collect":
                    Config.check(Config.cfg, obji + "item");
                    Config.check(Config.cfg, obji + "amount");
                    Config.objectives.add(new CollectObjective(Material.getMaterial(Config.cfg.getInt(obji + "item")),
                            Config.cfg.getInt(obji + "amount"), t));
                    break;
                default:
                    Common.Logger.warning("Unknown objective type " + s);
            }
            
            if (Config.cfg.contains(obji + "commands")) {
                Debug.log("loading commands for " + obji);
                Config.objectives.get(Config.objectives.size() - 1).commands = Config.cfg.getStringList(obji
                        + "commands");
            }
        }
    }
}
