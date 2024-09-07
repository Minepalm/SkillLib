package org.karn.skilllib.collider;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class Collider {
    private static final double BIGGEST_ENTITY_WIDTH = 10.0d;
    private static final double BIGGEST_ENTITY_HEIGHT = 20.0d;
    private static final double BIGGEST_ENTITY_WIDTH_HALF = BIGGEST_ENTITY_WIDTH/2.0d;
    public static final double BIGGEST_ENTITY_DIAGONAL_SIZE =
            Math.sqrt( (Math.pow(BIGGEST_ENTITY_WIDTH, 2)*2.0)  + Math.pow(BIGGEST_ENTITY_HEIGHT, 2));

    protected World world;
    protected Vector center;

    public Collider(World world){
        this.world = world;
        this.center = new Vector(0,0,0);
    }

    public Vector getCenter(){
        return center;
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
}
