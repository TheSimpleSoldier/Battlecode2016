package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team037.DataStructures.AppendOnlyMapLocationArray;

/**
 * Utilities for finding parts and neutral units.
 */
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

        AppendOnlyMapLocationArray parts = new AppendOnlyMapLocationArray();

        MapLocation[] squares = MapLocation.getAllMapLocationsWithinRadiusSq(currentLocation, sensorRadiusSquared);

        for (int i = squares.length; --i>=0; )
        {
            if (rc.senseParts(squares[i]) > 0)
            {
                parts.add(squares[i]);
            }
        }

        RobotInfo[] neutralBots = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);

        for (int i = neutralBots.length; --i>=0; ) {
            parts.add(neutralBots[i].location);
        }


        return parts;
    }

    public static MapLocation[] findPartsAndNeutrals(RobotController rc)
    {
        RobotType type = rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;
        if (sensorRadiusSquared <= 0) {
            return new MapLocation[0];
        }

        AppendOnlyMapLocationArray parts = new AppendOnlyMapLocationArray();

        MapLocation[] squares = rc.sensePartLocations(sensorRadiusSquared);

        for (int i = squares.length; --i>=0; )
        {
            if (rc.senseParts(squares[i]) > 0)
            {
                parts.add(squares[i]);
            }
        }

        RobotInfo[] neutralBots = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);

        for (int i = neutralBots.length; --i>=0; ) {
            parts.add(neutralBots[i].location);
        }

        MapLocation[] locs = new MapLocation[parts.length];

        for (int i = locs.length; --i>=0; )
        {
            locs[i] = parts.array[i];
        }

        return locs;
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
