package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.karn.skilllib.util.SkillMath;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public class Arc extends Cylinder{
    private double startAngle;
    private double endAngle;

    public Arc(Location l, double up, double down, double radius, double startAngle, double endAngle) {
        super(l, up, down, radius);
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    public static Arc create(Location l, double up, double down, double radius, double startAngle, double endAngle) {
        return new Arc(l, up, down, radius, startAngle, endAngle);
    }

    public boolean isCollide(BoundingBox box) {
        if (center.getY() + ((up + down) * 0.5d) < box.getMinY() || center.getY() - ((up + down) * 0.5d) > box.getMaxY()) {
            return false;
        }

        var points = SkillMath.getBoxPoints(box);
        for (Vector point : points) {
            double dx = point.getX() - center.getX();
            double dz = point.getZ() - center.getZ();
            double distanceSquared = dx * dx + dz * dz;

            if (distanceSquared > radius * radius)
                continue;

            double angle = Math.toDegrees(Math.atan2(dz, dx));
            angle = (angle + 360) % 360 - ((angle + 360) % 360 > 180 ? 360 : 0);
            startAngle = (startAngle + 360) % 360 - ((startAngle + 360) % 360 > 180 ? 360 : 0);
            endAngle = (endAngle + 360) % 360 - ((endAngle + 360) % 360 > 180 ? 360 : 0);

            if (startAngle <= endAngle) {
                return angle >= startAngle && angle <= endAngle;
            } else {
                return angle >= startAngle || angle <= endAngle;
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    public Collection<Entity> getEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity -> isCollide(entity.getBoundingBox()) &&
                (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

    public Collection<Entity> getLivingEntities(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().isAlive() && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }

    public Collection<Entity> getPlayers(@Nullable Predicate<Entity> predicate){
        Predicate<Entity> fillter = entity ->
                entity.getType().equals(EntityType.PLAYER) && isCollide(entity.getBoundingBox()) &&
                        (predicate == null || predicate.test(entity));
        return Collider.getEntitiesInNearChunks(center.toLocation(world),diagonal,fillter);
    }
}
