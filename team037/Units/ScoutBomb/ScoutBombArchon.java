package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;
import team037.Units.PacMan.PacMan;
import team037.Utilites.MapUtils;

public class ScoutBombArchon extends BaseArchon implements PacMan {

    ZombieSpawnSchedule schedule;
    Direction last;

    public ScoutBombArchon(RobotController rc) {
        super(rc);
        schedule = rc.getZombieSpawnSchedule();
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
            return true;
        }

        if (navigator.getTarget() == null) {
            return true;
        }

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
