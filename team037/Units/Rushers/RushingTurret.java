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
    private MapLocation[] updatedLocs;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    public RushingTurret(RobotController rc)
    {
        super(rc);
        updatedLocs = new MapLocation[enemyArchonStartLocs.length];

        for (int i = updatedLocs.length; --i>=0; )
        {
            updatedLocs[i] = enemyArchonStartLocs[i];
        }

        rushTarget = MapUtils.getNearestLocation(enemyArchonStartLocs, currentLocation);
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
    public MapLocation getNextSpot()
    {
        if (currentIndex != -1)
        {
            if (currentIndex < updatedLocs.length)
            {
                updatedLocs[currentIndex] = null;
            }
            else
            {
                currentIndex = 0;
                for (int i = updatedLocs.length; --i>=0; )
                {
                    updatedLocs[i] = enemyArchonStartLocs[i];
                }
            }

            rushTarget =  MapUtils.getNearestLocation(updatedLocs, currentLocation);
        }

        currentIndex++;
        return rushTarget;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (zombies.length > 0 || enemies.length > 0) return false;
        MapLocation target = navigator.getTarget();
        if (target == null) return true;
        if (currentLocation.equals(target) || currentLocation.isAdjacentTo(target)) return true;
        return false;
    }
}
