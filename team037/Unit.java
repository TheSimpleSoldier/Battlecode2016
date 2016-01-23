package team037;

import battlecode.common.*;
import team037.DataStructures.SimpleRobotInfo;
import team037.Enums.Bots;
import team037.Enums.Strategies;
import team037.Messages.*;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.Scouts.ScoutingScout;
import team037.Utilites.MapUtils;

public abstract class Unit
{
    public static int turnCreated;
    public static RobotController rc;
    public static int range;
    public static RobotType type;
    public static int sightRange;
    public static Team us;
    public static Team opponent;
    public static RobotInfo[] nearByEnemies;
    public static RobotInfo[] nearByAllies;
    public static RobotInfo[] enemies;
    public static RobotInfo[] allies;
    public static RobotInfo[] nearByZombies;
    public static RobotInfo[] zombies;
    public static MapLocation[] enemyArchonStartLocs;
    public static MapLocation[] alliedArchonStartLocs;
    public static MapLocation target;
    public static Direction[] dirs;
    public static FightMicro fightMicro;
    public static Navigator navigator;
    public static Communicator communicator;
    public static Bots nextBot = null;
    public static Bots thisBot= null;
    public static int id;
    public static int round;
    public static Communication[] communications;
    public static MapKnowledge mapKnowledge = new MapKnowledge();
    public static MapLocation start;
    public static boolean repaired;
    public static int msgsSent = 0;
    public static boolean defendingArchon = false;
    int rubbleUpdate = 0;

    public static boolean enemyComs = true;
    public static boolean archonComs = true;
    public static boolean archonDistressComs = true;
    public static boolean mapComs = true;
    public static boolean missionComs = true;

    public static MapLocation locationLastTurn;
    public static MapLocation previousLocation;
    public static MapLocation currentLocation;
    public static MapLocation rushTarget;
    public static MapLocation rallyPoint;
    public static MapLocation distressedArchon;
    public static MapLocation turtlePoint;
    public static MapLocation enemyArchonCenterOfMass;
    public static MapLocation alliedArchonCenterOfMass;
    public static int centerOfMassDifference;
    public static int myArchon;

    public Unit()
    {
        // default constructor
    }

