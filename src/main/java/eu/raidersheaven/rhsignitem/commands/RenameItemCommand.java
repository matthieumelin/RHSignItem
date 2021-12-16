package eu.raidersheaven.rhsignitem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RenameItemCommand extends Command {
    public RenameItemCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                renameItem(args);
            } else {
                sendUsage(player);
            }
        }
        return false;
    }

    private void renameItem(String[] args) {

    }

    private void sendUsage(Player player) {

    }
}
