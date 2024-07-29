package org.karn.skilllib.hitbox;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.karn.skilllib.util.EntityFinder;

import java.util.ArrayList;
import java.util.List;

public class LineHitbox {
    public static List<Entity> getEntities(World level, Vector start, Vector end, double width, int step){
        double dx = (end.getX() - start.getX()) / step;
        double dy = (end.getY() - start.getY()) / step;
        double dz = (end.getZ() - start.getZ()) / step;
        List<Entity> entities = new ArrayList<>();
        Vector current = start;
        for( int i = 0; i < step; i++ ){
            current = start.add(new Vector(dx*i,dy*i,dz*i));
            BoundingBox box = BoundingBox.of(current.add(new Vector(width, width, width)),current.add(new Vector(-width, -width, -width)));
            entities.addAll(level.getNearbyEntities(box));
            //level.getServer().overworld().sendParticles(ParticleTypes.END_ROD,current.x,current.y,current.z,1,0,0,0,0);
        }
        return entities.stream().distinct().toList();
    }

    public static List<LivingEntity> getLivingEntities(World level, Vector start, Vector end, double width, int step){
        return EntityFinder.filter(getEntities(level,start,end,width,step), LivingEntity.class);
    }

    public static List<Player> getPlayers(World level, Vector start, Vector end, double width, int step){
        return EntityFinder.filter(getEntities(level,start,end,width,step), Player.class);
    }
}