    public Unit(RobotController robotController)
    {
        rc = robotController;
        type = rc.getType();
        id = rc.getID();
        range = type.attackRadiusSquared;
        sightRange = type.sensorRadiusSquared;
        us = rc.getTeam();
        opponent = us.opponent();
        dirs = new Direction[]{Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
        Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
        fightMicro = new FightMicro(rc);
        navigator = new Navigator(rc);
        communicator = new Communicator(rc);
        turnCreated = rc.getRoundNum();
        locationLastTurn = rc.getLocation();
        previousLocation = locationLastTurn;
        currentLocation = locationLastTurn;
        start = rc.getLocation();
        repaired = false;
        alliedArchonStartLocs = rc.getInitialArchonLocations(us);
        alliedArchonCenterOfMass = MapUtils.getCenterOfMass(alliedArchonStartLocs);
        enemyArchonStartLocs = rc.getInitialArchonLocations(opponent);
        enemyArchonCenterOfMass = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        centerOfMassDifference = alliedArchonCenterOfMass.distanceSquaredTo(enemyArchonCenterOfMass);

        myArchon = -1;

        mapKnowledge.updateEdgesFromLocation(currentLocation);
        for(int k = alliedArchonStartLocs.length; --k >= 0;)
        {
            mapKnowledge.updateEdgesFromLocation(alliedArchonStartLocs[k]);
            mapKnowledge.updateEdgesFromLocation(enemyArchonStartLocs[k]);
        }
    }

    public boolean act() throws GameActionException
    {
        if (precondition()) return false;

        if (aidDistressedArchon());
        else if (fight() || fightZombies());
        else if (carryOutAbility());
        else
        {
            if (updateTarget()) {
                navigator.setTarget(getNextSpot());
            }
            return takeNextStep();
        }

        return false;
    }

    public boolean updateTarget() throws GameActionException
    {
        return false;
    }

    public MapLocation getNextSpot() throws GameActionException
    {
        return null;
    }

    public boolean precondition()
    {
        return false;
    }

    public boolean aidDistressedArchon() throws GameActionException
    {
        if (type == RobotType.SOLDIER && distressedArchon != null)
        {
            if (!defendingArchon || !navigator.getTarget().equals(distressedArchon))
            {
                defendingArchon = true;
                navigator.setTarget(distressedArchon);
            }

            // condition for when we should stop rallying around an archon
            if (rc.getLocation().distanceSquaredTo(distressedArchon) < 5 && enemies.length == 0)
            {
                distressedArchon = null;
                defendingArchon = false;
                return false;
            }

            // rush towards archon shooting anything in path
            if (rc.isWeaponReady() && (nearByEnemies.length > 0 || nearByZombies.length > 0))
            {
                if (fightMicro.basicFightMicro(nearByEnemies));
                else if (fightMicro.basicFightMicro(nearByZombies));
            }
            else if (rc.isCoreReady())
            {
                navigator.takeNextStep();
            }

            return true;
        }

        return false;
    }

    // abstract methods that all units will need to implement
    public abstract boolean takeNextStep() throws GameActionException;
    public abstract boolean fight() throws GameActionException;
    public abstract boolean fightZombies() throws GameActionException;

    // additional methods with default behavior
    public void handleMessages() throws GameActionException
    {
        rubbleUpdate = 0;

        communications = communicator.processCommunications();
        for(int k = communications.length; --k >= 0;)
        {
            switch(communications[k].opcode)
            {
                case MAP_BOUNDS:
                case SDEN:
                case SKILLED_DEN:
                case DEAD_DEN:
                case RUBBLE:
                    if(mapComs)
                    {
                        interpretMapKnowlege(communications[k]);
                    }
                    break;
                case PARTS:
                case GOING_AFTER_PARTS:
                case NEUTRAL:
                    if(type == RobotType.ARCHON)
                    {
                        interpretArchonMapKnowledge(communications[k]);
                    }
                    break;
                case EXPLORE_EDGE:
                    if(type == RobotType.SCOUT)
                    {
                        interpretScoutMapKnowledge(communications[k]);
                    }
                    break;
                case OENEMY:
                case ENEMY:
                    if(enemyComs)
                    {
                        interpretEnemy(communications[k]);
                    }
                    break;
                case CHANGEMISSION:
                    if(missionComs)
                    {
                        interpretMissionChange(communications[k]);
                    }
                    break;
                case ATTACK:
                case RALLY_POINT:
                    if(archonComs)
                    {
                        interpretLocFromArchon(communications[k]);
                    }
                    break;
                case ARCHON_DISTRESS:
                    if(archonDistressComs)
                    {
                        interpretDistressFromArchon(communications[k]);
                    }
                    break;
            }
        }
    }

    public void sendMessages() throws GameActionException
    {
        return;
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        //rc.setIndicatorString(1, "current: " + thisBot + ", next: " + nextBot);
        if(nextBot != null && nextBot != thisBot)
        {
            rc.setIndicatorString(0, "Changing to: " + nextBot);
            Unit toReturn = Bots.returnUnit(nextBot, rc);
            Unit.thisBot = nextBot;
            Unit.nextBot = null;
            return toReturn;
        }

        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    public void collectData() throws GameActionException
    {
        enemies = rc.senseNearbyRobots(sightRange, opponent);
        allies = rc.senseNearbyRobots(sightRange, us);
        zombies = rc.senseNearbyRobots(sightRange, Team.ZOMBIE);

        if (type.attackRadiusSquared > 0) {
            nearByAllies = rc.senseNearbyRobots(range, us);
            nearByZombies = rc.senseNearbyRobots(range, Team.ZOMBIE);
            nearByEnemies = rc.senseNearbyRobots(range, opponent);
        }


        round = rc.getRoundNum();

        MapLocation newLoc = rc.getLocation();
        locationLastTurn = currentLocation;
        if (!newLoc.equals(currentLocation)) {
            previousLocation = currentLocation;
        }

        if(!mapKnowledge.allEdgesReached())
        {
            int edge;
            if(!mapKnowledge.edgeReached(Direction.NORTH))
            {
                edge = MapUtils.senseEdge(rc, Direction.NORTH);
                if(edge != Integer.MIN_VALUE)
                {
                    mapKnowledge.firstFoundEdge = true;
                    mapKnowledge.reachEdge(Direction.NORTH);
                    mapKnowledge.minY = edge;
                }
            }
            if(!mapKnowledge.edgeReached(Direction.EAST))
            {
                edge = MapUtils.senseEdge(rc, Direction.EAST);
                if(edge != Integer.MIN_VALUE)
                {
                    mapKnowledge.firstFoundEdge = true;
                    mapKnowledge.reachEdge(Direction.EAST);
                    mapKnowledge.maxX = edge;
                }
            }
            if(!mapKnowledge.edgeReached(Direction.SOUTH))
            {
                edge = MapUtils.senseEdge(rc, Direction.SOUTH);
                if(edge != Integer.MIN_VALUE)
                {
                    mapKnowledge.firstFoundEdge = true;
                    mapKnowledge.reachEdge(Direction.SOUTH);
                    mapKnowledge.maxY = edge;
                }
            }
            if(!mapKnowledge.edgeReached(Direction.WEST))
            {
                edge = MapUtils.senseEdge(rc, Direction.WEST);
                if(edge != Integer.MIN_VALUE)
                {
                    mapKnowledge.firstFoundEdge = true;
                    mapKnowledge.reachEdge(Direction.WEST);
                    mapKnowledge.minX = edge;
                }
            }
        }

        currentLocation = newLoc;
    }

    private void interpretMapKnowlege(Communication communication)
    {
        switch(communication.opcode)
        {
            case MAP_BOUNDS:
                mapKnowledge.updateEdgesFromMessage(communication);
                break;
            case SDEN:
                SimpleBotInfoCommunication com = (SimpleBotInfoCommunication) communication;
                MapLocation den = new MapLocation(com.x, com.y);
                if (!mapKnowledge.dens.contains(den))
                {
                    mapKnowledge.dens.add(den);
                }
                break;
            case SKILLED_DEN:
            case DEAD_DEN:
                com = (SimpleBotInfoCommunication) communication;
                MapLocation spot = new MapLocation(com.x, com.y);

                if(mapKnowledge.dens.contains(spot))
                {
                    mapKnowledge.dens.remove(spot);
                }
            case RUBBLE:
                if (type != RobotType.SCOUT && rubbleUpdate < 2) {
                    rubbleUpdate++;
                    Navigation.map.updateFromComms(communication);
                }
                break;
        }
    }

    private void interpretArchonMapKnowledge(Communication communication)
    {
        switch(communication.opcode)
        {
            case PARTS:
                PartsCommunication com = (PartsCommunication) communication;
                MapLocation loc = new MapLocation(com.x, com.y);

                if(!BaseArchon.sortedParts.contains(loc))
                {
                    BaseArchon.sortedParts.addParts(loc, com.parts, false);
                }
                break;

            case NEUTRAL:
                com = (PartsCommunication) communication;
                loc = new MapLocation(com.x, com.y);

                if(!BaseArchon.sortedParts.contains(loc))
                {
                    BaseArchon.sortedParts.addParts(loc, com.parts, true);
                }
                break;
            case GOING_AFTER_PARTS:
                BotInfoCommunication comm = (BotInfoCommunication) communication;
                if (id < comm.id)
                {
                    loc = new MapLocation(comm.x, comm.y);
                    int index = BaseArchon.sortedParts.getIndexOfMapLocation(loc);
                    BaseArchon.sortedParts.remove(index);
                    try { navigator.setTarget(BaseArchon.getNextPartLocation()); } catch (Exception e) {e.printStackTrace();}
                }
                break;
        }
    }

    private void interpretScoutMapKnowledge(Communication communication)
    {

        switch(communication.opcode)
        {
            case EXPLORE_EDGE:
                ExploringMapEdge com = (ExploringMapEdge)communication;
                ScoutMapKnowledge tempKnow = (ScoutMapKnowledge) mapKnowledge;

                if(rc.getRoundNum() < 30)
                {

                    if(type == RobotType.SCOUT)
                    {
                        if(ScoutingScout.getScoutDir() == com.edge && id > com.id)
                        {
                            ScoutingScout.updateScoutDirection();
                        }
                    }

                    tempKnow.setEdgesBeingExplored(com.edge);
                }
                break;
        }
    }

    private void interpretEnemy(Communication communication)
    {
        switch(communication.opcode)
        {
            case OENEMY:
                SimpleBotInfoCommunication com = (SimpleBotInfoCommunication) communication;
                mapKnowledge.updateArchon(new SimpleRobotInfo(com.id,
                        new MapLocation(com.x, com.y), RobotType.ARCHON, opponent), false);
                mapKnowledge.updateEdgesFromLocation(new MapLocation(com.x, com.y));
                break;
            case ENEMY:
                BotInfoCommunication botCom = (BotInfoCommunication) communication;
                if(botCom.type == RobotType.ARCHON)
                {
                    mapKnowledge.addArchon(new SimpleRobotInfo(botCom.id,
                            new MapLocation(botCom.x, botCom.y), RobotType.ARCHON,
                            opponent), false);
                }
                break;
        }
    }

    private void interpretMissionChange(Communication communication)
    {
        switch(communication.opcode)
        {
            case CHANGEMISSION:
                MissionCommunication comm = (MissionCommunication) communication;
                rc.setIndicatorString(0, "next bot is " + comm.newBType);
                if(comm.id == rc.getID())
                {
                    nextBot = comm.newBType;
                    rc.setIndicatorString(0, "changing mission to " + nextBot);
                    if(nextBot == Bots.RUSHGUARD || nextBot == Bots.RUSHSCOUT ||
                       nextBot == Bots.RUSHINGSOLDIER || nextBot == Bots.RUSHTURRET ||
                       nextBot == Bots.RUSHINGVIPER)
                    {
                        rc.setIndicatorString(2, "setting target");
                        rushTarget = new MapLocation(comm.x, comm.y);
                    }
                }
                break;
        }
    }

    private void interpretLocFromArchon(Communication communication)
    {

        switch(communication.opcode)
        {
            case ATTACK:
                AttackCommunication com = (AttackCommunication)communication;
                if(myArchon == -1 || myArchon == com.signalID)
                {
                    rushTarget = new MapLocation(com.x, com.y);
                }
                break;
            case RALLY_POINT:
                com = (AttackCommunication)communication;
                rallyPoint = new MapLocation(com.x, com.y);
                break;
        }
    }

    private void interpretDistressFromArchon(Communication communication)
    {
        switch(communication.opcode)
        {
            case ARCHON_DISTRESS:
                BotInfoCommunication com = (BotInfoCommunication) communication;
                distressedArchon = new MapLocation(com.x, com.y);
                navigator.setTarget(distressedArchon);
                break;
        }
    }

    /**
     * If we are not infected and about to die to enemy zombies then we should
     * disintigrate to not turn into a zombie
     */
    public void suicide() throws GameActionException
    {
        if (RobotPlayer.strategy.equals(Strategies.SCOUT_BOMB)) {
            return;
        }

        if (rc.getHealth() <= 15)
        {
            if (zombies.length > 0 && type != RobotType.ARCHON)
            {
                if (!rc.isInfected())
                {
                    for (int i = zombies.length; --i>=0;)
                    {
                        // if we are in range
                        if (currentLocation.distanceSquaredTo(zombies[i].location) <= zombies[i].type.attackRadiusSquared)
                        {
                            // if zombie has a weapon cool down less than or equal to 1
                            if (zombies[i].weaponDelay <= 1)
                            {
                                // don't want to stop from becoming a zombie in enemy base
                                if (enemies.length < allies.length)
                                {
                                    System.out.println("WE disintegrated to avoid turning into a zombie");
                                    rc.setIndicatorDot(currentLocation, 1, 1, 1);
                                    rc.disintegrate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
