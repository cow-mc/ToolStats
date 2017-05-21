package farm.the.toolstats.listener;

import farm.the.toolstats.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class ToolCombiningListener implements Listener {
    @EventHandler
    public void onToolCombine(PrepareAnvilEvent e) {
        if (e.getResult().getType() != Material.AIR) {
            AnvilInventory anvilInventory = e.getInventory();
            ItemStack slot1 = anvilInventory.getItem(0);
            ItemStack slot2 = anvilInventory.getItem(1);

            if (slot1 != null && slot2 != null
                    && slot1.getType() == slot2.getType() && ItemUtils.isTool(slot1.getType())
                    && slot1.getItemMeta().hasLore() && slot2.getItemMeta().hasLore()) {
                // both items are the same tool, both have lore entries
                ItemUtils.combineLore(slot1, slot2, e.getResult());
            }
        }
    }
}
