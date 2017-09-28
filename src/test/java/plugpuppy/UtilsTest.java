package plugpuppy;

import org.testng.annotations.Test;
import plugpuppy.utils.PluginUtil;
import plugpuppy.utils.Utils;

import static org.testng.Assert.*;

public class UtilsTest {

    //gist's link gets changed after editing

    @Test
    void readResourceIDFromGistTest() {
        String id = Utils.readResourceIDFromGit(null, "PowerNBT");
        Utils.logger.info("ID: " + id);
        assertEquals( id, "9098" );
    }

    @Test
    void readVaultIDTest() {
        String id = Utils.readResourceIDFromGit(null, "Vault");
        Utils.logger.info("ID: " + id);
        assertEquals( id, "41918" );
    }

    @Test
    void testLatestVersionTest() {
        String latestVersion = Utils.getLatestVersion(null, "PowerNBT");
        Utils.logger.info("Latest Version: " + latestVersion);
        assertEquals(latestVersion, "0.8.9.2");
    }

    @Test
    void getCurrentFileNameTest() {
        String path = "plugins/PlugPuppy-1.2-SNAPSHOT.jar";
        assertEquals(PluginUtil.getCurrentPluginFileName(path), "PlugPuppy-1.2-SNAPSHOT");
    }

    @Test
    void getCurrentOSTest() {
        Utils.logger.info(System.getProperty("os.name"));
        assertTrue(true);
    }

    @Test
    void compareArgsTest() {
        String[] strings = {"a", "s", "d"};
        assertTrue(Utils.compareArgs(null, strings, "a", "s", "d"));
    }
}
