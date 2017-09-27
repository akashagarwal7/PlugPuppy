package plugpuppy.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static plugpuppy.Variables.*;

public class Utils {

    private static Gson gson = new Gson();
    public static Logger logger = Logger.getLogger("foo");

    public static String getLatestVersion (CommandSender sender, String resourceID) {
        String latestVersionInfo =
                readFrom(sender, SPIGET_BASE_RESOURCES_URL + resourceID + "/versions/latest");
        if (latestVersionInfo == null) return null;
        Type type = new TypeToken<JsonObject>() {}.getType();
        JsonObject object = gson.fromJson(latestVersionInfo, type);
        return object.get("name").getAsString();
    }

    private static String readFrom(CommandSender sender, String url){
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readResourceIDFromGit(CommandSender sender, String name) {
        JsonObject data = read(sender, PLUGINS_RESOURCE_ID_URL);
//        Utils.logger.info(data.toString());
        if (data != null && data.get(name) != null)
            return String.valueOf(data.get(name).toString().replace('"', ' ').trim());
        return null;
    }

    private static JsonObject read(CommandSender sender, String address) {
        try {
            URL url = new URL(address);
            InputStream in = url.openStream();
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String addPluginPrefix(String msg) {return PLUGIN_PREFIX + msg;}

    public static String colorMsg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', addPluginPrefix(msg));
    }

    public static String yellowMsg(String msg) { return colorMsg(ChatColor.YELLOW + msg); }

    public static String blueMsg(String msg) { return colorMsg(ChatColor.BLUE + msg); }

    public static String redMsg(String msg) { return colorMsg(ChatColor.RED + msg); }

    public static void exceptionMsg(CommandSender sender) {iMsg(sender, redMsg(EXCEPTION_CHECK_CONSOLE));}

    public static void sendUpdateListEmptyMsg(CommandSender sender) {
        iMsg(sender, redMsg(NO_UPDATES_AVAILABLE));
        iMsg(sender, yellowMsg(PLUGINS_RESOURCE_ID_URL));
        iMsg(sender, yellowMsg(GENERAL_MSG1));
        iMsg(sender, yellowMsg(GENERAL_MSG2));
        iMsg(sender, yellowMsg(GENERAL_MSG3));
    }

    public static void sendResourceNotFoundMsg(CommandSender sender) {
        iMsg(sender,redMsg(RESOURCE_UNKNOWN));
        iMsg(sender, yellowMsg(PLUGINS_RESOURCE_ID_URL));
        iMsg(sender, yellowMsg(GENERAL_MSG1));
        iMsg(sender, yellowMsg(GENERAL_MSG2));
        iMsg(sender, yellowMsg(GENERAL_MSG3));
    }

    public static void iMsg(CommandSender sender, String msg) {
        if (sender != null) {
            sender.sendMessage(msg);
        } else {
            Bukkit.getConsoleSender().sendMessage(msg);
        }
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
