package org.karn.skilllib.collider;


import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public class SphericalCap extends Sphere{
    public Vector direction;
    public double degree;

    public SphericalCap(Location center, Vector direction, double degree) {
        super(center,direction.length());
        if (direction.isZero()){
            direction = new Vector(0,0,1);
        }
        this.direction = direction.clone().normalize();
        this.degree = degree;
    }

    public SphericalCap(Location center, Vector direction, double radius, double degree) {
        super(center,radius);
        this.direction = direction.clone().normalize();
        this.degree = degree;
    }

    public static SphericalCap create(Location center, Vector direction, double degree){
        return new SphericalCap(center,direction,degree);
    }

    public static SphericalCap create(Location center, Vector direction, double radius, double degree){
        return new SphericalCap(center,direction,radius,degree);
    }


    @Override
    public boolean isCollide(Location l) {
        if(!Objects.equals(l.getWorld(),world)){
            return false;
        }
        return isCollide(l.toVector());
    }

    @Override
    public boolean isCollide(Vector v) {
        double angle = Math.toRadians(degree);
        return v.clone().subtract(getCenter()).angle(direction) <= angle;
    }

    @Override
    public boolean isCollide(Entity e) {
        if(!Objects.equals(e.getWorld(),world)){
            return false;
        }
        return isCollide(e.getBoundingBox());
    }

    @Override
    public boolean isCollide(BoundingBox box){
        double radiusSq = this.radius * this.radius;
        Vector capcenter = getCenter();
        if(getMinDistLoc_DotWithAABB(capcenter,box).distanceSquared(capcenter) > radiusSq){
            return false;
        }

        double angle = Math.toRadians(degree);
        if(box.getCenter().subtract(capcenter).angle(direction) <= angle){
            return true;
        }

        RayTraceResult result = box.rayTrace(capcenter,direction,radius);
        if(result != null) {
            return true;
        }

        for(Vector corner : AABB.getAllPoints(box)){
            Vector temp = corner.clone().subtract(capcenter);
            if(temp.lengthSquared() <= radiusSq && temp.angle(direction) <= angle){
                return true;
            }
        }

        Vector linebot = new Vector(box.getCenterX(),box.getMinY(),box.getCenterX());
        Vector minlinedistloc = getMinDistLoc_DotWithVertical(capcenter,linebot,box.getHeight());
        return minlinedistloc.subtract(capcenter).angle(direction) <= angle;
    }
    //-----------------------------------------------------------------------------------------------------------------------

    public Collection<Entity> getEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity -> isCollide(entity.getBoundingBox()) &&
                (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),radius,fillter);
    }

    public Collection<Entity> getLivingEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().isAlive() && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),radius,fillter);
    }

    public Collection<Entity> getPlayers(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().equals(EntityType.PLAYER) && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),radius,fillter);
    }


}