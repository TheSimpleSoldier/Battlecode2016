package team037.Units.SuperRush;

import battlecode.common.*;
import team037.DataStructures.UnitProportion;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Enums.Strategies;
import team037.Messages.AttackCommunication;
import team037.Messages.Communication;
import team037.Messages.MissionCommunication;
import team037.RobotPlayer;
import team037.Unit;

import java.util.Random;

/**
 * Created by joshua on 1/22/16.
 */
public class SuperRushArchon extends Unit
{
    public UnitProportion unitProportion;

    public RobotType first;
    public boolean spawnedFirst;
    public int distanceFromArchon;

    public MapLocation targetArchon;
    public Direction dirTo;
    public int targetID;

    private int turnHealed = 0;

    private int timesNotSeen = 0;
    private int lastSeen = 0;
    private int moved = 0;

    private boolean notRetreating = false;

    public SuperRushArchon(RobotController rc)
    {
        super(rc);
        first = RobotType.GUARD;
        unitProportion = new UnitProportion(0., 1., 3., 0., 0.);

        spawnedFirst = false;
        targetArchon = chooseBestArchon();
        dirTo = currentLocation.directionTo(targetArchon);
        targetID = -1;

        distanceFromArchon = 4;

        navigator.setTarget(targetArchon);
    }

    private MapLocation chooseBestArchon()
    {
        int[] archons = new int[enemyArchonStartLocs.length];

        for(int k = archons.length; --k >= 0;)
        {
            int dist = currentLocation.distanceSquaredTo(enemyArchonStartLocs[k]);
            archons[k] += dist;
            for(int a = alliedArchonStartLocs.length; --a >= 0;)
            {
                if(!alliedArchonStartLocs[a].equals(currentLocation))
                {
                    if(alliedArchonStartLocs[a].distanceSquaredTo(enemyArchonStartLocs[k]) <= dist)
                    {
                        archons[k] += dist;
                    }
                }
            }

            MapLocation current = currentLocation;
            while(rc.canSenseLocation(current) && !current.equals(enemyArchonStartLocs[k]))
            {
                double rubble = rc.senseRubble(current);
                if(rubble >= 100)
                {
                    archons[k] += 1000;
                }
                current = current.add(current.directionTo(enemyArchonStartLocs[k]));
            }
        }

        int index = 0;
        int min = 9999999;
        for(int k = archons.length; --k >= 0;)
        {
            System.out.print(archons[k] + ", ");
            if(archons[k] < min)
            {
                min = archons[k];
                index = k;
            }
        }
        System.out.println();

        return enemyArchonStartLocs[index];
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        boolean move = false;
        int num = 0;

        if(zombies.length > 10 && allies.length < 5)
        {
            nextBot = Bots.SCOUTBOMBARCHON;
            RobotPlayer.strategy = Strategies.SCOUT_BOMB;
        }

        for(int k = enemies.length; --k >= 0;)
        {
            if(enemies[k].type == RobotType.ARCHON)
            {
                if(allies.length < 1 && enemies.length > 3)
                {
                    nextBot = Bots.SCOUTBOMBARCHON;
                    RobotPlayer.strategy = Strategies.SCOUT_BOMB;
                }

                if(currentLocation.distanceSquaredTo(targetArchon) < 5 && enemies[k].coreDelay < 1)
                {
                    if(moved >= 5 && !notRetreating)
                    {
                        if(allies.length < 2 && enemies.length - 1 > allies.length)
                        {
                            nextBot = Bots.SCOUTBOMBARCHON;
                            RobotPlayer.strategy = Strategies.SCOUT_BOMB;
                        }
                    }
                    else if(enemies[k].location.equals(targetArchon))
                    {
                        moved++;
                        rc.setIndicatorString(1, "" + moved);
                    }
                    else
                    {
                        moved = 0;
                    }
                }
                if(targetID == -1)
                {
                    Direction toArchon = currentLocation.directionTo(enemies[k].location);
                    if(!toArchon.equals(dirTo.opposite()))
                    {
                        targetArchon = enemies[k].location;
                        dirTo = currentLocation.directionTo(targetArchon);
                        targetID = enemies[k].ID;
                        lastSeen = round;
                        num++;
                        break;
                    }
                }
                else if(enemies[k].ID == targetID)
                {
                    if(!enemies[k].location.equals(targetArchon))
                    {
                        move = true;
                    }
                    targetArchon = enemies[k].location;
                    dirTo = currentLocation.directionTo(targetArchon);
                    lastSeen = round;
                    num++;
                    break;
                }
            }
        }
        if(num > 0)
        {
            if(move)
            {
                AttackCommunication attackCommunication = new AttackCommunication();
                attackCommunication.opcode = CommunicationType.ATTACK;
                attackCommunication.x = targetArchon.x;
                attackCommunication.y = targetArchon.y;
                communicator.sendCommunication(mapKnowledge.getRange(), attackCommunication);
            }
        }
        else
        {
            if(currentLocation.distanceSquaredTo(targetArchon) < 24 && round - lastSeen < 2)
            {
                targetID = -1;
                //We are the champions
                //time to change professions
                nextBot = Bots.SCAVENGERARCHON;
            }
        }

        if(num == 0 && timesNotSeen > 20)
        {
            targetArchon = enemyArchonStartLocs[Math.abs(new Random(round).nextInt()) % enemyArchonStartLocs.length];
            AttackCommunication attackCommunication = new AttackCommunication();
            attackCommunication.opcode = CommunicationType.ATTACK;
            attackCommunication.x = targetArchon.x;
            attackCommunication.y = targetArchon.y;
            communicator.sendCommunication(mapKnowledge.getRange(), attackCommunication);
            timesNotSeen = 0;
            targetID = -1;
        }
        else if(num== 0 && rc.canSenseLocation(targetArchon))
        {
            timesNotSeen++;
        }
        rc.setIndicatorLine(currentLocation, targetArchon, 0, 0, 0);
    }

