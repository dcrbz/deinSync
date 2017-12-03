package bz.dcr.deinsync.cmd;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            plugin.getSyncManager().loadPlayer(player);
            player.sendMessage(ChatColor.GREEN + "Your profile has been loaded successfully!");
            return true;
        }

        // Open command
        if(args.length == 2 && args[0].equalsIgnoreCase("open")) {

            return true;
        }

        return false;
    }

}
