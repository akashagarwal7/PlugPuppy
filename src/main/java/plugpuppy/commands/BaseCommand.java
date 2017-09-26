package plugpuppy.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import plugpuppy.Main;
import plugpuppy.Variables;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, Variables {
    private List<String> subCommands = new ArrayList<>();

    public BaseCommand() {
        Main.getInstance()
                .getCommand(getCommand())
                .setExecutor(this);
    }

    public abstract String getCommand();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String[] getArguments();

    public abstract String getPermission();

    public boolean hasPermission(CommandSender sender) {
//        if (!(sender instanceof Player)) {
////            sender.sendMessage(ChatWriter.pluginMessage("Only players should execute this command!"));
//            return false;
//        }

        if (!sender.hasPermission("puppy." + this.getPermission())) {
//            sender.sendMessage(ChatWriter
//                    .pluginMessage(ChatColor.RED + "You don't have permission to execute this command!"));
            return false;
        }

        return true;
    }
}
