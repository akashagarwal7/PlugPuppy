package plugpuppy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import plugpuppy.utils.PluginUtil;
import plugpuppy.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PP extends BaseCommand implements TabCompleter {
    private List<String> list = new ArrayList<>(3), update = new ArrayList<>(2);

    public PP() {
        list = new ArrayList<>();
        list.add("check");
        list.add("help");
        list.add("update");

        update = new ArrayList<>(2);
        update.add("all");
        update.add("list");
        update.add("plugin");
        update.add("single");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        //switch, if-else here for different commands

        if (!hasPermission(sender)) return false;

        switch (strings.length) {
            case 1:
                if (strings[0].equalsIgnoreCase("check")) {
                    PluginUtil.getInstance().checkForUpdates();
                }
                break;
            case 2:
                if (strings[0].equalsIgnoreCase("update")) {
                    if (strings[1].equalsIgnoreCase("list")) {

                        if (PluginUtil.getInstance().getPluginsWithAvailableUpdates().size() == 0) {
                            sender.sendMessage(Utils.yellowMsg("No updates available"));
                            return true;
                        }

                        sender.sendMessage(Utils.yellowMsg("update list command"));
                        for (String key : PluginUtil.getInstance().getPluginNamesWithAvailableUpdates()) {
                            sender.sendMessage(key);
                        }

                    } else if (strings[1].equalsIgnoreCase("all")) {
//                        PluginUtil.getInstance().updateAll();
                    } else if (strings[1].equalsIgnoreCase("plugin")) {

                    } else if (strings[1].equalsIgnoreCase("single")) {

                    }
                }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (strings.length) {
            case 2:
                if (strings[0].equalsIgnoreCase("check")) {
                    return new ArrayList<>();
                } else if (strings[0].equalsIgnoreCase("update")) {
                    return createReturnList(update, strings[1]);
                } else {
                    return new ArrayList<>();
                }
            case 3:
                if (strings[1].equalsIgnoreCase("single") || strings[1].equalsIgnoreCase("plugin")) {
                    if (!PluginUtil.getInstance().isUpdatesChecked()) {
                        // check updates
                        PluginUtil.getInstance().checkForUpdates();
                        commandSender.sendMessage(Utils.yellowMsg("Checking for updates.."));
                        return new ArrayList<>();
                    }
                    if (PluginUtil.getInstance().getPluginsWithAvailableUpdates().size() == 0) {
                        commandSender.sendMessage(Utils.yellowMsg("No updates available"));
                        return new ArrayList<>();
                    }
                    return createReturnList(PluginUtil.getInstance().getPluginNamesWithAvailableUpdates(), strings[2]);
                } else {
                    return new ArrayList<>();
                }
            case 4:
                return new ArrayList<>();
        }

        return createReturnList(list, strings[0]);
    }

    private List<String> createReturnList(List<String> list, String strings) {
        if (strings.equals("")) return list;

        List<String> returnList = new ArrayList<>();
        for (String item : list) {
            if (item.toLowerCase().startsWith(strings.toLowerCase())) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    public String getCommand() {
        return "pp";
    }

    public String getName() {
        return "pp";
    }

    public String getDescription() {
        return "Base command for PlugPuppy";
    }

    public String[] getArguments() {
        return new String[0];
    }

    public String getPermission() {
        return "base";
    }
}
