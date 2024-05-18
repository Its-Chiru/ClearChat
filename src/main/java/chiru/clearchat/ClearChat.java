package chiru.clearchat;

import chiru.clearchat.Events.MessageSend;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClearChat extends JavaPlugin {

    public FileConfiguration config;
    @Override
    public void onEnable() {
        // Save the default config if it doesn't exist
        saveDefaultConfig();

        // Plugin startup logic
        System.out.println(ChatColor.GREEN+"ClearChat has been enabled.");

        //Register Events
        getServer().getPluginManager().registerEvents(new MessageSend(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println(ChatColor.RED+"ClearChat has been disabled.");
    }
}
