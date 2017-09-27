package plugpuppy.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.*;
import plugpuppy.Main;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.*;
import java.util.*;

import plugpuppy.datastructures.PluginInfo;

import java.io.*;
import java.util.List;

import static plugpuppy.Variables.SPIGET_BASE_RESOURCES_URL;

/**
 * Credits to AutoUpdaterAPI for example code
 * AutoUpdaterAPI - https://github.com/GamerKing195/AutoUpdaterAPI/
 */

public class PluginUtil {

    @Getter
    private Map<String, Boolean> updatedPlugins = new HashMap<>();
    @Getter
    private Map<String, PluginInfo> pluginsWithAvailableUpdates = new HashMap<String, PluginInfo>();
    private Map<String, PluginInfo> failedUpates = new HashMap<String, PluginInfo>();
    @Getter
    private List<String> pluginNamesWithAvailableUpdates = new ArrayList<>();
    @Getter
    private boolean updatesChecked = false;
    private volatile boolean alreadyChecking = false;

    private static PluginUtil instance = null;

    private static char SLASH = '/';

    private PluginUtil() {}

    public static PluginUtil getInstance() {
        synchronized (PluginUtil.class) {
            if (instance == null) {
                instance = new PluginUtil();
            }
        }
        return instance;
    }

    public boolean checkForUpdates(final CommandSender sender) {
        Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {

                if (alreadyChecking) return;
                alreadyChecking = true;

                if (pluginsWithAvailableUpdates.size() > 0) {
                    pluginsWithAvailableUpdates.clear();
                    pluginNamesWithAvailableUpdates.clear();
                }

                Iterator<Plugin> pluginIterator = Main.getInstance().getServerPlugins().iterator();
                while (pluginIterator.hasNext()) {
                    Plugin plugin = pluginIterator.next();
                    Bukkit.getConsoleSender().sendMessage(Utils.colorMsg("&bChecking updates for: " + plugin.getName()));
                    try {
                        Utils.logger.info(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    String currentVersion = plugin.getDescription().getVersion();
                    String resourceID = Utils.readResourceIDFromGit(sender, plugin.getName());
                    if (resourceID == null) {
                        //resource ID doesn't not exist, do something about it
                        Bukkit.getConsoleSender().sendMessage(Utils.colorMsg("&cResource not found: " + plugin.getName()));
                        continue;
                    }

                    final String latestVersion = Utils.getLatestVersion(sender, resourceID);
                    if (latestVersion == null) {
                        return;
                    }
                    if (!currentVersion.equals(latestVersion)) {
                        Utils.logger.info(plugin.getName() + " is outdated.");
                        pluginsWithAvailableUpdates.put(plugin.getName(), new PluginInfo(resourceID, latestVersion));
                        pluginNamesWithAvailableUpdates.add(plugin.getName());
                        assert (pluginNamesWithAvailableUpdates.size() > 0);
                    }

                    updatesChecked = true;
                }

            }
        }, 20L);
        alreadyChecking = false;
        return pluginsWithAvailableUpdates.size() > 0;
    }

    /**
     * Defaults safe to true
     * @param sender
     */
    public void updateAll(CommandSender sender) {
        updateAll(sender, true, false);
    }

    /**
     * Defaults parallel to false
     * @param sender
     * @param safe
     */
    public void updateAll(CommandSender sender, final boolean safe) {
        updateAll(sender, safe, false);
    }

