package org.karn.skilllib.util;

import org.bukkit.entity.Entity;

public class TypeFilter {
    public static boolean isDamageable(Entity entity) {
        return entity instanceof org.bukkit.entity.Damageable;
    }

    public static boolean isLivingEntity(Entity entity) {
        return entity instanceof org.bukkit.entity.LivingEntity;
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof org.bukkit.entity.Player;
    }

    public static boolean isProjectile(Entity entity) {
        return entity instanceof org.bukkit.entity.Projectile;
    }

    public static boolean isRemoved(Entity entity) {
        return entity.isDead() || !entity.isValid();
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof org.bukkit.entity.Vehicle;
    }
}
