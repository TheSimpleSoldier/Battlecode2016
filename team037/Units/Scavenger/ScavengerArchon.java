package team037.Units.Scavenger;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.DataStructures.MapLocationBuffer;
import team037.DataStructures.MessageBuffer;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Navigation;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.PacMan.PacMan;
import team037.Units.PacMan.PacManUtils;
import team037.Utilites.Utilities;

public class ScavengerArchon extends BaseArchon implements PacMan {

    public static double[] adjacentRubble;
    public static MapLocation lastScan, zombieCenterOfMass, myNeutral;
    public static RobotInfo scout1, scout2, scout3;
    public static int myScouts, bigZombies, offensiveEnemies;
    public static AppendOnlyMapLocationSet turretLocations;
    public static MessageBuffer messageBuffer;
    public static RobotType SCOUT = RobotType.SCOUT;

    public ScavengerArchon(RobotController rc) {
        super(rc);
        adjacentRubble = Navigation.map.scan(currentLocation);
        lastScan = currentLocation;
        scout1 = null;
        scout2 = null;
        scout3 = null;
        myNeutral = null;
        bigZombies = 0;
        myScouts = 0;
        turretLocations = new AppendOnlyMapLocationSet();
        messageBuffer = new MessageBuffer();
    }

    public boolean precondition() {
        return false;
    }

    public boolean fight() {
//        if (offensiveEnemies == 0 || (offensiveEnemies == 1 && zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN))) {
//            return false;
//        }
        return runAway(null,true,true);
    }

    public boolean fightZombies() {
        if (zombies.length == 0 || zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN)) {
            return false;
        }

        return runAway(null,true,true);
    }

    public int[] applyAdditionalWeights(int[] directions) {
        directions = PacManUtils.applySimpleWeights(currentLocation,directions, allies);
        return directions;
    }

    public void collectData() throws GameActionException {
        super.collectData();

        // Sense rubble on adjacent locations
        // Scan 24 radius squared for rubble topography, returning only 8 adjacent squares
        if (!currentLocation.equals(lastScan)) {
            adjacentRubble = Navigation.map.scan(currentLocation);
            lastScan = currentLocation;
            Navigation.lastScan = lastScan;
        }


        if (zombies.length > 1 && zombies.length < 8) {
            zombieCenterOfMass = PacManUtils.centerOfMass(zombies);
        } else {
            zombieCenterOfMass = null;
        }

        // Reset offensiveEnemies and do a recount

        offensiveEnemies += zombies.length;

        if (scout1 != null && (!rc.canSenseRobot(scout1.ID) || currentLocation.distanceSquaredTo(scout1.location) > 25)) {
            scout1 = null;
            myScouts--;
        } else if (scout1 != null) {
            scout1 = rc.senseRobot(scout1.ID);
        }

        if (scout2 != null && (!rc.canSenseRobot(scout2.ID) || currentLocation.distanceSquaredTo(scout2.location) > 25)) {
            scout2 = null;
            myScouts--;
        } else if (scout2 != null) {
            scout2 = rc.senseRobot(scout2.ID);
        }

        if (scout3 != null && (!rc.canSenseRobot(scout3.ID) || currentLocation.distanceSquaredTo(scout3.location) > 25)) {
            scout3 = null;
            myScouts--;
        } else if (scout3 != null) {
            scout3 = rc.senseRobot(scout3.ID);
        }

        if (sortedParts.contains(currentLocation))
        {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
            Communication communication = new BotInfoCommunication();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(400, communication);
        }
    }

    @Override
    public boolean carryOutAbility() throws GameActionException {
        if (!(rc.isCoreReady() && rc.hasBuildRequirements(SCOUT))) {
            return false;
        }

        if (scout1 == null) {
            if (rc.canBuild(Direction.NORTH,SCOUT)) {
                rc.build(Direction.NORTH,SCOUT);
                scout1 = rc.senseRobotAtLocation(currentLocation.add(Direction.NORTH));
                sendInitialMessages(Direction.NORTH,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            } else if (rc.canBuild(Direction.NORTH_WEST,SCOUT)) {
                rc.build(Direction.NORTH_WEST,SCOUT);
                scout1 = rc.senseRobotAtLocation(currentLocation.add(Direction.NORTH_WEST));
                sendInitialMessages(Direction.NORTH_WEST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            } else if (rc.canBuild(Direction.NORTH_EAST,SCOUT)) {
                rc.build(Direction.NORTH_EAST,SCOUT);
                scout1 = rc.senseRobotAtLocation(currentLocation.add(Direction.NORTH_EAST));
                sendInitialMessages(Direction.NORTH_EAST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            }
        } else if (scout2 == null) {
            if (rc.canBuild(Direction.EAST,SCOUT)) {
                rc.build(Direction.EAST,SCOUT);
                scout2 = rc.senseRobotAtLocation(currentLocation.add(Direction.EAST));
                sendInitialMessages(Direction.EAST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            } else if (rc.canBuild(Direction.SOUTH_EAST,SCOUT)) {
                rc.build(Direction.SOUTH_EAST,SCOUT);
                scout2 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH_EAST));
                sendInitialMessages(Direction.SOUTH_EAST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            } else if (rc.canBuild(Direction.SOUTH,SCOUT)) {
                rc.build(Direction.SOUTH,SCOUT);
                scout2 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH));
                sendInitialMessages(Direction.SOUTH,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            }
        } else if (scout3 == null) {
            if (rc.canBuild(Direction.WEST,SCOUT)) {
                rc.build(Direction.WEST,SCOUT);
                scout3 = rc.senseRobotAtLocation(currentLocation.add(Direction.WEST));
                sendInitialMessages(Direction.WEST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            } else if (rc.canBuild(Direction.SOUTH_WEST,SCOUT)) {
                rc.build(Direction.SOUTH_WEST,SCOUT);
                scout3 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH_WEST));
                sendInitialMessages(Direction.SOUTH_WEST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            } else if (rc.canBuild(Direction.SOUTH,SCOUT)) {
                rc.build(Direction.SOUTH,SCOUT);
                scout3 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH));
                sendInitialMessages(Direction.SOUTH,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                myScouts++;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
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

        if (bestParts != null && !bestParts.equals(currentTarget))
            return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        return getNextPartLocation();
    }
}
