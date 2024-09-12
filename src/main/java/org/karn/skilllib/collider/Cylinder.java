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
import java.util.Objects;
import java.util.function.Predicate;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static org.karn.skilllib.collider.Particle.Line;

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
        this.diagonal = (new Vector(radius*2,up+down,0)).length();
    }

    public static Cylinder create(Location l, double up,double down,double radius){
        return new Cylinder(l,up,down,radius);
    }

    public boolean isCollide(Location l) {
        if(!Objects.equals(l.getWorld(),world)){
            return false;
        }
        return isCollide(l.toVector());
    }

    public boolean isCollide(Vector v) {
        Vector cylcenter = getCenter();

        double height = down + up;
        double y = v.getY();
        if (y < (cylcenter.getY() - height*0.5d) || y > (cylcenter.getY() + height*0.5d)){
            return false; // Y좌표가 원통의 수직 범위 내에 있는지 반환
        }

        Vector delta = v.clone().subtract(cylcenter);
        double distanceSquared = delta.getX() * delta.getX() + delta.getZ() * delta.getZ();
        return distanceSquared <= Math.pow(radius, 2);
    }

    public boolean isCollide(Entity e) {
        if(!Objects.equals(e.getWorld(),world)){
            return false;
        }
        return isCollide(e.getBoundingBox());
    }

    public boolean isCollide(BoundingBox box) {
        Vector cylcenter = getCenter();
        Vector centerbox = box.getCenter();
        double halfWidthX = box.getWidthX() / 2.0;
        double halfHeight = box.getHeight() / 2.0;

        double closestX = Math.max(centerbox.getX() - halfWidthX, Math.min(cylcenter.getX(), centerbox.getX() + halfWidthX));
        double closestZ = Math.max(centerbox.getZ() - halfWidthX, Math.min(cylcenter.getZ(), centerbox.getZ() + halfWidthX));

        double distanceSquared = new Vector(closestX, cylcenter.getY(), closestZ).distanceSquared(cylcenter);
        if (distanceSquared > Math.pow(radius, 2)) {
            return false;
        }

        double closestY = Math.max(centerbox.getY() - halfHeight, Math.min(cylcenter.getY(), centerbox.getY() + halfHeight));
        return !(closestY < cylcenter.getY() - down || closestY > cylcenter.getY() + up);
    }

    @Override
    public void draw(){
        double height = (up+down);
        Location top = getCenter().toLocation(world).add(0,height/2.0d,0);
        Location down = getCenter().toLocation(world).subtract(0,height/2.0d,0);
        Particle.Circle("END_ROD", top,radius,360,0,0,0,0,0,true,null);
        Particle.Circle("END_ROD", getCenter().toLocation(world),radius,360,0,0,0,0,0,true,null);
        Particle.Circle("END_ROD", down,radius,360,0,0,0,0,0,true,null);

        Vector linestart = new Vector(0,0,radius);
        for(int i = 0; i < 8 ; i++){
            Location start = down.clone().add(linestart);
            Location end = start.clone().add(0,height,0);
            Line("END_ROD",start,end,0,0,0,0,0,0.1f,true,null);
            linestart.rotateAroundY(Math.PI/4);
        }
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
