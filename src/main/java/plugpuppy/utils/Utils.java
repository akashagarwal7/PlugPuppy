package plugpuppy.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static plugpuppy.Variables.PLUGINS_RESOURCE_ID_URL;
import static plugpuppy.Variables.SPIGET_BASE_RESOURCES_URL;

public class Utils {

    private static Gson gson = new Gson();
    public static Logger logger = Logger.getLogger("foo");

    public static String getLatestVersion (String id) {

        String latestVersion = null;
        try {
            String latestVersionInfo =
                    readFrom(SPIGET_BASE_RESOURCES_URL + id + "/versions/latest");
            Type type = new TypeToken<JsonObject>() {}.getType();
            JsonObject object = gson.fromJson(latestVersionInfo, type);
            latestVersion = object.get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latestVersion;
    }

    private static String readFrom(String url) throws IOException {

        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            return sb.toString();
        }
    }

    public static String readResourceIDFromGist(String name) {
        JsonObject data = null;
        try {
            data = read(PLUGINS_RESOURCE_ID_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Utils.logger.info(data.toString());
        if (data != null && data.get(name) != null)
            return String.valueOf(data.get(name).toString().replace('"', ' ').trim());
        return null;
    }

    private static JsonObject read(String address) throws IOException {
        URL url = new URL(address);
        try (InputStream in = url.openStream(); InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, JsonObject.class);
        }
    }

    public static String colorMsg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String yellowMsg(String msg) { return colorMsg(ChatColor.YELLOW + msg); }

    public static String blueMsg(String msg) { return colorMsg(ChatColor.BLUE + msg); }

    public static String redMsg(String msg) { return colorMsg(ChatColor.RED + msg); }
}
