package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import static org.karn.skilllib.collider.Particle.Line;
import static org.karn.skilllib.collider.Particle.getVector;


public class Capsule extends Collider{
    private Location start;
    private Vector direction;
    private double range;
    private double lineRadius;
    //-----------------------------------------------------------------------------------------------------------------------

    public Capsule(Location start,Vector direction,double range,double radius){
        super(start.getWorld());
        if(direction.isZero()){
            range = 0.0;
            this.center = start.toVector();
        }else{
            this.center = start.toVector().add(direction.clone().normalize().multiply(range*0.5d));
        }
        this.start = start.clone();
        this.direction = direction.clone();
        this.range = range;
        this.lineRadius = radius;
    }

    public static Capsule create(Location start,Vector direction,double range,double radius){
        return new Capsule(start,direction,range,radius);
    }

    public static Collection<Entity> sortNearest(Capsule capsule, Collection<Entity> collection){
        ArrayList<Entity> sortedList = new ArrayList<>(collection);
        Vector startpoint = capsule.start.toVector();
        sortedList.sort((e1, e2) -> {
            RayTraceResult result1 = e1.getBoundingBox().expand(capsule.lineRadius)
                    .rayTrace(startpoint, capsule.direction, capsule.range);
            double dist1;
            if (result1 == null) {
                dist1 = Double.MAX_VALUE;
            } else {
                dist1 = startpoint.distanceSquared(result1.getHitPosition());
            }

            RayTraceResult result2 = e2.getBoundingBox().expand(capsule.lineRadius)
                    .rayTrace(startpoint, capsule.direction, capsule.range);
            double dist2;
            if (result2 == null) {
                dist2 = Double.MAX_VALUE;
            } else {
                dist2 = startpoint.distanceSquared(result2.getHitPosition());
            }
            return Double.compare(dist1, dist2);
        });
        return sortedList; // 정렬된 리스트 반환
    };

    public boolean isCollide(Location l) {
        if(!Objects.equals(l.getWorld(),world)){
            return false;
        }
        return isCollide(l.toVector());
    }

    public boolean isCollide(Vector v) {
        Vector end = start.toVector().add(direction.normalize().multiply(range));
        return getMinDistLoc_DotWithLine(v,start.toVector(),end).distanceSquared(v) <= lineRadius*lineRadius;
    }

    public boolean isCollide(Entity e) {
        if(!Objects.equals(e.getWorld(),world)){
            return false;
        }
        return isCollide(e.getBoundingBox());
    }
    public boolean isCollide(BoundingBox box) {
        return box.clone().expand(lineRadius).rayTrace(start.toVector(), direction, range) != null;
    }

    @Override
    public void draw(){
        if(direction.isZero()){
            Particle.Sphere("END_ROD",getCenter().toLocation(world),
                    lineRadius,60,0,0,0,0,0,true,null);
            return;
        }
        Location end = start.clone().add(direction.clone().normalize().multiply(range));

        Vector v = Particle.getVector(Particle.getYaw(direction),Particle.getPitch(direction)-90.0d);

        Vector v2 = Particle.getVector(Particle.getYaw(direction)+90.0d,0);

        Particle.Cycle("END_ROD",start,lineRadius,v,direction,
                60,360,0,true,0,0,0,0,0,true,null);
        Particle.Cycle("END_ROD",end,lineRadius,v,direction,
                60,360,0,true,0,0,0,0,0,true,null);

        Particle.Cycle("END_ROD",start,lineRadius,direction,v,
                60,360,0,true,0,0,0,0,0,true,null);
        Particle.Cycle("END_ROD",end,lineRadius,direction,v,
                60,360,0,true,0,0,0,0,0,true,null);

        Particle.Cycle("END_ROD",start,lineRadius,direction,v2,
                60,360,0,true,0,0,0,0,0,true,null);
        Particle.Cycle("END_ROD",end,lineRadius,direction,v2,
                60,360,0,true,0,0,0,0,0,true,null);

        Line("END_ROD",start,end,0,0,0,0,0,0.1f,true,null);
    }
    //-----------------------------------------------------------------------------------------------------------------------
    public Collection<Entity> getEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity -> isCollide(entity.getBoundingBox()) &&
                (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),range+lineRadius,fillter);
    }

    public Collection<Entity> getLivingEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().isAlive() && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),range+lineRadius,fillter);
    }

    public Collection<Entity> getPlayers(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().equals(EntityType.PLAYER) && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),range+lineRadius,fillter);
    }

}
