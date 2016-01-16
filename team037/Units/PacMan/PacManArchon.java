package team037.Units.PacMan;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.*;
import team037.Navigation;
import team037.ScoutMapKnowledge;
import team037.Unit;
import team037.Utilites.MapUtils;
import team037.Utilites.Utilities;

/**
 * PacMan bot runs away. That's it.
 * Created by davej on 1/13/2016.
 */
public class PacManArchon extends Unit implements PacMan {

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
                    {1, .25, .333333, .5, .5},  // enemy weights (enemies in sensor range)
                    {8, 4, 2, 1, 0},            // target constants (attract towards target)
                    {1, .5, .5, .5, .5},        // parts weights (move towards parts locations in sensor range)
            };

    public RobotInfo[] myScouts;
    public int scoutCount;
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;
    RobotInfo[] neutralBots;
    public static SortedParts sortedParts = new SortedParts();
    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public PacManArchon(RobotController rc) {
        super(rc);
        buildOrder = createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Bots.typeFromBot(nextBot);
        myScouts = new RobotInfo[5];
        scoutCount = 0;
    }

    public static BuildOrder createBuildOrder() {
        Bots[][] buildOrder = {
                {Bots.PACMANGUARD, null},
        };

        int[] times = {1000};

        return new BuildOrder(buildOrder, times);
    }

    @Override
    public boolean aidDistressedArchon() {
        return false;
    }

