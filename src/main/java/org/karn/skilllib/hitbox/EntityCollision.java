package org.karn.skilllib.hitbox;

import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.karn.skilllib.util.SkillMath;

public class EntityCollision {
    public static boolean checkCollisionEntity(Entity entity, Entity target){
        return checkCollisionBox(entity.getBoundingBox(), target);
    }

    public static boolean checkCollisionBox(BoundingBox box, Entity target){
        return SkillMath.getBoxIntersect(box, target.getBoundingBox());
    }
}
