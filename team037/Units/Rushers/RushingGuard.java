package team037.Units.Rushers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseGaurd;

public class RushingGuard extends BaseGaurd
{
    private boolean rushing = true;

    public RushingGuard(RobotController rc)
    {
        super(rc);
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
