package team037.Units.PacMan;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.*;
import team037.Navigation;
import team037.Unit;
import team037.Utilites.BuildOrderCreation;
import team037.Utilites.Utilities;

/**
 * PacMan bot runs away. That's it.
 * Created by davej on 1/13/2016.
 */
public class PacManArchon extends Unit {
    public RobotInfo[] myScouts;
    public int scoutCount;
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;
    RobotInfo[] neutralBots;
    public static SortedParts sortedParts = new SortedParts();

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
                {Bots.SCOUTINGSCOUT, Bots.HERDINGSCOUT},
                {Bots.HERDINGSCOUT, Bots.HERDINGSCOUT},
                {Bots.RUSHINGVIPER, Bots.RUSHINGVIPER},
                {Bots.HERDINGSCOUT, Bots.RUSHINGVIPER},
        };

        int[] times = {1, 2, 3, 1000};

        return new BuildOrder(buildOrder, times);
    }

    @Override
    public boolean aidDistressedArchon() {return false;}

    @Override
    public boolean precondition() {
        if (!locationLastTurn.equals(currentLocation)) {
            Navigation.map.scan(currentLocation);
            Navigation.lastScan = currentLocation;
        }

        try {
            if (sortedParts.contains(currentLocation)) {
                sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
                Communication communication = new BotInfoCommunication();
                communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
                communicator.sendCommunication(400, communication);
            }

            if (updateTarget()) {
                navigator.setTarget(sortedParts.getBestSpot(currentLocation));
            }
        } catch (GameActionException e) {}
        return false;
    }

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

        MapLocation currentTarget = navigator.getTarget();
        if (!ability && fightZombies()) {
            boolean out = takeNextStep();
            navigator.setTarget(currentTarget);
            return out;
        }

        return takeNextStep();
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

        if (zombies == null || zombies.length == 0) {
            return false;
        }
        int length = zombies.length;

        int[] directions = new int[8];
        for (int i = length; --i >= 0; ) {
            MapLocation nextZombie = zombies[i].location;
            int add = 38 - nextZombie.distanceSquaredTo(currentLocation);
            int addAdjacent = add / 2;
            int addPerp = addAdjacent / 2;
            int addPerpAdj = addPerp / 2;
            int addOpp = addPerpAdj / 2;
            switch (nextZombie.directionTo(currentLocation).opposite()) {
                case NORTH:
                    directions[4] += addOpp;
                    directions[5] += addPerpAdj;
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    directions[3] += addPerpAdj;
                    break;
                case NORTH_EAST:
                    directions[5] += addOpp;
                    directions[6] += addPerpAdj;
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    directions[4] += addPerpAdj;
                    break;
                case EAST:
                    directions[6] += addOpp;
                    directions[7] += addPerpAdj;
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    directions[5] += addPerpAdj;
                    break;
                case SOUTH_EAST:
                    directions[7] += addOpp;
                    directions[0] += addPerpAdj;
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    directions[6] += addPerpAdj;
                    break;
                case SOUTH:
                    directions[0] += addOpp;
                    directions[1] += addPerpAdj;
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    directions[7] += addPerpAdj;
                    break;
                case SOUTH_WEST:
                    directions[1] += addOpp;
                    directions[2] += addPerpAdj;
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    directions[0] += addPerpAdj;
                    break;
                case WEST:
                    directions[2] += addOpp;
                    directions[3] += addPerpAdj;
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    directions[1] += addPerpAdj;
                    break;
                case NORTH_WEST:
                    directions[3] += addOpp;
                    directions[4] += addPerpAdj;
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    directions[2] += addPerpAdj;
                    break;
            }
        }

        if (enemies != null) {
            length = enemies.length;
            for (int i = length; --i >= 0; ) {
                MapLocation nextEnemy = enemies[i].location;
                int add = 38 - nextEnemy.distanceSquaredTo(currentLocation);
                int addAdjacent = add / 4;
                int addPerp = addAdjacent / 3;
                int addPerpAdj = addPerp / 2;
                int addOpp = addPerpAdj / 2;
                switch (nextEnemy.directionTo(currentLocation)) {
                    case NORTH:
                        directions[4] += addOpp;
                        directions[5] += addPerpAdj;
                        directions[6] += addPerp;
                        directions[7] += addAdjacent;
                        directions[0] += add;
                        directions[1] += addAdjacent;
                        directions[2] += addPerp;
                        directions[3] += addPerpAdj;
                        break;
                    case NORTH_EAST:
                        directions[5] += addOpp;
                        directions[6] += addPerpAdj;
                        directions[7] += addPerp;
                        directions[0] += addAdjacent;
                        directions[1] += add;
                        directions[2] += addAdjacent;
                        directions[3] += addPerp;
                        directions[4] += addPerpAdj;
                        break;
                    case EAST:
                        directions[6] += addOpp;
                        directions[7] += addPerpAdj;
                        directions[0] += addPerp;
                        directions[1] += addAdjacent;
                        directions[2] += add;
                        directions[3] += addAdjacent;
                        directions[4] += addPerp;
                        directions[5] += addPerpAdj;
                        break;
                    case SOUTH_EAST:
                        directions[7] += addOpp;
                        directions[0] += addPerpAdj;
                        directions[1] += addPerp;
                        directions[2] += addAdjacent;
                        directions[3] += add;
                        directions[4] += addAdjacent;
                        directions[5] += addPerp;
                        directions[6] += addPerpAdj;
                        break;
                    case SOUTH:
                        directions[0] += addOpp;
                        directions[1] += addPerpAdj;
                        directions[2] += addPerp;
                        directions[3] += addAdjacent;
                        directions[4] += add;
                        directions[5] += addAdjacent;
                        directions[6] += addPerp;
                        directions[7] += addPerpAdj;
                        break;
                    case SOUTH_WEST:
                        directions[1] += addOpp;
                        directions[2] += addPerpAdj;
                        directions[3] += addPerp;
                        directions[4] += addAdjacent;
                        directions[5] += add;
                        directions[6] += addAdjacent;
                        directions[7] += addPerp;
                        directions[0] += addPerpAdj;
                        break;
                    case WEST:
                        directions[2] += addOpp;
                        directions[3] += addPerpAdj;
                        directions[4] += addPerp;
                        directions[5] += addAdjacent;
                        directions[6] += add;
                        directions[7] += addAdjacent;
                        directions[0] += addPerp;
                        directions[1] += addPerpAdj;
                        break;
                    case NORTH_WEST:
                        directions[3] += addOpp;
                        directions[4] += addPerpAdj;
                        directions[5] += addPerp;
                        directions[6] += addAdjacent;
                        directions[7] += add;
                        directions[0] += addAdjacent;
                        directions[1] += addPerp;
                        directions[2] += addPerpAdj;
                        break;
                }
            }
        }

        try {
            int[] ping = Navigation.map.ping(currentLocation, 0, 3);
            rc.setIndicatorString(0, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            int divide = 17;
            int left = divide / ++ping[0], mid = divide / ++ping[1], right = divide / ++ping[2];
            directions[7] *= 1 + left / 2;
            directions[0] *= 1 + mid;
            directions[1] *= 1 + right / 2;

            ping = Navigation.map.ping(currentLocation, 2, 3);
            rc.setIndicatorString(1, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            left = divide / ++ping[0];
            mid = divide / ++ping[1];
            right = divide / ++ping[2];
            directions[1] *= 1 + left / 2;
            directions[2] *= 1 + mid;
            directions[3] *= 1 + right / 2;

            ping = Navigation.map.ping(currentLocation, 4, 3);
            left = divide / ++ping[0];
            mid = divide / ++ping[1];
            right = divide / ++ping[2];
            directions[3] *= 1 + left / 2;
            directions[4] *= 1 + mid;
            directions[5] *= 1 + right / 2;

            ping = Navigation.map.ping(currentLocation, 6, 3);
            rc.setIndicatorString(2, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            left = divide / ++ping[0];
            mid = divide / ++ping[1];
            right = divide / ++ping[2];
            directions[5] *= 1 + left / 2;
            directions[6] *= 1 + mid;
            directions[7] *= 1 + right / 2;
        } catch (Exception e) {
        }


        MapLocation target = navigator.getTarget();
        if (target != null) {
            int toCentroid = 8;
            switch (currentLocation.directionTo(navigator.getTarget())) {
                case NORTH:
                    directions[5] -= toCentroid / 8;
                    directions[6] -= toCentroid / 4;
                    directions[7] -= toCentroid / 2;
                    directions[0] -= toCentroid;
                    directions[1] -= toCentroid / 2;
                    directions[2] -= toCentroid / 4;
                    directions[3] -= toCentroid / 8;
                    break;
                case NORTH_EAST:
                    directions[6] -= toCentroid / 8;
                    directions[7] -= toCentroid / 4;
                    directions[0] -= toCentroid / 2;
                    directions[1] -= toCentroid;
                    directions[2] -= toCentroid / 2;
                    directions[3] -= toCentroid / 4;
                    directions[4] -= toCentroid / 8;
                    break;
                case EAST:
                    directions[7] -= toCentroid / 8;
                    directions[0] -= toCentroid / 4;
                    directions[1] -= toCentroid / 2;
                    directions[2] -= toCentroid;
                    directions[3] -= toCentroid / 2;
                    directions[4] -= toCentroid / 4;
                    directions[5] -= toCentroid / 8;
                    break;
                case SOUTH_EAST:
                    directions[0] -= toCentroid / 8;
                    directions[1] -= toCentroid / 4;
                    directions[2] -= toCentroid / 2;
                    directions[3] -= toCentroid;
                    directions[4] -= toCentroid / 2;
                    directions[5] -= toCentroid / 4;
                    directions[6] -= toCentroid / 8;
                    break;
                case SOUTH:
                    directions[1] -= toCentroid / 8;
                    directions[2] -= toCentroid / 4;
                    directions[3] -= toCentroid / 2;
                    directions[4] -= toCentroid;
                    directions[5] -= toCentroid / 2;
                    directions[6] -= toCentroid / 4;
                    directions[7] -= toCentroid / 8;
                    break;
                case SOUTH_WEST:
                    directions[2] -= toCentroid / 8;
                    directions[3] -= toCentroid / 4;
                    directions[4] -= toCentroid / 2;
                    directions[5] -= toCentroid;
                    directions[6] -= toCentroid / 2;
                    directions[7] -= toCentroid / 4;
                    directions[0] -= toCentroid / 8;
                    break;
                case WEST:
                    directions[3] -= toCentroid / 8;
                    directions[4] -= toCentroid / 4;
                    directions[5] -= toCentroid / 2;
                    directions[6] -= toCentroid;
                    directions[7] -= toCentroid / 2;
                    directions[0] -= toCentroid / 4;
                    directions[1] -= toCentroid / 8;
                    break;
                case NORTH_WEST:
                    directions[4] -= toCentroid / 8;
                    directions[5] -= toCentroid / 4;
                    directions[6] -= toCentroid / 2;
                    directions[7] -= toCentroid;
                    directions[0] -= toCentroid / 2;
                    directions[1] -= toCentroid / 4;
                    directions[2] -= toCentroid / 8;
                    break;
            }
        }

        int min = directions[0];
        int minDir = 0;

        if (min > directions[1] && rc.onTheMap(currentLocation.add(dirs[1]))) {
            minDir = 1;
            min = directions[1];
        }
        if ((min > directions[2] || (min == directions[2] && minDir % 2 != 0)) && rc.onTheMap(currentLocation.add(dirs[2]))) {
            minDir = 2;
            min = directions[2];
        }
        if (min > directions[3] && rc.onTheMap(currentLocation.add(dirs[3]))) {
            minDir = 3;
            min = directions[3];
        }
        if ((min > directions[4] || (min == directions[4] && minDir % 2 != 0)) && rc.onTheMap(currentLocation.add(dirs[4]))) {
            minDir = 4;
            min = directions[4];
        }
        if (min > directions[5] && rc.onTheMap(currentLocation.add(dirs[5]))) {
            minDir = 5;
            min = directions[5];
        }
        if ((min > directions[6] || (min == directions[6] && minDir % 2 != 0)) && rc.onTheMap(currentLocation.add(dirs[6]))) {
            minDir = 6;
            min = directions[6];
        }
        if (min > directions[7] && rc.onTheMap(currentLocation.add(dirs[7]))) {
            minDir = 7;
            min = directions[7];
        }

        MapLocation nextLoc = currentLocation.add(dirs[minDir], 1);
        if (!rc.onTheMap(nextLoc)) {
            nextLoc = currentLocation.add(dirs[minDir].rotateRight(), 1);
        }
        if (!rc.onTheMap(nextLoc)) {
            nextLoc = currentLocation.add(dirs[minDir].rotateLeft(), 1);
        }
        navigator.setTarget(nextLoc);
        return true;
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
//        healNearbyAllies();

//        if (neutralBots.length > 0 && rc.isCoreReady()) {
//            rc.activate(neutralBots[0].location);
//            return !rc.isCoreReady();
//        }
//
//        if (enemies.length > allies.length || zombies.length > allies.length) {
//            return false;
//        }
//
//        if (rc.hasBuildRequirements(nextType) && rc.isCoreReady()) {
//            double rubble = Double.MAX_VALUE;
//            Direction least = dirs[0];
//            for (int i = dirs.length; --i >= 0; ) {
//                if (build(dirs[i])) {
//                    return true;
//                }
//                double tempRubble = rc.senseRubble(currentLocation.add(dirs[i]));
//                if (tempRubble < rubble && tempRubble > 0) {
//                    rubble = tempRubble;
//                    least = dirs[i];
//                }
//            }
//            try {
//                rc.clearRubble(least);
//            } catch (Exception e) {
//            }
//        }

        return false;
    }

    private boolean build(Direction dir) throws GameActionException
    {
        while (nearByAllies.length > 10 && nextType == RobotType.SOLDIER && nextBot == Bots.CASTLESOLDIER)
        {
            nextBot = buildOrder.nextBot();
            nextType = Bots.typeFromBot(nextBot);
        }

        if (rc.canBuild(dir, nextType))
        {
            rc.build(dir, nextType);

            if (nextBot == Bots.RUSHINGSOLDIER || nextBot == Bots.RUSHINGVIPER)
            {
                Communication rushMsg = new AttackCommunication();

                MapLocation archonCOM = mapKnowledge.getArchonCOM();

                rushMsg.setValues(new int[] {CommunicationType.toInt(CommunicationType.RALLY_POINT), archonCOM.x, archonCOM.y} );
                communicator.sendCommunication(2, rushMsg);

                MapLocation rushLoc = mapKnowledge.getOppositeCorner(archonCOM);
                rushMsg.setValues(new int[] {CommunicationType.toInt(CommunicationType.ATTACK), rushLoc.x, rushLoc.y} );
                communicator.sendCommunication(2, rushMsg);
            }

            int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;
            MissionCommunication communication = new MissionCommunication();
            communication.opcode = CommunicationType.CHANGEMISSION;
            communication.id = id;
            communication.rType = Bots.typeFromBot(nextBot);
            communication.bType = nextBot;
            communication.newBType = nextBot;
            communicator.sendCommunication(2, communication);

            Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication(id);
            communicator.sendCommunication(5, mapBoundsCommunication);

            for (int j = mapKnowledge.exploredEdges.length; --j>=0; )
            {
                if (mapKnowledge.exploredEdges[j])
                {
                    Communication mapBoundDiscovered = new EdgeDiscovered();
                    mapBoundDiscovered.setValues(new int[]{CommunicationType.toInt(CommunicationType.EDGE_EXPLORED), id, j});
                    communicator.sendCommunication(5, mapBoundDiscovered);
                }
            }


            if (Bots.typeFromBot(nextBot) == RobotType.GUARD || Bots.typeFromBot(nextBot) == RobotType.SOLDIER)
            {
                for (int j = mapKnowledge.denLocations.length; --j>=0; )
                {
                    MapLocation den = mapKnowledge.denLocations.array[j];

                    if (den != null)
                    {
                        Communication communicationDen = new SimpleBotInfoCommunication();
                        communicationDen.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
                        communicator.sendCommunication(2, communicationDen);
                    }
                }
            }

            nextBot = buildOrder.nextBot();
            nextType = Bots.typeFromBot(nextBot);
            return true;
        }

        return false;
    }
}
