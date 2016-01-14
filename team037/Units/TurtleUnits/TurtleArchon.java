package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Units.BaseArchon;
import team037.Utilites.Utilities;

public class TurtleArchon extends BaseArchon
{
    public MapLocation turtleSpot;
    private boolean reachedTurtleSpot = false;

    public TurtleArchon(RobotController rc)
    {
        super(rc);

        int x = 0, y = 0, len = alliedArchonStartLocs.length;
        for (int i = len; --i>=0; )
        {
            x += alliedArchonStartLocs[i].x;
            y += alliedArchonStartLocs[i].y;
        }
        turtleSpot = new MapLocation(x/len,y/len);
        turtleSpot = turtleSpot.add(turtleSpot.directionTo(currentLocation), 3);
    }

    // currently not used for turtle archons
    @Override
    public boolean carryOutAbility()
    {
        return false;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();

        if (target == null) return true;
        if (target.equals(turtleSpot) && currentLocation.distanceSquaredTo(target) <= 2) return true;
        if (currentLocation.equals(target)) return true;
        if (!target.equals(turtleSpot) && rc.canSenseLocation(target) && rc.senseParts(target) == 0 && (rc.senseRobotAtLocation(target) == null || rc.senseRobotAtLocation(target).team != Team.NEUTRAL)) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (currentLocation.distanceSquaredTo(turtleSpot) > 2)
        {
            return turtleSpot;
        }
        else if (!reachedTurtleSpot)
        {
            reachedTurtleSpot = true;
        }

        return sortedParts.getBestSpot(currentLocation);
    }
}
