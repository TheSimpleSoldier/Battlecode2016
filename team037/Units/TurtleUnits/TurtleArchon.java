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
import team037.Utilites.ZombieTracker;

public class TurtleArchon extends BaseArchon implements PacMan
{
    private boolean reachedTurtleSpot = false;
    private boolean updatedTurtleSpot = false;
    private MapLocation origionalTurtleSpot;
    private boolean hiding = false;
    private int updateRound = 0;
    private int index = 0;

    public TurtleArchon(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
        origionalTurtleSpot = new MapLocation(turtlePoint.x, turtlePoint.y);
        rc.setIndicatorString(0, "Turtle archon");

        for (int i = alliedArchonStartLocs.length; --i>=0;)
        {
            if (alliedArchonStartLocs[i].equals(currentLocation))
            {
                index = i;
                break;
            }
        }
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();

        if (target == null) return true;
        if (target.equals(turtlePoint) && currentLocation.distanceSquaredTo(target) <= 2) return true;
        if (currentLocation.equals(target)) return true;
        if (!target.equals(turtlePoint) && rc.canSenseLocation(target) && rc.senseParts(target) == 0 && (rc.senseRobotAtLocation(target) == null || rc.senseRobotAtLocation(target).team != Team.NEUTRAL)) return true;
        if (zombieTracker.getNextZombieRound() - rc.getRoundNum() <= 25) return true;

        return false;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        rc.setIndicatorString(0, "TurtlePoint: " + turtlePoint);

        if (rallyPoint != null)
        {
            turtlePoint = rallyPoint;
            updateRound = rc.getRoundNum();
        }

        if ((rc.getRoundNum() - updateRound) < 100)
        {
        }
        else if (rc.getRoundNum() < 500)
        {
        }
        else if (rc.getRoundNum() % 5 != index)
        {
        }
        else if (zombieTracker.getNextZombieRound() - rc.getRoundNum() < 25)
        {
        }
        else if (rc.getRoundNum() > 300 && mapKnowledge.dens.hasLocations())
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
                communicator.sendCommunication(1600, newRallyPoint);

                hiding = false;
            }
        }
        else
        {
            MapLocation bestParts = sortedParts.getBestSpot(currentLocation);

            if (bestParts == null && !hiding && rc.getRoundNum() > 750)
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
                        turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(rightX, bottomY)), 5);
                    }
                    else
                    {
                        turtlePoint = new MapLocation(leftX, bottomY);
                        turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(rightX, topY)), 5);
                    }
                }
                // go right
                else
                {
                    if (distToTopRight < distToBottonRight)
                    {
                        turtlePoint = new MapLocation(rightX, topY);
                        turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(leftX, bottomY)), 5);
                    }
                    else
                    {
                        turtlePoint = new MapLocation(rightX, bottomY);
                        turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(leftX, topY)), 5);
                    }
                }

                Communication newRallyPoint = new AttackCommunication();
                newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
                communicator.sendCommunication(1600, newRallyPoint);

                // no need to keep updating this
                hiding = true;
            }
        }

        if (hiding)
        {
            rc.setIndicatorLine(currentLocation, turtlePoint, 0, 255, 0);
        }
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (zombieTracker.getNextZombieRound() - rc.getRoundNum() <= 25) return turtlePoint;
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

    @Override
    public void sendInitialMessages(Direction dir) throws GameActionException
    {
        super.sendInitialMessages(dir);
        Communication newRallyPoint = new AttackCommunication();
        newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
        communicator.sendCommunication(2, newRallyPoint);

    }

    @Override
    public Bots getDefaultBotTypes(RobotType type)
    {
        switch (type)
        {
            case SOLDIER:
                return Bots.TURTLESOLDIER;
            case TURRET:
                return Bots.TURTLETURRET;
            case TTM:
                return Bots.TURTLETURRET;
            case GUARD:
                return Bots.TURTLEGUARD;
            case SCOUT:
                return Bots.SCOUTBOMBSCOUT;
            case ARCHON:
                return Bots.TURTLEARCHON;
            case VIPER:
                return Bots.RUSHINGVIPER;
            default:
                return Bots.TURTLESOLDIER;
        }
    }

    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        // preconditions
        if ((enemies.length + zombies.length) > allies.length) return false;
        if (currentLocation.distanceSquaredTo(turtlePoint) >= 100) return false;

        nextBot = changeBuildOrder(nextBot);

        if (rc.hasBuildRequirements(nextType) && rc.isCoreReady())
        {
            Direction dir = build();
            if (dir != Direction.NONE)
            {
                sendInitialMessages(dir);
                nextBot = buildOrder.nextBot();
                nextType = Bots.typeFromBot(nextBot);
                return true;
            }
        }

        return false;
    }

    @Override
    public Bots changeBuildOrder(Bots nextBot)
    {
        rc.setIndicatorString(2, "Zombies: " + zombieTracker.getNextZombieRound());
        if (zombieTracker.getNextZombieRound() - rc.getRoundNum() < 30)
        {
            nextType = RobotType.SCOUT;
            return Bots.SCOUTBOMBSCOUT;
        }

        return nextBot;
    }
}
