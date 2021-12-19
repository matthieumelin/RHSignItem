package eu.raidersheaven.rhsignitem.commands;

import com.google.common.collect.Lists;
import eu.raidersheaven.rhsignitem.RHSignItem;
import eu.raidersheaven.rhsignitem.utilities.DateUtils;
import eu.raidersheaven.rhsignitem.utilities.HexColor;
import eu.raidersheaven.rhsignitem.utilities.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
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
                    if (PlayerUtils.havePermission(player, "RHSignItem.unsign")) {
                        delete(player);
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-permission"))));
                    }
                } else if (args[0].equalsIgnoreCase("lock")
                        || args[0].equalsIgnoreCase("lockitem")) {
                    if (PlayerUtils.havePermission(player, "RHSignItem.lockitem")) {
                        lock(player);
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-permission"))));
                    }
                } else if (args[0].equalsIgnoreCase("unlock")
                        || args[0].equalsIgnoreCase("unlockitem")) {
                    if (PlayerUtils.havePermission(player, "RHSignItem.unlockitem")) {
                        unlock(player);
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-permission"))));
                    }
                } else if (args[0].equalsIgnoreCase("reload")
                        || args[0].equalsIgnoreCase("rel")) {
                    if (PlayerUtils.havePermission(player, "RHSignItem.reload")) {
                        reload(player);
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-permission"))));
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

    /**
     * Sign an item
     *
     * @param player
     * @param args
     */
    private void sign(Player player, String[] args) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        // check item in main hand is not null
        if (!itemInMainHand.getType().equals(Material.AIR)) {
            // not blacklisted
            if (RHSignItem.get().getConfig().getStringList("items-blacklist").stream().noneMatch(item -> item.toUpperCase().replace(" ", "_").contains(itemInMainHand.getType().toString()))) {
                ItemMeta itemInMainHandMeta = itemInMainHand.getItemMeta();
                if (itemInMainHandMeta != null) {
                    if (!RHSignItem.get().getConfig().getBoolean("block-items-with-lore")) {
                        PersistentDataContainer data = itemInMainHandMeta.getPersistentDataContainer();
                        if (!data.has(new NamespacedKey(RHSignItem.get(), "locked"), PersistentDataType.STRING)) {
                            if (!data.has(new NamespacedKey(RHSignItem.get(), "signed"), PersistentDataType.STRING)) {
                                // sign text
                                StringBuilder stringBuilder = new StringBuilder();

                                for (String arg : args) {
                                    stringBuilder.append(arg).append(" ");
                                }

                                // sign the item
                                data.set(new NamespacedKey(RHSignItem.get(), "signed"), PersistentDataType.STRING, "true");
                                data.set(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING, player.getName());

                                // add new metas
                                List<String> replacedList = new ArrayList<>();
                                RHSignItem.get().getConfig().getStringList("sign.content").forEach(line -> replacedList.add(line
                                        .replace("%text%", HexColor.format(stringBuilder.toString()))
                                        .replace("%player%", player.getName())
                                        .replace("%date%", DateUtils.getCurrentDate(RHSignItem.get().getConfig().getString("sign.date-format")))
                                        .replace("&", "§")));

                                itemInMainHandMeta.setLore(replacedList);
                                itemInMainHand.setItemMeta(itemInMainHandMeta);

                                // update item in main hand
                                player.getInventory().setItemInMainHand(itemInMainHand);

                                player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-signed"))
                                        .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                            } else {
                                player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-already-signed"))
                                        .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
                                        .replace("%player%",
                                                Objects.requireNonNull(data.get(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING)))));
                            }
                        } else {
                            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-already-locked"))
                                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
                                    .replace("%player%",
                                            Objects.requireNonNull(data.get(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING)))));
                        }
                    } else {
                        if (itemInMainHandMeta.hasLore()) {
                            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.blocked-lore"))
                                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
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

    /**
     * Delete an signature from item
     *
     * @param player
     */
    private void delete(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        // check item in main hand is not null
        if (!itemInMainHand.getType().equals(Material.AIR)) {
            ItemMeta itemInMainHandMeta = itemInMainHand.getItemMeta();
            if (itemInMainHandMeta != null) {
                PersistentDataContainer data = itemInMainHandMeta.getPersistentDataContainer();
                // data is not empty
                if (!data.isEmpty()) {
                    if (data.has(new NamespacedKey(RHSignItem.get(), "signed"), PersistentDataType.STRING)) {
                        if (Objects.equals(data.get(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING), player.getName())) {
                            // delete data of the item
                            data.remove(new NamespacedKey(RHSignItem.get(), "signed"));
                            data.remove(new NamespacedKey(RHSignItem.get(), "player"));

                            itemInMainHandMeta.setLore(Lists.newArrayList());
                            itemInMainHand.setItemMeta(itemInMainHandMeta);

                            // update item in main hand
                            player.getInventory().setItemInMainHand(itemInMainHand);

                            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-deleted"))
                                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                        } else {
                            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-wrong-owner"))
                                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                        }
                    } else {
                        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-not-signed"))
                                .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                    }
                }
            }
        } else {
            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-item-in-main-hand"))
                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
            ));
        }
    }

    /**
     * Lock an item
     *
     * @param player
     */
    private void lock(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        // check item in main hand is not null
        if (!itemInMainHand.getType().equals(Material.AIR)) {
            ItemMeta itemInMainHandMeta = itemInMainHand.getItemMeta();
            if (itemInMainHandMeta != null) {
                PersistentDataContainer data = itemInMainHandMeta.getPersistentDataContainer();
                if (!data.has(new NamespacedKey(RHSignItem.get(), "locked"), PersistentDataType.STRING)) {
                    // lock the item
                    data.set(new NamespacedKey(RHSignItem.get(), "locked"), PersistentDataType.STRING, "true");
                    data.set(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING, player.getName());

                    itemInMainHand.setItemMeta(itemInMainHandMeta);

                    player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-locked"))
                            .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                } else {
                    player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-already-locked"))
                            .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
                            .replace("%player%",
                                    Objects.requireNonNull(data.get(new NamespacedKey(RHSignItem.get(), "player"), PersistentDataType.STRING)))));
                }
            }
        } else {
            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-item-in-main-hand"))
                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
            ));
        }
    }

    /**
     * Unlock an item
     *
     * @param player
     */
    private void unlock(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        // check item in main hand is not null
        if (!itemInMainHand.getType().equals(Material.AIR)) {
            ItemMeta itemInMainHandMeta = itemInMainHand.getItemMeta();
            if (itemInMainHandMeta != null) {
                PersistentDataContainer data = itemInMainHandMeta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(RHSignItem.get(), "locked"), PersistentDataType.STRING)) {
                    // unlock the item
                    data.remove(new NamespacedKey(RHSignItem.get(), "locked"));
                    data.remove(new NamespacedKey(RHSignItem.get(), "player"));

                    itemInMainHand.setItemMeta(itemInMainHandMeta);

                    player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-unlocked"))
                            .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                } else {
                    player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.item-not-locked"))
                            .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
                }
            }
        } else {
            player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.no-item-in-main-hand"))
                    .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))
            ));
        }
    }

    /**
     * Reload the configuration
     *
     * @param player
     */
    private void reload(Player player) {
        RHSignItem.get().reloadConfig();
        player.sendMessage(HexColor.format(Objects.requireNonNull(RHSignItem.get().getConfig().getString("messages.sign.reload"))
                .replace("%prefix%", HexColor.format(RHSignItem.get().getConfig().getString("messages.prefix")))));
    }
}
