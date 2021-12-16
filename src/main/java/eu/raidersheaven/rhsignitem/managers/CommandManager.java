package eu.raidersheaven.rhsignitem.managers;

import eu.raidersheaven.rhsignitem.commands.RenameItemCommand;
import eu.raidersheaven.rhsignitem.commands.SignCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

public class CommandManager {
    public CommandManager() {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            Arrays.asList(
                    new SignCommand("sign", "Signs the item in hand.", "/sign <text|option>", Collections.singletonList("signieren")),
                    new RenameItemCommand("renameitem", "Rename the item in hand", "/renameitem", Collections.singletonList("umbenennen"))).forEach(command -> commandMap.register(command.getName(), command));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
