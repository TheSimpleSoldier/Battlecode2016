package team037.Units.Rushers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseTurret;
import team037.Utilites.MapUtils;

public class RushingTurret extends BaseTurret
{
    public RushingTurret(RobotController rc)
    {
        super(rc);
        rushTarget = MapUtils.getCenterOfMass(enemyArchonStartLocs);
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
}
