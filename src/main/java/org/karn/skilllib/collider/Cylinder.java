package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Cylinder extends Collider{
    private double radius;
    private double up;
    private double down;
    private double diagonal;
    //-----------------------------------------------------------------------------------------------------------------------

    public Cylinder(Location l, double up,double down,double radius){
        super(l.getWorld());
        double height = center.getY() - down + ((up+down)*0.5d);
        this.center = l.toVector().setY(height);
        this.radius = radius;
        this.up = up;
        this.down = down;
        Vector top = center.clone().setY(center.getY()+ ((up+down)*0.5d) );
        this.diagonal = this.center.distance(top);
    }

    public static Cylinder create(Location l, double up,double down,double radius){
        return new Cylinder(l,up,down,radius);
    }

    public boolean isCollide(BoundingBox box) {
        if( abs(center.getY()-box.getMin().getY()) > abs(max(up,down)) + box.getHeight()){
            return false;
        }
        Vector centerbox = box.getCenter();
        double halfWidthX = box.getWidthX() / 2.0;
        double halfHeight = box.getHeight() / 2.0;

        double closestX = Math.max(centerbox.getX() - halfWidthX, Math.min(center.getX(), centerbox.getX() + halfWidthX));
        double closestZ = Math.max(centerbox.getZ() - halfWidthX, Math.min(center.getZ(), centerbox.getZ() + halfWidthX));

        double distanceSquared = new Vector(closestX, center.getY(), closestZ).distanceSquared(center);
        if (distanceSquared > Math.pow(radius, 2)) {
            return false;
        }

        double closestY = Math.max(centerbox.getY() - halfHeight, Math.min(center.getY(), centerbox.getY() + halfHeight));
        return !(closestY < center.getY() - down || closestY > center.getY() + up);
    }
    //-----------------------------------------------------------------------------------------------------------------------
    public Collection<Entity> getEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity -> isCollide(entity.getBoundingBox()) &&
                (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

    public Collection<Entity> getLivingEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().isAlive() && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

    public Collection<Entity> getPlayers(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().equals(EntityType.PLAYER) && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

}
