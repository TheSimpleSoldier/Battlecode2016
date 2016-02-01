package team037;

import battlecode.common.*;
import team037.DataStructures.SimpleRobotInfo;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Enums.Strategies;
import team037.Messages.*;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.Scouts.ScoutingScout;
import team037.Utilites.MapUtils;

/**
 * Unit is the base class for all units, regardless of type. Holds data and
 * methods used by almost all units at some point in every game.
 *
 * RobotPlayer executes the following methods for Unit objects once every round (in order):
 *
 * 1) collectData();        // Gather data at the start of the round
 * 2) handleMessages();     // Decode messages and do something with them
 * 3) sendMessages();       // Send messages if needed
 * 4) act();                // Prioritize and take actions
 * 5) getNewStrategy(unit); // Get a new object that extends Unit to change this unit's behavior
 * 6) suicide();            // Disintegrate if this unit poses a threat to its team
 */
public abstract class Unit
{
    // Variables for data needed by most units
    // Made public and static to enable bytecode-efficient access for utility classes.
    public static int turnCreated;  // Turn this unit was built
    public static RobotController rc;   // Link between this unit and the game engine
    public static int range;        // Weapon range
    public static RobotType type;   // This unit's RobotType
    public static int sightRange;   // Sight range
    public static Team us;          // Our team
    public static Team opponent;    // Enemy team
    public static RobotInfo[] nearByEnemies;    // Enemies in weapon range
    public static RobotInfo[] nearByAllies;     // Allies in weapon range
    public static RobotInfo[] nearByZombies;    // Zombies in weapon range
    public static RobotInfo[] enemies;  // Enemies in sight range
    public static RobotInfo[] allies;   // Allies in sight range
    public static RobotInfo[] zombies;  // Zombies in sight range
    public static MapLocation[] enemyArchonStartLocs;   // Enemy archon starting locations
    public static MapLocation[] alliedArchonStartLocs;  // Allied archon starting locations
    public static MapLocation target;   // Unit's current target
    public static Direction[] dirs;     // Compass converts from integers to Directions
    public static FightMicro fightMicro;// Class dictating how this unit fights
    public static Navigator navigator;  // Class dictating how this unit moves
    public static Communicator communicator;    // Class used to encode/decode signals
    public static Bots nextBot = null;  // Bots enum representing this unit's next extension of Unit
    public static Bots thisBot= null;   // Bots enum representing this unit's current extension of Unit
    public static int id;       // This unit's unique ID used to identify it apart from other units
    public static int round;    // The current round number
    public static Communication[] communications;   // Communication objects created from messages received this round
    public static MapKnowledge mapKnowledge = new MapKnowledge();   // Data related to map bounds, topography, etc.
    public static MapLocation start;    // Location where this unit was built
    public static boolean repaired;
    public static int msgsSent = 0;     // Tracks the number of messages this unit sent this round
    public static boolean defendingArchon = false;  // Flag for when this unit is actively defending an archon
    public static int rubbleUpdate = 0; // Number of updates to map topography in this round

    // Flags determining which communications this unit needs
    public static boolean enemyComs = true;
    public static boolean archonComs = true;
    public static boolean archonDistressComs = true;
    public static boolean mapComs = true;
    public static boolean missionComs = true;
    public static boolean anyComs = true;

    // Significant MapLocations
    public static MapLocation locationLastTurn; // Where this unit was last turn
    public static MapLocation previousLocation; // Where this unit was before its current location
    public static MapLocation currentLocation;  // Current location of this unit
    public static MapLocation rushTarget;       // Target this unit is rushing
    public static MapLocation rallyPoint;       // Target for this unit to regroup with other units
    public static MapLocation distressedArchon; // Location of a distressed archon, if any
    public static MapLocation turtlePoint;      // Point where this unit is turtling
    public static MapLocation enemyArchonCenterOfMass;  // Average of all enemy archon starting locations
    public static MapLocation alliedArchonCenterOfMass; // Average of all allied archon starting locations
    public static MapLocation enemyArchon;      // Location of a recently sighted enemy archon
    public static int centerOfMassDifference;   // Distance between allied and enemy archon centers of mass
    public static int myArchon;                 // The archon that built this unit
    public static int turretSupportMsgRound;    // Round this unit sends messages to turrets

    public Unit()
    {
        // default constructor
    }

    // Constructor
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

