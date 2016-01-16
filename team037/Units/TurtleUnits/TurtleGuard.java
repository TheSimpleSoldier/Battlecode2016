package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseGaurd;

public class TurtleGuard extends BaseGaurd
{
    private static int turnsArrivedLoc = -1;
    private static boolean arrived = false;
    private boolean chasingZombies = false;
    private boolean healing = false;

    public TurtleGuard(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();
        if (zombies.length > 0) return true;
        if (target == null) return true;
        if ((currentLocation.equals(target) || currentLocation.isAdjacentTo(target)) && (rc.getRoundNum() - turnsArrivedLoc) > 2) return true;
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
    public MapLocation getNextSpot()
    {
        if (healing || rc.getHealth() <= 25)
        {
            healing = true;
            return turtlePoint.add(currentLocation.directionTo(turtlePoint), 3);
        }

        if (zombies.length > 0)
        {
            chasingZombies = true;
            return closestUnit(zombies);
        }
        
        if (turnsArrivedLoc == -1 || chasingZombies)
        {
            chasingZombies = false;
            arrived = false;
            return turtlePoint.add(currentLocation.directionTo(turtlePoint), 3);
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 3);

            if (possible.distanceSquaredTo(turtlePoint) <= 49)
            {
                arrived = false;
                return possible;
            }
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 6);

            if (possible.distanceSquaredTo(turtlePoint) <= 49)
            {
                arrived = false;
                return possible;
            }
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 10);

            if (possible.distanceSquaredTo(turtlePoint) <= 49)
            {
                arrived = false;
                return possible;
            }
        }

        return null;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (!arrived && currentLocation.equals(navigator.getTarget()))
        {
            turnsArrivedLoc = rc.getRoundNum();
            arrived = true;
        }

        if (healing && rc.getHealth() > 100)
        {
            healing = false;
        }
    }

    /**
     * This method calculates the closest unit
     *
     * @param units
     * @return
     */
    public MapLocation closestUnit(RobotInfo[] units)
    {
        int dist = Integer.MAX_VALUE;
        MapLocation closest = null;

        for (int i = units.length; --i>=0; )
        {
            MapLocation spot = units[i].location;
            int currentDist = spot.distanceSquaredTo(currentLocation);
            if (currentDist < dist)
            {
                dist = currentDist;
                closest = units[i].location;
            }
        }

        return closest;
    }
}
