package farm.the.toolstats.listener;

import farm.the.toolstats.ToolStats;
import farm.the.toolstats.util.ItemUtils;
import farm.the.toolstats.util.ToolType;
import farm.the.toolstats.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack usedItem = player.getInventory().getItemInHand();
        if (Utils.canTrack(player, usedItem.getType())) {
            ToolType usedTool = ToolType.getByMaterial(usedItem.getType());
            switch (usedTool) {
                case AXE:
                case SPADE:
                case SHEARS:
                case PICKAXE:
                    break;
                default:
                    return;
            }
            if (ToolStats.debug) {
                player.sendMessage("§7Broke §8" + e.getBlock().getType() + " §7with §8" + usedTool);
            }

            ItemUtils.updateLore(usedItem, e.getBlock().getType().name());
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHoeUse(PlayerItemDamageEvent e) {
        ToolType usedTool = ToolType.getByMaterial(e.getItem().getType());

        if (usedTool == ToolType.HOE && Utils.canTrack(e.getPlayer(), e.getItem().getType())) {
            if (ToolStats.debug) {
                e.getPlayer().sendMessage("§7Hoed a block!");
            }
            ItemUtils.updateLore(e.getItem(), "", false);
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent e) {
        if (e.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && e.getPlayer() != null) {
            Player player = e.getPlayer();

            if (Utils.canTrack(player, Material.FLINT_AND_STEEL)) {
                if (ToolStats.debug) {
                    player.sendMessage("§7Ignited a block!");
                }
                ItemUtils.updateLore(player.getItemInHand(), "", false);
                player.updateInventory();
            }
        }
    }
}
