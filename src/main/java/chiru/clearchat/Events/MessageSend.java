package chiru.clearchat.Events;

import chiru.clearchat.ClearChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import sun.jvm.hotspot.debugger.posix.elf.ELFSectionHeader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSend implements Listener {

    private final FileConfiguration config;
    private final ClearChat plugin;

    public MessageSend(ClearChat plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    Boolean canSend = true;  // Initially true, will be checked for spam below
    String lastMessage = "";

    @EventHandler
    public void onMessageSend(AsyncPlayerChatEvent event) {

        String message = event.getMessage();

        // AntiSpam
        if (config.getBoolean("Config.antispam.enabled")) {
            double time = config.getDouble("Config.antispam.time");
            String messageToSend = ChatColor.translateAlternateColorCodes('&', config.getString("Config.antispam.message"));

            // Scheduling a delayed task to reset the canSend flag
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    resetCanSend();
                }
            }, (long) (time * 20));  // Convert seconds to ticks (20 ticks = 1 second)

            // If spamming, cancel the event and notify the player
            if (!canSend) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(messageToSend);
            } else {
                canSend = false;
            }
        }

        //Anti Repeat Messages
        if (config.getBoolean("Config.antirepeatingmessages.enabled")) {
            String messageToSend = ChatColor.translateAlternateColorCodes('&', config.getString("Config.antirepeatingmessages.message"));
            if(message.equals(lastMessage)){
                event.setCancelled(true);
                event.getPlayer().sendMessage(messageToSend);
            }

            lastMessage = message;

        }
        // Anti Links
        if (config.getBoolean("Config.antilinks.enabled")) {
            String messageToSend = ChatColor.translateAlternateColorCodes('&', config.getString("Config.antilinks.message"));
            if (containsLink(message)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(messageToSend);
            }
        }
    }

    private void resetCanSend() {
        canSend = true;
    }

    private boolean containsLink(String message) {
        // Regex pattern to detect URLs
        String urlPattern = "(?i)\\b((?:https?|ftp|file):\\/\\/|www\\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(message);
        return matcher.find();
    }
}
