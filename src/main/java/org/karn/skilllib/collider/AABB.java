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
import java.util.Objects;
import java.util.function.Predicate;

import static org.karn.skilllib.collider.Particle.Line;

public class AABB extends Collider{
    private BoundingBox aabb;
    private double diagonal;
    //-----------------------------------------------------------------------------------------------------------------------
    public AABB(World w,BoundingBox box){
        super(w);
        this.aabb = box.clone();
        this.center = this.aabb.getCenter();
        this.diagonal = this.center.distance(this.aabb.getMax());
    }

    public AABB(Location l1, Location l2){
        super(l1.getWorld());
        if(!Objects.equals(l1.getWorld(), l2.getWorld())){
            throw new IllegalArgumentException("l1과 l2는 같은 월드에 있어야 합니다.");
        }
        this.aabb = BoundingBox.of(l1,l2);
        this.center = this.aabb.getCenter();
    }

    public AABB(Location l, double up,double side,double down){
        super(l.getWorld());
        this.aabb = new BoundingBox(
                l.getX()+side,l.getY()+up,l.getZ()+side
                ,l.getX()-side,l.getY()-down,l.getZ()-side);
        this.center = this.aabb.getCenter();
    }

    public static AABB create(World w,BoundingBox box){
        return new AABB(w,box);
    }

    public static AABB create(Location l1, Location l2){
        return new AABB(l1,l2);
    }

    public static AABB create(Location l, double up,double side,double down){
        return new AABB(l,up,side,down);
    }


    public boolean isCollide(Location l) {
        if(!Objects.equals(l.getWorld(),world)){
            return false;
        }
        return isCollide(l.toVector());
    }

    public boolean isCollide(Vector v) {
        return aabb.contains(v);
    }

    public boolean isCollide(Entity e) {
        if(!Objects.equals(e.getWorld(),world)){
            return false;
        }
        return isCollide(e.getBoundingBox());
    }

    public boolean isCollide(BoundingBox box) {
        return aabb.overlaps(box);
    }

    public Location[] getAllPoints(){
        Location[] array = {
                new Location(this.world,aabb.getMinX(), aabb.getMinY(), aabb.getMinZ()),
                new Location(this.world,aabb.getMinX(), aabb.getMinY(), aabb.getMaxZ()),
                new Location(this.world,aabb.getMinX(), aabb.getMaxY(), aabb.getMinZ()),
                new Location(this.world,aabb.getMinX(), aabb.getMaxY(), aabb.getMaxZ()),
                new Location(this.world,aabb.getMaxX(), aabb.getMinY(), aabb.getMinZ()),
                new Location(this.world,aabb.getMaxX(), aabb.getMinY(), aabb.getMaxZ()),
                new Location(this.world,aabb.getMaxX(), aabb.getMaxY(), aabb.getMinZ()),
                new Location(this.world,aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ())
        };
        return array;
    }

    public static Vector[] getAllPoints(BoundingBox aabb){
        Vector[] array = {
                new Vector(aabb.getMinX(), aabb.getMinY(), aabb.getMinZ()),
                new Vector(aabb.getMinX(), aabb.getMinY(), aabb.getMaxZ()),
                new Vector(aabb.getMinX(), aabb.getMaxY(), aabb.getMinZ()),
                new Vector(aabb.getMinX(), aabb.getMaxY(), aabb.getMaxZ()),
                new Vector(aabb.getMaxX(), aabb.getMinY(), aabb.getMinZ()),
                new Vector(aabb.getMaxX(), aabb.getMinY(), aabb.getMaxZ()),
                new Vector(aabb.getMaxX(), aabb.getMaxY(), aabb.getMinZ()),
                new Vector(aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ())
        };
        return array;
    }

    @Override
    public void draw(){
        Location[] points = getAllPoints();
        Line("END_ROD",points[0],points[1],0,0,0,0,0,0.1f,true,null);
        Line("END_ROD",points[0],points[2],0,0,0,0,0,0.1f,true,null);
        Line("END_ROD",points[0],points[4],0,0,0,0,0,0.1f,true,null);

        Line("END_ROD",points[1],points[3],0,0,0,0,0,0.1f,true,null);
        Line("END_ROD",points[1],points[5],0,0,0,0,0,0.1f,true,null);

        Line("END_ROD",points[2],points[3],0,0,0,0,0,0.1f,true,null);
        Line("END_ROD",points[2],points[6],0,0,0,0,0,0.1f,true,null);

        Line("END_ROD",points[3],points[7],0,0,0,0,0,0.1f,true,null);

        Line("END_ROD",points[4],points[5],0,0,0,0,0,0.1f,true,null);
        Line("END_ROD",points[4],points[6],0,0,0,0,0,0.1f,true,null);

        Line("END_ROD",points[5],points[7],0,0,0,0,0,0.1f,true,null);

        Line("END_ROD",points[6],points[7],0,0,0,0,0,0.1f,true,null);
    }
    //-----------------------------------------------------------------------------------------------------------------------
    public Collection<Entity> getEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity -> entity.getBoundingBox().overlaps(this.aabb) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

    public Collection<Entity> getLivingEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().isAlive() && entity.getBoundingBox().overlaps(this.aabb) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

    public Collection<Entity> getPlayers(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().equals(EntityType.PLAYER) && entity.getBoundingBox().overlaps(this.aabb) &&
                (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }
}