    /**
     * Update all the updatable plugins
     * @param sender Console or player who initiated the request
     * @param safe Type of update: download-only(safe), dynamic reload(unsafe)
     * @param parallel Parallel download of plugin or not. Parallel may slow down the server depending upon the number
     *                 of plugins.
     */
    public void updateAll(CommandSender sender, final boolean safe, boolean parallel) {
        if (!parallel) {
            Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Iterator pluginIterator = pluginsWithAvailableUpdates.entrySet().iterator();
                    while (pluginIterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) pluginIterator.next();
                        if (!updateSingle(entry, safe))
                            failedUpates.put((String) entry.getKey(), ((PluginInfo) entry.getValue()));
                        pluginIterator.remove();
                    }
                }
            }, 20L);
        } else {
            //TODO Future task
            //code for parallel
            final Iterator pluginIterator = pluginsWithAvailableUpdates.entrySet().iterator();
            while (pluginIterator.hasNext()) {
                final Map.Entry entry = (Map.Entry) pluginIterator.next();
                Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (!updateSingle(entry, safe))
                            failedUpates.put((String) entry.getKey(), ((PluginInfo) entry.getValue()));
                        pluginIterator.remove();
                    }
                }, 20L);
            }
        }
    }

    public void updateSingleWithPluginName(CommandSender sender, String pluginName) {
        if (!pluginsWithAvailableUpdates.containsKey(pluginName)) {
            Utils.sendResourceNotFoundMsg(sender);
            return;
        }
        String resourceID = pluginsWithAvailableUpdates.get(pluginName).getResourceID();
        String latestVersion = pluginsWithAvailableUpdates.get(pluginName).getLatestVersion();
        updateSingle(pluginName, latestVersion, resourceID, true);
    }

    public void updateSingleWithPluginInfo(CommandSender sender, String pluginName, String resourceID) {
        String latestVersion = Utils.getLatestVersion(sender, resourceID);
        if (latestVersion == null) {
            Utils.sendResourceNotFoundMsg(sender);
            return;
        }
        //TODO Future task
        //if the resource is valid
        //add this resource to the public common resources json file, if it doesn't exist
        updateSingle(pluginName, latestVersion, resourceID, true);
    }

    public boolean updateSingle(Map.Entry entry, boolean safe) {
        return updateSingle((String) entry.getKey(), ((PluginInfo) entry.getValue()).getLatestVersion(),
                ((PluginInfo) entry.getValue()).getResourceID(), safe);
    }

    public boolean updateSingle(String pluginName, String latestVersion, String resourceID, boolean safe) {
        Plugin plugin = Main.getInstance().getServer()
                .getPluginManager().getPlugin(pluginName);

        if (!safe) {
            unload(plugin);
        }

        String folderPath = Main.getInstance().getDataFolder().getPath();
        folderPath = folderPath.substring(0, folderPath.lastIndexOf(SLASH)) + SLASH;

        String newPluginName = pluginName + "-" + latestVersion + ".jar";
        //TODO Future task
        //if overwrite in config is false
        //  if newPluginName == oldPluginName, append latestVersion with something

        if (downloadPlugin(resourceID, folderPath, newPluginName)) {
            deleteOld(plugin);
        } else {
            return false;
        }

        setPluginUpdated(pluginName);

        if (!safe) {
            loadPlugin(plugin, folderPath, newPluginName);
        } else {
            //send message for restart
        }
        return true;
    }

    /**
     * Method is from PlugMan, developed by Ryan Clancy "rylinaux"
     *
     * PlugMan https://dev.bukkit.org/projects/plugman
     *
     * @param plugin The plugin that needs to be unloaded.
     */

    private void unload(Plugin plugin) {

        String name = plugin.getName();

        PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        boolean reloadlisteners = true;

        if (pluginManager != null) {

            pluginManager.disablePlugin(plugin);

            try {

                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e) {
                    reloadlisteners = false;
                }

                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);

            } catch (NoSuchFieldException e) {
                Main.getInstance().getServer().getConsoleSender().sendMessage(e.toString());
            } catch (IllegalAccessException e) {
                Main.getInstance().getServer().getConsoleSender().sendMessage(e.toString());
            }
        }

        pluginManager.disablePlugin(plugin);

        if (plugins != null && plugins.contains(plugin))
            plugins.remove(plugin);

        if (names != null && names.containsKey(name))
            names.remove(name);

        if (listeners != null && reloadlisteners) {
            for (SortedSet<RegisteredListener> set : listeners.values()) {
                for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext(); ) {
                    RegisteredListener value = it.next();
                    if (value.getPlugin() == plugin) {
                        it.remove();
                    }
                }
            }
        }

        if (commandMap != null) {
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                }
            }
        }

        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {
            try {
                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        System.gc();
    }

    boolean deleteOld(Plugin plugin) {
        String path = getPluginPath(plugin);
        return path != null && new File(path).delete();
    }

    boolean downloadPlugin(String resourceID, String folderPath, String newPluginName) {
        HttpURLConnection httpConnection = null;
        try {
            URL downloadUrl = new URL(SPIGET_BASE_RESOURCES_URL + resourceID + "/download");
            httpConnection = (HttpURLConnection) downloadUrl.openConnection();
            httpConnection.setRequestProperty("User-Agent", "SpigetResourceUpdater");
            long completeFileSize = httpConnection.getContentLength();

            java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());

            java.io.FileOutputStream fos = new java.io.FileOutputStream(new File(folderPath + newPluginName));

            java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x;
            while ((x = in.read(data, 0, 1024)) >= 0) {
                downloadedFileSize += x;

                if (downloadedFileSize % 5000 == 0) {
//                    final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 15);
//
//                    final String currentPercent = String.format("%.2f", (((double) downloadedFileSize) / ((double) completeFileSize)) * 100);
//
//                    String bar = "&a:::::::::::::::";
//
//                    bar = bar.substring(0, currentProgress + 2) + "&c" + bar.substring(currentProgress + 2);
//
//                    sendActionBar(initiator, locale.getUpdatingDownload().replace("%plugin%", pluginName).replace("%old_version%", currentVersion).replace("%new_version%", newVersion).replace("%download_bar%", bar).replace("%download_percent%", currentPercent + "%") + " &8[DOWNLOADING]");
                }
                bout.write(data, 0, x);
            }

            bout.close();
            in.close();
            return true;
        } catch (MalformedURLException mfe) {
            mfe.printStackTrace();
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    void loadPlugin(Plugin plugin, String folderPath, String newPluginName) {
        try {
            Bukkit.getPluginManager().loadPlugin(new File(folderPath + newPluginName));
        } catch (InvalidPluginException e) {
            e.printStackTrace();
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
        }
        Bukkit.getPluginManager().enablePlugin(plugin);
    }

    private String getPluginPath(Plugin plugin) {
        try {
            return plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentPluginFileName(String path) {
        return path.substring(path.lastIndexOf(SLASH) + 1, path.lastIndexOf('.'));
    }

    boolean isPluginUpdated(String name) {
        return pluginNamesWithAvailableUpdates.contains(name);
    }

    void setPluginUpdated(String name) {
//        pluginsWithAvailableUpdates.remove(name);
//        pluginNamesWithAvailableUpdates.remove(name);
    }
}