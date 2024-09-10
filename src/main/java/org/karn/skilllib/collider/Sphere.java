package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

import static org.karn.skilllib.collider.Particle.Line;


public class Sphere extends Collider{
    private double radius;
    //-----------------------------------------------------------------------------------------------------------------------

    public Sphere(Location l,double radius){
        super(l.getWorld());
        this.center = l.toVector();
        this.radius = radius;
    }

    public static Sphere create(Location l,double radius){
        return new Sphere(l,radius);
    }

    public boolean isCollide(BoundingBox box) {
        Vector boxcenter = box.getCenter();
        double halfWidthX = box.getWidthX()/2.0;
        double halfHeight = box.getHeight()/2.0;
        double halfDepthZ = box.getWidthZ()/2.0;

        double closestX = Math.max(boxcenter.getX() - halfWidthX, Math.min(center.getX(), boxcenter.getX() + halfWidthX));
        double closestY = Math.max(boxcenter.getY() - halfHeight, Math.min(center.getY(), boxcenter.getY() + halfHeight));
        double closestZ = Math.max(boxcenter.getZ() - halfDepthZ, Math.min(center.getZ(), boxcenter.getZ() + halfDepthZ));

        return new Vector(closestX, closestY, closestZ).distanceSquared(center) <= Math.pow(radius, 2);
    }

    @Override
    public void draw(){
        Particle.Sphere("END_ROD",getCenter().toLocation(world),radius,360,0,0,0,0,0,true,null);
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
