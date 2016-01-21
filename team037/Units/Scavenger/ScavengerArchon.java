package team037.Units.Scavenger;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.DataStructures.MessageBuffer;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Navigation;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.PacMan.PacMan;
import team037.Utilites.Utilities;

/**
 * Created by davej on 1/20/2016.
 */
public class ScavengerArchon extends BaseArchon implements PacMan {

    static double[] adjacentRubble;
    static MapLocation lastScan, lastTurret, archonSighted;
    static RobotInfo scout1, scout2, scout3;
    static int offensiveEnemies, turretsSighted, myScouts, fastZombies, bigZombies;
    static AppendOnlyMapLocationSet turretLocations;
    static MessageBuffer messageBuffer;
    static boolean middle;

    public ScavengerArchon(RobotController rc) {
        super(rc);
        adjacentRubble = Navigation.map.scan(currentLocation);
        lastScan = currentLocation;
        scout1 = null;
        scout2 = null;
        scout3 = null;
        offensiveEnemies = 0;
        turretsSighted = 0;
        fastZombies = 0;
        bigZombies = 0;
        turretLocations = new AppendOnlyMapLocationSet();
        lastTurret = null;
        messageBuffer = new MessageBuffer();
        middle = false;
    }

//    public boolean precondition() {
//
//    }

    public boolean fight() {
        if (offensiveEnemies == 0) {
            return false;
        }
        return runAway(null);
    }

    public boolean fightZombies() {
        if (zombies.length == 0 || zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN)) {
            return false;
        }
        return runAway(null);
    }

    public boolean takeNextStep() throws GameActionException {
//        MapLocation target = navigator.getTarget();
//        if (target == null) {
//            return runAway(null);
//        }
//        middle = false;
        return navigator.takeNextStep();
    }

