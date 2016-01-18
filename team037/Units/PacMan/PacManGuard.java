package team037.Units.PacMan;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseGaurd;

/**
 * Created by davej on 1/15/2016.
 */
public class PacManGuard extends BaseGaurd implements PacMan {

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
                    {1, .25, .333333, .5, .5},  // enemy weights (enemies in sensor range)
                    {-8, -4, -2, -1, 0},            // target constants (attract towards target)
//                    {1, .5, .5, .5, .5},   // friendly unit weights (friendlies in sensor range)
                    {2, .5, 0, 0, 0},        // Archon weights (constantly repel from friendly Archons)
            };

    public PacManGuard(RobotController rc) {
        super(rc);
        try {
            MapLocation[] badArchons = rc.getInitialArchonLocations(opponent);
            if (updateTarget() && badArchons != null && badArchons.length > 0) {
                int max = -1;
                for (int i = badArchons.length; --i >= 0;) {
                    if (badArchons[i].distanceSquaredTo(currentLocation) > max) {
                        navigator.setTarget(badArchons[i]);
                    }
                }
            }
        } catch (Exception e) {}
    }

    public boolean fight() throws GameActionException {
        return fightMicro.basicNetFightMicro(nearByEnemies,nearByAllies,enemies,allies,navigator.getTarget());
    }


    public boolean fightZombies() {
        // No need to fight zombies if there aren't any
        if (zombies == null || zombies.length == 0) {
            return false;
        }

        return runAway(PACMAN_WEIGHTS);
    }

    public boolean takeNextStep() {
        if (allies == null || allies.length < 1) {
            return false;
        }

        return runAway(PACMAN_WEIGHTS);
    }

    public int[] applyAdditionalWeights(int[] directions, double[][] weights) {

        directions = applyUnitWeights(currentLocation,directions,allies,weights[3]);

//        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
//        if (myArchons != null && myArchons.length > 0) {
//            directions = applyConstants(currentLocation,directions,myArchons,weights[4]);
//        }

        return directions;
    }

//    public int[] applyAdditionalConstants(int[] directions, double[][] weights) {
//        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
//        if (myArchons == null || myArchons.length == 0) {
//            return directions;
//        }
//
//        directions = applyConstants(currentLocation,directions,myArchons,weights[4]);
//
//        return directions;
//    }

    public boolean updateTarget() throws GameActionException
    {
        if (target == null || currentLocation.equals(navigator.getTarget())) {
            return true;
        }
        return false;
    }


    public MapLocation getNextSpot() throws GameActionException
    {
        MapLocation newTarget = currentLocation;
        try {
            MapLocation[] badArchons = rc.getInitialArchonLocations(opponent);
            if (badArchons != null && badArchons.length > 0) {
                int max = -1;
                for (int i = badArchons.length; --i >= 0;) {
                    if (badArchons[i].distanceSquaredTo(currentLocation) > max) {
                        newTarget = badArchons[i];
                    }
                }
            }
        } catch (Exception e) {}
        return newTarget;
    }

    public boolean aidDistressedArchon() throws GameActionException {return false;}
//    public void handleMessages() throws GameActionException { }
    public void sendMessages()
    {
        return;
    }
}