    @Override
    public boolean act() throws GameActionException
    {
        String happening = "";
        healNearbyAllies();
        if(!rc.isCoreReady())
        {
            happening = "nothing";
            rc.setIndicatorString(0, "happening: " + happening);
            return false;
        }
        else if(!spawnedFirst)
        {
            happening = "spawning first";
            if(spawnUnit())
            {
                spawnedFirst = true;
            }
        }
        else if(activateUnit()){happening = "activating";}
        else if(collectParts()){happening = "collecting parts";}
        else if(moveToSeeEnemy()){happening = "moving to enemy";}
        else if(runAway()){happening = "running";}
        else if(spawnUnit()){happening = "spawning";}
        rc.setIndicatorString(0, "happening: " + happening);

        return true;
    }

    private boolean activateUnit() throws GameActionException
    {
        RobotInfo[] neutrals = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadiusSquared, Team.NEUTRAL);
        if(neutrals.length > 0)
        {
            RobotInfo nearest = null;
            int minDist = 99999999;
            for(int k = neutrals.length; --k >= 0;)
            {
                int tempDist = currentLocation.distanceSquaredTo(neutrals[k].location);
                if(tempDist < minDist)
                {
                    minDist = tempDist;
                    nearest = neutrals[k];
                }
            }

            if(minDist <= 2)
            {
                rc.activate(nearest.location);
                unitProportion.addBot(nearest.type);
                sendInitialMessages(currentLocation.directionTo(nearest.location), nearest.type);
                return true;
            }

            navigator.setTarget(nearest.location);
            navigator.takeNextStep();
            return true;
        }

