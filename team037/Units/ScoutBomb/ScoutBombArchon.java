package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;
import team037.Utilites.MapUtils;

public class ScoutBombArchon extends BaseArchon
{

    ZombieSpawnSchedule schedule;
    public ScoutBombArchon(RobotController rc) {
        super(rc);
        rc.getZombieSpawnSchedule();
    }

    @Override
    public void

    @Override
    public boolean updateTarget() throws GameActionException
    {
        // TODO: if we see enemies, run away
        if (navigator.getTarget().equals(currentLocation)) {
            return true;
        }

        if (navigator.getTarget() == null) {
            return true;
        }

    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        // if we can see parts/neutrals, let's get them!
        MapLocation parts =  sortedParts.getBestSpot(currentLocation);
        if (parts != null) {
            return parts;
        }

        MapLocation enemyCenter = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        Direction toMove = enemyCenter.directionTo(currentLocation);

        int radius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared));

        MapLocation target = currentLocation.add(toMove, radius);
        if (rc.onTheMap(target)) {
            return target;
        }

        if ((currentLocation.x + currentLocation.y) % 2 == 0) {
            toMove = toMove.rotateLeft().rotateLeft();
        } else {
            toMove = toMove.rotateRight().rotateRight();
        }

        target = currentLocation.add(toMove, radius);
        if (rc.onTheMap(target)) {
            return target;
        }

        toMove = toMove.opposite();
        target = currentLocation.add(toMove, radius);
        if (rc.onTheMap(target)) {
            return target;
        }

        return null;
    }

    @Override
    public Bots changeBuildOrder(Bots nextBot)
    {
        return Bots.SCOUTBOMBSCOUT;
    }
}
