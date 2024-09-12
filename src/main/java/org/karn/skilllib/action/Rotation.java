package org.karn.skilllib.action;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rotation implements Listener {
    public static Map<UUID, Double> PLAYER_DIR = new HashMap<>();

    public static double getRelativeDirection(Player player){
        double deg = getAbsoluteDirection(player) - player.getLocation().getYaw();
        if (deg > 180) deg -= 360;
        if (deg < -180) deg += 360;
        return deg;
    }

    public static Double getAbsoluteDirection(Player player) {
        return PLAYER_DIR.get(player.getUniqueId());
    }
}
