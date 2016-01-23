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
import team037.Units.PacMan.PacManUtils;
import team037.Utilites.Utilities;

public class ScavengerArchon extends BaseArchon implements PacMan {

    static double[] adjacentRubble;
    static MapLocation lastScan, lastTurret, archonSighted, enemyCenterOfMass, zombieCenterOfMass, myNeutral;
    static RobotInfo scout1, scout2, scout3, scout4, countermeasure;
    static int offensiveEnemies, turretsSighted, myScouts, bigZombies;
    static AppendOnlyMapLocationSet turretLocations;
    static MessageBuffer messageBuffer;

    public ScavengerArchon(RobotController rc) {
        super(rc);
        adjacentRubble = Navigation.map.scan(currentLocation);
        lastScan = currentLocation;
        scout1 = null;
        scout2 = null;
        scout3 = null;
        scout4 = null;
        myNeutral = null;
        countermeasure = null;
        offensiveEnemies = 0;
        turretsSighted = 0;
        bigZombies = 0;
        myScouts = 0;
        turretLocations = new AppendOnlyMapLocationSet();
        lastTurret = null;
        messageBuffer = new MessageBuffer();
    }

    public boolean precondition() {
//        if (zombies.length < 7) {
//            if (rc.isCoreReady() && countermeasure != null) {
//                return runAwayWithCountermeasures(null);
//            }
//            try {
//                return carryOutAbility();
//            } catch (Exception e) {}
//        }

        return false;
    }

    public boolean fight() {
//        if (offensiveEnemies == 0 || (offensiveEnemies == 1 && zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN))) {
//            return false;
//        }
        if (enemies.length == 0 || zombies.length > enemies.length) {
            return false;
        }

        if (enemies.length < 5) {
            return runAwayWithCountermeasures(null);
        }
        return runAway(null);
    }

    public boolean fightZombies() {
        if (zombies.length == 0 || zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN)) {
            return false;
        }