//    @Override
//    public boolean precondition() {
//        if (!locationLastTurn.equals(currentLocation)) {
//            Navigation.map.scan(currentLocation);
//            Navigation.lastScan = currentLocation;
//        }
//
//        try {
//            if (sortedParts.contains(currentLocation)) {
//                sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
//                Communication communication = new BotInfoCommunication();
//                communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
//                communicator.sendCommunication(400, communication);
//            }
//
//            if (updateTarget()) {
//                navigator.setTarget(sortedParts.getBestSpot(currentLocation));
//            }
//        } catch (GameActionException e) {
//        }
//        return false;
//    }

    @Override
    public boolean act() throws GameActionException {
        boolean ability = carryOutAbility();
        if (!locationLastTurn.equals(currentLocation)) {
            Navigation.map.scan(currentLocation);
            Navigation.lastScan = currentLocation;
        }

        if (sortedParts.contains(currentLocation)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
            Communication communication = new BotInfoCommunication();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(400, communication);
        }

        if (updateTarget()) {
            navigator.setTarget(sortedParts.getBestSpot(currentLocation));
        }

        return ability || fightZombies();
    }

    public boolean takeNextStep() throws GameActionException {
        return navigator.takeNextStep();
    }


    public boolean fight() throws GameActionException {
        // Call vipers
        return false;
    }

    public boolean updateTarget() throws GameActionException {
        MapLocation currentTarget = navigator.getTarget();
        if (currentTarget == null)
            return true;
        if (rc.getLocation().equals(currentTarget))
            return true;
        if (rc.canSenseLocation(currentTarget) && (rc.senseParts(currentTarget) == 0 && rc.senseRobotAtLocation(currentTarget) == null)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentTarget));
            return true;
        }

        MapLocation bestParts = sortedParts.getBestSpot(currentLocation);

        if (!bestParts.equals(currentTarget))
            return true;

        return false;
    }

    public boolean fightZombies() throws GameActionException {
        // No need to fight zombies if there aren't any
        if (zombies == null || zombies.length == 0 || !rc.isCoreReady()) {
            return false;
        }

        return runAway(PACMAN_WEIGHTS);
    }

    public void collectData() throws GameActionException {
        super.collectData();
        neutralBots = rc.senseNearbyRobots(2, Team.NEUTRAL);

        // don't need to check every round
        if (rc.getRoundNum() % 5 == 0) {
            sortedParts.findPartsAndNeutralsICanSense(rc);
        }
    }

    public boolean healNearbyAllies() throws GameActionException {
        // precondition
        if (nearByAllies.length == 0 || !repaired) {
            return false;
        }

        double weakestHealth = 9999;
        RobotInfo weakest = null;

        for (int i = nearByAllies.length; --i >= 0; ) {
            double health = nearByAllies[i].health;
            if (nearByAllies[i].type != RobotType.ARCHON && health < nearByAllies[i].maxHealth && currentLocation.distanceSquaredTo(nearByAllies[i].location) <= RobotType.ARCHON.attackRadiusSquared) {
                if (health < weakestHealth) {
                    weakestHealth = health;
                    weakest = nearByAllies[i];
                }
            }
        }

        if (weakest != null) {
            rc.repair(weakest.location);
            repaired = true;
            return true;
        }
        return false;
    }

    // maybe spawn a unit or repair a damaged unit
    public boolean carryOutAbility() throws GameActionException {
        // heal doesn't effect core cooldown
        healNearbyAllies();

        if (neutralBots.length > 0 && rc.isCoreReady()) {
            rc.activate(neutralBots[0].location);
            return !rc.isCoreReady();
        }

        if (enemies.length > 0 || zombies.length > 0) {
            return false;
        }

        if (rc.hasBuildRequirements(nextType) && rc.isCoreReady()) {
            double rubble = Double.MAX_VALUE;
            Direction least = dirs[0];
            for (int i = dirs.length; --i >= 0; ) {
                if (build(dirs[i])) {
                    return true;
                }
                double tempRubble = rc.senseRubble(currentLocation.add(dirs[i]));
                if (tempRubble < rubble && tempRubble > 0) {
                    rubble = tempRubble;
                    least = dirs[i];
                }
            }
            try {
                rc.clearRubble(least);
            } catch (Exception e) {
            }
        }

        return false;
    }

    private boolean build(Direction dir) throws GameActionException {
        nextBot = changeBuildOrder(nextBot);

        if (rc.canBuild(dir, nextType)) {
            rc.build(dir, nextType);

//            if (nextBot == Bots.RUSHINGSOLDIER || nextBot == Bots.RUSHINGVIPER) {
//                Communication rushMsg = new AttackCommunication();
//
//                MapLocation[] archons = mKnowledge.getArchonLocations(false);
//                MapLocation archonCOM = MapUtils.getCenterOfMass(archons);
//
//                rushMsg.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), archonCOM.x, archonCOM.y});
//                communicator.sendCommunication(2, rushMsg);
//
//                MapLocation rushLoc = mKnowledge.getOppositeCorner(archonCOM);
//                rushMsg.setValues(new int[]{CommunicationType.toInt(CommunicationType.ATTACK), rushLoc.x, rushLoc.y});
//                communicator.sendCommunication(2, rushMsg);
//            }
//
//            int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;
//            MissionCommunication communication = new MissionCommunication();
//            communication.opcode = CommunicationType.CHANGEMISSION;
//            communication.id = id;
//            communication.rType = Bots.typeFromBot(nextBot);
//            communication.bType = nextBot;
//            communication.newBType = nextBot;
//            communicator.sendCommunication(2, communication);
//
//            Communication mapBoundsCommunication = mKnowledge.getMapBoundsCommunication(id);
//            communicator.sendCommunication(5, mapBoundsCommunication);
//
//            for (int j = mKnowledge.exploredEdges.length; --j >= 0; ) {
//                if (mKnowledge.exploredEdges[j]) {
//                    Communication mapBoundDiscovered = new EdgeDiscovered();
//                    mapBoundDiscovered.setValues(new int[]{CommunicationType.toInt(CommunicationType.EDGE_EXPLORED), id, j});
//                    communicator.sendCommunication(5, mapBoundDiscovered);
//                }
//            }
//
//
//            if (Bots.typeFromBot(nextBot) == RobotType.GUARD || Bots.typeFromBot(nextBot) == RobotType.SOLDIER) {
//                for (int j = mKnowledge.dens.length; --j >= 0; ) {
//                    MapLocation den = mKnowledge.dens.array[j];
//
//                    if (den != null) {
//                        Communication communicationDen = new SimpleBotInfoCommunication();
//                        communicationDen.setValues(new int[]{CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
//                        communicator.sendCommunication(2, communicationDen);
//                    }
//                }
//            }

            nextBot = buildOrder.nextBot();
            nextType = Bots.typeFromBot(nextBot);
            return true;
        }

        return false;
    }

    public Bots changeBuildOrder(Bots nextBot) {
        return nextBot;
    }

    public void handleMessages() throws GameActionException { }
    public void sendMessages()
    {
        return;
    }
}
