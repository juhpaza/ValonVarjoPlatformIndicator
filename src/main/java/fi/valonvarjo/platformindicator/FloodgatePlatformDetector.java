package fi.valonvarjo.platformindicator;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

final class FloodgatePlatformDetector implements PlatformDetector {
    private final FloodgateApi floodgateApi;

    FloodgatePlatformDetector() {
        this.floodgateApi = FloodgateApi.getInstance();
        if (floodgateApi == null) {
            throw new IllegalStateException("FloodgateApi.getInstance() returned null");
        }
    }

    @Override
    public boolean isBedrock(Player sender) {
        return floodgateApi.isFloodgatePlayer(sender.getUniqueId());
    }
}
