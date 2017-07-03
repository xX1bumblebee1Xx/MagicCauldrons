package main.java.me.bumblebeee_.magic;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Messages {

    public static File f;
    public static YamlConfiguration c;

    public void setup() {
        f = new File(Magic.getInstance().getDataFolder() + File.separator + "messages.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            c = YamlConfiguration.loadConfiguration(f);

            createMessage("noPermissions", "&cYou do not have the required permissions.");
            createMessage("configReloaded", "&aSuccessfully reloaded config!");
            createMessage("givenWand", "&aSuccessfully given wand.");
            createMessage("emptyCauldron", "&cCauldron is empty!");
            createMessage("spellNotFound", "&cCould not find a spell for them ingredients!");
            createMessage("levelToLow", "&cThis cauldrons level is too low for this spell!");
            createMessage("notHoldingWand", "&cYou must be holding your wand!");
            createMessage("alreadyHaveSpell", "&cYou already have that spell!");
            createMessage("successAddedSpell", "&aSuccessfully added spell to your wand!");
            createMessage("noSpellSelected", "&cYou do not have any spell selected!");
            createMessage("notEnoughMana", "&cYou do not have enough mana! Wait for it to recharge.");
            createMessage("deselectedSpell", "&aSuccessfully deselected all spells.");
            createMessage("alreadySelected", "&cYou already have that spell selected!");
            createMessage("selectedSpell", "&aYou have selected <spell>!");

            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            c = YamlConfiguration.loadConfiguration(f);
        }
    }

    public void createMessage(String key, String value) {
        c.set(key, value);
    }

    public String getMessage(String key) {
        String msg = c.getString(key);
        if (msg == null) {
            Magic.getInstance().getLogger().warning(ChatColor.RED + "Failed to find message with key " + key);
            Magic.getInstance().getLogger().warning(ChatColor.RED + "Deleting the messages.yml file or adding the key will fix this");
            return ChatColor.translateAlternateColorCodes('&', "&cFailed to find message! Please report this to a server admin.");
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
