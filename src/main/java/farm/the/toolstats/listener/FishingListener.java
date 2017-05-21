package farm.the.toolstats.listener;

import farm.the.toolstats.ToolStats;
import farm.the.toolstats.util.ItemUtils;
import farm.the.toolstats.util.ToolType;
import farm.the.toolstats.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onFishCaught(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH && Utils.canTrack(e.getPlayer(), Material.FISHING_ROD)) {
            Player player = e.getPlayer();
            Item caught = (Item) e.getCaught();

            String trigger;

            // for details: http://minecraft.gamepedia.com/Fishing#Junk_and_treasures
            switch (caught.getItemStack().getType()) {
                case RAW_FISH:
                    // contains all fish: normal, salmon, clown, puffer
                    if (ToolStats.debug) {
                        player.sendMessage("§7Caught: §8Fish! " + caught.getItemStack());
                    }
                    trigger = "FISH";
                    break;
                case BOW:
                case ENCHANTED_BOOK:
                case FISHING_ROD:
                case NAME_TAG:
                case SADDLE:
                case WATER_LILY:
                    // we caught treasure!
                    if (ToolStats.debug) {
                        player.sendMessage("§7Caught: §8Treasure! " + caught.getItemStack());
                    }
                    trigger = "TREASURE";
                    break;
                default:
                    // welp, junk :c
                    if (ToolStats.debug) {
                        player.sendMessage("§7Caught: §8Junk :(  " + caught.getItemStack());
                    }
                    trigger = "JUNK";
            }

            ItemUtils.updateLore(ItemUtils.getUsedItem(e.getPlayer().getInventory(), ToolType.FISHING_ROD), trigger);
            e.getPlayer().updateInventory();
        }
    }
}
