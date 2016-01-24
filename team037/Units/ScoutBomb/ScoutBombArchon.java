package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.SlugNavigator;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.PacMan.PacMan;
import team037.Utilites.MapUtils;
import team037.Utilites.MoveUtils;


/**
 * Using messages could be improved!
 *
 *
 * WHY DON'T WE USE MESSAGES!!!111!!!!
 *
         * There a couple clear scenarios where we should
         *
         *  CLEAR SCENARIO #1
         *    A           S FFFF   A
         *    if we keep sending scounts, we herd the Fast Zombies (F) back to us
         *    When S is about to die by the Fs, send a MAX range message, saying the there are Fs at their location
         *    All Scouts between the fast swarm and the archons (probably just use BornLocation in unit) should discintegrte (if not infected)
         *       archons should attempt to move away
         *       or spawn guards
         *
         *
         *    CLEAR SCENARIO
         *    Message enemy archon locations
         *    if you are hanging out waiting for zombies (you shouldn't do this much)
         *    or about to die (message all the things)
         *    and you see an enemey archon
         *    message about it!
         *
         *
         * Possible:
         *   Map Bounds
         *      (as long as we actually use this information)
 *
 *
 * WHY DON'T WE STOP ACTING LIKE IDIOTS WHEN WE SEE ENEMIES ON THE FIELD
 * by enemies I mean non-archon, non-scout enemies
 *
         * we should try and herd them too!
         * back to the archon!
         * everybody let's go!
         *
 *
 *
 *
 *
 *
 */
public class ScoutBombArchon extends BaseArchon implements PacMan {

    private static String previousLastAction = "none";
    private static String lastAction = "none";
    private static final String MOVE_TO_BETTER_SPAWN = "moving to better location";
    private static final String SPAWN = "spawning";
    private static final String LET_SCOUT_HELP = "letting scout herd away zombie";
    private static final String RUN_AWAY_FROM_CROWD = "run away from crowd";
    private static final String FAST_CLOUD = "fast cloud spotted";


    private static int vipersSpawned = 0;

    private static int nearestZombieDist;
    private static RobotInfo nearestZombieInfo;

    private static int nearestAlliedDist;
    private static RobotInfo nearestAlliedInfo;

    private static SlugNavigator move;

    ZombieSpawnSchedule schedule;
    Direction last;

    public ScoutBombArchon(RobotController rc) {
        super(rc);
        schedule = rc.getZombieSpawnSchedule();
        move = new SlugNavigator(rc);
    }

    @Override
    public void collectData() throws GameActionException {
        super.collectData();

        nearestZombieDist = Integer.MAX_VALUE;
        nearestZombieInfo = null;
        for (int i = zombies.length; --i >= 0;) {
            int dist = currentLocation.distanceSquaredTo(zombies[i].location);
            rc.setIndicatorLine(currentLocation, zombies[i].location, 0, 255, 0);
            if (dist < nearestZombieDist) {
                nearestZombieDist = dist;
                nearestZombieInfo = zombies[i];
            }
        }

        nearestAlliedDist = Integer.MAX_VALUE;
        nearestAlliedInfo = null;
        for (int i = allies.length; --i >= 0;) {
            int dist = currentLocation.distanceSquaredTo(allies[i].location);
            if (dist < nearestAlliedDist) {
                nearestAlliedDist = dist;
                nearestAlliedInfo = allies[i];
            }
        }
    }

    @Override
    public boolean act() throws GameActionException {
        healNearbyAllies();

        if (!rc.isCoreReady()) {
            rc.setIndicatorString(0, "waiting on core");
            return false;
        } else if (runAwayFromCrowd()) {
            previousLastAction = lastAction;
            lastAction = RUN_AWAY_FROM_CROWD;
        } else if (letScoutHelp()) {
            previousLastAction = lastAction;
            lastAction = MOVE_TO_BETTER_SPAWN;
        } else if (moveToBetterSpawn()) {
            previousLastAction = lastAction;
            lastAction = MOVE_TO_BETTER_SPAWN;
        } else if (spawn()) {
            previousLastAction = lastAction;
            lastAction = SPAWN;
        } else {
            rc.setIndicatorString(0, "NOTHING :(");
            return false;
        }
        rc.setIndicatorString(0, lastAction);
        return true;
    }

