package org.karn.skilllib.collider;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

public class Collider {
    protected static final double BIGGEST_ENTITY_WIDTH = 10.0d;
    protected static final double BIGGEST_ENTITY_HEIGHT = 20.0d;
    protected static final double BIGGEST_ENTITY_WIDTH_HALF = BIGGEST_ENTITY_WIDTH/2.0d;
    public static final double BIGGEST_ENTITY_DIAGONAL_SIZE =
            Math.sqrt( (Math.pow(BIGGEST_ENTITY_WIDTH, 2)*2.0)  + Math.pow(BIGGEST_ENTITY_HEIGHT, 2));

    protected World world;
    protected Vector center;

    public Collider(World world){
        this.world = world;
        this.center = new Vector(0,0,0);
    }

    public Vector getCenter(){
        return center.clone();
    }

    public void draw(){

    }
    //---------------------------------------------------------------------------------------------------------------

    public static Collection<Entity> getEntitiesInNearChunks(Location location, double chunkrange, @Nullable Predicate<Entity> pred) {
        return getEntitiesInNearChunks(location,chunkrange,chunkrange,pred);
    }

    public static Collection<Entity> getEntitiesInNearChunks(Location location, double Xradius, double Zradius
            ,@Nullable Predicate<Entity> pred) {
        Xradius += BIGGEST_ENTITY_WIDTH_HALF;
        Zradius += BIGGEST_ENTITY_WIDTH_HALF;
        int xRangeInChunks = (int) Math.ceil(Xradius / 16.0d);
        int zRangeInChunks = (int) Math.ceil(Zradius / 16.0d);

        Collection<Entity> nearbyEntities = new ArrayList<>();
        if(xRangeInChunks >= 12 || zRangeInChunks >= 12 || xRangeInChunks*zRangeInChunks > (8*8)){
            nearbyEntities = location.getWorld().getEntities();
            if(pred != null){
                nearbyEntities.removeIf(pred.negate());
            }
            return nearbyEntities;
        }
        World world = location.getWorld();


        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        if (pred == null){
            for (int dx = -xRangeInChunks; dx <= xRangeInChunks; dx++) {
                for (int dz = -zRangeInChunks; dz <= zRangeInChunks; dz++) {
                    Chunk chunk = world.getChunkAt(chunkX + dx, chunkZ + dz);
                    if (!chunk.isLoaded() || !chunk.isEntitiesLoaded()) {
                        continue;
                    }
                    Collections.addAll(nearbyEntities, chunk.getEntities());
                }
            }
        }else{
            for (int dx = -xRangeInChunks; dx <= xRangeInChunks; dx++) {
                for (int dz = -zRangeInChunks; dz <= zRangeInChunks; dz++) {
                    Chunk chunk = world.getChunkAt(chunkX + dx, chunkZ + dz);
                    if (!chunk.isLoaded() || !chunk.isEntitiesLoaded()) {
                        continue;
                    }
                    for (Entity entity : chunk.getEntities()) {
                        if(pred.test(entity)){
                            nearbyEntities.add(entity);
                        }
                    }
                }
            }
        }
        return nearbyEntities;
    }

    //----------------------------------------------------------------------------------------------------------
    public static Location getMinDistLoc_DotWithAABB(Location l, Entity e){
        if(!Objects.equals(e.getWorld(),l.getWorld())){
            return null;
        }
        return getMinDistLoc_DotWithAABB(l.toVector(),e.getBoundingBox()).toLocation(l.getWorld());
    }

    public static Location getMinDistLoc_DotWithAABB(Location l, BoundingBox box){
        return getMinDistLoc_DotWithAABB(l.toVector(),box).toLocation(l.getWorld());
    }

    public static Vector getMinDistLoc_DotWithAABB(Vector l, BoundingBox box){
        Vector boxcenter = box.getCenter();
        double halfWidthX = box.getWidthX()/2.0;
        double halfHeight = box.getHeight()/2.0;
        double halfDepthZ = box.getWidthZ()/2.0;

        double closestX = Math.max(boxcenter.getX() - halfWidthX, Math.min(l.getX(), boxcenter.getX() + halfWidthX));
        double closestY = Math.max(boxcenter.getY() - halfHeight, Math.min(l.getY(), boxcenter.getY() + halfHeight));
        double closestZ = Math.max(boxcenter.getZ() - halfDepthZ, Math.min(l.getZ(), boxcenter.getZ() + halfDepthZ));

        return new Vector(closestX, closestY, closestZ);
    }


    public static Location getMinDistLoc_DotWithLine(Location pointC,Location Linestart, Location Lineend){
        if(Linestart.getWorld() != Lineend.getWorld() || Linestart.getWorld() != pointC.getWorld()){
            return null;
        }
        return getMinDistLoc_DotWithLine(pointC.toVector(),Linestart.toVector(),Lineend.toVector()).toLocation(pointC.getWorld());
    }

    public static Vector getMinDistLoc_DotWithLine(Vector pointC,Vector Linestart, Vector Lineend){
        Vector lineAB = Lineend.clone().subtract(Linestart);
        Vector lineAC = pointC.clone().subtract(Linestart);
        double lineABLengthSquared = lineAB.lengthSquared();
        double dotProduct = lineAC.dot(lineAB);
        if (dotProduct <= 0) {
            return Linestart;
        }
        if (dotProduct >= lineABLengthSquared) {
            return Lineend;
        }
        double distanceAlongLine = dotProduct / lineABLengthSquared;
        return lineAB.multiply(distanceAlongLine).add(Linestart);
    }

    public static Location getMinDistLoc_DotWithVertical(Location pointC, Location lineStart, double height) {
        if(lineStart.getWorld() != pointC.getWorld()) {
            return null;
        }
        return getMinDistLoc_DotWithVertical(pointC.toVector(),lineStart.toVector(),height).toLocation(pointC.getWorld());
    }

    public static Vector getMinDistLoc_DotWithVertical(Vector pointC, Vector lineStart, double height) {
        Vector lineEnd = lineStart.clone().setY(lineStart.getY()+height);
        double clampedY = Math.max(lineStart.getY(), Math.min(pointC.getY(), lineEnd.getY()));
        // 반환할 Location 객체 생성, x와 z는 선분의 시작점 또는 끝점과 동일
        return new Vector(lineStart.getX(), clampedY, lineStart.getZ());
    }

}
