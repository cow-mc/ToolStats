package farm.the.toolstats.command;

import farm.the.toolstats.util.ConfigUtils;
import farm.the.toolstats.util.ItemUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToolStatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command is for players only!");
        } else if (args.length == 0) {
            sendHelp(sender);
        } else if (args[0].equals("remove")) {
            removeToolStat(sender, args);
        } else if (args[0].equals("clear")) {
            clearToolStats(sender, args);
        } else {
            sendHelp(sender);
        }
        return true;
    }

    private void removeToolStat(CommandSender sender, String[] args) {
        if (!sender.hasPermission("toolstats.command.remove")) {
            sender.sendMessage(ConfigUtils.get("toolstats-command.no-permissions"));
        } else if (args.length == 2) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            String statToRemove = ConfigUtils.toFancyCase(args[1]);

            if (!ItemUtils.isTool(item.getType())) {
                player.sendMessage(ConfigUtils.get("toolstats-command.invalid-tool"));
            } else if (ItemUtils.removeStat(item, statToRemove)) {
                player.updateInventory();
                player.sendMessage(ConfigUtils.get("toolstats-command.remove.success", "%stat%", statToRemove));
            } else {
                player.sendMessage(ConfigUtils.get("toolstats-command.remove.stat-not-found", "%stat%", statToRemove));
            }
        } else {
            sender.sendMessage(ConfigUtils.get("toolstats-command.remove.usage"));
        }
    }

    private void clearToolStats(CommandSender sender, String[] args) {
        if (!sender.hasPermission("toolstats.command.clear")) {
            sender.sendMessage(ConfigUtils.get("toolstats-command.no-permissions"));
        } else if (args.length == 2 && args[1].equals(ConfigUtils.getClearConfirmWord())) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (!ItemUtils.isTool(item.getType())) {
                player.sendMessage(ConfigUtils.get("toolstats-command.invalid-tool"));
            } else if (ItemUtils.clearStats(item)) {
                player.updateInventory();
                player.sendMessage(ConfigUtils.get("toolstats-command.clear.success"));
            } else {
                player.sendMessage(ConfigUtils.get("toolstats-command.clear.no-stats"));
            }
        } else {
            sender.sendMessage(ConfigUtils.get("toolstats-command.clear.usage"));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ConfigUtils.get("toolstats-command.usage"));
    }
}