    /*
    ===============================
    RUN_AWAY_FROM_CROWD
    If there are lots of zombies/enemies nearby, run away!
    ===============================
     */
    private boolean runAwayFromCrowd() {
        // preconditions
        if (enemies.length > 0 || zombies.length > 3) {
            return runAway(null);
        }
        if (rc.getHealth() < .2 * type.maxHealth && (enemies.length > 0 || zombies.length > 3)) {
            return runAway(null);
        }
        return false;
    }

    /*
    ===============================
    LET_SCOUT_HELP
    There are a couple zombies nearby, if we have a unit just move away
    I was still trying to get scouts to intelligently herd away from ARCHONS
    but now with the guard, don't need to do that
    This should be changed, to move toward a guard?
        NOTE: only do this if the zombies.length <= 3
    ===============================
     */
    private boolean letScoutHelp() throws GameActionException {
        if (zombies.length == 0) {
            return false;
        }
        if (allies.length == 0 && rc.getTeamParts() >= RobotType.GUARD.partCost) {
            // if we can build a guard instead of running, do it!
            return false;
        }

        if (allies.length > 0) {
            RobotInfo[] friends = rc.senseNearbyRobots(nearestZombieInfo.location, nearestZombieDist, us);
            // if we have a unit closer to the nearest zombie than we are, run away from zombie
            if (friends.length > 0) {
                if (nearestAlliedDist > 10 && nearestZombieDist > 10 && nearestAlliedInfo.type.equals(RobotType.ARCHON.GUARD)) {
                    //stay and heal
                    rc.setIndicatorString(1, "staying and healing the guard");
                    return true;
                }
                rc.setIndicatorString(1, "we have help, move away casually");
                if (nearestZombieDist < 10) {
                    Direction away = nearestZombieInfo.location.directionTo(currentLocation);
                    if (MoveUtils.tryMoveForwardOrLeftRight(away, false)) {
                        rc.setIndicatorString(2, "moving away from zombie");
                        return true;
                    }
                }
                Direction away = nearestZombieInfo.location.directionTo(currentLocation);
                Direction target = currentLocation.directionTo(navigator.getTarget());
                Direction toMove = MapUtils.addDirections(away, target);
                if (toMove.equals(Direction.NONE)) {
                    // zombie is blocking us from where we want to go
                    if (MoveUtils.tryMoveForwardOrSideways(away, false)) {
                        return true;
                    } else if (MoveUtils.tryClearForwardOrSideways(away)) {
                        return true;
                    }
                } else {
                    // we can sort of move where we want to go
                    if (MoveUtils.tryMoveForwardOrLeftRight(toMove, false)) {
                        return true;
                    } else if (MoveUtils.tryClearForwardOrLeftRight(toMove)) {
                        return true;
                    }
                }
            } else {
                // otherwise run toward that unit if it's a guard
                if (nearestAlliedInfo.type == RobotType.GUARD && !currentLocation.isAdjacentTo(nearestAlliedInfo.location)) {
                    rc.setIndicatorString(1, "save us guard!");
                    Direction toMove = currentLocation.directionTo(nearestAlliedInfo.location);
                    if (MoveUtils.tryMoveForwardOrLeftRight(toMove, false)) {
                        return true;
                    }
                }
            }
        }
        rc.setIndicatorString(1, "can't figure it out, running away!!");
        return runAway(null);
    }

    /*
    ===============================
    MOVE_TO_BETTER_SPAWN
    Assuming there are no zombies nearby, move to a "better" spawn location
    //TODO: this will move right onto DENS
    // TODO: this moves direction away from enemy center of mass
              so if archons are spread evenly, it doesn't really move us away from them

    TODO: take into account closest enemy starting achons, take into account dens
    TODO: try and get into a corner?
    ===============================
     */
    public boolean moveToBetterSpawn() throws GameActionException {
        // preconditions
        if (rc.getTeamParts() > Math.min(RobotType.GUARD.partCost, RobotType.SCOUT.partCost)) {
            // we only want to enforce the move, move, spawn if we have parts for spawning
            if (lastAction.equals(MOVE_TO_BETTER_SPAWN) && previousLastAction.equals(MOVE_TO_BETTER_SPAWN)) {
                return false;
            }
        }
        if (zombies.length > 0) {
            return false;
        }


        // TODO: take into account dens here

        if (move.getTarget() == null || currentLocation.isAdjacentTo(move.getTarget())) {
            move.setTarget(getNextBestSpawnLocation());
        }

        if (move.getTarget() == null) {
            return false;
        }

        rc.setIndicatorLine(currentLocation, move.getTarget(), 0, 0, 0);
        return move.moveAndClear();
    }


