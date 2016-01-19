package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseSoldier;
import team037.Utilites.MapUtils;

public class TurtleSoldier extends BaseSoldier
{
    private static int turnsArrivedLoc = -1;
    private static boolean arrived = false;
    private boolean chasingZombies = false;
    private boolean healing = false;
    private boolean updatedTurtleSpot = false;

    public TurtleSoldier(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (zombies.length > 0) return true;
        MapLocation target = navigator.getTarget();

        if (target == null) return true;

        if ((currentLocation.equals(target) || currentLocation.isAdjacentTo(target))) return true;
        if (rc.canSense(target) && !rc.onTheMap(target)) return true;
        if (rc.getHealth() <= 25) return true;

        return false;
    }

    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        if (!rc.isCoreReady()) return false;
        if (turnsArrivedLoc == -1) return false;
        if (zombies.length > 0 || enemies.length > 0) return false;

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation next = currentLocation.add(dirs[i]);
            if (rc.canSense(next) && rc.senseRubble(next) > 0)
            {
                rc.clearRubble(dirs[i]);
                return true;
            }
        }
        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (healing || rc.getHealth() <= 15)
        {
            healing = true;
            return turtlePoint.add(currentLocation.directionTo(turtlePoint), 3);
        }

        if (zombies.length > 0)
        {
            chasingZombies = true;
            return MapUtils.closestUnit(zombies, currentLocation);
        }

        if (turnsArrivedLoc == -1 || chasingZombies)
        {
            chasingZombies = false;
            arrived = false;
            return turtlePoint.add(turtlePoint.directionTo(currentLocation), 3);
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 3);

            if (rc.canSenseLocation(possible) && !rc.onTheMap(possible)) continue;

            if (possible.distanceSquaredTo(turtlePoint) <= 49)
            {
                arrived = false;
                return possible;
            }
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 6);

            if (rc.canSenseLocation(possible) && !rc.onTheMap(possible)) continue;

            if (possible.distanceSquaredTo(turtlePoint) <= 49)
            {
                arrived = false;
                return possible;
            }
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 10);

            if (rc.canSenseLocation(possible) && !rc.onTheMap(possible)) continue;

            if (possible.distanceSquaredTo(turtlePoint) <= 49)
            {
                arrived = false;
                return possible;
            }
        }

        return turtlePoint;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (!arrived && (rc.canSense(turtlePoint)))
        {
            turnsArrivedLoc = rc.getRoundNum();
            arrived = true;
        }

        if (healing && rc.getHealth() > (type.maxHealth - 20))
        {
            healing = false;
        }

        if (rallyPoint != null)
        {
            rc.setIndicatorLine(currentLocation, rallyPoint, 0, 0, 255);
            rc.setIndicatorString(1, "Going to new rally point");
            turtlePoint = rallyPoint;
        }
    }
}
