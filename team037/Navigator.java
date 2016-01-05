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
        dirs = Direction.values();
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
