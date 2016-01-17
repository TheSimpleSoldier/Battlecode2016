package team037.Units.Rushers;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseSoldier;
import team037.Utilites.MapUtils;

public class RushingSoldier extends BaseSoldier
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private MapLocation[] updatedLocs;
    private int currentIndex = -1;

    public RushingSoldier(RobotController rc)
    {
        super(rc);
        updatedLocs = new MapLocation[enemyArchonStartLocs.length];

        for (int i = updatedLocs.length; --i>=0; )
        {
            updatedLocs[i] = enemyArchonStartLocs[i];
        }

        rushTarget = MapUtils.getNearestLocation(enemyArchonStartLocs, currentLocation);
        rc.setIndicatorString(0, "Rushing Soldier x: " + rushTarget.x + " y: " + rushTarget.y);
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
        MapLocation target = navigator.getTarget();
        if (target == null) return true;
        if (currentLocation.equals(target) || currentLocation.isAdjacentTo(target)) return true;
        return false;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        if (rushing)
        {
            return fightMicro.aggressiveFightMicro(nearByEnemies, enemies, nearByAllies);
        }
        return super.fight();
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (rushing)
            return false;

        return super.fightZombies();
    }
}
