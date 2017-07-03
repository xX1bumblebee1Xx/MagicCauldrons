package main.java.me.bumblebeee_.magic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    SpellManager spells = new SpellManager();
    Messages msgs = new Messages();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wand")) {

            if (args.length > 0) {
                if (!sender.hasPermission("magic.reload")) {
                    sender.sendMessage(msgs.getMessage("noPermissions"));
                    return false;
                }
                Magic.getInstance().reloadConfig();
                sender.sendMessage(msgs.getMessage("configReloaded"));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command");
                return false;
            }

            Player p = (Player) sender;
            spells.giveWandToPlayer(p);
            sender.sendMessage(msgs.getMessage("givenWand"));
            return true;
        }
        return false;
    }
}