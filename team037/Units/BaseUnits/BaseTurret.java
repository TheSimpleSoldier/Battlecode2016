package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Unit;
import team037.Utilites.MapUtils;

/**
 * Base Turret is the foundation of all turret units
 *
 * To move all that needs to happen is for the target to be set
 * to a location other than the current location using the updateTarget()
 * and setTarget() methods
 *
 * If you want control over when the Turret turns into a ttm or when a ttm turns into
 * a turret then implement the packIntoTTM() and unpackTTM() methods
 *
 * Otherwise you shouldn't need to implement any code
 */
public class BaseTurret extends Unit
{
    public MapLocation enemyArchonCOM;
    private int turnsAtLoc = 0;
    private boolean arrived = false;

    public BaseTurret(RobotController rc)
    {
        super(rc);
        enemyArchonCOM = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        enemyArchonCOM = enemyArchonCOM.add(enemyArchonCOM.directionTo(currentLocation), 5);
        type = RobotType.TURRET;
    }

    /**
     * This method is a check to make sure that we should pack up
     * it defaults to true
     * @return
     */
    public boolean packIntoTTM()
    {
        return true;
    }

    /**
     * This method is for determining if we should unpack form a ttm into a turret
     *
     * @return
     */
    public boolean unpackTTM()
    {
        return false;
    }

    // Turrets don't move except if they are ttms
    @Override
    public boolean takeNextStep() throws GameActionException
    {
        if (rc.getType() == RobotType.TURRET)
        {
            if (packIntoTTM() && navigator.getTarget() != null && !navigator.getTarget().equals(currentLocation) && (zombies.length == 0 && enemies.length == 0))
            {
                arrived = false;
                rc.pack();
            }
            return false;
        }

        // otherwise we are a ttm
        return navigator.takeNextStepTTM();
    }

    @Override
    public boolean fight() throws GameActionException
    {
        if (rc.getType() == RobotType.TTM)
        {
            if (zombies.length > 0 || enemies.length > 0)
            {
                rc.unpack();
                return true;
            }

            return false;
        }

        return fightMicro.turretFightMicro(nearByEnemies, nearByZombies, enemies, allies, target, communications);
    }

    // zombie fight micro happens in fight()
    @Override
    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    @Override
    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (currentLocation.distanceSquaredTo(enemyArchonCOM) < 25) return true;
        MapLocation target = navigator.getTarget();
        if (target == null || target.equals(currentLocation)) return true;
        if (rc.canSense(target) && rc.senseRobotAtLocation(target) != null) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (!rc.canSense(enemyArchonCOM))
            return enemyArchonCOM;
        else
            return MapUtils.getClosestUnoccupiedSquare(currentLocation, enemyArchonCOM);
    }

    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        if (rc.getType() == RobotType.TTM)
        {
            if (unpackTTM())
            {
                rc.unpack();
            }

            if (currentLocation.equals(navigator.getTarget()))
            {
                if (!arrived)
                {
                    arrived = true;
                    turnsAtLoc = rc.getRoundNum();
                }

                if (rc.getRoundNum() - turnsAtLoc > 5)
                {
                    rc.unpack();
                    arrived = false;
                    return true;
                }
            }
        }

        return false;
    }
}
