package farm.the.toolstats.util;

import farm.the.toolstats.ToolStats;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ItemUtils {
    /**
     * Tries to find an item of type 'toolType' in player's main or offhand.
     *
     * @param inventory player inventory
     * @param toolType  tool type that should be looked for
     * @return used item if found, otherwise null
     */
    public static ItemStack getUsedItem(PlayerInventory inventory, ToolType toolType) {
        ItemStack usedItem = inventory.getItemInMainHand();
        if (ToolType.getByMaterial(usedItem.getType()) == toolType) {
            // toolType found in main hand
            return usedItem;
        }

        if (toolType.canBeInOffhand()) {
            usedItem = inventory.getItemInOffHand();
            if (ToolType.getByMaterial(usedItem.getType()) == toolType) {
                // toolType found in offhand
                return usedItem;
            }
        }
        // toolType not found
        return null;
    }

    /**
     * Update the lore and add the triggered statistic to the item
     *
     * @param item    item that should be updated
     * @param trigger trigger
     */
    public static void updateLore(ItemStack item, String trigger) {
        updateLore(item, trigger, true);
    }

    /**
     * Update the lore and add the triggered statistic to the item
     *
     * @param item         item that should be updated
     * @param trigger      trigger
     * @param forceTrigger true, if trigger has to be config file, otherwise false
     */
    public static void updateLore(ItemStack item, String trigger, boolean forceTrigger) {
        ToolType toolType = ToolType.getByMaterial(item.getType());
        Validate.isTrue(toolType != ToolType.IGNORED, "First make sure item is valid tool via Utils#canTrack");

        String defaultLoreLine = ConfigUtils.getDefaultLoreLine(toolType, trigger, forceTrigger);

        if (defaultLoreLine == null) {
            // item shouldn't be tracked, don't change lore
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore;

        loreCheck:
        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
            boolean hasAnyToolStatsLore = false;

            for (int i = 0; i < lore.size(); i++) {
                String loreLine = lore.get(i);
                if (loreLine.startsWith(defaultLoreLine)) {
                    int counter = 1;
                    try {
                        counter += Integer.parseInt(loreLine.substring(defaultLoreLine.length()));
                    } catch (IllegalArgumentException e) {
                        ToolStats.log.log(Level.WARNING, "Corrupted lore: " + loreLine, e);
                    }
                    lore.set(i, defaultLoreLine + counter);
                    itemMeta.setLore(lore);
                    break loreCheck;
                } else if (loreLine.startsWith(ConfigUtils.LORE_PREFIX)) {
                    hasAnyToolStatsLore = true;
                }
            }
            if (!hasAnyToolStatsLore) {
                // if we end up here, the tool has lore entries, but not a single entry from ToolStats
                lore.add(getFirstLine(toolType));
            }
            // since the lore has no entry for the current trigger, add a new line with start value 1
            lore.add(defaultLoreLine + 1);
        } else {
            // tool has no lore yet
            lore = new ArrayList<>();
            lore.add(getFirstLine(toolType));
            lore.add(defaultLoreLine + 1);
        }

        itemMeta.setLore(sortStats(lore, toolType));
        item.setItemMeta(itemMeta);
    }

    /**
     * Update lore of attack weapons
     *
     * @param e          entity death event
     * @param killer     killer of the entity
     * @param weaponType used weapon type
     */
    public static void updateWeaponLore(EntityDeathEvent e, Player killer, ToolType weaponType) {
        ItemStack murderWeapon = getUsedItem(killer.getInventory(), weaponType);

        if (murderWeapon != null && Utils.canTrack(killer, murderWeapon.getType())) {
            if (ToolStats.debug) {
                killer.sendMessage("§7Killed §8" + e.getEntityType() + " §7with §8" + weaponType);
            }

            List<String> triggers = ConfigUtils.getTriggers(weaponType);
            String trigger = e.getEntityType().toString();
            if (triggers.contains(trigger)) {
                // found matching trigger!
            } else if (e.getEntityType() == EntityType.PLAYER && triggers.contains("players")) {
                trigger = "players";
            } else if (e.getEntity() instanceof Zombie && triggers.contains(EntityType.ZOMBIE.name())) {
                trigger = EntityType.ZOMBIE.name();
            } else if (e.getEntity() instanceof Skeleton && triggers.contains(EntityType.SKELETON.name())) {
                trigger = EntityType.SKELETON.name();
            } else if (e.getEntity() instanceof Spider && triggers.contains(EntityType.SPIDER.name())) {
                trigger = EntityType.SPIDER.name();
            } else if (e.getEntity() instanceof Slime) {
                if (triggers.contains(EntityType.SLIME.name())) {
                    trigger = EntityType.SLIME.name();
                } else if (triggers.contains("hostile")) {
                    // officially slimes are neither passive nor hostile
                    trigger = "hostile";
                } else {
                    return;
                }
            } else if (e.getEntityType() == EntityType.SHULKER) {
                trigger = "hostile";
            } else if (triggers.contains("passive") &&
                    (e.getEntityType() == EntityType.SQUID
                            || e.getEntityType() == EntityType.BAT
                            || e.getEntityType() == EntityType.VILLAGER
                            || e.getEntityType() == EntityType.SNOWMAN
                            || e.getEntityType() == EntityType.IRON_GOLEM)) {
                // none of these are 'animals', but are passive
                trigger = "passive";
            } else if (e.getEntity() instanceof Monster && triggers.contains("hostile")) {
                trigger = "hostile";
            } else if (e.getEntity() instanceof Animals && triggers.contains("passive")) {
                trigger = "passive";
            } else if (triggers.contains("miscellaneous")) {
                trigger = "miscellaneous";
            } else {
                return;
            }

            updateLore(murderWeapon, trigger, false);
            killer.updateInventory();
        }
    }

    /**
     * Remove a line of statistics from the item lore
     *
     * @param item         item
     * @param statToRemove the (partial) stat
     * @return true if a line was removed, otherwise false
     */
    public static boolean removeStat(ItemStack item, String statToRemove) {
        ItemMeta itemMeta = item.getItemMeta();
        if (!itemMeta.hasLore()) {
            return false;
        }

        List<String> lore = itemMeta.getLore();
        boolean removedStat = lore.removeIf(line -> {
            if (line.startsWith(ConfigUtils.LORE_PREFIX)) {
                String colorlessLine = ChatColor.stripColor(line);
                if (colorlessLine.startsWith(statToRemove)) {
                    return true;
                }
            }
            return false;
        });
        if (removedStat) {
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        return removedStat;
    }

    /**
     * Removes all lore lines from an item that belong to the ToolStats plugin
     *
     * @param item the item
     * @return true if at least one line was removed, otherwise false
     */
    public static boolean clearStats(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            boolean removed = lore.removeIf(s -> s.startsWith(ConfigUtils.LORE_PREFIX));
            if (removed) {
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                return true;
            }
        }
        return false;
    }

    /**
     * Combines lore of item1 and item2 and adds up matching tool stats
     *
     * @param item1  on item item
     * @param item2  another item item
     * @param result result item that receives the combined lore
     */
    public static void combineLore(ItemStack item1, ItemStack item2, ItemStack result) {
        ToolType toolType = ToolType.getByMaterial(item1.getType());

        List<String> combinedLore = new ArrayList<>(item1.getItemMeta().getLore());
        combinedLore.addAll(item2.getItemMeta().getLore());

        Map<String, Integer> toolStats = new LinkedHashMap<>();

        for (String line : combinedLore) {
            if (line.startsWith(ConfigUtils.LORE_PREFIX) && !line.equals(getFirstLine(toolType))) {
                int counter = extractCounterFromLore(line);
                String trigger = extractTriggerFromLore(line, counter);
                if (toolStats.containsKey(trigger)) {
                    counter += toolStats.get(trigger);
                }
                toolStats.put(trigger, counter);
            }
        }

        ItemMeta resultItemMeta = result.getItemMeta();
        List<String> resultLore = resultItemMeta.getLore();

        // remove old ToolStats entries and add new default first line
        resultLore.removeIf(entry -> entry.startsWith(ConfigUtils.LORE_PREFIX));
        resultLore.add(getFirstLine(toolType));

        for (Map.Entry<String, Integer> stat : toolStats.entrySet()) {
            resultLore.add(stat.getKey() + stat.getValue());
        }

        resultItemMeta.setLore(sortStats(resultLore, toolType));
        result.setItemMeta(resultItemMeta);
    }

    public static boolean isTool(Material type) {
        return ToolType.getByMaterial(type) != ToolType.IGNORED;
    }

    private static String getFirstLine(ToolType toolType) {
        return ConfigUtils.LORE_PREFIX + ConfigUtils.get("tracking." + toolType + ".first-line");
    }

    private static int extractCounterFromLore(String entry) {
        String colorlessEntry = ChatColor.stripColor(entry);
        try {
            int colonIndex = colorlessEntry.indexOf(": ");
            if (colonIndex != -1) {
                return Integer.parseInt(colorlessEntry.substring(colonIndex + 2));
            } else {
                return Integer.parseInt(colorlessEntry);
            }
        } catch (NumberFormatException e) {
            ToolStats.log.log(Level.WARNING, "Tried to extract counter from broken lore entry: " + entry, e);
            return 0;
        }
    }

    private static String extractTriggerFromLore(String entry, int counter) {
        String counterColor = ConfigUtils.get("tracking.color.counter");
        return entry.substring(0, entry.indexOf(counterColor + counter) + counterColor.length());
    }

    /**
     * Sorts lore:
     * <ol><li>non-ToolStats-lore lines</li>
     * <li>first "head" line of the toolType</li>
     * <li>all tool stats sorted by their counter</li></ol>
     *
     * @param lore     unsorted lore
     * @param toolType type of tool
     * @return sorted lore
     */
    private static List<String> sortStats(List<String> lore, ToolType toolType) {
        lore.sort((s1, s2) -> {
            boolean isS1ToolStat = s1.startsWith(ConfigUtils.LORE_PREFIX);
            boolean isS2ToolStat = s2.startsWith(ConfigUtils.LORE_PREFIX);

            if (!isS1ToolStat && !isS2ToolStat) {
                return 0;
            } else if (!isS1ToolStat) {
                return -1;
            } else if (!isS2ToolStat) {
                return 1;
            } else if (s1.equals(getFirstLine(toolType))) {
                return -1;
            } else if (s2.equals(getFirstLine(toolType))) {
                return 1;
            } else {
                return Integer.compare(extractCounterFromLore(s2), extractCounterFromLore(s1));
            }
        });
        return lore;
    }
}
