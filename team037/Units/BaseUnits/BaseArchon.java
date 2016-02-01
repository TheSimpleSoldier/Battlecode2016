package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Messages.MissionCommunication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.Unit;
import team037.MapKnowledge;
import team037.Units.PacMan.PacMan;
import team037.Utilites.BuildOrderCreation;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.ZombieTracker;

/**
 * Base extension of the Unit class for units of RobotType Archon.
 */
public class BaseArchon extends Unit implements PacMan {

    // Additional variables used by almost all Archon implementations
    public BuildOrder buildOrder;   // Dictates the basic order in which units are created
    public static Bots nextBot;     // Dictates the next extension of Unit this archon should build
    public static RobotType nextType;       // Dictates the next RobotType this archon should build
    public static RobotInfo[] neutralBots;  // Checks 8 adjacent locations for neutral units to activate
    public static SortedParts sortedParts = new SortedParts();  // Hash stores locations of parts and neutral units
    private int rushingUnits = 0;           // Number of units rushing
    private boolean sentRushSignal = false; // Flag for when this archon signals other units to rush
    private int turnHealed = 0;     // Last turn this archon healed
    private int retreatCall = 0;    // Last turn this archon signaled for retreat
    public static ZombieTracker zombieTracker;  // Track the strength and schedule of zombie spawns
    private static int distToFurthestArchon;    // Distance to the furthest allied archon for signaling purposes

    // Constructor
    public BaseArchon(RobotController rc) {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Bots.typeFromBot(nextBot);
        zombieTracker = new ZombieTracker(rc);
        archonDistressComs = false;
        rc.setIndicatorString(0, "Base Archon zombie Strength: " + zombieTracker.getZombieStrength());

        distToFurthestArchon = 0;
        for (int i = alliedArchonStartLocs.length; --i >= 0; ) {
            int currentDist = currentLocation.distanceSquaredTo(alliedArchonStartLocs[i]);
            if (currentDist > distToFurthestArchon) {
                distToFurthestArchon = currentDist;
            }
        }

    }

    @Override // precondition() in class Unit. Return true if we cannot move or build this turn.
    public boolean precondition() {
        return !rc.isCoreReady();
    }
    @Override // takeNextStep() in class Unit by contract of extension. Moves prioritize nearby parts.
    public boolean takeNextStep() throws GameActionException {
        // Try to move

        if (currentLocation != null && navigator.getTarget() != null) {
            rc.setIndicatorLine(currentLocation, navigator.getTarget(), 255, 0, 0);
        }


        // if there are no visible zombies then we should move to collect parts
        if (!FightMicroUtilites.offensiveEnemies(enemies) && !FightMicroUtilites.offensiveEnemies(zombies)) {
            MapLocation navigatorTarget;

            // if we are trying to get to parts and the location has rubble on it we should clear it
            try {
                navigatorTarget = navigator.getTarget();

                if (rc.isCoreReady() && currentLocation != null && navigatorTarget != null && currentLocation.isAdjacentTo(navigatorTarget)) {
                    if (rc.canSense(navigatorTarget) && rc.senseRubble(navigatorTarget) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                        rc.clearRubble(currentLocation.directionTo(navigatorTarget));
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!sortedParts.contains(navigator.getTarget())) {
                MapLocation parts = getNextPartLocationInSight();

                if (parts != null) {
                    navigator.setTarget(parts);
                    rc.setIndicatorLine(currentLocation, parts, 0, 0, 255);
                    rc.setIndicatorString(2, "Parts loc x: " + parts.x + " y: " + parts.y + " round: " + rc.getRoundNum());
                }
            }
        }

        return navigator.takeNextStep();
    }
    @Override // collectData() in class Unit. Calls super.collectData() and finds nearby parts and neutral units.
    public void collectData() throws GameActionException {
        super.collectData();

        neutralBots = rc.senseNearbyRobots(2, Team.NEUTRAL);

        if (sortedParts.contains(currentLocation)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
        }

        MapLocation target = navigator.getTarget();
        if (target != null && rc.canSenseLocation(target) && rc.senseParts(target) == 0) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(target));
        }

        int index = sortedParts.getIndexOfMapLocation(currentLocation);

        if (index >= 0) {
            sortedParts.remove(index);
        }

        sortedParts.findPartsAndNeutralsICanSense(rc);

        // heal doesn't effect core cooldown
        healNearbyAllies();

        if (neutralBots.length > 0 && rc.isCoreReady()) {
            rc.activate(neutralBots[0].location);
            Bots currentBot = nextBot;
            nextBot = getDefaultBotTypes(neutralBots[0].type);
            sendInitialMessages(currentLocation.directionTo(neutralBots[0].location));
            nextBot = currentBot;
        }

        MapLocation neutralArchon = sortedParts.getNeutralArchon();
        
        if (neutralArchon != null)
        {
            if (navigator.getTarget() == null || !navigator.getTarget().equals(neutralArchon))
            {
                System.out.println("We see a neutral archon");
                navigator.setTarget(neutralArchon);
            }

            if (rc.isCoreReady())
            {
                navigator.takeNextStep();
            }
        }

    }
    @Override // fight() required by the extension of class Unit. Calls PacMan runAway if enemy units are present.
    public boolean fight() throws GameActionException {
        if (!FightMicroUtilites.offensiveEnemies(enemies)) return false;

        rc.setIndicatorDot(currentLocation, 255, 0, 0);

        return runAway(null);
    }

