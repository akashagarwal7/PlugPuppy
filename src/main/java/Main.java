import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class Main extends JavaPlugin {

    private Map<String, Boolean> updatedPlugins = new HashMap<String, Boolean>();
    private Map<String, Integer> pluginsWithAvailableUpdates = new HashMap<String, Integer>();
    private List<Plugin> serverPlugins = new ArrayList<Plugin>();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    List<Plugin> getPlugins() {
        return (serverPlugins = Arrays.asList(getServer().getPluginManager().getPlugins()));
    }

    boolean checkForUpdates() {
        Iterator<Plugin> pluginIterator = getPlugins().iterator();
        while (pluginIterator.hasNext()) {
            Plugin plugin = pluginIterator.next();
            String currentVersion = plugin.getDescription().getVersion();
            int resourceID = Utils.readResourceIDFromGist(plugin.getName());
            if (resourceID == -1) {
                //resource ID doesn't not exist
                continue;
            }

            if (!currentVersion.equals(Utils.getLatestVersion(resourceID))) {
                pluginsWithAvailableUpdates.put(plugin.getName(), resourceID);
            }
        }
        return pluginsWithAvailableUpdates.size() > 0;
    }

    void updateAll() {

    }

    void updateSingle() {

    }

    boolean isPluginUpdated(String name) {
        return updatedPlugins.get(name);
    }

    void setPluginUpdated(String name, boolean value) {
        updatedPlugins.put(name, value);
    }

}
