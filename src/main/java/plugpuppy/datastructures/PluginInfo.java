package plugpuppy.datastructures;

import lombok.Getter;

public class PluginInfo {
    @Getter
    String resourceID, latestVersion;

    public PluginInfo(String resourceID, String latestVersion) {
        this.resourceID = resourceID;
        this.latestVersion = latestVersion;
    }
}
