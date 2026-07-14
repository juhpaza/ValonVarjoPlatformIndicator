package fi.valonvarjo.platformindicator;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ValonVarjoPlatformIndicatorPlugin extends JavaPlugin implements Listener {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private PlatformDetector platformDetector;
    private String permission;
    private Component bedrockIndicator;
    private Component javaIndicator;
    private Component spacing;
    private boolean debugLogPlatformDetection;
    private boolean useGeyserFallback;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadLocalConfig();
        platformDetector = createPlatformDetector();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Platform indicators enabled for recipients with " + permission);
    }

    private void reloadLocalConfig() {
        reloadConfig();
        permission = getConfig().getString("permission", "valonvarjo.chat.platformindicator");
        bedrockIndicator = loadIndicator("indicators.bedrock", "<gray>[</gray><dark_gray>B</dark_gray><gray>]</gray>");
        javaIndicator = loadIndicator("indicators.java", "<gray>[</gray><green>J</green><gray>]</gray>");
        spacing = Component.text(getConfig().getString("spacing", " "));
        debugLogPlatformDetection = getConfig().getBoolean("debug-log-platform-detection", false);
        useGeyserFallback = getConfig().getBoolean("detection.geyser-api-fallback", true);
    }

    private Component loadIndicator(String path, String fallback) {
        ConfigurationSection section = getConfig().getConfigurationSection(path);
        if (section == null) {
            return miniMessage.deserialize(fallback);
        }

        String openBracket = section.getString("open-bracket", "");
        String letter = section.getString("letter", "");
        String closeBracket = section.getString("close-bracket", "");
        return miniMessage.deserialize(openBracket + letter + closeBracket);
    }

    private PlatformDetector createPlatformDetector() {
        Plugin floodgate = Bukkit.getPluginManager().getPlugin("floodgate");
        if (floodgate != null && floodgate.isEnabled()) {
            try {
                getLogger().info("Using Floodgate API for Bedrock platform detection.");
                return new FloodgatePlatformDetector();
            } catch (NoClassDefFoundError | IllegalStateException ex) {
                getLogger().warning("Floodgate API was not available: " + ex.getMessage());
            }
        }

        if (useGeyserFallback) {
            Plugin geyser = Bukkit.getPluginManager().getPlugin("Geyser-Spigot");
            if (geyser != null && geyser.isEnabled()) {
                try {
                    getLogger().info("Using Geyser API fallback for Bedrock platform detection.");
                    return new GeyserPlatformDetector();
                } catch (IllegalStateException ex) {
                    getLogger().warning("Geyser API fallback was not available: " + ex.getMessage());
                }
            }
        }

        getLogger().warning("Floodgate is not loaded and Geyser API fallback is unavailable. All senders are treated as Java players.");
        return sender -> false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        ChatRenderer originalRenderer = event.renderer();
        boolean bedrockSender = platformDetector.isBedrock(event.getPlayer());
        Component indicator = bedrockSender ? bedrockIndicator : javaIndicator;
        if (debugLogPlatformDetection) {
            getLogger().info("Chat platform detection: sender=" + event.getPlayer().getName()
                    + ", uuid=" + event.getPlayer().getUniqueId()
                    + ", platform=" + (bedrockSender ? "BEDROCK" : "JAVA"));
        }

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component rendered = originalRenderer.render(source, sourceDisplayName, message, viewer);
            boolean canSeeIndicator = canSeeIndicator(viewer);
            if (debugLogPlatformDetection) {
                getLogger().info("Chat indicator viewer: sender=" + source.getName()
                        + ", viewer=" + viewerName(viewer)
                        + ", canSeeIndicator=" + canSeeIndicator);
            }
            if (!canSeeIndicator) {
                return rendered;
            }
            return indicator.append(spacing).append(rendered);
        });
    }

    private boolean canSeeIndicator(Audience viewer) {
        return viewer instanceof Player player && player.hasPermission(permission);
    }

    private String viewerName(Audience viewer) {
        if (viewer instanceof Player player) {
            return player.getName();
        }
        return viewer.getClass().getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadLocalConfig();
            sender.sendMessage("ValonVarjoPlatformIndicator reloaded.");
            getLogger().info("Configuration reloaded by " + sender.getName());
            return true;
        }

        sender.sendMessage("Usage: /" + label + " reload");
        return true;
    }
}
