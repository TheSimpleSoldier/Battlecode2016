package team037;

import battlecode.common.*;

public class Navigator
{
    private static RobotController rc;
    private static MapLocation target;
    private static Direction[] dirs;

    public Navigator(RobotController robotController)
    {
        rc = robotController;
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
        if (!rc.isCoreReady())
        {
            return false;
        }

        // TODO: Implement this
        int index = (int) (Math.random() * dirs.length);
        if (rc.canMove(dirs[index]))
        {
            rc.move(dirs[index]);
        }
        else if (rc.senseRubble(rc.getLocation().add(dirs[index])) > 0)
        {
            rc.clearRubble(dirs[index]);
        }

        return false;
    }
}