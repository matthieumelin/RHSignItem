package eu.raidersheaven.rhsignitem.utilities;

import org.bukkit.entity.Player;

public class PlayerUtils {
    public static boolean havePermission(Player player, String permission) {
        return player.hasPermission("RHSignItem.*")
                || player.hasPermission(permission);
    }
}