    @Override // fightZombies() required by the extension of class Unit. Calls PacMan runAway if zombies are present.
    public boolean fightZombies() throws GameActionException {
        if (!FightMicroUtilites.offensiveEnemies(zombies)) return false;

        rc.setIndicatorDot(currentLocation, 255, 0, 0);

        return runAway(null);
    }

    /**
     * Get the default extension for every RobotType archons can build.
     * @param type RobotType enum used by the Battlecode engine
     * @return Bots enum representing the default extension for the given RobotType
     */
    public Bots getDefaultBotTypes(RobotType type) {
        switch (type) {
            case SOLDIER:
                return Bots.BASESOLDIER;
            case TURRET:
                return Bots.BASETURRET;
            case TTM:
                return Bots.BASETURRET;
            case GUARD:
                return Bots.BASEGAURD;
            case SCOUT:
                return Bots.BASESCOUT;
            case ARCHON:
                return Bots.ALPHAARCHON;
            case VIPER:
                return Bots.BASEVIPER;
            default:
                return Bots.BASESOLDIER;
        }
    }

    /**
     * Send messages to units regarding the following information:
     *  1) Send a distress call if being overwhelmed by enemy units.
     *  2) Discovery of a boundary of this map
     *  3) Changes to known bounds of the map
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    @Override // sendMessages() in class Unit.
    public void sendMessages() throws GameActionException {
        int offensiveEnemies = 0;

        for (int i = enemies.length; --i >= 0; ) {
            switch (enemies[i].type) {
                case TURRET:
                case GUARD:
                case SOLDIER:
                case VIPER:
                    offensiveEnemies++;
            }
        }

        offensiveEnemies += zombies.length;

        if (offensiveEnemies > allies.length && (rc.getRoundNum() - retreatCall) > 25 && msgsSent < 20) {
            retreatCall = rc.getRoundNum();
            Communication distressCall = new BotInfoCommunication();
            distressCall.setValues(new int[]{CommunicationType.toInt(CommunicationType.ARCHON_DISTRESS), 0, 0, id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(MapKnowledge.getRange(), distressCall);
            msgsSent++;
        }

        if (mapKnowledge.firstFoundEdge && msgsSent < 20) {
            Communication com = mapKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(distToFurthestArchon, com);
            msgsSent++;
            mapKnowledge.firstFoundEdge = false;
            mapKnowledge.updated = false;
        }
        if (mapKnowledge.updated && msgsSent < 20) {
            Communication com = mapKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(MapKnowledge.getRange(), com);
            msgsSent++;
            mapKnowledge.updated = false;
        }
    }

    /**
     * Selects the friendly unit with the lowest health and heals it.
     * Does not affect the archon's delay beyond bytecode usage
     * @return true if the archon has healed a a friendly unit.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public boolean healNearbyAllies() throws GameActionException
    {
        // precondition
        if (nearByAllies.length == 0 || turnHealed == rc.getRoundNum())
        {
            return false;
        }

        double weakestHealth = 9999;
        RobotInfo weakest = null;

        for (int i = nearByAllies.length; --i >= 0; ) {
            double health = nearByAllies[i].health;
            if (nearByAllies[i].type != RobotType.ARCHON && health < nearByAllies[i].maxHealth &&
                    currentLocation.distanceSquaredTo(nearByAllies[i].location) <= RobotType.ARCHON.attackRadiusSquared)
            {

                if (health < weakestHealth)
                {
                    weakestHealth = health;
                    weakest = nearByAllies[i];
                }
            }
        }

        try {
            if (weakest != null)
            {
                if (rc.senseRobotAtLocation(weakest.location) != null)
                {
                    rc.repair(weakest.location);
                }
                turnHealed = rc.getRoundNum();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override // carryOutAbility() in class Unit. Try building a new unit.
    public boolean carryOutAbility() throws GameActionException {
        if (enemies.length > allies.length) {
            return false;
        }

        // Try to build a unit.
        return buildNextUnit();
    }

    /**
     * Create a scout that will escort this archon, assist with digging, and lead zombies away.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void buildScavengerScout() throws GameActionException
    {
        Bots temp = Bots.SCAVENGERSCOUT;
        nextType = RobotType.SCOUT;

        if (rc.hasBuildRequirements(Bots.typeFromBot(temp)) && rc.isCoreReady())
        {
            Bots temp2 = Bots.fromInt(Bots.toInt(nextBot));
            nextBot = temp;

            nextType = Bots.typeFromBot(temp);
            Direction dir = build();
            if (dir != Direction.NONE)
            {
                sendInitialMessages(dir);
                nextBot = temp2;
                nextType = Bots.typeFromBot(nextBot);
                System.out.println("We are spawning a scavenger scout");
            }
        }
    }

    /**
     * Find an empty location adjacent to the archon and build the Bot specified by the current value of nextBot.
     * @return true if we built a unit, false otherwise.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public boolean buildNextUnit() throws GameActionException {
        // if there are multiple archons and we have limited parts and we see either parts or neutrals we
        // should let the other archon's build while we pick them up.
        if (alliedArchonStartLocs.length > 1 && rc.getTeamParts() < 200 &&
                (rc.sensePartLocations(sightRange).length > 0 ||
                        rc.senseNearbyRobots(sightRange, Team.NEUTRAL).length > 0)) {
            return false;
        }

        Bots temp = changeBuildOrder(nextBot);

        if (!temp.equals(nextBot))
        {
            if (rc.hasBuildRequirements(Bots.typeFromBot(temp)) && rc.isCoreReady())
            {
                Bots temp2 = Bots.fromInt(Bots.toInt(nextBot));
                nextBot = temp;

                nextType = Bots.typeFromBot(temp);
                Direction dir = build();
                if (dir != Direction.NONE) {
                    sendInitialMessages(dir);
                    nextBot = temp2;
                    nextType = Bots.typeFromBot(nextBot);
                    return true;
                }
            }
        }
        else if(rc.hasBuildRequirements(Bots.typeFromBot(nextBot)) && rc.isCoreReady())
        {
            Direction dir = build();
            if (dir != Direction.NONE) {
                sendInitialMessages(dir);
                nextBot = buildOrder.nextBot();
                nextType = Bots.typeFromBot(nextBot);
                return true;
            }
        }

        return false;
    }

    /**
     * Sends messages to a unit to dictate its behavior. BuildOrder specifies the next unit to build.
     * @param dir Direction of the unit we just built
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void sendInitialMessages(Direction dir) throws GameActionException {

        int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;

        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = id;
        communication.newBType = nextBot;
        communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication();
        communicator.sendCommunication(2, mapBoundsCommunication);

        for (int j = mapKnowledge.dens.length; --j >= 0; ) {
            MapLocation den = mapKnowledge.dens.array[j];

            if (den != null) {
                Communication communicationDen = new SimpleBotInfoCommunication();
                communicationDen.setValues(new int[]
                        {
                                CommunicationType.toInt(CommunicationType.SDEN),
                                0,
                                den.x,
                                den.y
                        });
                communicator.sendCommunication(2, communicationDen);
            }
        }
    }

    /**
     * Sends messages to a unit to dictate its behavior. Specify all data.
     * @param dir direction of spawn relative to the archon
     * @param nextType spawned bot's RobotType enum
     * @param nextBot spawned bot's Bots enum
     * @param sendDenLocs set true to inform unit of known dens
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void sendInitialMessages(Direction dir, RobotType nextType,
                                    Bots nextBot, boolean sendDenLocs) throws GameActionException {

        int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;

        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = id;
        communication.newBType = nextBot;
        communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication();
        communicator.sendCommunication(2, mapBoundsCommunication);

        if (sendDenLocs) {
            for (int j = mapKnowledge.dens.length; --j >= 0; ) {
                MapLocation den = mapKnowledge.dens.array[j];

                if (den != null) {
                    Communication communicationDen = new SimpleBotInfoCommunication();

                    communicationDen.setValues(new int[]
                            {
                                    CommunicationType.toInt(CommunicationType.SDEN),
                                    0,
                                    den.x,
                                    den.y
                            });

                    communicator.sendCommunication(2, communicationDen);
                }
            }
        }
    }

    /**
     * @deprecated with the release of RobotController.getInitialArchonLocations(Team team)
     * Send a signal with the initial location of the archon.
     * @return
     */
    public static void sendOutInitialLocation() {
        try {
            Communication communication = new SimpleBotInfoCommunication();

            communication.setValues(new int[]
                    {
                            CommunicationType.toInt(CommunicationType.SARCHON),
                            id,
                            rc.getLocation().x,
                            rc.getLocation().y
                    });

            communicator.sendCommunication(2500, communication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the best part in SortedParts.
     * @return MapLocation of the next part in sortedParts.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public static MapLocation getNextPartLocation() throws GameActionException {
        MapLocation next = sortedParts.getBestSpot(currentLocation);
        MapLocation lastTarget = null;

        while (next != null && (rc.canSenseLocation(next) && rc.senseParts(next) == 0 &&
                (rc.senseRobotAtLocation(next) == null || !rc.senseRobotAtLocation(next).team.equals(Team.NEUTRAL)))) {

            int index = sortedParts.getIndexOfMapLocation(next);
            if (index < 0) {
                sortedParts.hardRemove(next);
                lastTarget = new MapLocation(next.x, next.y);
            } else {
                sortedParts.remove(index);
            }

            next = sortedParts.getBestSpot(currentLocation);

            if (lastTarget != null && lastTarget.equals(next)) {
                System.out.println("we have a problem");
            }
        }

        return next;
    }


    /**
     * Get the best part in SortedParts within sight range.
     * @return MapLocation of the next part in sortedParts.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public static MapLocation getNextPartLocationInSight() throws GameActionException {
        MapLocation next = sortedParts.getBestSpotInSightRange(currentLocation);
        MapLocation lastTarget = null;

        while (next != null && (rc.canSenseLocation(next) && rc.senseParts(next) == 0 &&
                (rc.senseRobotAtLocation(next) == null || !rc.senseRobotAtLocation(next).team.equals(Team.NEUTRAL)))) {

            int index = sortedParts.getIndexOfMapLocation(next);
            if (index < 0) {
                sortedParts.hardRemove(next);
                lastTarget = new MapLocation(next.x, next.y);
            } else {
                sortedParts.remove(index);
            }

            next = sortedParts.getBestSpotInSightRange(currentLocation);

            if (lastTarget != null && lastTarget.equals(next)) {
                System.out.println("we have a problem");
            }
        }

        if (next != null) {
            rc.setIndicatorString(1, "x: " + next.x + " y: " + next.y);
        }
        return next;
    }


    /**
     * Spawn a unit.
     * @return Direction the the unit was spawned relative to the archon.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public Direction build() throws GameActionException {
        double rubble = Double.MAX_VALUE;
        Direction least = null;
        for (int i = dirs.length; --i>=0; )
        {
            if(rc.onTheMap(currentLocation.add(dirs[i])))
            {
                if(rc.canBuild(dirs[i], nextType))
                {
                    rc.build(dirs[i], nextType);
                    return dirs[i];
                }
                double tempRubble = rc.senseRubble(currentLocation.add(dirs[i]));
                if (tempRubble < rubble && tempRubble > 0) {
                    rubble = tempRubble;
                    least = dirs[i];
                }
            }
        }
        if (least != null)
        {
            try { rc.clearRubble(least); } catch (Exception e) { e.printStackTrace(); }
        }

        return Direction.NONE;
    }

    /**
     * Override this method to change the build order mid game.
     * @param nextBot next extension of class Unit to build.
     * @return
     */
    public Bots changeBuildOrder(Bots nextBot) {
        return nextBot;
    }

    @Override // suicide() in class Unit
    /**
     * Never disintegrate archons.
     */
    public void suicide() throws GameActionException
    {
        return;
    }
}
