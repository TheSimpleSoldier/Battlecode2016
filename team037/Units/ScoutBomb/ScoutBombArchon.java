package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.Navigation;
import team037.Units.BaseUnits.BaseArchon;
import team037.Utilites.MapUtils;

public class ScoutBombArchon extends BaseArchon
{

    ZombieSpawnSchedule schedule;
    Direction last;

    public ScoutBombArchon(RobotController rc) {
        super(rc);
        schedule = rc.getZombieSpawnSchedule();
    }


    @Override
    public boolean updateTarget() throws GameActionException
    {
        // TODO: if we see enemies, run away
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
        if (parts != null) {
            last = null;
            return parts;
        }

        int radius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared));

        // if we were already moving in a direction, move that way if possible!
        if (last != null) {
            MapLocation target = currentLocation.add(last, radius);
            if (rc.onTheMap(target)) {
                return target;
            }

        }

        MapLocation enemyCenter = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        Direction toMove = enemyCenter.directionTo(currentLocation);

        MapLocation target = currentLocation.add(toMove, radius);
        if (rc.onTheMap(target)) {
            last = toMove;
            return target;
        }

        if ((currentLocation.x + currentLocation.y) % 2 == 0) {
            toMove = toMove.rotateLeft().rotateLeft();
        } else {
            toMove = toMove.rotateRight().rotateRight();
        }

        target = currentLocation.add(toMove, radius);
        if (rc.onTheMap(target)) {
            last = toMove;
            return target;
        }

        toMove = toMove.opposite();
        target = currentLocation.add(toMove, radius);
        if (rc.onTheMap(target)) {
            last = toMove;
            return target;
        }

        return null;
    }

    @Override
    public void handleMessages() throws GameActionException {
        return;
    }

    @Override
    public Bots changeBuildOrder(Bots nextBot)
    {
        return Bots.SCOUTBOMBSCOUT;
    }
}
