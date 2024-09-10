package org.karn.skilllib.collider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

import static org.bukkit.util.NumberConversions.round;
import static org.karn.skilllib.SkillLib.that;

public class Particle {
    private static final double TO_DEGREES = 180.0d / Math.PI;
    private static final double TO_RADIANS = Math.PI / 180.0d;

    public static Vector getVector(double yaw, double pitch) {
        double y = Math.sin(pitch * TO_RADIANS);
        double div = Math.cos(pitch * TO_RADIANS);
        double x = Math.cos(yaw * TO_RADIANS);
        double z = Math.sin(yaw * TO_RADIANS);
        x *= div;
        z *= div;
        return new Vector(x,-y,z).rotateAroundY(-Math.PI/2.0);
    }
    public static float getYaw(Vector vector) {
        if (((Double) vector.getX()).equals((double) 0) && ((Double) vector.getZ()).equals((double) 0)){
            return 0;
        }
        double y = (Math.atan2(vector.getZ(), vector.getX()) * TO_DEGREES) - 90;
        if (y < 0d){
            y += 360d;
        }
        else if (y > 360d){
            y -= 360d;
        }
        return (float)y;
    }

    public static float getPitch(Vector vector) {
        double xy = Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
        return (float) -(Math.atan(vector.getY() / xy) * TO_DEGREES);
    }


    public static void Line(String partictext, Location start, Location end,
                            double x, double y, double z, int amount, double speed, float density, boolean force, @Nullable Object data){
        if(!start.getWorld().equals(end.getWorld())){
            return;
        }
        org.bukkit.Particle particle;
        try{
            particle = org.bukkit.Particle.valueOf(partictext);
        }catch (IllegalArgumentException ex){
            particle = org.bukkit.Particle.CLOUD;
        }
        start = start.clone();
        end = end.clone();
        Vector v = (end.clone().subtract(start).toVector()).normalize().multiply(density);
        World w = start.getWorld();
        double distance = end.distance(start);
        int loop = round(distance / density);
        for (short i = 0; i <= loop; i++) {
            w.spawnParticle(particle,start,amount,x,y,z,speed,data,force);
            start.add(v);
        }
    }

    public static void Circle(String partictext, final Location c,double radius,int points,
                              double x,double y,double z,int amount,double speed,boolean force,@Nullable Object data){
        org.bukkit.Particle particle;
        try{
            particle = org.bukkit.Particle.valueOf(partictext);
        }catch (IllegalArgumentException ex){
            particle = org.bukkit.Particle.CLOUD;
        }
        Vector v = getVector(c.getYaw(),0).normalize().multiply(radius);
        double rotate = (Math.PI*2.0f)/points;
        World w = c.getWorld();
        for(short i=0; i<points ;i++){
            v.rotateAroundY(rotate);
            w.spawnParticle(particle,c.clone().add(v),amount,x,y,z,speed,data,force);
        }
    }

    public static void Cycle(String partictext, final Location c, double radius, Vector v, Vector rot,
                             int points, double degree, double time, boolean clockwise,
                             double x,double y,double z,int amount,double speed, boolean force,@Nullable Object data){
        org.bukkit.Particle particle;
        try{
            particle = org.bukkit.Particle.valueOf(partictext);
        }catch (IllegalArgumentException ex){
            particle = org.bukkit.Particle.CLOUD;
        }
        Vector vc = v.clone();
        Vector rotc = rot.clone();
        vc.normalize().multiply(radius);
        if(!clockwise){
            degree *= -1;
        }
        double angle = (degree/points)*(Math.PI/180.0f);
        int delay = round((double)points/time);
        World w = c.getWorld();
        for(int i = 1,j=0;i<= points;i++){
            if(i % delay == 0){
                j++;
            }
            org.bukkit.Particle finalParticle = particle;
            new BukkitRunnable() {
                public void run() {
                    vc.rotateAroundAxis(rotc,angle);
                    w.spawnParticle(finalParticle,c.clone().add(vc),amount,x,y,z,speed,data,force);
                }
            }.runTaskLater(that, j);
        }
    }

    public static void Sphere(String partictext,final Location c,double radius,int points,
                              double x,double y,double z,int amount,double speed,boolean force,@Nullable Object data){
        org.bukkit.Particle particle;
        try{
            particle = org.bukkit.Particle.valueOf(partictext);
        }catch (IllegalArgumentException ex){
            particle = org.bukkit.Particle.CLOUD;
        }
        Vector v = new Vector(radius,0,0);
        Vector v2 = v.clone().rotateAroundY(Math.PI/2.0d);
        double rotate = (Math.PI*2.0f)/points;
        World w = c.getWorld();
        for(int i=0,k = points/2; i<k ;i++){
            for(int j=0; j<points ;j++){
                v.rotateAroundAxis(v2,rotate);
                w.spawnParticle(particle,c.clone().add(v),amount,x,y,z,speed,data,force);
            }
            v.rotateAroundY(rotate);
            v2.rotateAroundY(rotate);
        }
    }
}
