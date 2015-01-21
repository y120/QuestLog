package com.github.y120.bukkit.questlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.y120.bukkit.Common.CC;

public final class Constants {
    //public static final String QUEST_LOG_DISPLAY_NAME = CC.G + "Quest Log";
    public static final String QUEST_LOG_LORE_TEXT = "A record of your adventure.";
    public static final String QUEST_LOG_ARCHIVE_LORE_TEXT = "An archived record of your adventure.";
    
    public static final List<String> QUEST_LOG_LORE;
    public static final List<String> QUEST_LOG_ARCHIVE_LORE;
    
    public static final int ARCHIVES_SIZE = 9;
    static {
        List<String> temp = new ArrayList<String>();
        temp.add(Constants.QUEST_LOG_LORE_TEXT);
        QUEST_LOG_LORE = Collections.unmodifiableList(temp);
    };
    
    static {
        List<String> temp = new ArrayList<String>();
        temp.add(Constants.QUEST_LOG_ARCHIVE_LORE_TEXT);
        QUEST_LOG_ARCHIVE_LORE = Collections.unmodifiableList(temp);
    }
    
    public static final String ARCHIVES_TITLE(Player p) {
        return CC.Y + p.getDisplayName() + "'s " + Config.QUEST_LOG_DISPLAY_NAME + CC.Y + " Archives";
    };
}
