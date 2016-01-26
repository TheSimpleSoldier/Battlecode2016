package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Unit;
import team037.Units.PacMan.PacMan;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;

/**
 * Base Turret is the foundation of all turret units
 *
 * To move all that needs to happen is for targetLoc to be set
 * using setTargetLoc()
 *
 * If you want control over when the Turret turns into a ttm or when a ttm turns into
 * a turret then implement the packIntoTTM() and unpackTTM() methods
 *
 * Otherwise you shouldn't need to implement any code
 */
public class BaseTurret extends Unit implements PacMan
{
    public MapLocation targetLoc;
    private int turnsAtLoc = 0;
    private boolean arrived = false;

    public BaseTurret(RobotController rc)
    {
        super(rc);
//        targetLoc = MapUtils.getCenterOfMass(enemyArchonStartLocs);
//        targetLoc = targetLoc.add(targetLoc.directionTo(currentLocation), 5);
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

    public void setTargetLoc(MapLocation targetLoc)
    {
        this.targetLoc = targetLoc;
    }

    // Turrets don't move except if they are ttms
    @Override
    public boolean takeNextStep() throws GameActionException
    {
        if (rc.getType() == RobotType.TURRET)
        {
            if (packIntoTTM() && navigator.getTarget() != null && !navigator.getTarget().equals(currentLocation) && (!FightMicroUtilites.offensiveEnemies(enemies) && zombies.length == 0))
            {
                arrived = false;
                rc.pack();
            }
            return false;
        }

        rc.setIndicatorString(0, "Navigation: " + rc.getRoundNum() + " bytecodes: " + Clock.getBytecodeNum());

        // otherwise we are a ttm
        boolean returnVal = navigator.takeNextStepTTM();
        rc.setIndicatorString(1, "After navigations: " + rc.getRoundNum() + " bytecodes: " + Clock.getBytecodeNum());
        return returnVal;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        if (rc.getType() == RobotType.TTM)
        {
            if (zombies.length > 0 || enemies.length > 0)
            {
                if (fightMicro.enemiesInMinimumRange(zombies) || fightMicro.enemiesInMinimumRange(enemies))
                {
                    return runAway(null);
                }
                else if (FightMicroUtilites.offensiveEnemies(allies))
                {
                    rc.unpack();
                    return true;
                }
            }

            return false;
        }

        boolean fought = fightMicro.turretFightMicro(nearByEnemies, nearByZombies, enemies, allies, target, communications);

        if (pack(fought))
        {
//            System.out.println("Flee from enemies: Offensive allies: " + FightMicroUtilites.offensiveEnemies(allies) + " allies engaged: " + FightMicroUtilites.unitsEngaged(allies, enemies) + " allies.length: " + allies.length + " enemies.length: " + enemies.length);
            rc.pack();
            return false;
        }

        return fought;
    }

    private boolean pack(boolean fought)
    {
        if (fightMicro.enemiesInMinimumRange(zombies) || fightMicro.enemiesInMinimumRange(enemies) && !fought && rc.isWeaponReady())
        {
            if (!FightMicroUtilites.offensiveEnemies(allies)) return true;
            if (!FightMicroUtilites.unitsEngaged(allies, enemies) && !FightMicroUtilites.unitsEngaged(allies, zombies)) return true;
        }

        return false;
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
        if (targetLoc == null) return true;
        if (currentLocation.distanceSquaredTo(targetLoc) < 25) return true;
        MapLocation target = navigator.getTarget();
        if (target == null || target.equals(currentLocation)) return true;
        if (rc.canSense(target) && rc.senseRobotAtLocation(target) != null) return true;
        if (currentLocation.x % 2 == currentLocation.y % 2) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (targetLoc == null || !rc.canSense(targetLoc))
            return targetLoc;
        else
        {
            rc.setIndicatorString(1, "setting target: " + rc.getRoundNum());
            MapLocation target = MapUtils.getClosestUnoccupiedSquareCheckeredBoard(currentLocation, targetLoc);
            rc.setIndicatorString(2, "Target x: " + target.x + " y: " + target.y + " round: " + rc.getRoundNum());
            rc.setIndicatorLine(currentLocation, target, 255, 0, 255);
            return target;
        }

            //return MapUtils.getClosestUnoccupiedSquare(currentLocation, targetLoc);
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
