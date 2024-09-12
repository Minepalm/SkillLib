package org.karn.skilllib;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;
import static org.karn.skilllib.action.Rotation.PLAYER_DIR;

public final class SkillLib extends JavaPlugin implements Listener {

    public static SkillLib that; //in your case "plugin" would be "Main.that"

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("SkillLib online!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        double dis = from.distance(to);
        if (dis < 0.02) return;
        double degree = Math.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX())) - 90;
        degree = (degree + 360) % 360;
        if (degree > 180) degree -= 360;
        PLAYER_DIR.put(event.getPlayer().getUniqueId(), degree);
    }
}
