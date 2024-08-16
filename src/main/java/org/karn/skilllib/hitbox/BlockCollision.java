package org.karn.skilllib.hitbox;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.karn.skilllib.util.SkillMath;

import java.util.Collection;

public class BlockCollision {
    public static boolean checkCollisionEntity(Entity entity, Block block){
        return checkCollisionBox(entity.getBoundingBox(), block);
    }

    public static boolean checkCollisionBox(BoundingBox box, Block block){
        Collection<BoundingBox> blockBox = block.getCollisionShape().getBoundingBoxes();
        for (BoundingBox b : blockBox) {
            b.shift(new Vector(block.getX(), block.getY(), block.getZ()));
            if(SkillMath.getBoxIntersect(box, b))
                return true;
        }
        return false;
    }
}
