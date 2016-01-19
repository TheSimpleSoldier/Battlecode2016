package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Enums.CommunicationType;
import team037.Messages.AttackCommunication;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.Units.PacMan.PacMan;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;
import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;

public class TurtleArchon extends BaseArchon implements PacMan
{
    private boolean reachedTurtleSpot = false;
    private boolean updatedTurtleSpot = false;
    private MapLocation origionalTurtleSpot;
    private boolean hiding = false;

    public TurtleArchon(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
        origionalTurtleSpot = new MapLocation(turtlePoint.x, turtlePoint.y);
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

        if (rallyPoint != null)
        {
            turtlePoint = rallyPoint;
        }

        if (rc.getRoundNum() > 500 && mapKnowledge.dens.length > 0)
        {
            rc.setIndicatorString(1, "len: " + mapKnowledge.dens.length);

            int closestDen = 99999;
            MapLocation den = null;

            for (int i = mapKnowledge.dens.length; --i >= 0; )
            {
                if (mapKnowledge.dens.array[i] != null)
                {
                    MapLocation currentDen = mapKnowledge.dens.array[i];

                    if (rc.canSenseLocation(currentDen) && (rc.senseRobotAtLocation(currentDen) == null || rc.senseRobotAtLocation(currentDen).team != Team.ZOMBIE))
                    {
                        mapKnowledge.dens.remove(currentDen);

                        Communication communication = new SimpleBotInfoCommunication();
                        communication.opcode = CommunicationType.DEAD_DEN;
                        communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.DEAD_DEN), 0, currentDen.x, currentDen.y});
                        communicator.sendCommunication(400, communication);
                    }
                    else
                    {
                        int currentDist = currentDen.distanceSquaredTo(turtlePoint);

                        if (currentDist < closestDen)
                        {
                            closestDen = currentDist;
                            den = currentDen;
                        }
                    }
                }
            }

            if (den != null && den.distanceSquaredTo(turtlePoint) > 20)
            {
                rc.setIndicatorString(2, "We have a den location!!! x: " + den.x + " y: " + den.y + " round " + rc.getRoundNum());
                rc.setIndicatorLine(currentLocation, den, 0, 0, 0);
                turtlePoint = den.add(den.directionTo(turtlePoint), 3);

                Communication newRallyPoint = new AttackCommunication();
                newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
                communicator.sendCommunication(400, newRallyPoint);

                hiding = false;
            }
            else if (!hiding && rc.getRoundNum() > 750)
            {
                int leftX = mapKnowledge.minX;
                int rightX = mapKnowledge.maxX;
                int topY = mapKnowledge.minY;
                int bottomY = mapKnowledge.maxY;

                int currentX = turtlePoint.x;
                int currentY = turtlePoint.y;

                int distToTopLeft = (leftX - currentX) * (leftX - currentX) + (topY - currentY) * (topY - currentY);
                int distToBottonLeft = (leftX - currentX) * (leftX - currentX) + (bottomY - currentY) * (bottomY - currentY);
                int distToTopRight = (rightX - currentX) * (rightX - currentX) + (topY - currentY) * (topY - currentY);
                int distToBottonRight = (rightX - currentX) * (rightX - currentX) + (bottomY - currentY) * (bottomY - currentY);

                // go left
                if (distToTopLeft < distToTopRight)
                {
                    if (distToTopLeft < distToBottonLeft)
                    {
                        turtlePoint = new MapLocation(leftX, topY);
                    }
                    else
                    {
                        turtlePoint = new MapLocation(leftX, bottomY);
                    }
                }
                // go right
                else
                {
                    if (distToTopRight < distToBottonRight)
                    {
                        turtlePoint = new MapLocation(rightX, topY);
                    }
                    else
                    {
                        turtlePoint = new MapLocation(rightX, bottomY);
                    }
                }

                Communication newRallyPoint = new AttackCommunication();
                newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
                communicator.sendCommunication(400, newRallyPoint);

                // no need to keep updating this
                hiding = true;
            }

            if (hiding)
            {
                rc.setIndicatorLine(currentLocation, turtlePoint, 0, 255, 0);
            }
        }
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (!reachedTurtleSpot && currentLocation.distanceSquaredTo(turtlePoint) > 10) return turtlePoint;
        if (!reachedTurtleSpot) reachedTurtleSpot = true;

        MapLocation bestParts = getNextPartLocation();

        if (bestParts == null) return turtlePoint;

        rc.setIndicatorString(1, "BestParts x: " + bestParts.x + " y: " + bestParts.y);

        return bestParts;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(enemies)) return false;
        if (turtlePoint != null) navigator.setTarget(turtlePoint);

        return runAway(null);
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(zombies)) return false;
        if (turtlePoint != null) navigator.setTarget(turtlePoint);

        return runAway(null);
    }
}
