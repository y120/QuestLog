package com.github.y120.bukkit.questlog.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.y120.bukkit.questlog.QuestLog;

public class InventoryClickListener implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent ice) {
        Inventory i = ice.getInventory();
        
        if (ice.getRawSlot() == SlotType.OUTSIDE.ordinal())
            return;
        
        if (i.getType() == InventoryType.PLAYER)
            return;
        
        //Debug.log("shift? " + ice.isShiftClick() + "; slot: " + ice.getRawSlot());
        
        /*if (i.getTitle().equalsIgnoreCase(Config.QUEST_LOG_DISPLAY_NAME + " Archive")) {
            // TO-DO handle clicks for archive
            // never mind: doesn't need to be handled
            //if (ice.isShiftClick() && ice.getRawSlot())
        } else {*/
        if (ice.isShiftClick() && (QuestLog.is(ice.getCurrentItem()) || QuestLog.isArchive(ice.getCurrentItem()))) {
            // on shift-click, destroy shifted item and add it back to the inventory
            
            ice.getWhoClicked().getInventory().addItem(ice.getCurrentItem());
            ice.setCurrentItem(new ItemStack(Material.AIR));
            
            // handle regular clicks
        } else if (!ice.isShiftClick() && (QuestLog.is(ice.getCursor()) || QuestLog.isArchive(ice.getCursor())))
            try {
                i.setItem(ice.getRawSlot(), new ItemStack(Material.AIR));
                ice.getWhoClicked().getInventory().addItem(ice.getCursor());
                ice.setCursor(new ItemStack(Material.AIR));
                ((Player) ice.getWhoClicked()).updateInventory();
                // TODO: remove updateInventory()
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                // eat the exception
                // caused by placing in inventory with another view open
            }
        ; // force proper formatting by java, lol.
          //}
    }
}
