package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Units.PacMan.PacMan;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;
import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;

public class TurtleArchon extends BaseArchon implements PacMan
{
    private boolean reachedTurtleSpot = false;

    public TurtleArchon(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
        turtlePoint = turtlePoint.add(turtlePoint.directionTo(currentLocation), 3);
        rc.setIndicatorString(0, "Turtle archon");
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
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (mapKnowledge.dens.length > 0)
        {
            rc.setIndicatorString(1, "len: " + mapKnowledge.dens.length);
            for (int i = mapKnowledge.dens.length; --i>=0; )
            {
                if (mapKnowledge.dens.array[i] != null)
                {
                    rc.setIndicatorString(2, "We have a den location!!! x: " + mapKnowledge.dens.array[i].x + " y: " + mapKnowledge.dens.array[i].y);
                    rc.setIndicatorLine(currentLocation, mapKnowledge.dens.array[i], 0, 0, 0);
//                    rc.setIndicatorDot(mapKnowledge.dens.array[i], 255, 0, 0);
                }
            }
        }
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (!reachedTurtleSpot && currentLocation.distanceSquaredTo(turtlePoint) > 10) return turtlePoint;
        if (!reachedTurtleSpot) reachedTurtleSpot = true;

        MapLocation bestParts = sortedParts.getBestSpot(currentLocation);

        if (bestParts == null) return turtlePoint;

        rc.setIndicatorString(1, "BestParts x: " + bestParts.x + " y: " + bestParts.y);

        return sortedParts.getBestSpot(currentLocation);
    }

    public boolean fight() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(enemies)) return false;
        if (turtlePoint != null) navigator.setTarget(turtlePoint);

        return runAway(null);
    }

    public boolean fightZombies() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(zombies)) return false;
        if (turtlePoint != null) navigator.setTarget(turtlePoint);

        return runAway(null);
    }
}