        turretSupportMsgRound = 0;
        enemyArchon = null;
    }

    /**
     * Prioritize actions for the unit to take each turn.
     *
     * Order of method calls made by act() (short-circuits if a method returns true):
     *
     * precondition() ||
     * aidDistressedArchon() ||
     * fight() ||
     * fightZombies() ||
     * carryOutAbility() ||
     * takeNextStep()
     *
     * @return moved (true) or did not move (false)
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
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

    /**
     * Check if this unit needs to change its target.
     * @return True if we need to update this unit's target.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public boolean updateTarget() throws GameActionException
    {
        return false;
    }

    /**
     * Find a target for this unit.
     * @return a target for this unit.
     * @throws GameActionException
     */
    public MapLocation getNextSpot() throws GameActionException
    {
        return null;
    }

    /**
     * If this archon cannot act, we need not waste bytecodes.
     * Called first, before fight(), by method act() in class Unit by default.
     *
     * @return Can act (true) or cannot act (false)
     */
    public boolean precondition()
    {
        return false;
    }

    /**
     * Run to an archon that sent a distress signal.
     * @return True if we took an action towards assisting our archon, false otherwise.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
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
    /**
     * Method used to move the unit about the map.
     * Called last, after carryOutAbility(), by method act() in class Unit by default.
     *
     * @return moved (true) or did not move (false)
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public abstract boolean takeNextStep() throws GameActionException;

    /**
     * Handle the presence of offensive non-zombie enemy units.
     * Called second, after precondition() and before fightZombies(), by method act() in class Unit by default.
     *
     * @return true if we took action related to non-zombie enemies, false otherwise.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public abstract boolean fight() throws GameActionException;

    /**
     * Handle the presence of offensive zombie units.
     * Called third, after fight() and before carryOutAbility(), by method act() in class Unit by default.
     *
     * @return true we took action related to zombies, false otherwise.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public abstract boolean fightZombies() throws GameActionException;



    // additional methods with default behavior

    /**
     * Distribute message signals received this round based on opcodes dictating message types.
     * Called second in RobotPlayer's loop each turn. Follows collectData() and precedes sendMessages().
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void handleMessages() throws GameActionException
    {
        if(anyComs)
        {
            rubbleUpdate = 0;

            communications = communicator.processCommunications();
            for(int k = communications.length; --k >= 0; )
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

                    case TURRET_SUPPORT:
                        handleTurretSupport(communications[k]);
                        break;
                }
            }
        }
    }

    /**
     * Message turrets for targets.
     * @param communication Communication object created from signal received this round.
     */
    public void handleTurretSupport(Communication communication)
    {
        turretSupportMsgRound = rc.getRoundNum();
    }

    /**
     * Extensions of class Unit should override sendMessages() to send messages.
     * Called third in RobotPlayer's loop each turn. Follows handleMessages() and precedes act().
     *
     * TODO: Consider executing this after act(), as its actions affect core delay.
     *
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void sendMessages() throws GameActionException
    {
        return;
    }

    /**
     * Change this unit from one extension of Unit to another.
     * @param current object representing this unit's current class.
     * @return an instance of this unit's new class.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
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

    /**
     * Use the abilities of this unit if it has any.
     * Called 5th in RobotPlayer's loop each turn. Follows act() and precedes suicide().
     *
     * @return true if this unit used an ability affecting core delay.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    /**
     * Collect any information needed prior to the execution of a turn.
     * Called 4th in RobotPlayer's loop each turn. Follows sendMessages() and precedes getNewStrategy().
     *
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
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

    /**
     * Handle messages related to map knowledge.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretMapKnowlege(Communication communication)
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

    /**
     * Handle messages related to map knowledge, specifically for archons.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretArchonMapKnowledge(Communication communication)
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

    /**
     * Handle messages related to map knowledge, specifically for scouts.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretScoutMapKnowledge(Communication communication)
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

    /**
     * Handle messages from enemies.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretEnemy(Communication communication)
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

    /**
     * Handle messages calling for units to change their Unit type.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretMissionChange(Communication communication)
    {
        switch(communication.opcode)
        {
            case CHANGEMISSION:
                MissionCommunication comm = (MissionCommunication) communication;
                rc.setIndicatorString(0, "next bot is " + comm.newBType + " round: " + rc.getRoundNum());
                if(comm.id == rc.getID())
                {
                    nextBot = comm.newBType;
                    rc.setIndicatorString(0, "changing mission to " + nextBot  + " round: " + rc.getRoundNum());
                    if(nextBot == Bots.RUSHGUARD || nextBot == Bots.RUSHSCOUT ||
                       nextBot == Bots.RUSHINGSOLDIER || nextBot == Bots.SUPERRUSHTURRET ||
                       nextBot == Bots.SUPERRUSHVIPER)
                    {
                        rc.setIndicatorString(2, "setting target");
                        rushTarget = new MapLocation(comm.x, comm.y);
                    }
                }

                if (nextBot != null && Bots.typeFromBot(nextBot) != type)
                {
                    System.out.println("Trying to be the wrong type");
                    nextBot = null;
                }
                break;
        }
    }

    /**
     * Get the location of an archons based on coordinatesn in a communication taken from a signal.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretLocFromArchon(Communication communication)
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

    /**
     * Handle Communications with opcodes indicating archon distress calls.
     * @param communication communication object storing data decoded from a signal.
     */
    public static void interpretDistressFromArchon(Communication communication)
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
     * If we are not infected and about to die to enemy zombies then we should disintegrate to
     * avoid turning into a zombie.
     * Called last in RobotPlayer's loop each turn. Follows getNewStrategy().
     *
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
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
