package me.bumblebeee_.magic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    SpellManager spells = new SpellManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wand")) {

            if (args.length > 0) {
                if (!sender.hasPermission("magic.reload")) {
                    sender.sendMessage( "You do not have the required permissions!");
                    return false;
                }
                Magic.getInstance().reloadConfig();
                sender.sendMessage("Successfully reloaded config!");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command");
                return false;
            }

            Player p = (Player) sender;
            spells.giveWandToPlayer(p);
            sender.sendMessage("Given wand");
            return true;
        }
        return false;
    }
}