    private MapLocation getNextBestSpawnLocation() throws GameActionException {
        MapLocation target;
        MapLocation halfTarget;


        int radius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared));
        int diagRadius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared) / 1.5);

        Direction toMove = enemyArchonCenterOfMass.directionTo(currentLocation);

        if (toMove.isDiagonal()) {
            target = currentLocation.add(toMove, diagRadius);
            halfTarget = currentLocation.add(toMove, diagRadius/2);
        } else {
            target = currentLocation.add(toMove, radius);
            halfTarget = currentLocation.add(toMove, radius/2);
        }

        if (rc.onTheMap(target)) {
            return target;
        }
        if (rc.onTheMap(halfTarget)) {
            return halfTarget;
        }

        if ((currentLocation.x + currentLocation.y) % 2 == 0) {
            toMove = toMove.rotateLeft().rotateLeft();
        } else {
            toMove = toMove.rotateRight().rotateRight();
        }

        if (toMove.isDiagonal()) {
            target = currentLocation.add(toMove, diagRadius);
            halfTarget = currentLocation.add(toMove, diagRadius/2);
        } else {
            target = currentLocation.add(toMove, radius);
            halfTarget = currentLocation.add(toMove, radius/2);
        }
        if (rc.onTheMap(target)) {
            return target;
        }
        if (rc.onTheMap(halfTarget)) {
            return halfTarget;
        }

        toMove = toMove.opposite();
        if (toMove.isDiagonal()) {
            target = currentLocation.add(toMove, diagRadius);
            halfTarget = currentLocation.add(toMove, diagRadius/2);
        } else {
            target = currentLocation.add(toMove, radius);
            halfTarget = currentLocation.add(toMove, radius/2);
        }
        if (rc.onTheMap(target)) {
            return target;
        }
        if (rc.onTheMap(halfTarget)) {
            return halfTarget;
        }

        return null;
    }


    /*
    ===============================
    SPAWN
    Spawn something!
    ===============================
    */
    private boolean spawn() throws GameActionException {
        int numGuards = 0;
        for (int i = allies.length; --i > 0;) {
            if (allies[i].type.equals(RobotType.GUARD)) {
                numGuards++;
            }
        }
        if (start.equals(alliedArchonStartLocs[0]) && centerOfMassDifference < 1000 && vipersSpawned < rc.getRoundNum() / 300 ) {
            if (rc.hasBuildRequirements(RobotType.VIPER)) {
                Direction toSpawn = MapUtils.getRCCanMoveDirection();
                if (!toSpawn.equals(Direction.NONE)) {
                    rc.build(toSpawn, RobotType.VIPER);
                    sendInitialMessages(toSpawn, RobotType.VIPER, Bots.SCOUTBOMBVIPER, false);
                    vipersSpawned++;
                    rc.setIndicatorString(1, "spawning viper, distance is " + centerOfMassDifference);
                }
            }
        }

        else if (zombies.length > 0 || numGuards < Math.min(6, round / 300)) {
            if (rc.hasBuildRequirements(RobotType.GUARD)) {
                Direction toSpawn = MapUtils.getRCCanMoveDirection();
                if (!toSpawn.equals(Direction.NONE)) {
                    rc.build(toSpawn, RobotType.GUARD);
                    sendInitialMessages(toSpawn, RobotType.GUARD, Bots.SCOUTBOMBGUARD, false);
                    if (zombies.length > 0) {
                        rc.setIndicatorString(1, "zombies are about, best spawn a guard");
                    } else {
                        rc.setIndicatorString(1, "spawning a guard to keep up min count");
                    }
                    return true;
                }
            }
        } else {
            if (rc.hasBuildRequirements(RobotType.SCOUT)) {
                Direction toSpawn = MapUtils.getRCCanMoveDirection();
                if (!toSpawn.equals(Direction.NONE)) {
                    rc.build(toSpawn, RobotType.SCOUT);
                    sendInitialMessages(toSpawn, RobotType.SCOUT, Bots.SCOUTBOMBSCOUT, false);
                    rc.setIndicatorString(1, "all clear, spawn a scout");
                    return true;
                }
            }
        }

        return false;
    }



    @Override
    public void sendMessages() {
        return;
    }

    @Override
    public void handleMessages() throws GameActionException {
        return;
    }

}
