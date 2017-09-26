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

import static plugpuppy.Variables.SPIGET_BASE_RESOURCES_URL;

public class Utils {

    private static Gson gson = new Gson();
    public static Logger logger = Logger.getLogger("fool");

//    static final String url = "https://pastebin.com/raw/5Zey4qc7";
//    private static final String url = "https://gist.githubusercontent.com/akashaggarwal7/a4460b2093f775f0a2e0d3437f44ef9d/raw/6e4bc4718a7e9d5fd12db34cafea9a46c82bdf46/PluginID";
    private static final String url = "https://gist.githubusercontent.com/akashaggarwal7/a4460b2093f775f0a2e0d3437f44ef9d/raw/da47ee80ec4f1a4d92fe96052d0123d1af43b445/PluginID";
    // move the json file to github from gist

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
            data = read(url);
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