        return false;
    }

    private boolean collectParts() throws GameActionException
    {
        if(rc.getTeamParts() < 30)
        {
            MapLocation[] locs = rc.sensePartLocations(5);
            if(locs.length > 0)
            {
                MapLocation nearest = null;
                int minDist = 99999999;
                for(int k = locs.length; --k >= 0;)
                {
                    int tempDist = currentLocation.distanceSquaredTo(locs[k]);
                    if(tempDist < minDist)
                    {
                        minDist = tempDist;
                        nearest = locs[k];
                    }
                }

                navigator.setTarget(nearest);
                navigator.takeNextStep();
                return true;
            }
        }
        return false;
    }

    private boolean spawnUnit() throws GameActionException
    {
        RobotType toSpawn;

        if(!spawnedFirst)
        {
            toSpawn = first;
        }
        else
        {
            toSpawn = unitProportion.nextBot();
        }

        if(enemies.length + 3 < allies.length)
        {
            return false;
        }

        double minRubble = 999999999;
        Direction minDirection = Direction.NONE;
        if(rc.hasBuildRequirements(toSpawn))
        {
            double tempRubble = tryBuilding(dirTo, toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo;
            }
            tempRubble = tryBuilding(dirTo.rotateLeft(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateLeft();
            }
            tempRubble = tryBuilding(dirTo.rotateRight(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateRight();
            }
            tempRubble = tryBuilding(dirTo.rotateLeft().rotateLeft(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateLeft().rotateLeft();
            }
            tempRubble = tryBuilding(dirTo.rotateRight().rotateRight(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateRight().rotateRight();
            }
            tempRubble = tryBuilding(dirTo.rotateLeft().rotateLeft().rotateLeft(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateLeft().rotateLeft().rotateLeft();
            }
            tempRubble = tryBuilding(dirTo.rotateRight().rotateRight().rotateRight(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateRight().rotateRight().rotateRight();
            }
            tempRubble = tryBuilding(dirTo.opposite(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minDirection = dirTo.opposite();
            }

            if(minDirection != Direction.NONE)
            {
                rc.clearRubble(minDirection);
            }
        }
        return false;
    }

    private double tryBuilding(Direction dir, RobotType toSpawn) throws GameActionException
    {
        if(rc.onTheMap(currentLocation.add(dir)))
        {
            if(rc.canBuild(dir, toSpawn))
            {
                rc.build(dir, toSpawn);
                spawnedFirst = true;
                unitProportion.addBot(toSpawn);
                sendInitialMessages(dir, toSpawn);
                return -100;
            }
            else
            {
                return rc.senseRubble(currentLocation.add(dir));
            }
        }

        return -1;
    }

    private void sendInitialMessages(Direction dir, RobotType toSpawn) throws GameActionException
    {
        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = rc.senseRobotAtLocation(currentLocation.add(dir)).ID;
        switch(toSpawn)
        {
            case GUARD:
                communication.newBType = Bots.RUSHGUARD;
                break;
            case SCOUT:
                communication.newBType = Bots.RUSHSCOUT;
                break;
            case SOLDIER:
                communication.newBType = Bots.RUSHINGSOLDIER;
                break;
            case TURRET:
                communication.newBType = Bots.SUPERRUSHTURRET;
                break;
            case VIPER:
                communication.newBType = Bots.SUPERRUSHVIPER;
                break;
            case ARCHON:
                communication.newBType = Bots.TURTLEARCHON;
                break;
        }
        communication.x = targetArchon.x;
        communication.y = targetArchon.y;
        communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication();
        communicator.sendCommunication(2, mapBoundsCommunication);
    }

    private boolean moveToSeeEnemy() throws GameActionException
    {
        if(notRetreating)
        {
            return false;
        }
        if(currentLocation.distanceSquaredTo(targetArchon) < distanceFromArchon)
        {
            return false;
        }

        navigator.setTarget(targetArchon);
        return navigator.takeNextStep();
    }

    private boolean runAway() throws GameActionException
    {
        if(!notRetreating)
        {
            return false;
        }

        if(currentLocation.distanceSquaredTo(targetArchon) < 17)
        {
            navigator.setTarget(currentLocation.add(currentLocation.directionTo(targetArchon).opposite(), 2));
            return navigator.takeNextStep();
        }

        return false;
    }

    @Override
    public boolean takeNextStep() throws GameActionException
    {
        return false;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        return false;
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    public boolean healNearbyAllies() throws GameActionException {
        // precondition
        if (nearByAllies.length == 0 || turnHealed == rc.getRoundNum()) {
            return false;
        }

        double weakestHealth = 9999;
        RobotInfo weakest = null;

        for (int i = nearByAllies.length; --i>=0; )
        {
            double health = nearByAllies[i].health;
            if (nearByAllies[i].type != RobotType.ARCHON && health < nearByAllies[i].maxHealth && currentLocation.distanceSquaredTo(nearByAllies[i].location) <= RobotType.ARCHON.attackRadiusSquared)
            {
                if (health < weakestHealth)
                {
                    weakestHealth = health;
                    weakest = nearByAllies[i];
                }
            }
        }

        try
        {
            if (weakest != null)
            {
                if (rc.senseRobotAtLocation(weakest.location) != null)
                {
                    rc.repair(weakest.location);
                }
                turnHealed = rc.getRoundNum();
                return true;
            }
        } catch (Exception e) {e.printStackTrace();}

        return false;
    }
}