        if (zombies.length < 8) {
            return runAwayWithCountermeasures(null);
        }
        return runAway(null);
    }

    public int[] applyAdditionalWeights(int[] directions) {
        directions = PacManUtils.applySimpleWeights(currentLocation,directions, allies);
        return directions;
    }

    public int[] applyAdditionalConstants(int[] directions) {
        if (countermeasure != null) {
            directions = PacManUtils.applySimpleConstant(currentLocation,directions,countermeasure.location,new int[]{999999,64,32});
        }
        return directions;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (myNeutral != null) {
            return false;
        }
        MapLocation currentTarget = navigator.getTarget();
        if (currentTarget == null)
            return true;
        if (rc.getLocation().equals(currentTarget))
            return true;
        if (rc.canSenseLocation(currentTarget) && (rc.senseParts(currentTarget) == 0 || rc.senseRobotAtLocation(currentTarget) == null)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentTarget));
            return true;
        }

        MapLocation bestParts = sortedParts.getBestSpot(currentLocation);

        if (myNeutral == null && bestParts != null && !bestParts.equals(currentTarget))
            return true;


        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        MapLocation target = navigator.getTarget();
        if (target == null) {
            rc.setIndicatorString(1, "old target: null");
        } else {
            rc.setIndicatorString(1, "old target: (" + target.x + "," + target.y + ")");
        }

        if (myNeutral == null)
            target = sortedParts.getBestSpot(currentLocation);
        else
            target = myNeutral;

        if (target == null) {
            rc.setIndicatorString(2, "new target: null");
        } else {
            rc.setIndicatorString(2,"new target: (" + target.x+","+target.y+")");
        }
        return target;
    }


    public boolean safeToCommunicate() {
        return offensiveEnemies == 0 && turretsSighted == 0;
    }

    public void sendMessages() {
        if (!safeToCommunicate()) {
            return;
        }
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

        neutralBots = rc.senseNearbyRobots(sightRange, Team.NEUTRAL);

        int neutralPriority = -1;
        int index = -1;

        myNeutral = null;
        for (int i = neutralBots.length; --i >=0;) {
            switch(neutralBots[i].type) {
                case ARCHON:
                    if (neutralPriority < 5) {
                        neutralPriority = 5;
                        index = i;
                    } else {
                        int distanceOld = neutralBots[index].location.distanceSquaredTo(currentLocation);
                        int distanceNew = neutralBots[i].location.distanceSquaredTo(currentLocation);
                        if (distanceNew < distanceOld) {
                            index = i;
                        }
                    }
                    break;
                case GUARD:
                    if (neutralPriority < 4) {
                        neutralPriority = 4;
                        index = i;
                    } else if (neutralPriority == 3) {
                        int distanceOld = neutralBots[index].location.distanceSquaredTo(currentLocation);
                        int distanceNew = neutralBots[i].location.distanceSquaredTo(currentLocation);
                        if (distanceNew < distanceOld) {
                            index = i;
                        }
                    }
                    break;
                case TURRET:
                case TTM:
                    if (neutralPriority < 3) {
                        neutralPriority = 3;
                        index = i;
                    } else if (neutralPriority == 3) {
                        int distanceOld = neutralBots[index].location.distanceSquaredTo(currentLocation);
                        int distanceNew = neutralBots[i].location.distanceSquaredTo(currentLocation);
                        if (distanceNew < distanceOld) {
                            index = i;
                        }
                    }
                    break;
                case VIPER:
                    if (neutralPriority < 2) {
                        neutralPriority = 2;
                        index = i;
                    } else if (neutralPriority == 2) {
                        int distanceOld = neutralBots[index].location.distanceSquaredTo(currentLocation);
                        int distanceNew = neutralBots[i].location.distanceSquaredTo(currentLocation);
                        if (distanceNew < distanceOld) {
                            index = i;
                        }
                    }
                    break;
                case SOLDIER:
                    if (neutralPriority < 1) {
                        neutralPriority = 1;
                        index = i;
                    } else if (neutralPriority == 1) {
                        int distanceOld = neutralBots[index].location.distanceSquaredTo(currentLocation);
                        int distanceNew = neutralBots[i].location.distanceSquaredTo(currentLocation);
                        if (distanceNew < distanceOld) {
                            index = i;
                        }
                    }
                    break;
                case SCOUT:
                    if (neutralPriority < 0) {
                        neutralPriority = 0;
                        index = i;
                    } else if (neutralPriority == 0) {
                        int distanceOld = neutralBots[index].location.distanceSquaredTo(currentLocation);
                        int distanceNew = neutralBots[i].location.distanceSquaredTo(currentLocation);
                        if (distanceNew < distanceOld) {
                            index = i;
                        }
                    }
                    break;
            }
        }

        if (index >= 0) {
            myNeutral = neutralBots[index].location;
            navigator.setTarget(myNeutral);
        } else {
            myNeutral = null;
        }

//
//        if (zombies.length > 1 && zombies.length < 8) {
//            zombieCenterOfMass = PacManUtils.centerOfMass(zombies);
//        } else {
//            zombieCenterOfMass = null;
//        }
//
//        // Reset offensiveEnemies and do a recount
//        offensiveEnemies = 0;
//        if (enemies.length > 0 && enemies.length < 8) {
//            // Check for significant enemies and find the enemy center of mass
//            int archons = 0;
//            int x = 0, y = 0;
//            for (int i = enemies.length; --i >= 0; ) {
//                MapLocation enemyLoc = enemies[i].location;
//                x += enemyLoc.x;
//                y += enemyLoc.y;
//                switch (enemies[i].type) {
//                    case TURRET:
//                        if (!enemyLoc.equals(lastTurret)) {
//                            lastTurret = enemyLoc;
//                            turretLocations.add(lastTurret);
//                            turretsSighted++;
//                        }
//                        offensiveEnemies++;
//                        break;
//                    case GUARD:
//                    case SOLDIER:
//                    case VIPER:
//                        offensiveEnemies++;
//                        break;
//                    case ARCHON:
//                        if (!enemyLoc.equals(archonSighted)) {
//                            archonSighted = enemyLoc;
//                            archons++;
//                        }
//                        break;
//                }
//            }
//            x /= enemies.length;
//            y /= enemies.length;
//            enemyCenterOfMass = new MapLocation(x, y);
//
//            if (archons == 0) {
//                archonSighted = null;
//            }
//        } else {
//            enemyCenterOfMass = null;
//        }
//
//
//        offensiveEnemies += zombies.length;
//
//        if (scout1 != null && (!rc.canSenseRobot(scout1.ID) || currentLocation.distanceSquaredTo(scout1.location) > 13)) {
//            scout1 = null;
//            myScouts--;
//        }
//
//        if (scout2 != null && (!rc.canSenseRobot(scout2.ID) || currentLocation.distanceSquaredTo(scout2.location) > 13)) {
//            scout2 = null;
//            myScouts--;
//        }
//
//        if (scout3 != null && (!rc.canSenseRobot(scout3.ID) || currentLocation.distanceSquaredTo(scout3.location) > 13)) {
//            scout3 = null;
//            myScouts--;
//        }
//
//        if (scout4 != null && (!rc.canSenseRobot(scout4.ID) || currentLocation.distanceSquaredTo(scout4.location) > 13)) {
//            scout4 = null;
//            myScouts--;
//        }
//
//        if (countermeasure != null && !rc.canSenseRobot(countermeasure.ID)) {
//            countermeasure = null;
//        }


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
        if (!rc.isCoreReady() || !rc.hasBuildRequirements(RobotType.GUARD)) {
            return false;
        }

        Bots nextBot = null;
        Direction toSpawn = Direction.NONE;

        // Let's see if we need to deploy countermeasures or can build a scout
        switch (zombies.length) {
            case 0:
                if (offensiveEnemies > 0) {
//                    nextBot = Bots.COUNTERMEASUREGUARD;
//                    if (enemyCenterOfMass != null) {
//                        toSpawn = currentLocation.directionTo(enemyCenterOfMass);
//                    } else {
//                        toSpawn = currentLocation.directionTo(enemies[0].location);
//                    }
                    if (us.equals(Team.A)) {
                        return runAwayWithCountermeasures(null);
                    } else {
                        return runAway(null);
                    }
                }
//                else if (myScouts < 4) {
//                    nextBot = Bots.SCAVENGERSCOUT;
//                    int direction = 0;
//                    if (scout2 == null) {
//                        direction = 2;
//                    } else if (scout3 == null) {
//                        direction = 4;
//                    } else if (scout4 == null) {
//                        direction = 6;
//                    }
//                    toSpawn = dirs[direction];
//                } else if (navigator.getTarget() == null) {
//                    nextBot = Bots.SCOUTBOMBSCOUT;
//                    toSpawn = currentLocation.directionTo(enemyArchonCenterOfMass);
//                }
                break;
            case 1:
//                    RobotType zombie = zombies[0].type;
//                    if (zombie.equals(RobotType.ZOMBIEDEN) && myScouts < 4) {
//                        nextBot = Bots.SCAVENGERSCOUT;
//                        toSpawn = currentLocation.directionTo(zombies[0].location);
//                    } else if (countermeasure == null) {
//                        nextBot = Bots.COUNTERMEASUREGUARD;
//                        toSpawn = currentLocation.directionTo(zombies[0].location);
//                    }
//                if (countermeasure == null) {
//                    toSpawn = currentLocation.directionTo(zombies[0].location);
//                    countermeasure = PacManUtils.deployCountermeasure(toSpawn);
//                    return true;
//                }
//                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
//                if (countermeasure == null) {
////                    nextBot = Bots.COUNTERMEASUREGUARD;
//                    toSpawn = currentLocation.directionTo(zombies[0].location);
//                    countermeasure = PacManUtils.deployCountermeasure(toSpawn);
//                    return true;
//                }
                if (us.equals(Team.A)) {
                    return runAwayWithCountermeasures(null);
                } else {
                    return runAway(null);
                }
//                break;
            default:
                return false;
        }

        return false;
//        if (nextBot == null) {
//            return false;
//        }
//        RobotType nextType = Bots.typeFromBot(nextBot);
//
//        if (nextBot != null) {
//            if (nextType.equals(RobotType.GUARD)) {
//                if (rc.canBuild(toSpawn, nextType)) {
//                    rc.build(toSpawn, nextType);
//                    countermeasure = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
//                    return true;
//                } else if (rc.canBuild(toSpawn.rotateLeft(), nextType)) {
//                    rc.build(toSpawn.rotateLeft(), nextType);
//                    countermeasure = rc.senseRobotAtLocation(currentLocation.add(toSpawn.rotateLeft()));
//                    return true;
//                } else if (rc.canBuild(toSpawn.rotateRight(), nextType)) {
//                    rc.build(toSpawn.rotateRight(), nextType);
//                    countermeasure = rc.senseRobotAtLocation(currentLocation.add(toSpawn.rotateRight()));
//                    return true;
//                } else if (rc.canBuild(toSpawn.rotateLeft().rotateLeft(), nextType)) {
//                    rc.build(toSpawn.rotateLeft().rotateLeft(), nextType);
//                    countermeasure = rc.senseRobotAtLocation(currentLocation.add(toSpawn.rotateLeft().rotateLeft()));
//                    return true;
//                } else if (rc.canBuild(toSpawn.rotateRight().rotateRight(), nextType)) {
//                    rc.build(toSpawn.rotateRight().rotateRight(), nextType);
//                    countermeasure = rc.senseRobotAtLocation(currentLocation.add(toSpawn.rotateRight().rotateRight()));
//                    return true;
//                }
//            } else if (nextType.equals(RobotType.SCOUT)) {
//                if (rc.canBuild(toSpawn, nextType)) {
//                    rc.build(toSpawn, nextType);
//                } else {
//                    for (int i = dirs.length; --i >= 0; ) {
//                        if (rc.onTheMap(currentLocation.add(dirs[i]))) {
//                            if (rc.canBuild(dirs[i], nextType)) {
//                                rc.build(dirs[i], nextType);
//                                toSpawn = dirs[i];
//                                sendInitialMessages(toSpawn);
//                                myScouts++;
//                                if (scout1 == null)
//                                    scout1 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
//                                else if (scout2 == null)
//                                    scout2 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
//                                else if (scout3 == null)
//                                    scout3 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
//                                else
//                                    scout4 = rc.senseRobotAtLocation(currentLocation.add(toSpawn));
//
//                                return true;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return false;
    }
}
