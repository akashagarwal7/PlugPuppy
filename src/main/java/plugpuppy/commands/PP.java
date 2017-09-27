package plugpuppy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import plugpuppy.utils.PluginUtil;
import plugpuppy.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PP extends BaseCommand implements TabCompleter {
    private List<String> list = new ArrayList<>(3), update = new ArrayList<>(5),
                        safe = new ArrayList<>(2), parallel = new ArrayList<>(2);

    private enum SubCommands {
        CHECK("check"), HELP("help"), UPDATE("update"), ALL("all"), ALL_EXCEPT("allExcept"), LIST("list"),
        PLUGIN_WITH_INFO("pluginWithInfo"), SINGLE("single"), SAFE("safe"), UNSAFE("unsafe"), YOLO("yolo"),
        PARALLEL("parallel"), SEQUENTIAL("sequential");

        private String name;
        SubCommands(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public PP() {
        list = new ArrayList<>();
        list.add(SubCommands.CHECK.toString());
        list.add(SubCommands.HELP.toString());
        list.add(SubCommands.UPDATE.toString());

        update = new ArrayList<>(2);
        update.add(SubCommands.ALL.toString());
        update.add(SubCommands.ALL_EXCEPT.toString());
        update.add(SubCommands.LIST.toString());
        update.add(SubCommands.PLUGIN_WITH_INFO.toString());
        update.add(SubCommands.SINGLE.toString());

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
                    if (strings[1].equalsIgnoreCase("all")) {
                        if (strings.length == 2) {
                            PluginUtil.getInstance().updateAll(sender);
                        } else if (strings.length == 3) {
                            if (strings[2].isEmpty()) {
                                return false;
                            }
                            String safe = strings[2];
                            if (safe.equalsIgnoreCase(SubCommands.UNSAFE.toString())) {
                                PluginUtil.getInstance().updateAll(sender, false);
                            } else if (safe.equalsIgnoreCase(SubCommands.YOLO.toString())) {
                                PluginUtil.getInstance().updateAll(sender, false, true);
                            } else if (!safe.equalsIgnoreCase(SubCommands.SAFE.toString())) {
                                //invalid argument
                            }
                            PluginUtil.getInstance().updateAll(sender, true);
                        } else if (strings.length == 4) {
                            boolean safeFlag = true, parallelFlag = false;
                            String safe = strings[2], parallel = strings[3];
                            if (safe.equalsIgnoreCase(SubCommands.UNSAFE.toString())) {
                                safeFlag = false;
                            } else if (safe.equalsIgnoreCase(SubCommands.YOLO.toString())) {
                                PluginUtil.getInstance().updateAll(sender, false, true);
                                return true;
                            } else if (!safe.equalsIgnoreCase(SubCommands.SAFE.toString())) {
                                //invalid argument
                            }

                            if (strings[3].isEmpty()) {
                                PluginUtil.getInstance().updateAll(sender, safeFlag);
                                return true;
                            }

                            if (parallel.equalsIgnoreCase(SubCommands.PARALLEL.toString())) {
                                parallelFlag = true;
                            }  else if(!parallel.equalsIgnoreCase(SubCommands.SEQUENTIAL.toString())) {
                                //invalid argument
                            }

                            PluginUtil.getInstance().updateAll(sender, safeFlag, parallelFlag);


                        }
                    } else if (strings[1].equalsIgnoreCase("allExcept")) {
                        sender.sendMessage( Utils.yellowMsg("To be implemented!"));
                    } else if (strings[1].equalsIgnoreCase("list")) {

                        if (PluginUtil.getInstance().getPluginsWithAvailableUpdates().size() == 0) {
                            Utils.sendUpdateListEmptyMsg(sender);
                            return true;
                        }

                        sender.sendMessage(Utils.yellowMsg("Update list: "));
                        for (String key : PluginUtil.getInstance().getPluginNamesWithAvailableUpdates()) {
                            sender.sendMessage(key);
                        }

                    } else if (strings[1].equalsIgnoreCase("pluginWithInfo")) {
                        if (strings.length < 4) {
                            return false;
                        }

                        if (strings[2].isEmpty()) {
                            return false;
                        }

                        if (!Utils.isInteger(strings[3])) {
                            return false;
                        }

                        PluginUtil.getInstance().updateSingleWithPluginInfo(sender, strings[2], strings[3]);
                    } else if (strings[1].equalsIgnoreCase("single")) {
                        if (strings.length < 3) {
                            return false;
                        }

                        if (strings[2].isEmpty()) {
                            return false;
                        }

                        PluginUtil.getInstance().updateSingleWithPluginName(sender, strings[2]);
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
