package plugpuppy;

/**
 * Created by akashagarwal on 21/06/17.
 */
public interface Variables {
    String c_warning = "&d&l";
    String m_notAuthorized = c_warning + "You're not authorized to do that!";
    String SPIGET_BASE_RESOURCES_URL = "https://api.spiget.org/v2/resources/";
    String PLUGINS_RESOURCE_ID_URL =
            "https://raw.githubusercontent.com/akashaggarwal7/PlugPuppy/plugins-data/plugins-data/data.json";
    String NO_UPDATES_AVAILABLE = "No updates available FOR THE KNOWN RESOURCES. Check them out here: ";
    String GENERAL_MSG1 = "Consider updating the plugins manually using /pp update pluginWithInfo <PluginName> <resourceID>";
    String GENERAL_MSG2 = "You can find the resource ID of a plugin following in the URL of the plugin's Spigot page.";
    String GENERAL_MSG3 = "For example: "; //TODO insert PlugPuppy's example here

    String RESOURCE_UNKNOWN = "We do not know the resource for that plugin at this moment. Check the knowns here: ";

    //Plugin prefix

    String PLUGIN_PREFIX = "&r&7[&bPlugPuppy&7]&r ";

    //Error messages

    String EXTRA_ARGUMENTS = "Extra arguments!";
    String INSUFFICIENT_ARGUMENTS = "Insufficient arguments!";
    String INVALID_ARGUMENT = "The argument %arg% is invalid!";
    String EXCEPTION_CHECK_CONSOLE = "There was an error. Please check console.";
    String NOT_A_NUMBER = "The argument %arg% is not a number!";

    //Info messages

    String CHECKING_UPDATES = "Checking for plugin updates..";

    String DOWNLOAD_BEGIN = "Beginning download of plugin: %plugin%";
    String DOWNLOAD_FINISH = "Finished download of plugin: %plugin%";
    String DOWNLOAD_FAILED = "Download failed of plugin: %plugin%";

    String DELETE_S = "Deletion successful for outdated file of plugin: %plugin%";
    String DELETE_F = "Deletion failed for outdated file of plugin: %plugin%";

    String UNLOADING_PLUGIN = "Unloading plugin: %plugin%";
    String UNLOAD_S = "Unload successful for plugin: %plugin%";
    String UNLOAD_F = "Unload failed for plugin: %plugin%";

    String RELOADING_PLUGIN = "Reloading plugin: %plugin%";
    String RELOAD_S = "Reload successful for plugin: %plugin%";
    String RELOAD_F = "Reload failed for plugin: %plugin%";

    //Place holders

    String PH_PLUGIN = "%plugin%";
    String PH_ARG = "%arg%";




}
