package plugpuppy;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import plugpuppy.commands.PP;
import plugpuppy.utils.PluginUtil;
import plugpuppy.utils.Utils;

import java.util.*;

public class Main extends JavaPlugin {

    @Getter
    private List<Plugin> serverPlugins = new ArrayList<>();

    @Getter
    private static Main instance = null;

    private boolean autoCheckUpdates = false;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this);

        instance = this;
        serverPlugins = Arrays.asList(getServer().getPluginManager().getPlugins());
        registerCommands();
        //TODO Future task
        //add autoCheckUpdates to config
        if (autoCheckUpdates)
            PluginUtil.getInstance().checkForUpdates();

        PluginDescriptionFile pdf = getDescription();
        Bukkit.getConsoleSender().sendMessage(Utils.colorMsg("&b&l" + pdf.getName() + " has been loaded (V." + pdf.getVersion() + ")"));
    }

    void registerCommands() {
        new PP();
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        Bukkit.getConsoleSender().sendMessage(Utils.colorMsg("&c&l" + pdf.getName() + " has been disabled."));
    }

}
