package farm.the.toolstats.util;

import farm.the.toolstats.ToolStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

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

    /**
     * Checks if damage is blockable with a shield
     *
     * @param cause damage cause
     * @return is blockable?
     */
    public static boolean isBlockableDamage(EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case ENTITY_ATTACK:
            case ENTITY_SWEEP_ATTACK:
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
            case THORNS:
            case PROJECTILE:
                return true;
            default:
                return false;
        }
    }
}
