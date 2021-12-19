package eu.raidersheaven.rhsignitem.commands;

import eu.raidersheaven.rhsignitem.RHSignItem;
import eu.raidersheaven.rhsignitem.utilities.HexColor;
import eu.raidersheaven.rhsignitem.utilities.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class RenameItemCommand extends Command {
    public RenameItemCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (PlayerUtils.havePermission(player, "RHSignItem.renameitem")) {
                    renameItem(player, args);
                } else {
                    player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-permission"))));
                }
            } else {
                sendUsage(player);
            }
        }
        return false;
    }

    /**
     * Rename item
     *
     * @param player
     * @param args
     */
    private void renameItem(Player player, String[] args) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        // check item in main hand is not null
        if (!itemInMainHand.getType().equals(Material.AIR)) {
            ItemMeta itemInMainHandMeta = itemInMainHand.getItemMeta();
            // item meta not null
            if (itemInMainHandMeta != null) {
                // sign text
                StringBuilder stringBuilder = new StringBuilder();

                for (String arg : args) {
                    stringBuilder.append(arg).append(" ");
                }

                // remove the empty space at last index of text
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));

                itemInMainHandMeta.setDisplayName(HexColor.format(stringBuilder.toString()));
                itemInMainHand.setItemMeta(itemInMainHandMeta);

                // update item in main hand
                player.getInventory().setItemInMainHand(itemInMainHand);

                player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-renamed"))
                        .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
            }
        } else {
            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-item-in-main-hand"))
                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
            ));
        }

    }

    /**
     * Send usage to player
     *
     * @param player
     */
    private void sendUsage(Player player) {
        player.sendMessage(getUsage());
    }
}
