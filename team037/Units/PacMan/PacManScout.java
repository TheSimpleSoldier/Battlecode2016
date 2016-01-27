package team037.Units.PacMan;

import battlecode.common.*;
import team037.Navigation;
import team037.Navigator;
import team037.Unit;
import team037.Units.Scouts.HerdingScout;
import team037.Units.Scouts.ScoutingScout;
import team037.Utilites.MapUtils;

/**
 * PacManArchon runs away. PacManScout escorts PacManArchon.
 * Created by davej on 1/13/2016.
 */
public class PacManScout extends Unit implements PacMan {
//    RobotInfo myArchon;
//    MapLocation archonLocation;

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .15, .2, 1},        // zombie weights (zombies in sensor range)
                    {1.5, .5, .125, .25, 1.5},        // enemy weights (enemies in sensor range)
                    {-8, -4, -2, -1, 0},            // target constants (attract towards target)
                    {1, .5, .5, .5, .5},   // friendly unit weights (friendlies in sensor range)
                    {1, .5, .15, .2, 1},        // archon weights
            };

    static final int ALLIES = 3;
    static final int MYARCHONS = 4;
    static final int ENEMYARCHONS = 5;

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

    public boolean fight() {
//        if (rc.isInfected() && enemies.length > 2 && enemies.length < zombies.length) {
//            return true;
//        }
        return false;
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

    public int[] applyAllWeights(int[] directions, double[][] weights) {

        directions = applyOrbitWeights(directions, zombies, PACMAN_WEIGHTS[0]);
//        if (rc.isInfected()) {
//
//        }
        directions = applyUnitWeights(directions, allies, weights[ALLIES]);

        return directions;
    }

    public int[] applyAllConstants(int[] directions, double[][] weights) {
        MapLocation loc = Unit.navigator.getTarget();
        if (loc != null) {
            directions = applyConstant(directions, loc, weights[TARGET]);
        }

        directions = applyAdditionalConstants(directions);

        return directions;
    }

    public int[] applyAdditionalConstants(int[] directions) {
        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null || myArchons.length == 0) {
            return directions;
        }

        directions = PacManUtils.applySimpleConstants(currentLocation, directions, myArchons, new int[]{16,8,4});

        return directions;
    }

    public boolean precondition() {
        return false;
    }

    public boolean updateTarget() throws GameActionException {
        return false;
    }


    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    public void handleMessages() throws GameActionException {}

    public int[] applyUnitWeights(int[] directions, RobotInfo[] units, double[] scalars) {

        if (units == null) {
            return directions;
        }

        int length = units.length;

        for (int i = length; --i >= 0; ) {
            RobotInfo unit = units[i];
            while(unit.equals(RobotType.ARCHON) && --i >= 0) {
                directions = applyOrbitWeights(directions,new RobotInfo[]{unit},PACMAN_WEIGHTS[MYARCHONS]);
                unit = units[i];
            }
            MapLocation nextUnit = unit.location;
            double add = (38 - nextUnit.distanceSquaredTo(currentLocation)) * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = addAdjacent * scalars[2];
            double addPerpAdj = addPerp * scalars[3];
            double addOpp = addPerpAdj * scalars[4];
            switch (currentLocation.directionTo(nextUnit)) {
                case NORTH:
                    directions[4] += addOpp;
                    directions[5] += addPerpAdj;
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    directions[3] += addPerpAdj;
                    break;
                case NORTH_EAST:
                    directions[5] += addOpp;
                    directions[6] += addPerpAdj;
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    directions[4] += addPerpAdj;
                    break;
                case EAST:
                    directions[6] += addOpp;
                    directions[7] += addPerpAdj;
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    directions[5] += addPerpAdj;
                    break;
                case SOUTH_EAST:
                    directions[7] += addOpp;
                    directions[0] += addPerpAdj;
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    directions[6] += addPerpAdj;
                    break;
                case SOUTH:
                    directions[0] += addOpp;
                    directions[1] += addPerpAdj;
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    directions[7] += addPerpAdj;
                    break;
                case SOUTH_WEST:
                    directions[1] += addOpp;
                    directions[2] += addPerpAdj;
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    directions[0] += addPerpAdj;
                    break;
                case WEST:
                    directions[2] += addOpp;
                    directions[3] += addPerpAdj;
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    directions[1] += addPerpAdj;
                    break;
                case NORTH_WEST:
                    directions[3] += addOpp;
                    directions[4] += addPerpAdj;
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    directions[2] += addPerpAdj;
                    break;
            }
        }

        return directions;
    }

    public int[] applyConstant(int[] directions, MapLocation location, double[] constants) {
        if (location != null) {
            switch (currentLocation.directionTo(location)) {
                case NORTH:
                    directions[6] += constants[2];
                    directions[7] += constants[1];
                    directions[0] += constants[0];
                    directions[1] += constants[1];
                    directions[2] += constants[2];
                    break;
                case NORTH_EAST:
                    directions[7] += constants[2];
                    directions[0] += constants[1];
                    directions[1] += constants[0];
                    directions[2] += constants[1];
                    directions[3] += constants[2];
                    break;
                case EAST:
                    directions[0] += constants[2];
                    directions[1] += constants[1];
                    directions[2] += constants[0];
                    directions[3] += constants[1];
                    directions[4] += constants[2];
                    break;
                case SOUTH_EAST:
                    directions[1] += constants[2];
                    directions[2] += constants[1];
                    directions[3] += constants[0];
                    directions[4] += constants[1];
                    directions[5] += constants[2];
                    break;
                case SOUTH:
                    directions[2] += constants[2];
                    directions[3] += constants[1];
                    directions[4] += constants[0];
                    directions[5] += constants[1];
                    directions[6] += constants[2];
                    break;
                case SOUTH_WEST:
                    directions[3] += constants[2];
                    directions[4] += constants[1];
                    directions[5] += constants[0];
                    directions[6] += constants[1];
                    directions[7] += constants[2];
                    break;
                case WEST:
                    directions[4] += constants[2];
                    directions[5] += constants[1];
                    directions[6] += constants[0];
                    directions[7] += constants[1];
                    directions[0] += constants[2];
                    break;
                case NORTH_WEST:
                    directions[5] += constants[2];
                    directions[6] += constants[1];
                    directions[7] += constants[0];
                    directions[0] += constants[1];
                    directions[1] += constants[2];
                    break;
            }
        }
        return directions;
    }

    public int[] applyOrbitWeights(int[] directions, RobotInfo[] units, double[] scalars) {
        if (units == null) {
            return directions;
        }

        int length = units.length;

        for (int i = length; --i >= 0; ) {
            MapLocation nextUnit = units[i].location;
            double add = (36 - nextUnit.distanceSquaredTo(currentLocation)) * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = add * scalars[2];
            double addPerpLeft = add * scalars[3];
            double addPerpAdj = -addAdjacent;
            double addOpp = -add * scalars[4];
            switch (currentLocation.directionTo(nextUnit)) {
                case NORTH:
                    directions[4] += addOpp;
                    directions[5] += addPerpAdj;
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerpLeft;
                    directions[3] += addPerpAdj;
                    break;
                case NORTH_EAST:
                    directions[5] += addOpp;
                    directions[6] += addPerpAdj;
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerpLeft;
                    directions[4] += addPerpAdj;
                    break;
                case EAST:
                    directions[6] += addOpp;
                    directions[7] += addPerpAdj;
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerpLeft;
                    directions[5] += addPerpAdj;
                    break;
                case SOUTH_EAST:
                    directions[7] += addOpp;
                    directions[0] += addPerpAdj;
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerpLeft;
                    directions[6] += addPerpAdj;
                    break;
                case SOUTH:
                    directions[0] += addOpp;
                    directions[1] += addPerpAdj;
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerpLeft;
                    directions[7] += addPerpAdj;
                    break;
                case SOUTH_WEST:
                    directions[1] += addOpp;
                    directions[2] += addPerpAdj;
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerpLeft;
                    directions[0] += addPerpAdj;
                    break;
                case WEST:
                    directions[2] += addOpp;
                    directions[3] += addPerpAdj;
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerpLeft;
                    directions[1] += addPerpAdj;
                    break;
                case NORTH_WEST:
                    directions[3] += addOpp;
                    directions[4] += addPerpAdj;
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerpLeft;
                    directions[2] += addPerpAdj;
                    break;
            }
        }

        return directions;
    }

    private boolean runAway() {
        try {
            double[][] weights = PACMAN_WEIGHTS;


        /* This is the array that will ultimately decide where we go.
        The direction with the smallest weight will be taken. */
            int[] directions = new int[8];

            // First: apply weights of nearby units
            directions = applyAllWeights(directions, weights);

            // Third: apply constant modifiers to the weights
            directions = applyAllConstants(directions, weights);

            // Last: find the smallest value whose direction leads to a valid location.
            MapLocation nextLoc;
            int min, minDir;
            do {
                minDir = 0;
                min = directions[0];
                if (min > directions[1]) {
                    minDir = 1;
                    min = directions[1];
                }
                if (min > directions[2]) {
                    minDir = 2;
                    min = directions[2];
                }
                if (min > directions[3]) {
                    minDir = 3;
                    min = directions[3];
                }
                if (min > directions[4]) {
                    minDir = 4;
                    min = directions[4];
                }
                if (min > directions[5]) {
                    minDir = 5;
                    min = directions[5];
                }
                if (min > directions[6]) {
                    minDir = 6;
                    min = directions[6];
                }
                if (min > directions[7]) {
                    minDir = 7;
                }

                directions[minDir] = Integer.MAX_VALUE;
                nextLoc = currentLocation.add(dirs[minDir], 1);
            }
            while (!(rc.canMove(dirs[minDir]) || rc.senseRubble(nextLoc) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH || min == Integer.MAX_VALUE));
            MapLocation saveTarget = navigator.getTarget();
            navigator.setTarget(nextLoc);
            boolean out = navigator.takeNextStep();
            navigator.setTarget(saveTarget);
            return out;
        } catch (Exception e) {
            return false;
        }
    }
}
