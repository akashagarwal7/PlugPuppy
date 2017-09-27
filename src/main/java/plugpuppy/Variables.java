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
}
