package bz.dcr.deinsync.cmd;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeinSyncCommand implements CommandExecutor {

    private DeinSyncPlugin plugin;


    public DeinSyncCommand(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Sender is not a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run as a player!");
            return true;
        }

        final Player player = (Player) sender;

        // Update command
        if(args.length == 1 && args[0].equalsIgnoreCase("update")) {
            // Player has no permission
            if (!player.hasPermission("deinsync.update")) {
                player.sendMessage("§cDu hast keine Berechtigung dafür.");
                return true;
            }

            plugin.getSyncManager().loadPlayer(player);
            player.sendMessage(ChatColor.GREEN + "Your profile has been loaded successfully!");
            return true;
        }

        // Open command
        if(args.length == 2 && args[0].equalsIgnoreCase("open")) {
            return true;
        }

        // Clear command
        if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            // Player has no permission
            if (!player.hasPermission("deinsync.clear")) {
                player.sendMessage("§cDu hast keine Berechtigung dafür.");
                return true;
            }

            plugin.getExecutorService().execute(() -> {
                // Get UUID by name
                final UUID uuid = plugin.getDcCore().getIdentificationProvider().getUUID(args[1]);

                // Player does not exist
                if (uuid == null) {
                    player.sendMessage("§cThe player §f" + args[1] + " §cdoes not exist.");
                    return;
                }

                // Clear player profile
                plugin.getSyncManager().clearPlayer(uuid);

                // Send message
                player.sendMessage("§aDer Spielstand von §f" + args[1] + " §awurde zurückgesetzt.");
            });
        }

        return true;
    }

}
