package team037;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Navigator
{
    private static RobotController rc;
    private static MapLocation target;

    public Navigator(RobotController robotController)
    {
        rc = robotController;
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
    public boolean takeNextStep()
    {
        // TODO: Implement this

        return false;
    }
}
