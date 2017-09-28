package plugpuppy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import plugpuppy.utils.PluginUtil;
import plugpuppy.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PP extends BaseCommand implements TabCompleter {
    private List<String> list = new ArrayList<>(3), update = new ArrayList<>(5),
            safe = new ArrayList<>(3), sequential = new ArrayList<>(2);

    final String CHECK = ("check"), HELP = ("help"), UPDATE = ("update"), ALL = ("all"), ALL_EXCEPT = ("allExcept"),
            LIST = ("list"), PLUGIN_WITH_INFO = ("pluginWithInfo"), SINGLE = ("single"), SAFE = ("safe"),
            UNSAFE = ("unsafe"), YOLO = ("yolo"), PARALLEL = ("parallel"), SEQUENTIAL = ("sequential");

    HashMap<String, Boolean> safeFlag = new HashMap<>(3);
    HashMap<String, Boolean> parallelFlag = new HashMap<>(3);

    public PP() {
        list.add(CHECK);
        list.add(HELP);
        list.add(UPDATE);

        update.add(ALL);
        update.add(ALL_EXCEPT);
        update.add(LIST);
        update.add(PLUGIN_WITH_INFO);
        update.add(SINGLE);

        safe.add(SAFE);
        safe.add(UNSAFE);
        safe.add(YOLO);

        sequential.add(PARALLEL);
        sequential.add(SEQUENTIAL);


        safeFlag.put(SAFE, true);
        safeFlag.put(UNSAFE, false);
        safeFlag.put(YOLO, false);

        parallelFlag.put(PARALLEL, false);
        parallelFlag.put(SEQUENTIAL, true);
        parallelFlag.put(YOLO, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

//        Utils.logger.info("Args length is: " + strings.length);

        if (!hasPermission(sender)) return false;

        return test(sender, strings);
    }

    boolean test(CommandSender sender, String[] strings) {
        if (Utils.compareArgs(sender, strings, CHECK)) {
            PluginUtil.getInstance().checkForUpdates(sender);
            return true;
        } else if (Utils.compareArgs(sender, strings, UPDATE, ALL)) {
            if (strings.length == 2) {
                PluginUtil.getInstance().updateAll(sender);
            } else if (strings.length >= 3) {

                if (!strings[2].isEmpty() && safeFlag.containsKey(strings[2].toLowerCase())) {
                    if (strings[2].equalsIgnoreCase(YOLO)) {
                        PluginUtil.getInstance().updateAll(sender, false, false);
                        return true;
                    }
                }
                boolean safe, sequential = true;
                if (safeFlag.get(strings[2].toLowerCase()) == null) {
                    //invalid argument
                    return false;
                }
                safe = safeFlag.get(strings[2].toLowerCase());

                if (strings.length == 4 && !strings[3].isEmpty()) {
                    if (safeFlag.get(strings[3].toLowerCase()) == null) {
                        //invalid argument
                        return false;
                    }
                    sequential = safeFlag.get(strings[3].toLowerCase());
                }

                PluginUtil.getInstance().updateAll(sender, safe, sequential);
                return true;

            }
        } else if (Utils.compareArgs(sender, strings, UPDATE, ALL_EXCEPT)) {
            sender.sendMessage( Utils.yellowMsg("To be implemented!"));
            return true;
        } else if (Utils.compareArgs(sender, strings, UPDATE, LIST)) {
            if (PluginUtil.getInstance().getPluginsWithAvailableUpdates().size() == 0) {
                Utils.sendUpdateListEmptyMsg(sender);
                return true;
            }

            sender.sendMessage(Utils.yellowMsg("Update list: "));
            for (String key : PluginUtil.getInstance().getPluginNamesWithAvailableUpdates()) {
                sender.sendMessage(key);
            }
            return true;
        } else if (Utils.compareArgs(sender, strings, UPDATE, PLUGIN_WITH_INFO)) {
            if (strings.length < 4) {
                Utils.iMsg(sender, Utils.redMsg(INSUFFICIENT_ARGUMENTS));
                return false;
            }

            if (strings[2].isEmpty()) {
                Utils.iMsg(sender, Utils.redMsg(INSUFFICIENT_ARGUMENTS));
                return false;
            }

            if (!Utils.isInteger(strings[3])) {
                Utils.iMsg(sender, Utils.redMsg(Utils.replaceAll(NOT_A_NUMBER, PH_PLUGIN, strings[3])));
                return false;
            }

            PluginUtil.getInstance().updateSingleWithPluginInfo(sender, strings[2], strings[3]);
            return true;
        } else if (Utils.compareArgs(sender, strings, UPDATE, SINGLE)) {
            if (strings.length < 3) {
                Utils.iMsg(sender, Utils.redMsg(INSUFFICIENT_ARGUMENTS));
                return false;
            }

            if (strings[2].isEmpty()) {
                Utils.iMsg(sender, Utils.redMsg(INSUFFICIENT_ARGUMENTS));
                return false;
            }

            PluginUtil.getInstance().updateSingleWithPluginName(sender, strings[2]);
            return true;
        } else if (Utils.compareArgs(sender, strings, HELP)) {
            sender.sendMessage( Utils.yellowMsg("To be implemented!"));
            return true;
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
                if (strings[0].equalsIgnoreCase(UPDATE)) {
                    if (strings[1].equalsIgnoreCase(ALL) || (strings[1].equalsIgnoreCase(ALL_EXCEPT))) {
                        return createReturnList(safe, strings[2]);
                    }
                    else if (strings[1].equalsIgnoreCase("single") || strings[1].equalsIgnoreCase("plugin")) {
                        if (!PluginUtil.getInstance().isUpdatesChecked()) {
                            // check updates
                            PluginUtil.getInstance().checkForUpdates(commandSender);
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
                }
            case 4:
                if (strings[0].equalsIgnoreCase(UPDATE)) {
                    if (strings[1].equalsIgnoreCase(ALL) || (strings[1].equalsIgnoreCase(ALL_EXCEPT))) {
                        if (!strings[2].equalsIgnoreCase(YOLO) && safeFlag.containsKey(strings[2].toLowerCase())) {
                            return createReturnList(sequential, strings[3]);
                        }
                    }
                } else {
                    return new ArrayList<>();
                }
            case 5:
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
