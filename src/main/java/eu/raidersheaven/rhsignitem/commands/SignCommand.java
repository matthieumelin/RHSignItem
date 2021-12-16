package eu.raidersheaven.rhsignitem.commands;

import eu.raidersheaven.rhsignitem.RHSignItem;
import eu.raidersheaven.rhsignitem.utilities.HexColor;
import eu.raidersheaven.rhsignitem.utilities.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SignCommand extends Command {
    public SignCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("delete")
                        || args[0].equalsIgnoreCase("del")
                        || args[0].equalsIgnoreCase("remove")
                        || args[0].equalsIgnoreCase("unsign")) {
                    if (havePermission(player, "commands.unsign.permission")) {
                        delete();
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.no-permission"))));
                    }
                } else if (args[0].equalsIgnoreCase("lock")
                        || args[0].equalsIgnoreCase("lockitem")) {
                    if (havePermission(player, "commands.lock.permission")) {
                        lock();
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.no-permission"))));
                    }
                } else if (args[0].equalsIgnoreCase("unlock")
                        || args[0].equalsIgnoreCase("unlockitem")) {
                    if (havePermission(player, "commands.unlock.permission")) {
                        unlock();
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.no-permission"))));
                    }
                } else if (args[0].equalsIgnoreCase("reload")
                        || args[0].equalsIgnoreCase("rel")) {
                    if (havePermission(player, "commands.reload.permission")) {
                        reload(player);
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.no-permission"))));
                    }
                } else {
                    sign(player, args);
                }
            } else {
                sendUsage(player);
            }
            return true;
        }
        return false;
    }

    private boolean havePermission(Player player, String permission) {
        return player.hasPermission(Objects.requireNonNull(RHSignItem.get().getConfig().getString("commands.admin.permission")))
                || player.hasPermission(Objects.requireNonNull(RHSignItem.get().getConfig().getString(permission)));
    }

    /**
     * Send usage to player
     *
     * @param player
     */
    private void sendUsage(Player player) {
        List<String> usageMessage = RHSignItem.get().getConfig().getStringList("messages.sign.usage");
        if (!usageMessage.isEmpty()) {
            usageMessage.forEach(message -> player.sendMessage(HexColor.format(message)));
        }
    }

    private void sign(Player player, String[] args) {
        ItemBuilder newItemInMainHand = new ItemBuilder(player.getInventory().getItemInMainHand().getType());
        ItemStack itemInMainHand = newItemInMainHand.build();

        // check item in main hand is not null
        if (itemInMainHand != null && player.getInventory().contains(itemInMainHand)) {
            // not blacklisted
            if (RHSignItem.get().getConfig().getStringList("items-blacklist").stream().noneMatch(item -> item.toUpperCase().replace(" ", "_").contains(itemInMainHand.getType().toString()))) {
                ItemMeta itemInMainHandMeta = itemInMainHand.getItemMeta();
                if (itemInMainHandMeta != null) {
                    PersistentDataContainer data = itemInMainHandMeta.getPersistentDataContainer();
                    if (data.isEmpty()) {
                        if (!data.has(new NamespacedKey(RHSignItem.get(), "locked"), PersistentDataType.STRING)) {
                            if (!data.has(new NamespacedKey(RHSignItem.get(), "signed"), PersistentDataType.STRING)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < args.length; i++) {
                                    stringBuilder.append(args[i]).append(" ");
                                }
                                // sign the item
                                data.set(new NamespacedKey(RHSignItem.get(), "signed"), PersistentDataType.STRING, "true");
                                data.set(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING, player.getName());

                                Bukkit.broadcastMessage("item signed");
                            } else {
                                player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.already-signed"))
                                        .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
                                        .replace("%player%",
                                                Objects.requireNonNull(data.get(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING)))));
                            }
                        } else {
                            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.already-locked"))
                                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
                                    .replace("%player%",
                                            Objects.requireNonNull(data.get(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING)))));
                        }
                    }
                }
            } else {
                player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-blacklisted"))
                        .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
                ));
            }
        } else {
            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-item-in-main-hand"))
                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
            ));
        }
    }

    private void delete() {

    }

    private void lock() {

    }

    private void unlock() {

    }

    private void reload(Player player) {
        String reloadMessage = RHSignItem.get().getConfig().getString("messages.sign.reload");
        if (reloadMessage != null) player.sendMessage(HexColor.format(reloadMessage));
    }
}
