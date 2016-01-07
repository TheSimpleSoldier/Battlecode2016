package team037;

import battlecode.common.*;
import team037.DataStructures.Communication;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;

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
        if (fight());
        else if (fightZombies());
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
        Communication[] communications = communicator.processCommunications();
        for(int k = 0; k < communications.length; k++)
        {
            if(communications[k].opcode == CommunicationType.INITIALMISSION &&
               communications[k].val1 == rc.getID())
            {
                System.out.println("getting message");
                nextBot = communications[k].bType1;
            }

            if(communications[k].opcode == CommunicationType.CHANGEMISSION)
            {
                if(communications[k].val1 == rc.getID() ||
                   (communications[k].val1 == 0 && communications[k].bType1 == thisBot))
                {
                    nextBot = communications[k].bType2;
                }
            }
        }
        /*
         * Sample communication creation
        Communication communication = new Communication();
        communication.opcode = CommunicationType.DEN;
        communication.val1 = 120;
        communication.loc1X = 68;
        communication.loc1Y = 52;
        communicator.sendCommunication(100, communication);
         */
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
