package farm.the.toolstats.util;

import farm.the.toolstats.ToolStats;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

public class ConfigUtils {
    static final String LORE_PREFIX = ChatColor.translateAlternateColorCodes('&', "&m&o&o&r");

    /**
     * Get formatted message from config.yml
     *
     * @param key config key
     * @return formatted config entry
     */
    public static String get(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                ToolStats.plugin.getConfig().getString(key, "&c✘ &7Missing entry in config.yml: " + key));
    }

    /**
     * Get formatted and placeholder-less message from config.yml
     *
     * @param key     config key
     * @param search  search for this placeholder
     * @param replace replace the placeholder with this value
     * @return formatted config entry
     */
    public static String get(String key, String search, String replace) {
        return StringUtils.replace(get(key), search, replace);
    }

    /**
     * Change str to Title Case
     *
     * @param str input string
     * @return title case'd string
     */
    public static String toFancyCase(String str) {
        return WordUtils.capitalizeFully(str.replace('_', ' '));
    }

    /**
     * Get word to confirm the clearing of tool stats
     *
     * @return word to confirm toolstat clear
     */
    public static String getClearConfirmWord() {
        String clearConfirmWord = ToolStats.plugin.getConfig().getString("toolstats-command.clear.confirm-with");
        int firstSpace = clearConfirmWord.indexOf(' ');
        return (firstSpace == -1) ? clearConfirmWord : clearConfirmWord.substring(0, firstSpace);
    }

    /**
     * Get all blocks, entities, ... that should be tracked in tool stats
     *
     * @param toolType type of tool
     * @return triggers that should be tracked
     */
    static List<String> getTriggers(ToolType toolType) {
        return ToolStats.plugin.getConfig().getStringList("tracking." + toolType + ".trigger");
    }

    /**
     * Get the default lore line without the actual counter value
     *
     * @param toolType     type of tool
     * @param trigger      trigger
     * @param forceTrigger true, if trigger has to be config file, otherwise false
     * @return lore line 'prefix', null if invalid trigger
     */
    static String getDefaultLoreLine(ToolType toolType, String trigger, boolean forceTrigger) {
        StringBuilder loreLine = new StringBuilder(LORE_PREFIX);

        trigger = getMatchingTrigger(toolType, trigger, forceTrigger);
        if (trigger == null) {
            return null;
        }

        if (!trigger.isEmpty()) {
            loreLine.append(get("tracking.color.trigger")).append(toFancyCase(trigger)).append(get("tracking.color.colon")).append(": ");
        }
        loreLine.append(get("tracking.color.counter"));

        return loreLine.toString();
    }

    /**
     * Tries to find a matching trigger to the toolType. Also generalizes e. g. logs and leaves types
     *
     * @param toolType     type of tool
     * @param trigger      trigger
     * @param forceTrigger true, if trigger has to be config file, otherwise false
     * @return matching trigger, null if none is found
     */
    private static String getMatchingTrigger(ToolType toolType, String trigger, boolean forceTrigger) {
        if (!forceTrigger) {
            return trigger;
        }
        List<String> triggers = getTriggers(toolType);
        if (triggers.isEmpty()) {
            return null;
        }

        switch (trigger) {
            case "LEAVES_2":
                // special case: acacia leaves
                trigger = Material.LEAVES.name();
                break;
            case "LOG_2":
                // special case: acacia and dark oak logs
                trigger = Material.LOG.name();
                break;
            case "GLOWING_REDSTONE_ORE":
                // special case: recently touched redstone ore
                trigger = Material.REDSTONE_ORE.name();
                break;
        }

        if (triggers.contains(trigger)) {
            // trigger found!
        } else if (triggers.contains("miscellaneous")) {
            // misc trigger found
            trigger = "miscellaneous";
        } else {
            // no matching trigger! not updating lore!
            return null;
        }
        return trigger;
    }
}
