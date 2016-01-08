package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team037.DataStructures.AppendOnlyMapLocationArray;


public class PartsUtilities
{

    public static AppendOnlyMapLocationArray findPartsAndNeutralsICanSense(RobotController rc)
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

        RobotInfo[] neutralBots = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);

        for (int i = neutralBots.length; --i>=0; ) {
            parts.add(neutralBots[i].location);
        }

        return parts;
    }

    public static AppendOnlyMapLocationArray findPartsAndNeutralsICanSenseNotImpassible(RobotController rc)
    {
        AppendOnlyMapLocationArray parts = findPartsAndNeutralsICanSense(rc);
        AppendOnlyMapLocationArray freeParts = new AppendOnlyMapLocationArray();
        for (int i = parts.length - 1; i >= 0; i--) {
            if (rc.senseRubble(parts.array[i]) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                freeParts.add(parts.array[i]);
            }
        }
        return freeParts;
    }

}
