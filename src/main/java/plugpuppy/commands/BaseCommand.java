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
        if (!sender.hasPermission("plugpuppy." + this.getPermission())) {
            return false;
        }
        return true;
    }
}
