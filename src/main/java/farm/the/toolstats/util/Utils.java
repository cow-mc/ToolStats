package farm.the.toolstats.util;

import farm.the.toolstats.ToolStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Utils {
    /**
     * Determines if a player is allowed to track stats for the used tool or weapon
     *
     * @param player   player
     * @param itemType used item type
     * @return true if player can track, otherwise false
     */
    public static boolean canTrack(Player player, Material itemType) {
        ToolType toolType = ToolType.getByMaterial(itemType);
        return toolType != ToolType.IGNORED && ToolStats.plugin.getConfig().getBoolean("tracking." + toolType + ".enabled")
                && player.hasPermission("toolstats.track." + itemType.name().toLowerCase());
    }
}
