package team037.Units.Rushers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseTurret;
import team037.Utilites.MapUtils;

public class RushingTurret extends BaseTurret
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    public RushingTurret(RobotController rc)
    {
        super(rc);

        rushTarget.add(rushTarget.directionTo(currentLocation), 3);
        dist = (int) Math.sqrt(currentLocation.distanceSquaredTo(rushTarget));
        dist = dist / 2;
        dist = dist*dist;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (currentLocation != null && rushTarget != null)
        {
            if (currentLocation.distanceSquaredTo(rushTarget) < dist)
            {
                rushing = true;
            }
            else
            {
                rushing = false;
            }
        }
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (zombies.length > 0) return false;
        MapLocation target = navigator.getTarget();
        if (target == null) return true;
        if (currentLocation.equals(target) || currentLocation.isAdjacentTo(target)) return true;
        return false;
    }
}
