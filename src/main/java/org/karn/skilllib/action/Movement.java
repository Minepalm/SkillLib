package org.karn.skilllib.action;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class Movement {
    public static void addRotation(Player p, float yaw, float pitch){
        ((CraftPlayer) p).getHandle().connection.send(new ClientboundPlayerPositionPacket(0,0,0,yaw,pitch, Set.of(RelativeMovement.values()), -1));
    }

    public static void setRotation(Player p, float yaw, float pitch){
        ((CraftPlayer) p).getHandle().connection.send(new ClientboundPlayerPositionPacket(0,0,0,yaw-((CraftPlayer) p).getYaw(),pitch-((CraftPlayer) p).getPitch(), Set.of(RelativeMovement.values()), -1));
    }

    public static void addPosition(Player p, double x, double y, double z){
        ((CraftPlayer) p).getHandle().connection.send(new ClientboundPlayerPositionPacket(x,y,z,0,0, Set.of(RelativeMovement.values()), -1));
    }

    public static void addVelocity(Player p, double x, double y, double z){
        ((CraftPlayer) p).getHandle().connection.send(new ClientboundSetEntityMotionPacket(p.getEntityId(),new Vec3(x,y,z)));
    }
}
