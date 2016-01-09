package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;


public class MapUtils
{


    /**
     * Senses the edge in the direction specified
     */
    public static int senseEdge(RobotController rc, Direction d) throws GameActionException {

//        assert !d.isDiagonal();

        RobotType type = rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;
        int radius = (int)Math.floor(Math.sqrt(sensorRadiusSquared));

        MapLocation current = rc.getLocation();

        if (rc.onTheMap(current.add(d, radius))) {
            return Integer.MIN_VALUE;
        }

        radius -= 1;
        while(!rc.onTheMap(current.add(d, radius))) {
            radius -= 1;
        }

        if (d.equals(Direction.EAST) || d.equals(Direction.WEST)) {
            return current.add(d, radius).x;
        } else {
            return current.add(d, radius).y;
        }
    }

}
