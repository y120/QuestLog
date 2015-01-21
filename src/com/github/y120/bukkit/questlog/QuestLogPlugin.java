package com.github.y120.bukkit.questlog;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.y120.bukkit.Common;
import com.github.y120.bukkit.Common.CC;
import com.github.y120.bukkit.Common.Debug;
import com.github.y120.bukkit.Common.Utils;
import com.github.y120.bukkit.questlog.listeners.EntityDeathListener;
import com.github.y120.bukkit.questlog.listeners.InventoryClickListener;
import com.github.y120.bukkit.questlog.listeners.PlayerDeathListener;
import com.github.y120.bukkit.questlog.listeners.PlayerDropItemListener;
import com.github.y120.bukkit.questlog.listeners.PlayerInteractListener;
import com.github.y120.bukkit.questlog.listeners.PlayerJoinListener;
import com.github.y120.bukkit.questlog.listeners.PlayerMoveListener;
import com.github.y120.bukkit.questlog.listeners.PlayerPickupItemListener;
import com.github.y120.bukkit.questlog.listeners.PlayerRespawnListener;

public class QuestLogPlugin extends JavaPlugin {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("questlog"))
            return false;
        
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage("There are " + Config.objectives.size() + " total objectives.");
            if (!QuestLog.isInInventory(p.getInventory())) {
                p.getInventory().addItem(QuestLog.make());
                p.sendMessage("Your Quest Log was missing for some reason. Please report this.");
            }
            /*if (args.length > 1) {
                int n = QuestLog.fromInventory(p.getInventory());
                ItemStack is = p.getInventory().getItem(n);
                BookMeta bm = (BookMeta) is.getItemMeta();
                for (int i = 0; i < Integer.parseInt(args[1]); i++)
                    bm.addPage("\n");
                is.setItemMeta(bm);
                p.getInventory().setItem(n, is);
                
                if (args.length > 2)
                    PlayerProgress.progress.put(p.getName(), new PlayerObjective(0, 0));
            }*/
        } else {
            if (args.length == 0)
                sender.sendMessage("Loaded " + Config.objectives.size() + " objectives.");
            else if (args[0].equals("debug")) {
                Debug.setEnabled(!Debug.isEnabled());
                sender.sendMessage("Beware of spam: debug mode " + Utils.strEnabled(Debug.isEnabled()));
            } else if (args[0].equals("msg")) {
                if (args.length < 3) {
                    sender.sendMessage("Not enough arguments given!");
                    return true;
                }
                
                Player p = Bukkit.getPlayerExact(args[1]);
                if (p == null || !p.isOnline()) {
                    sender.sendMessage(args[1] + " is not online!");
                    return true;
                }
                
                String s = "";
                for (int i = 2; i < args.length; i++)
                    s += " " + args[i];
                p.sendMessage(CC.Y + "[QuestLog] " + CC.RES + s.substring(1));
            }
            ; // format
        }
        
        return true;
    }
    
    @Override
    public void onDisable() {
        PlayerProgress.save();
    }
    
    @Override
    public void onEnable() {
        Common.init(this);
        //Debug.setEnabled(true);
        
        PluginManager pm = this.getServer().getPluginManager();
        
        // block item pickup/drop/move
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new PlayerDropItemListener(), this);
        
        // give item back
        pm.registerEvents(new PlayerRespawnListener(), this);
        
        // give the book if they don't have it upon joining
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        
        // check for open archives
        pm.registerEvents(new PlayerInteractListener(), this);
        
        // objective listeners
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
        
        Config.init();
        Config.load();
        
        PlayerProgress.init();
        PlayerProgress.load();
    }
}
