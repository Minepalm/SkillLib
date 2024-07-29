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

public class CylinderHitbox {
    public static List<Entity> getEntities(World level, Vector pos1, double radius, double height){
        return getEntities(level,pos1,radius,height,0,360);
    }

    public static List<Entity> getEntities(World level, Vector pos1, double radius, double height, double startAngle, double endAngle){
        List<Entity> list = CubicHitbox.getEntities(level,new Vector(pos1.getX() -radius,pos1.getY(),pos1.getZ()-radius),new Vector(pos1.getX()+radius,pos1.getY()+height,pos1.getZ()+radius));
        //System.out.println(list.toString());
        List<Entity> cylinder = new ArrayList<Entity>();
        list.forEach(entity -> {
            BoundingBox box = entity.getBoundingBox();
            var points = SkillMath.getBoxPoints(box);
            for (Vector point : points) {
                double dx = point.getX() - pos1.getX();
                double dz = point.getZ() - pos1.getZ();
                double distanceSquared = dx * dx + dz * dz;

                if (distanceSquared > radius * radius)
                    continue;

                double angle = Math.toDegrees(Math.atan2(dz, dx));
                angle = (angle + 360) % 360; // 각도를 0-360 범위로 정규화
                if (angle > 180) angle -= 360;
                //System.out.println(entity.getName());
                //System.out.println("distanceSquared: " + distanceSquared);
                //System.out.println("angle: " + angle);
                //System.out.println("startAngle: " + startAngle + ", endAngle: " + endAngle);
                if (isAngleInRange(angle, startAngle, endAngle)) {
                    cylinder.add(entity);
                    break;
                }
            }
        });
        return cylinder;
    }

    public static List<LivingEntity> getLivingEntities(World level, Vector pos1, double radius, double height) {
        return EntityFinder.filter(getEntities(level,pos1,radius,height), LivingEntity.class);
    }

    public static List<LivingEntity> getLivingEntities(World level, Vector pos1, double radius, double height, double startAngle, double endAngle) {
        return EntityFinder.filter(getEntities(level,pos1,radius,height,startAngle,endAngle), LivingEntity.class);
    }

    public static List<Player> getPlayers(World level, Vector pos1, double radius, double height){
        return EntityFinder.filter(getEntities(level,pos1,radius,height), Player.class);
    }

    public static List<Player> getPlayers(World level, Vector pos1, double radius, double height, double startAngle, double endAngle){
        return EntityFinder.filter(getEntities(level,pos1,radius,height,startAngle,endAngle), Player.class);
    }

    private static boolean isAngleInRange(double angle, double start, double end) {
        // 시작 각도와 끝 각도를 -180에서 180 사이로 정규화
        start = normalizeAngle(start);
        end = normalizeAngle(end);

        if (start <= end) {
            return angle >= start && angle <= end;
        } else {
            return angle >= start || angle <= end;
        }
    }

    private static double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle > 180) angle -= 360;
        else if (angle < -180) angle += 360;
        return angle;
    }
}
