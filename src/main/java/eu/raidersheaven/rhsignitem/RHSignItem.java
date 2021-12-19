package eu.raidersheaven.rhsignitem;

import eu.raidersheaven.rhsignitem.managers.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RHSignItem extends JavaPlugin {
    private static RHSignItem instance;

    @Override
    public void onEnable() {
        // Init plugin instance
        instance = this;

        // Save default configuration
        saveDefaultConfig();

        // Init Command manager
        new CommandManager();
    }

    /**
     * Get plugin instance
     * @return Plugin instance
     */
    public static RHSignItem get() {
        return instance;
    }
}
