package fi.valonvarjo.platformindicator;

import java.lang.reflect.Method;
import org.bukkit.entity.Player;

final class GeyserPlatformDetector implements PlatformDetector {
    private final Object geyserApi;
    private final Method connectionByUuidMethod;

    GeyserPlatformDetector() {
        try {
            Class<?> geyserApiClass = Class.forName("org.geysermc.geyser.api.GeyserApi");
            Method apiMethod = geyserApiClass.getMethod("api");
            this.geyserApi = apiMethod.invoke(null);
            if (geyserApi == null) {
                throw new IllegalStateException("GeyserApi.api() returned null");
            }
            this.connectionByUuidMethod = geyserApiClass.getMethod("connectionByUuid", java.util.UUID.class);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Geyser API was not available", ex);
        }
    }

    @Override
    public boolean isBedrock(Player sender) {
        try {
            return connectionByUuidMethod.invoke(geyserApi, sender.getUniqueId()) != null;
        } catch (ReflectiveOperationException ex) {
            return false;
        }
    }
}
