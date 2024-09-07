package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

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

    public boolean isCollide(BoundingBox box) {
        return aabb.overlaps(box);
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
