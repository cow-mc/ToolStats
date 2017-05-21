package farm.the.toolstats.listener;

import farm.the.toolstats.ToolStats;
import farm.the.toolstats.util.ItemUtils;
import farm.the.toolstats.util.ToolType;
import farm.the.toolstats.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class InteractListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onSheepShear(PlayerShearEntityEvent e) {
        if (e.getEntity().getType() == EntityType.SHEEP && Utils.canTrack(e.getPlayer(), Material.SHEARS)) {
            if (ToolStats.debug) {
                e.getPlayer().sendMessage("§7Sheared a sheep.");
            }
            ItemUtils.updateLore(ItemUtils.getUsedItem(e.getPlayer().getInventory(), ToolType.SHEARS), "SHEEP");
            e.getPlayer().updateInventory();
        }
    }
}
