package fi.valonvarjo.platformindicator;

import org.bukkit.entity.Player;

@FunctionalInterface
interface PlatformDetector {
    boolean isBedrock(Player sender);
}
