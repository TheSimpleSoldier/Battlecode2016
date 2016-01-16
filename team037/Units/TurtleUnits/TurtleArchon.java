package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Utilites.MapUtils;
import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;

public class TurtleArchon extends BaseArchon
{
    private boolean reachedTurtleSpot = false;

    public TurtleArchon(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
        turtlePoint = turtlePoint.add(turtlePoint.directionTo(currentLocation), 3);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();

        if (target == null) return true;
        if (target.equals(turtlePoint) && currentLocation.distanceSquaredTo(target) <= 2) return true;
        if (currentLocation.equals(target)) return true;
        if (!target.equals(turtlePoint) && rc.canSenseLocation(target) && rc.senseParts(target) == 0 && (rc.senseRobotAtLocation(target) == null || rc.senseRobotAtLocation(target).team != Team.NEUTRAL)) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (currentLocation.distanceSquaredTo(turtlePoint) > 2)
        {
            return turtlePoint;
        }
        else if (!reachedTurtleSpot)
        {
            reachedTurtleSpot = true;
        }

        return sortedParts.getBestSpot(currentLocation);
    }

    @Override
    public Bots changeBuildOrder(Bots nextBot)
    {
        while (nearByAllies.length > 10 && nextType == RobotType.SOLDIER && nextBot == Bots.CASTLESOLDIER)
        {
            nextBot = buildOrder.nextBot();
            nextType = Bots.typeFromBot(nextBot);
        }
        return nextBot;
    }
}
