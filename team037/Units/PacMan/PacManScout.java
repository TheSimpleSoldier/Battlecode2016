package team037.Units.PacMan;

import battlecode.common.*;
import team037.Units.Scouts.HerdingScout;
import team037.Units.Scouts.ScoutingScout;
import team037.Utilites.MapUtils;

/**
 * PacManArchon runs away. PacManScout escorts PacManArchon.
 * Created by davej on 1/13/2016.
 */
public class PacManScout extends ScoutingScout implements PacMan {
//    RobotInfo myArchon;
//    MapLocation archonLocation;

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
                    {1, .25, .333333, .5, .5},  // enemy weights (enemies in sensor range)
                    {8, 4, 2, 1, 0},            // target constants (attract towards target)
                    {1, .5, .5, .5, .5},   // friendly unit weights (friendlies in sensor range)
                    {16, 8, 4, 2, 0},        // Archon constants (constantly repel from friendly Archons)
            };


    public PacManScout(RobotController rc) {
        super(rc);
//        nearByAllies = rc.senseNearbyRobots(2,us);
//        for (int i = nearByAllies.length; --i >= 0;) {
//            if (nearByAllies[i].type.equals(RobotType.ARCHON)) {
//                myArchon = nearByAllies[i];
//                break;
//            }
//        }
//
//        if (myArchon != null) {
//            archonLocation = myArchon.location;
//        }
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

        return directions;
    }

    public int[] applyAdditionalConstants(int[] directions, double[][] weights) {
        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null || myArchons.length == 0) {
            return directions;
        }

        directions = applyConstants(currentLocation,directions,myArchons,weights[4]);

        return directions;
    }

    public boolean precondition()
    {
        return false;
    }

    public boolean updateTarget() throws GameActionException
    {
        return false;
    }
}
