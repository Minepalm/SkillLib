package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Cylinder extends Collider{
    protected double radius;
    protected double up;
    protected double down;
    protected double diagonal;
    //-----------------------------------------------------------------------------------------------------------------------

    public Cylinder(Location l, double up,double down,double radius){
        super(l.getWorld());
        double height = l.getY() - down + ((up+down)*0.5d);
        this.center = l.toVector().setY(height);
        this.radius = radius;
        this.up = up;
        this.down = down;
        Vector top = center.clone().setY(center.getY() + ((up+down)*0.5d));
        this.diagonal = this.center.distance(top);
    }

    public static Cylinder create(Location l, double up,double down,double radius){
        return new Cylinder(l,up,down,radius);
    }

    public boolean isCollide(BoundingBox box) {
        if(center.getY() + ((up+down)*0.5d) < box.getMinY() || center.getY() - ((up+down)*0.5d) > box.getMaxY()){
            return false;
        }

        for (Vector point : getPoints(box)) {
            Vector centerTemp = center.clone().setY(0);
            double dis = centerTemp.distance(point);
            if (dis < radius) return true;
        }

        return false;
    }

    private static List<Vector> getPoints(BoundingBox box){
        Vector min = box.getMin();
        Vector max = box.getMax();
        List<Vector> points = new ArrayList<>();
        points.add(new Vector(min.getX(),0,min.getZ()));
        points.add(new Vector(min.getX(),0,max.getZ()));
        points.add(new Vector(max.getX(),0,min.getZ()));
        points.add(new Vector(max.getX(),0,max.getZ()));
        return points;
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
