package plugpuppy;

import org.testng.annotations.Test;
import plugpuppy.utils.PluginUtil;
import plugpuppy.utils.Utils;

import static org.testng.Assert.*;

public class UtilsTest {

    //gist's link gets changed after editing

    @Test
    void readResourceIDFromGistTest() {
        String id = Utils.readResourceIDFromGit("PowerNBT");
        Utils.logger.info("ID: " + id);
        assertEquals( id, "9098" );
    }

    @Test
    void readVaultID() {
        String id = Utils.readResourceIDFromGit("Vault");
        Utils.logger.info("ID: " + id);
        assertEquals( id, "41918" );
    }

    @Test
    void testLatestVersion() {
        String latestVersion = Utils.getLatestVersion("PowerNBT");
        Utils.logger.info("Latest Version: " + latestVersion);
        assertEquals(latestVersion, "0.8.9.2");
    }

    @Test
    void getCurrentFileName() {
        String path = "plugins/PlugPuppy-1.2-SNAPSHOT.jar";
        assertEquals(PluginUtil.getCurrentPluginFileName(path), "PlugPuppy-1.2-SNAPSHOT");
    }
}
