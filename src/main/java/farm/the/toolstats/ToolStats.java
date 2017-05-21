package farm.the.toolstats;

import farm.the.toolstats.command.ToolStatsCommand;
import farm.the.toolstats.listener.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class ToolStats extends JavaPlugin {
    public static ToolStats plugin;
    public static Logger log;
    public static boolean debug;

    @Override
    public void onEnable() {
        plugin = this;
        log = getLogger();
        loadConfig();

        getCommand("toolstats").setExecutor(new ToolStatsCommand());

        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new ToolCombiningListener(), this);
        getServer().getPluginManager().registerEvents(new FishingListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
    }

    @Override
    public void onDisable() {
    }

    private void loadConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            log.info("config.yml not found, creating!");
            saveDefaultConfig();
        }
        debug = getConfig().getBoolean("debug");
    }
}
