package org.karn.skilllib.hitbox;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class BlockCollision {
    public static boolean checkCollisionEntity(Entity entity, Block block){
        return checkCollisionBox(entity.getBoundingBox(), block);
    }

    public static boolean checkCollisionBox(BoundingBox box, Block block){
        Vector len = new Vector(box.getWidthX() / 2.0, box.getHeight() / 2.0, box.getWidthZ() / 2.0);
        Location blockLoc = block.getLocation();
        Collection<BoundingBox> blockBox = block.getCollisionShape().getBoundingBoxes();

        for (BoundingBox b : blockBox) {
            if (box.intersection(b) != null) return true;
        }
        return false;
    }
}
