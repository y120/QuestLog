package com.github.y120.bukkit.questlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.y120.bukkit.Common;
import com.github.y120.bukkit.Common.CC;
import com.github.y120.bukkit.Common.Debug;
import com.github.y120.bukkit.questlog.quest.KillObjective;
import com.github.y120.bukkit.questlog.quest.Objective;
import com.github.y120.bukkit.questlog.quest.PlayerObjective;

// Utility Functions
public class QuestLog {
    public static final class AppendResult {
        public ItemStack questLog;
        public ItemStack overflow;
        
        public AppendResult(ItemStack questLog, ItemStack overflow) {
            this.questLog = questLog;
            this.overflow = overflow;
        }
    }
    
    public static void addProgress(Player p, int n) {
        if (!PlayerProgress.progress.containsKey(p.getName()))
            PlayerProgress.progress.put(p.getName(), new PlayerObjective(0, 0));
        
        PlayerObjective po = PlayerProgress.progress.get(p.getName());
        po.progress += n;
        PlayerProgress.progress.put(p.getName(), po);
    }
    
    public static AppendResult append(ItemStack is, String s) {
        if (!QuestLog.is(is)) {
            Common.Logger.severe("Tried to append to non-QuestLog");
            return new AppendResult(null, null);
        }
        
        ItemStack other = null;
        
        BookMeta bm = (BookMeta) is.getItemMeta();
        int pc = bm.getPageCount();
        
        if (pc == 0) { // only for newly created books
            bm.addPage("");
            pc++;
        } else
            s = "\n&8---------&0\n" + s; // add a separator line between entries
        s = s.replaceAll("&([0-9a-flmnor])", "\u00a7$1"); // fix formatting codes
        
        List<String> temp = bm.getPages();
        List<String> pages = new ArrayList<String>();
        pages.addAll(temp);
        Collections.reverse(pages);
        
        String st = pages.get(pc - 1), st2 = "";
        st += s;
        
        // find an acceptable point to break pages
        // TODO: make this calculate exact page widths
        int i, n = 0;
        for (i = 0; i < 256 && n < 14 && i < st.length(); i++)
            if (st.charAt(i) == '\n')
                n++;
        if (n >= 14 || i >= 256) {
            if (i >= 256)
                for (i = 256; i >= 0 && st.charAt(i) != '\n'; i--)
                    ;
            st2 = st.substring(i);
            st = st.substring(0, i);
        }
        
        //bm.setPage(pc, st.trim() + (st2.isEmpty() ? "\n" : ""));
        pages.set(pc - 1, st.trim());
        
        // if we can add a page, go ahead
        if (!st2.isEmpty() && pc < 50)
            //bm.addPage(st2.trim());
            pages.add(st2.trim());
        
        Collections.reverse(pages);
        bm.setPages(pages);
        is.setItemMeta(bm);
        
        // if not, we must make our old book an archive
        if (!st2.isEmpty() && pc >= 50) {
            other = is;
            is = QuestLog.make();
            bm = (BookMeta) is.getItemMeta();
            bm.addPage(st2.trim());
            is.setItemMeta(bm);
        }
        
        return new AppendResult(is, other);
    }
    
    public static void checkCurrentObjective(Player p) {
        Objective o = QuestLog.getCurrentObjective(p);
        if (o == null)
            return;
        else if (o instanceof KillObjective
                && ((KillObjective) o).n <= PlayerProgress.progress.get(p.getName()).progress)
            QuestLog.completeCurrentObjective(p);
    }
    
