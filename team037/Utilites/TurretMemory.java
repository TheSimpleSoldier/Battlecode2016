package team037.Utilites;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team037.DataStructures.MapLocationBuffer;

public class TurretMemory {

    public static final MapLocationBuffer mapLocationBuffer = new MapLocationBuffer(10);
    public static final int RADIUS = RobotType.TURRET.attackRadiusSquared;

    public static void addTurretLocation(MapLocation location) {
        if (!mapLocationBuffer.contains(location))
            mapLocationBuffer.addToBuffer(location);
    }

    public static boolean outOfTurretRange(MapLocation location) {
        MapLocation[] contents = mapLocationBuffer.getBuffer();
        for (int i = 10; --i >= 0;) {
            if (contents[i] != null && contents[i].distanceSquaredTo(location) <= RADIUS) {
                return false;
            }
        }
        return true;
    }

    public static MapLocation closestTurret(MapLocation location) {
        MapLocation[] contents = mapLocationBuffer.getBuffer();
        int closest = -1;
        int distance = 99999999;
        for (int i = 10; --i >= 0;) {
            if (contents[i] != null) {
                int distanceSquared = contents[i].distanceSquaredTo(location);
                if (distance > distanceSquared) {
                    closest = i;
                    distance = distanceSquared;
                }
            }
        }
        if (closest >= 0) {
            return contents[closest];
        } else {
            return null;
        }
    }

    public static MapLocation[] getBufferContents() {
        return mapLocationBuffer.getBuffer();
    }

    public static MapLocation[] dumpBuffer() {
        return mapLocationBuffer.dumpBuffer();
    }
}
