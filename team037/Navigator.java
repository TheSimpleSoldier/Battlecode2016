package team037;

import battlecode.common.*;

/**
 * Class enables units to use Navigation with a single method call. Also updates targets.
 */
public class Navigator
{
    private static RobotController rc;
    private static MapLocation target;
    private static Direction[] dirs;

    public Navigator(RobotController robotController)
    {
        rc = robotController;
        Navigation.initialize(rc);
        dirs = new Direction[]{Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
                Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    }

    public void setTarget(MapLocation t)
    {
        target = t;
    }

    public MapLocation getTarget()
    {
        return target;
    }

    // This method returns true if we moved and false otherwise
    public boolean takeNextStep() throws GameActionException
    {
        return Navigation.move(target);
    }

    // This method returns true if we moved and false otherwise
    public boolean takeNextStepTTM() throws GameActionException
    {
        return Navigation.moveTTM(target);
    }

    public Direction getNextStep(MapLocation currentLocation) throws GameActionException {
        return Direction.NONE;
    }
}