    public static void completeCurrentObjective(Player p) {
        Objective o = QuestLog.getCurrentObjective(p);
        if (o == null)
            return;
        
        p.sendMessage("You have completed an objective and your " + Config.QUEST_LOG_DISPLAY_NAME + CC.RES
                + " has been updated!");
        
        int n = QuestLog.fromInventory(p.getInventory());
        AppendResult ar = QuestLog.append(p.getInventory().getItem(n), QuestLog.replaceVarsPlayer(o.flavourText, p));
        p.getInventory().setItem(n, ar.questLog);
        
        // did we have to create a new quest log?
        if (ar.overflow != null) {
            p.sendMessage(CC.A + "In addition, you have managed to fill out an entire " + Config.QUEST_LOG_DISPLAY_NAME
                    + CC.A + "! Your old " + Config.QUEST_LOG_DISPLAY_NAME + CC.A
                    + " has been added to your archives (accessible by left-clicking with your new one).");
            Inventory i = null;
            if (PlayerProgress.archives.containsKey(p.getName()))
                i = PlayerProgress.archives.get(p.getName());
            else
                i = Bukkit.getServer().createInventory(null, 9,
                        p.getDisplayName() + "'s " + Config.QUEST_LOG_DISPLAY_NAME + CC.GR + " Archives");
            
            ItemStack is = ar.overflow;
            BookMeta bm = (BookMeta) is.getItemMeta();
            bm.setAuthor(p.getName());
            bm.setDisplayName(Config.QUEST_LOG_DISPLAY_NAME + CC.GR + " [Volume "
                    + (i.all(Material.WRITTEN_BOOK).size() + 1) + "]");
            bm.setLore(Constants.QUEST_LOG_ARCHIVE_LORE);
            is.setItemMeta(bm);
            i.addItem(is);
            
            PlayerProgress.archives.put(p.getName(), i);
        }
        
        // execute commands linked to objective
        if (o.commands != null)
            for (String s : o.commands) {
                String cmd = QuestLog.replaceVarsPlayer(s.substring(1), p);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                Debug.log("run: " + cmd);
            }
        
        if (PlayerProgress.progress.containsKey(p.getName()))
            PlayerProgress.progress.put(p.getName(), new PlayerObjective(
                    PlayerProgress.progress.get(p.getName()).objectiveID + 1, 0));
        else
            PlayerProgress.progress.put(p.getName(), new PlayerObjective(1, 0));
    }
    
    public static int fromInventory(Inventory i) {
        @SuppressWarnings("unchecked")
        HashMap<Integer, ItemStack> books = (HashMap<Integer, ItemStack>) i.all(Material.WRITTEN_BOOK.getId());
        for (Entry<Integer, ItemStack> e : books.entrySet())
            if (QuestLog.is(e.getValue()))
                return e.getKey();
        return -1;
    }
    
    public static Inventory getArchives(Player p) {
        if (QuestLog.hasArchives(p)) {
            Inventory i = Bukkit.getServer().createInventory(null, Constants.ARCHIVES_SIZE, Constants.ARCHIVES_TITLE(p));
            i.setContents(PlayerProgress.archives.get(p.getName()).getContents());
            return i;
        }
        return null;
    }
    
    public static Objective getCurrentObjective(Player p) {
        if (p == null)
            return null;
        int n = 0;
        if (PlayerProgress.progress.containsKey(p.getName()))
            n = PlayerProgress.progress.get(p.getName()).objectiveID;
        if (n >= Config.objectives.size())
            return null;
        return Config.objectives.get(n);
    }
    
    public static boolean hasArchives(Player p) {
        return PlayerProgress.archives.containsKey(p.getName());
    }
    
    public static boolean is(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        return is.getType() == Material.WRITTEN_BOOK && im.hasLore()
                && im.getLore().contains(Constants.QUEST_LOG_LORE_TEXT);
    }
    
    public static boolean isArchive(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        return is.getType() == Material.WRITTEN_BOOK && im.hasLore()
                && im.getLore().contains(Constants.QUEST_LOG_ARCHIVE_LORE_TEXT);
    }
    
    public static boolean isInInventory(Inventory i) {
        return QuestLog.fromInventory(i) != -1;
    }
    
    public static ItemStack make() {
        ItemStack result = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) result.getItemMeta();
        bm.setDisplayName(Config.QUEST_LOG_DISPLAY_NAME);
        bm.setLore(Constants.QUEST_LOG_LORE);
        result.setItemMeta(bm);
        return result;
    }
    
    public static String replaceVarsPlayer(String s, Player p) {
        return s.replaceAll("%[pP]%", p.getName()).replaceAll("%[dD][pP]%", p.getDisplayName());
    }
    
    public static void setProgress(Player p, int n) {
        if (!PlayerProgress.progress.containsKey(p.getName()))
            PlayerProgress.progress.put(p.getName(), new PlayerObjective(0, 0));
        
        PlayerObjective po = PlayerProgress.progress.get(p.getName());
        po.progress = n;
        PlayerProgress.progress.put(p.getName(), po);
    }
}