//    public int[] applyAdditionalConstants(int[] directions, double[][] weights) {
//        if (middle) {
//            MapLocation center = new MapLocation((enemyArchonCenterOfMass.x + start.x) / 2, (enemyArchonCenterOfMass.y + start.y) / 2);
//            directions = applyConstants(currentLocation,directions,new MapLocation[]{center},new double[]{-8,-4,-2,-1,0});
//        }
//        return directions;
//    }

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

        if (!bestParts.equals(currentTarget))
            return true;


        return false;
    }

    public MapLocation getNextSpot() throws GameActionException
    {
        MapLocation target = navigator.getTarget();
        if (target == null) {
            rc.setIndicatorString(1, "old target: null");
        } else {
            rc.setIndicatorString(1, "old target: (" + target.x + "," + target.y + ")");
        }
        target = sortedParts.getBestSpot(currentLocation);
        if (target == null) {
            rc.setIndicatorString(2, "new target: null");
        } else {
            rc.setIndicatorString(2,"new target: (" + target.x+","+target.y+")");
        }
        return target;
    }


    public boolean safeToCommunicate() {
        return offensiveEnemies == 0 &&
                turretsSighted == 0;
    }

    public void sendMessages() {
        if (!safeToCommunicate()) {
            return;
        }


    }

    public void collectData() throws GameActionException {
        super.collectData();

        offensiveEnemies = 0;
        // Sense rubble on adjacent locations
        // Scan 24 radius squared for rubble topography, returning only 8 adjacent squares
        if (!currentLocation.equals(lastScan)) {
            adjacentRubble = Navigation.map.scan(currentLocation);
            lastScan = currentLocation;
        }

        // Check for significant enemies
        int archons = 0;

        for (int i = enemies.length; --i >= 0; ) {
            switch (enemies[i].type) {
                case TURRET:
                    if (!enemies[i].location.equals(lastTurret)) {
                        lastTurret = enemies[i].location;
                        turretLocations.add(lastTurret);
                        turretsSighted++;
                    }
                    offensiveEnemies++;
                    break;
                case GUARD:
                case SOLDIER:
                case VIPER:
                    offensiveEnemies++;
                    break;
                case ARCHON:
                    if (!enemies[i].location.equals(archonSighted)) {
                        archonSighted = enemies[i].location;
                        archons++;
                    }
                    break;

            }
        }
        if (archons == 0) {
            archonSighted = null;
        }

        offensiveEnemies += zombies.length;

        fastZombies = 0;
        for (int i = zombies.length; --i >= 0; ) {
            if (zombies[i].type.equals(RobotType.FASTZOMBIE)) {
                fastZombies++;
            }
        }

        if (scout1 == null || !rc.canSenseRobot(scout1.ID) || currentLocation.distanceSquaredTo(scout1.location) > 10) {
            scout1 = null;
            myScouts--;
        }

        if (scout2 == null || !rc.canSenseRobot(scout2.ID) || currentLocation.distanceSquaredTo(scout2.location) > 10) {
            scout2 = null;
            myScouts--;
        }

        if (scout3 == null || !rc.canSenseRobot(scout3.ID) || currentLocation.distanceSquaredTo(scout3.location) > 10) {
            scout3 = null;
            myScouts--;
        }


        if (sortedParts.contains(currentLocation))
        {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
            Communication communication = new BotInfoCommunication();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(400, communication);
        }
    }

    public boolean carryOutAbility() throws GameActionException {
        if (!rc.hasBuildRequirements(RobotType.GUARD)) {
            return false;
        }

        Bots nextBot = null;
        Direction toSpawn = Direction.NONE;

        // Let's see if we need to deploy countermeasures or can build a scout
        switch (zombies.length) {
            case 0:
                if (myScouts < 2) {
                    nextBot = Bots.SCAVENGERSCOUT;
                    int direction = 0;
                    if (scout1 != null) {
                        direction = Navigation.directionToInt(currentLocation.directionTo(scout1.location));
                    }
                    if (scout2 != null) {

                        direction += Navigation.directionToInt(currentLocation.directionTo(scout2.location));
                    }
                    if (scout3 != null) {
                        direction += Navigation.directionToInt(currentLocation.directionTo(scout3.location));
                    }

                    if (myScouts == 2) {
                        direction += 2;
                        direction %= 8;
                    } else {
                        direction += 4;
                        direction %= 8;
                    }
                    toSpawn = dirs[direction];
                } else if (navigator.getTarget() == null) {
                    nextBot = Bots.SCOUTBOMBSCOUT;
                    toSpawn = currentLocation.directionTo(enemyArchonCenterOfMass);
                }
                break;
            case 1:
                    RobotType zombie = zombies[0].type;
                    if (zombie.equals(RobotType.ZOMBIEDEN) && myScouts < 2) {
                        nextBot = Bots.SCAVENGERSCOUT;
                        toSpawn = currentLocation.directionTo(zombies[0].location);
                    } else {
                        nextBot = Bots.PACMANGUARD;
                        toSpawn = currentLocation.directionTo(zombies[0].location);
                    }
                break;
            case 2:
            case 3:
            case 4:
//                if (fastZombies > 0) {
                    nextBot = Bots.PACMANGUARD;
//                    if (zombies[0].type.equals(RobotType.FASTZOMBIE))
                        toSpawn = currentLocation.directionTo(zombies[0].location);
//                    else if (zombies[1].type.equals(RobotType.FASTZOMBIE))
//                        toSpawn = currentLocation.directionTo(zombies[1].location);
//                    else if (zombies[2].type.equals(RobotType.FASTZOMBIE))
//                        toSpawn = currentLocation.directionTo(zombies[2].location);
//                    else
//                        toSpawn = currentLocation.directionTo(zombies[3].location);
//                }
                break;
            default:
                return false;
        }

        RobotType nextType = Bots.typeFromBot(nextBot);

        if (nextBot != null) {
            if (nextType.equals(RobotType.GUARD) && rc.canBuild(toSpawn, nextType)) {
                rc.build(toSpawn, nextType);
                return true;
            } else if (nextType.equals(RobotType.SCOUT)) {
                if (rc.canBuild(toSpawn, nextType)) {
                    rc.build(toSpawn, nextType);
                } else {
                    for (int i = dirs.length; --i >= 0; ) {
                        if (rc.onTheMap(currentLocation.add(dirs[i]))) {
                            if (rc.canBuild(dirs[i], nextType)) {
                                rc.build(dirs[i], nextType);
                                toSpawn = dirs[i];
                                sendInitialMessages(toSpawn);
                                myScouts++;
                                if (scout1 == null)
                                    scout1 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
                                else if (scout2 == null)
                                    scout2 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
                                else
                                    scout3 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
