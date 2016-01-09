package team037;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.MissionCommunication;

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
    public static MapLocation target;
    public static Direction[] dirs;
    public static FightMicro fightMicro;
    public static Navigator navigator;
    public static Communicator communicator;
    public static Bots nextBot = null;
    public static Bots thisBot= null;
    public static int id;
    public static Communication[] communications;

    public MapLocation locationLastTurn;
    public MapLocation previousLocation;
    public MapLocation currentLocation;

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
        dirs = Direction.values();
        fightMicro = new FightMicro(rc);
        navigator = new Navigator(rc);
        communicator = new Communicator(rc);
        turnCreated = rc.getRoundNum();
        locationLastTurn = rc.getLocation();
        previousLocation = locationLastTurn;
        currentLocation = locationLastTurn;
    }

    public boolean act() throws GameActionException {
        if (fight() || fightZombies());
        else if (carryOutAbility());
        else if (takeNextStep());

        return true;
    }

    // abstract methods that all units will need to implement
    public abstract boolean takeNextStep() throws GameActionException;
    public abstract boolean fight() throws GameActionException;
    public abstract boolean fightZombies() throws GameActionException;

    // additional methods with default behavior
    public void handleMessages() throws GameActionException
    {
        communications = communicator.processCommunications();
        for(int k = 0; k < communications.length; k++)
        {
            if(communications[k].opcode == CommunicationType.CHANGEMISSION)
            {
                MissionCommunication comm = (MissionCommunication) communications[k];
                if(comm.id == rc.getID())
                {
                    nextBot = comm.newBType;
                }
                else if(comm.id == 0 && comm.bType == thisBot)
                {
                    nextBot = comm.newBType;
                }
                else if(comm.id == 0 && comm.rType == rc.getType())
                {
                    nextBot = comm.newBType;
                }
            }
            else if (communications[k].opcode == CommunicationType.TURRET_SUPPORT)
            {
                System.out.println("TURRET_SUPPORT");
            }
        }
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if(nextBot != null && nextBot != thisBot)
        {
            Unit toReturn = Bots.returnUnit(nextBot, rc);
            toReturn.thisBot = nextBot;
            toReturn.nextBot = null;
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

        nearByEnemies = rc.senseNearbyRobots(range, opponent);
        nearByAllies = rc.senseNearbyRobots(range, us);

        enemies = rc.senseNearbyRobots(sightRange, opponent);
        allies = rc.senseNearbyRobots(sightRange, us);

        nearByZombies = rc.senseNearbyRobots(range, Team.ZOMBIE);
        zombies = rc.senseNearbyRobots(sightRange, Team.ZOMBIE);

        MapLocation newLoc = rc.getLocation();
        locationLastTurn = currentLocation;
        if (!newLoc.equals(currentLocation)) {
            previousLocation = currentLocation;
        }
        currentLocation = newLoc;
    }
}
