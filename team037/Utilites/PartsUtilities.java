package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team037.Utilites.AppendOnlyMapLocationArray;


public class PartsUtilities
{

    public static AppendOnlyMapLocationArray findPartsICanSense(RobotController rc)
    {
        MapLocation currentLocation = rc.getLocation();
        RobotType type = rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;
        if (sensorRadiusSquared <= 0) {
            return new AppendOnlyMapLocationArray();
        }

        int radius = (int)Math.ceil(Math.sqrt(sensorRadiusSquared));
        AppendOnlyMapLocationArray parts = new AppendOnlyMapLocationArray();

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j<= radius; j++) {
                if (i*i + j*j <= sensorRadiusSquared) {
                    MapLocation toCheck = currentLocation.add(i, j);
                    if (rc.senseParts(toCheck) > 0) {
                        parts.add(toCheck);
                    }
                }
            }
        }
        return parts;
    }

    public static AppendOnlyMapLocationArray findPartsICanSenseNotImpassible(RobotController rc)
    {
        AppendOnlyMapLocationArray parts = findPartsICanSense(rc);
        AppendOnlyMapLocationArray freeParts = new AppendOnlyMapLocationArray();
        for (int i = parts.length - 1; i >= 0; i--) {
            if (rc.senseRubble(parts.array[i]) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                freeParts.add(parts.array[i]);
            }
        }
        return freeParts;
    }

}
