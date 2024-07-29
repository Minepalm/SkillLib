package org.karn.skilllib.hitbox;

import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class EntityCollision {
    public static boolean checkCollisionEntity(Entity entity, Entity target){
        return checkCollisionBox(entity.getBoundingBox(), target);
    }

    public static boolean checkCollisionBox(BoundingBox box, Entity target){
        Vector len = new Vector(box.getWidthX() / 2.0, box.getHeight() / 2.0, box.getWidthZ() / 2.0);
        BoundingBox targetBox = target.getBoundingBox();

        if (box.intersection(targetBox) != null)
            return true;
        return false;
    }
}
