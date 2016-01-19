package team037.Units.DenShield;

import battlecode.common.*;
import team037.Unit;
import team037.Units.PacMan.PacMan;
import team037.Utilites.MapUtils;

/**
 * Created by davej on 1/18/2016.
 */
public class DenShieldViper extends Unit implements PacMan {

    public DenShieldViper(RobotController rc) {
        super(rc);
    }

    /*
    TODO: Add logic to assist scouts who need to be infected
     */
    public boolean precondition()
    {

        navigator.setTarget(enemyArchonCenterOfMass);
        try {
            if (updateTarget())
                navigator.setTarget(getNextSpot());
        } catch (Exception e) {}
        return false;
    }

    public boolean fight() throws GameActionException {
        return fightMicro.aggressiveFightMicro(nearByEnemies,enemies,nearByAllies);
    }

    public boolean fightZombies() {
        if (zombies == null || zombies.length == 0 || (zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN))) {
            return false;
        }

        MapLocation toEnemy;
        if (enemyArchonCenterOfMass != null)
            toEnemy = enemyArchonCenterOfMass;
        else {
            toEnemy = getNextSpot();
        }
        navigator.setTarget(toEnemy);

        return runAway(null);
    }

    public boolean takeNextStep() throws GameActionException {

        return navigator.takeNextStep();
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rc.getRoundNum() % 5 == 0)
        {
            mapKnowledge.updateDens(rc);
            if (mapKnowledge.dens.hasLocations())
            {
                navigator.setTarget(MapUtils.getNearestLocation(mapKnowledge.dens.array, currentLocation));
            }
        }
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation goal = navigator.getTarget();
        if (goal == null) return true;
        if (currentLocation.distanceSquaredTo(goal) < 25 && rc.senseRobotAtLocation(goal).type != RobotType.ZOMBIEDEN) return true;
        if (rc.canSenseLocation(goal) && (rc.senseRobotAtLocation(goal) == null || rc.senseRobotAtLocation(goal).team != Team.ZOMBIE || !rc.onTheMap(goal))) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot()
    {
        if (zombies.length > 1 || zombies.length == 1 && !zombies[0].equals(RobotType.ZOMBIEDEN)) {
            // TODO: move to the enemy!
            return enemyArchonCenterOfMass;
        }
        if (mapKnowledge.dens.hasLocations()) {
            MapLocation[] dens = mapKnowledge.dens.toDenseMapLocationArray();
            int max = -1;
            MapLocation nextDen = null;
            for (int i = dens.length; --i >= 0;) {
                int nextDistance = dens[i].distanceSquaredTo(currentLocation);
                if (max < nextDistance) {
                    max = nextDistance;
                    nextDen = dens[i];
                }
            }

            if (dens != null) {
                return nextDen;
            }

            return mapKnowledge.getOppositeCorner(start);
        }

        return null;
    }

    /**
     * Add additional constants to push the viper towards enemy Archons AND away from allied Archons
     * @param directions
     * @param weights
     * @return
     */
    public int[] applyAdditionalConstants(int[] directions, double[][] weights) {

        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null) {
            myArchons = rc.getInitialArchonLocations(us);
        }
        directions = applyConstants(currentLocation,directions,myArchons,new double[]{8,4,2,0,0});

        MapLocation[] badArchons = mapKnowledge.getArchonLocations(false);
        if (badArchons == null) {
            badArchons = rc.getInitialArchonLocations(us);
        }
        directions = applyConstants(currentLocation,directions,badArchons,new double[]{-16,-8,-4,0,0});

        return directions;
    }
}
