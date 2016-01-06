package team037;

import battlecode.common.*;
import team037.Utilites.Communication;

public abstract class Unit
{
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
    }

    // abstract methods that all units will need to implement
    public abstract boolean takeNextStep() throws GameActionException;
    public abstract boolean fight() throws GameActionException;
    public abstract boolean fightZombies() throws GameActionException;

    // additional methods with default behavior
    public void handleMessages() throws GameActionException
    {
        Communication[] communications = communicator.processCommunications();
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
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
    }
}
