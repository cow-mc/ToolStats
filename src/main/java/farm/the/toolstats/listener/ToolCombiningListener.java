package farm.the.toolstats.listener;

import farm.the.toolstats.ToolStats;
import farm.the.toolstats.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class ToolCombiningListener implements Listener {
    @EventHandler
    public void onToolCombine(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.ANVIL
                || (!e.isShiftClick() && e.getSlotType() != InventoryType.SlotType.CRAFTING)) {
            return;
        }

        AnvilInventory anvilInventory = (AnvilInventory) e.getInventory();

        Bukkit.getScheduler().runTask(ToolStats.plugin, () -> {
            ItemStack resultItem = anvilInventory.getItem(2);
            if (resultItem != null && resultItem.getType() != Material.AIR) {
                ItemStack slot1 = anvilInventory.getItem(0);
                ItemStack slot2 = anvilInventory.getItem(1);

                if (slot1 != null && slot2 != null
                        && slot1.getType() == slot2.getType() && ItemUtils.isTool(slot1.getType())
                        && slot1.getItemMeta().hasLore() && slot2.getItemMeta().hasLore()) {
                    // both items are the same tool, both have lore entries
                    ItemUtils.combineLore(slot1, slot2, resultItem);
                }
            }
        });
    }
}
