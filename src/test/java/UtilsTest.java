import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class UtilsTest {

    @Test
    void readResourceIDFromGistTest() {
        int id = Utils.readResourceIDFromGist("PowerNBT");
        Utils.logger.info("ID: " + id);
        assertEquals( id, 9098 );
    }


    @Test
    void testLatestVersion() {
        String latestVersion = Utils.getLatestVersion("PowerNBT");
        Utils.logger.info("Latest Version: " + latestVersion);
        assertEquals(latestVersion, "0.8.9.2");
    }
}
