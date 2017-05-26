package farm.the.toolstats.listener;

import farm.the.toolstats.util.ItemUtils;
import farm.the.toolstats.util.ToolType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityKill(EntityDeathEvent e) {
        EntityDamageEvent.DamageCause lastDmgCause = e.getEntity().getLastDamageCause().getCause();
        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            // no killer = no weapon to track stats for!
        } else if (lastDmgCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            // was killed by melee (could be a sword)
            if (ToolType.getByMaterial(killer.getItemInHand().getType()) == ToolType.SWORD) {
                // yep, it was a sword
                ItemUtils.updateWeaponLore(e, killer, ToolType.SWORD);
            }
        } else if (lastDmgCause == EntityDamageEvent.DamageCause.PROJECTILE) {
            // was killed maybe by bow
            if (ToolType.getByMaterial(killer.getItemInHand().getType()) == ToolType.BOW) {
                // yep, it was a bow
                ItemUtils.updateWeaponLore(e, killer, ToolType.BOW);
            }
        }
    }
}
