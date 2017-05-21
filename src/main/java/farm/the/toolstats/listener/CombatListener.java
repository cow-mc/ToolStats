package farm.the.toolstats.listener;

import farm.the.toolstats.ToolStats;
import farm.the.toolstats.util.ItemUtils;
import farm.the.toolstats.util.ToolType;
import farm.the.toolstats.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityKill(EntityDeathEvent e) {
        EntityDamageEvent.DamageCause lastDmgCause = e.getEntity().getLastDamageCause().getCause();
        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            // no killer = no weapon to track stats for!
        } else if (lastDmgCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || lastDmgCause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            // was killed by sword, can only be used in main hand
            ItemUtils.updateWeaponLore(e, killer, ToolType.SWORD);
        } else if (lastDmgCause == EntityDamageEvent.DamageCause.PROJECTILE) {
            // was killed maybe by bow, can be used in both hands
            ItemUtils.updateWeaponLore(e, killer, ToolType.BOW);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockWithShield(EntityDamageByEntityEvent e) {
        // update target's shield stats
        if (e.getEntityType() == EntityType.PLAYER && ((Player) e.getEntity()).isBlocking() && Utils.isBlockableDamage(e.getCause())
                && Utils.canTrack((Player) e.getEntity(), Material.SHIELD)) {
            Player target = (Player) e.getEntity();

            if (ToolStats.debug) {
                target.sendMessage("Blocked: " + e.getCause());
            }

            ItemUtils.updateLore(ItemUtils.getUsedItem(target.getInventory(), ToolType.SHIELD), "", false);
            target.updateInventory();
        }
    }
}
