package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Units.BaseGaurd;
import team037.Utilites.MapUtils;
import team037.Utilites.Utilities;

public class TurtleGuard extends BaseGaurd
{
    MapLocation turtlePoint;
    private static int turnsArrivedLoc = -1;
    private static boolean arrived = false;

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
        if (currentLocation.equals(target) && (rc.getRoundNum() - turnsArrivedLoc) > 10) return true;
        if (rc.canSense(target) && !rc.onTheMap(target)) return true;

        return false;
    }

    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        if (!rc.isCoreReady()) return false;

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
        if (zombies.length > 0)
            return zombies[0].location;
        
        if (turnsArrivedLoc == -1)
        {
            arrived = false;
            return turtlePoint.add(turtlePoint.directionTo(currentLocation), 3);
        }

        for (int i = dirs.length; --i>=0; )
        {
            MapLocation possible = currentLocation.add(dirs[i], 3);

            if (possible.distanceSquaredTo(turtlePoint) <= 49 && possible.distanceSquaredTo(turtlePoint) > 10)
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
    }
}
