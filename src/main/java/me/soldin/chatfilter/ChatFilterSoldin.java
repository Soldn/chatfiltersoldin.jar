package me.soldin.chatfilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ChatFilterSoldin extends JavaPlugin implements Listener {

    private List<String> blockedWords;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadBlockedWords();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("ChatFilterSoldin включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatFilterSoldin выключен!");
    }

    private void reloadBlockedWords() {
        FileConfiguration config = getConfig();
        blockedWords = config.getStringList("blocked-words");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("chatfilter.bypass")) return;

        String message = event.getMessage();

        for (String word : blockedWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                message = message.replaceAll("(?i)" + word, "****");
            }
        }

        event.setMessage(message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Используй: /chatfilter reload | info");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("chatfilter.reload")) {
                sender.sendMessage(ChatColor.RED + "Нет прав!");
                return true;
            }
            reloadConfig();
            reloadBlockedWords();
            sender.sendMessage(ChatColor.GREEN + "ChatFilterSoldin конфиг перезагружен!");
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            sender.sendMessage(ChatColor.AQUA + "Запрещённые слова:");
            for (String word : blockedWords) {
                sender.sendMessage(ChatColor.GRAY + "- " + word);
            }
            return true;
        }

        return false;
    }
}
