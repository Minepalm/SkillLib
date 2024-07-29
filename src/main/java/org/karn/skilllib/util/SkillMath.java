package org.karn.skilllib.util;

import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillMath {
    public static Vector getUnitVector(Vector vec1, Vector vec2){
        Vector a = new Vector(vec2.getX()-vec1.getX(),vec2.getY()-vec1.getY(),vec2.getZ()-vec1.getZ());
        double m = java.lang.Math.sqrt((float) (a.getX()*a.getX() + a.getY()*a.getY() + a.getZ()*a.getZ()));
        return new Vector(-a.getX()/m,-a.getY()/m,-a.getZ()/m);
    }
    public static Vector getLocalPos(Vector pos, Vector rot, double forward, double up, double right){
        return getLocalPos(pos,rot,new Vector(forward,up,right));
    }

    public static Vector getLocalPos(Vector pos, Vector rot, Vector movement) {
        float var4 = (float) java.lang.Math.cos((rot.getY() + 90.0F) * (float) (java.lang.Math.PI / 180.0));
        float var5 = (float) java.lang.Math.sin((rot.getY() + 90.0F) * (float) (java.lang.Math.PI / 180.0));
        float var6 = (float) java.lang.Math.cos(-rot.getX() * (float) (java.lang.Math.PI / 180.0));
        float var7 = (float) java.lang.Math.sin(-rot.getX() * (float) (java.lang.Math.PI / 180.0));
        float var8 = (float) java.lang.Math.cos((-rot.getX() + 90.0F) * (float) (java.lang.Math.PI / 180.0));
        float var9 = (float) java.lang.Math.sin((-rot.getX() + 90.0F) * (float) (java.lang.Math.PI / 180.0));
        Vector var10 = new Vector((double) (var4 * var6), (double) var7, (double) (var5 * var6));
        Vector var11 = new Vector((double) (var4 * var8), (double) var9, (double) (var5 * var8));
        Vector var12 = var10.getCrossProduct(var11).multiply(-1.0);
        double var13 = var10.getX() * movement.getZ() + var11.getX() * movement.getY() + var12.getX() * movement.getX();
        double var15 = var10.getY() * movement.getZ() + var11.getY() * movement.getY() + var12.getY() * movement.getX();
        double var17 = var10.getZ() * movement.getZ() + var11.getZ() * movement.getY() + var12.getZ() * movement.getX();
        return new Vector(pos.getX() + var13, pos.getY() + var15, pos.getZ() + var17);
    }

    public static boolean isAngleInRange(double angle, double startAngle, double endAngle) {
        if (startAngle <= endAngle) {
            return angle >= startAngle && angle <= endAngle;
        } else {
            return angle >= startAngle || angle <= endAngle;
        }
    }

    public static List<Vector> getBoxPoints(BoundingBox box){
        return getBoxPoints(box.getMin(),box.getMax());
    }

    public static List<Vector> getBoxPoints(Vector pos1, Vector pos2){
        List<Vector> points = new ArrayList<>();

        // 8개의 모서리 점
        Vector[] corners = {
                pos1,
                new Vector(pos1.getX(), pos1.getY(), pos2.getZ()),
                new Vector(pos1.getX(), pos2.getY(), pos1.getZ()),
                new Vector(pos1.getX(), pos2.getY(), pos2.getZ()),
                new Vector(pos2.getX(), pos1.getY(), pos1.getZ()),
                new Vector(pos2.getX(), pos1.getY(), pos2.getZ()),
                new Vector(pos2.getX(), pos2.getY(), pos1.getZ()),
                pos2
        };

        points.addAll(Arrays.asList(corners));

        // 모서리 점들 사이의 중간점 추가
        for (int i = 0; i < corners.length; i++) {
            for (int j = i + 1; j < corners.length; j++) {
                points.add(getMidpoint(corners[i], corners[j]));
            }
        }
        points.add(getMidpoint(pos1, pos2)); // 전체 박스의 중심점
        points.add(new Vector(pos1.getX(), getMidpoint(pos1, pos2).getY(), getMidpoint(pos1, pos2).getZ())); // 왼쪽 면 중심
        points.add(new Vector(pos2.getX(), getMidpoint(pos1, pos2).getY(), getMidpoint(pos1, pos2).getZ())); // 오른쪽 면 중심
        points.add(new Vector(getMidpoint(pos1, pos2).getX(), pos1.getY(), getMidpoint(pos1, pos2).getZ())); // 아래쪽 면 중심
        points.add(new Vector(getMidpoint(pos1, pos2).getX(), pos2.getY(), getMidpoint(pos1, pos2).getZ())); // 위쪽 면 중심
        points.add(new Vector(getMidpoint(pos1, pos2).getX(), getMidpoint(pos1, pos2).getY(), pos1.getZ())); // 앞쪽 면 중심
        points.add(new Vector(getMidpoint(pos1, pos2).getX(), getMidpoint(pos1, pos2).getY(), pos2.getZ())); // 뒤쪽 면 중심

        return points;
    }

    private static Vector getMidpoint(Vector v1, Vector v2) {
        return new Vector(
                (v1.getX() + v2.getX()) / 2,
                (v1.getY() + v2.getY()) / 2,
                (v1.getZ() + v2.getZ()) / 2
        );
    }
}
