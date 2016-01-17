package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.SlugNavigator;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.PacMan.PacMan;
import team037.Utilites.MapUtils;

public class ScoutBombArchon extends BaseArchon implements PacMan {

    private static String previousLastAction = "none";
    private static String lastAction = "none";
    private static final String MOVE_AWAY = "moving to better location";
    private static final String SPAWN = "spawning";
    private static final String RUN_AWAY = "run away";
    private static final String FAST_CLOUD = "fast cloud spotted";


    private static SlugNavigator move;

    ZombieSpawnSchedule schedule;
    Direction last;

    public ScoutBombArchon(RobotController rc) {
        super(rc);
        schedule = rc.getZombieSpawnSchedule();
        move = new SlugNavigator(rc);
    }



    @Override
    public boolean act() throws GameActionException {
        previousLastAction = lastAction;

        if (!rc.isCoreReady()) {
            rc.setIndicatorString(0, "waiting on core");
            return false;
        } else if (moveAway()) {
            lastAction = MOVE_AWAY;
        } else if (spawn()) {
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
    MOVE_AWAY
    Assuming there are no zombies nearby, move to a "better" spawn location
    ===============================
     */
    public boolean moveAway() throws GameActionException {
        // precondition
        if (lastAction.equals(MOVE_AWAY) && lastAction.equals(MOVE_AWAY)) {
            return false;
        }


        if (move.getTarget() == null || currentLocation.isAdjacentTo(move.getTarget())) {
            move.setTarget(getNextBestSpawnLocation());
        }

        if (move.getTarget() == null) {
            return false;
        }

        rc.setIndicatorLine(currentLocation, move.getTarget(), 0, 0, 0);
        if (move.takeNextStep()) {
            return true;
        } else if (move.moveAndClear()) {
            return true;
        }
        return false;
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
        if (rc.hasBuildRequirements(RobotType.SCOUT)) {
            Direction toSpawn = MapUtils.getRCCanMoveDirection(this);
            if (!toSpawn.equals(Direction.NONE)) {
                rc.build(toSpawn, RobotType.SCOUT);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean fight() throws GameActionException {
        if (enemies.length == 0) {
            return false;
        }
        if (enemies.length > 3) {
            return runAway(null);
        }
        return false;
    }


    @Override
    public boolean fightZombies() throws GameActionException {
        if (zombies.length == 0) {
            return false;
        }
        return runAway(null);
    }


    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (currentLocation.equals(navigator.getTarget())) {
            navigator.setTarget(null);
            rc.setIndicatorString(0, "we are at our location");
            return true;
        }

        if (navigator.getTarget() == null) {
            rc.setIndicatorString(0, "we have no location");
            return true;
        }

        rc.setIndicatorString(1, "dont' need to update target");
        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        // if we can see parts/neutrals, let's get them!
        MapLocation parts =  sortedParts.getBestSpot(currentLocation);
        MapLocation target;
        if (parts != null) {
            last = null;
            return parts;
        }

        int radius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared));
        int diagRadius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared) / 1.5);

        // if we were already moving in a direction, move that way if possible!
        if (last != null) {
            if (last.isDiagonal()) {
                target = currentLocation.add(last, diagRadius);
            } else {
                target = currentLocation.add(last, radius);
            }
            if (rc.onTheMap(target)) {
                return target;
            }

        }

        MapLocation enemyCenter = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        Direction toMove = enemyCenter.directionTo(currentLocation);

        if (toMove.isDiagonal()) {
            target = currentLocation.add(toMove, diagRadius);
        } else {
            target = currentLocation.add(toMove, radius);
        }

        if (rc.onTheMap(target)) {
            last = toMove;
            return target;
        }

        if ((currentLocation.x + currentLocation.y) % 2 == 0) {
            toMove = toMove.rotateLeft().rotateLeft();
        } else {
            toMove = toMove.rotateRight().rotateRight();
        }

        if (toMove.isDiagonal()) {
            target = currentLocation.add(toMove, diagRadius);
        } else {
            target = currentLocation.add(toMove, radius);
        }
        if (rc.onTheMap(target)) {
            last = toMove;
            return target;
        }

        toMove = toMove.opposite();
        if (toMove.isDiagonal()) {
            target = currentLocation.add(toMove, diagRadius);
        } else {
            target = currentLocation.add(toMove, radius);
        }
        if (rc.onTheMap(target)) {
            last = toMove;
            return target;
        }

        toMove = MapUtils.getRCCanMoveDirection(this);
        if (toMove.equals(Direction.NONE)) {
            return null;
        } else {
            return currentLocation.add(toMove);
        }
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
