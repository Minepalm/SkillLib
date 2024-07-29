package org.karn.skilllib.hitbox;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.karn.skilllib.util.EntityFinder;
import org.karn.skilllib.util.SkillMath;

import java.util.ArrayList;
import java.util.List;

public class SphereHitbox {
    public static List<Entity> getEntities(World level, Vector pos1, double radius){
        List<Entity> list = CubicHitbox.getEntities(level,new Vector(pos1.getX() -radius,pos1.getY()-radius,pos1.getZ()-radius),new Vector(pos1.getX()+radius,pos1.getY()+radius,pos1.getZ()+radius));
        //System.out.println(list.toString());
        List<Entity> sphere = new ArrayList<Entity>();
        list.forEach(entity -> {
            BoundingBox box = entity.getBoundingBox();
            var points = SkillMath.getBoxPoints(box);
            for (Vector point : points) {
                double dx = entity.getLocation().getX() - pos1.getX();
                double dz = entity.getLocation().getZ() - pos1.getZ();
                double dy = entity.getLocation().getY() - pos1.getY();
                double distanceSquared = dx * dx + dz * dz + dy * dy;

                if (distanceSquared <= radius * radius){
                    sphere.add(entity);
                    break;
                }
            }
        });
        return sphere;
    }

    public static List<LivingEntity> getLivingEntities(World level, Vector pos1, double radius){
        return EntityFinder.filter(getEntities(level,pos1,radius), LivingEntity.class);
    }

    public static List<Player> getPlayers(World level, Vector pos1, double radius){
        return EntityFinder.filter(getEntities(level,pos1,radius), Player.class);
    }
}
