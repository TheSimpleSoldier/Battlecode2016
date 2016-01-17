package team037.Units.Rushers;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseSoldier;
import team037.Utilites.MapUtils;

public class RushingSoldier extends BaseSoldier
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;

    public RushingSoldier(RobotController rc)
    {
        super(rc);
        rushTarget = MapUtils.getNearestLocation(enemyArchonStartLocs, currentLocation);
        rc.setIndicatorString(0, "Rushing Soldier x: " + rushTarget.x + " y: " + rushTarget.y);
    }

    @Override
    public MapLocation getNextSpot()
    {
        return rushTarget;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();
        if (target == null) return true;
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
