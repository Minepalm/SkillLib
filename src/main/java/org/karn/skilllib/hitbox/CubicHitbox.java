package org.karn.skilllib.hitbox;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.karn.skilllib.util.EntityFinder;

import java.util.List;

public class CubicHitbox {
    public static List<Entity> getEntities(World level, Vector pos1, Vector pos2) {
        BoundingBox box = new BoundingBox(pos1.getX(),pos1.getY(), pos1.getZ(), pos2.getX(),pos2.getY(),pos2.getZ());
        return level.getNearbyEntities(box).stream().toList();
    }

    public static List<LivingEntity> getLivingEntities(World level, Vector pos1, Vector pos2){
        return EntityFinder.filter(getEntities(level,pos1,pos2), LivingEntity.class);
    }

    public static List<Player> getPlayers(World level, Vector pos1, Vector pos2){
        return EntityFinder.filter(getEntities(level,pos1,pos2), Player.class);
    }
}
