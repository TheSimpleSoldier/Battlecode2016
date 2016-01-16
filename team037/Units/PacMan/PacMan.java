package team037.Units.PacMan;

import battlecode.common.*;
import team037.Navigation;
import team037.Navigator;
import team037.Unit;

/**
 * Created by davej on 1/15/2016.
 */
public interface PacMan {
    /**
     * These are the array indices of the zombies weights, enemies weights, and target constants.
     * Unless you override applyAllWeights and applyAllConstants, keep this in mind if you add additional
     * weights or constants to the weights array you pass the runAway method.
     */
    int ZOMBIES = 0, ENEMIES = 1, TARGET = 2;

    double[][] DEFAULT_WEIGHTS = new double[][] {
            {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
            {1, .25, .333333, .5, .5},  // enemy weights (enemies in sensor range)
            {-8, -4, -2, -1, 0},            // target constants (attract towards target)
    };

    /**
     * If you want to use PacMan navigation, you should use the default runAway() method. If you need to incorporate
     * additional factors, implement them in the applyAdditionalWeights and applyAdditionalConstants methods.
     *
     * Check out PacManArchon for its set of weights, and the comments above applyUnitWeights for a basic description.
     */
    default int[] applyAdditionalWeights(int[] directions, double[][] weights) { return directions; }
    default int[] applyAdditionalConstants(int[] directions, double[][] weights) { return directions; }
    default int[] applyAllWeights(int[] directions, double[][] weights) {
        directions = applyUnitWeights(Unit.currentLocation, directions, Unit.zombies, weights[ZOMBIES]);
        directions = applyUnitWeights(Unit.currentLocation, directions, Unit.enemies, weights[ENEMIES]);

        directions = applyAdditionalWeights(directions,weights);

        return directions;
    }
    default int[] applyAllConstants(int[] directions, double[][] weights) {
        MapLocation loc = Unit.navigator.getTarget();
        if (loc != null) {
            directions = applyConstants(Unit.currentLocation, directions, new MapLocation[]{loc}, weights[TARGET]);
        }

        directions = applyAdditionalConstants(directions,weights);

        return directions;
    }

    /**
     * This method assumes you have determined that we need to run away.
     *
     * @param weights
     * @return
     */
    default boolean runAway(double[][] weights) {
        try {
            RobotController rc = Unit.rc;
            MapLocation currentLocation = Unit.currentLocation;
            Navigator navigator = Unit.navigator;
            Direction[] dirs = Unit.dirs;

            if (weights == null) {
                weights = DEFAULT_WEIGHTS;
            }


        /* This is the array that will ultimately decide where we go.
        The direction with the smallest weight will be taken. */
            int[] directions = new int[8];

            // First: apply weights of nearby units
            directions = applyAllWeights(directions, weights);

            // Second: scale weights based on nearby rubble
            int[] ping = Navigation.map.ping(currentLocation, 0, 3);
//            rc.setIndicatorString(0, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            int divide = 17;
            int left = divide / ++ping[0], mid = divide / ++ping[1], right = divide / ++ping[2];
            directions[7] *= 1 + left / 2;
            directions[0] *= 1 + mid;
            directions[1] *= 1 + right / 2;

            ping = Navigation.map.ping(currentLocation, 2, 3);
//            rc.setIndicatorString(1, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            left = divide / ++ping[0];
            mid = divide / ++ping[1];
            right = divide / ++ping[2];
            directions[1] *= 1 + left / 2;
            directions[2] *= 1 + mid;
            directions[3] *= 1 + right / 2;

            ping = Navigation.map.ping(currentLocation, 4, 3);
//            rc.setIndicatorString(2, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");

            left = divide / ++ping[0];
            mid = divide / ++ping[1];
            right = divide / ++ping[2];
            directions[3] *= 1 + left / 2;
            directions[4] *= 1 + mid;
            directions[5] *= 1 + right / 2;

            ping = Navigation.map.ping(currentLocation, 6, 3);
//            rc.setIndicatorString(2, "ping[0]:" + ping[0] + ", ping[1]:" + ping[1] + ", ping[2]:" + ping[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            left = divide / ++ping[0];
            mid = divide / ++ping[1];
            right = divide / ++ping[2];
            directions[5] *= 1 + left / 2;
            directions[6] *= 1 + mid;
            directions[7] *= 1 + right / 2;


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

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions         8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param locations          MapLocation array whose to be used to find distances to the currentLocation variable
     * @param scalars            5 values used to scale the distances, {currentLoc.directionTo(locations[i]),
     *                           directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     *                           Set to 0 to ignore a specific direction, negative to attract unit to locations, & positive to
     *                           repel unit from locations.
     * @param maxDistanceSquared maximum distance squared that we care about. Usually is the unit's sensor range.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    default int[] applyLocationWeights(MapLocation currentLocation, int[] directions, MapLocation[] locations, double[] scalars, int maxDistanceSquared) {

        if (locations == null) {
            return directions;
        }


        int length = locations.length;

        for (int i = length; --i >= 0; ) {
            double add = (maxDistanceSquared - locations[i].distanceSquaredTo(currentLocation)) * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = addAdjacent * scalars[2];
            double addPerpAdj = addPerp * scalars[3];
            double addOpp = addPerpAdj * scalars[4];
            switch (currentLocation.directionTo(locations[i])) {
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

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param units      RobotInfo array whose locations will be used to find distances to the currentLocation variable
     * @param scalars    values used to scale the distances, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     *                   Set to 0 to ignore a specific direction, negative to attract unit to units, & positive to
     *                   repel unit from locations.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    default int[] applyUnitWeights(MapLocation currentLocation, int[] directions, RobotInfo[] units, double[] scalars) {

        if (units == null) {
            return directions;
        }

        int length = units.length;

        for (int i = length; --i >= 0; ) {
            MapLocation nextUnit = units[i].location;
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

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param locations  MapLocation array to be used to find distances to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    default int[] applyConstants(MapLocation currentLocation, int[] directions, MapLocation[] locations, double[] constants) {
        if (locations != null) {
            for (int i = locations.length; --i >= 0; ) {
                switch (currentLocation.directionTo(locations[i])) {
                    case NORTH:
                        directions[4] += constants[4];
                        directions[5] += constants[3];
                        directions[6] += constants[2];
                        directions[7] += constants[1];
                        directions[0] += constants[0];
                        directions[1] += constants[1];
                        directions[2] += constants[2];
                        directions[3] += constants[3];
                        break;
                    case NORTH_EAST:
                        directions[5] += constants[4];
                        directions[6] += constants[3];
                        directions[7] += constants[2];
                        directions[0] += constants[1];
                        directions[1] += constants[0];
                        directions[2] += constants[1];
                        directions[3] += constants[2];
                        directions[4] += constants[3];
                        break;
                    case EAST:
                        directions[6] += constants[4];
                        directions[7] += constants[3];
                        directions[0] += constants[2];
                        directions[1] += constants[1];
                        directions[2] += constants[0];
                        directions[3] += constants[1];
                        directions[4] += constants[2];
                        directions[5] += constants[3];
                        break;
                    case SOUTH_EAST:
                        directions[7] += constants[4];
                        directions[0] += constants[3];
                        directions[1] += constants[2];
                        directions[2] += constants[1];
                        directions[3] += constants[0];
                        directions[4] += constants[1];
                        directions[5] += constants[2];
                        directions[6] += constants[3];
                        break;
                    case SOUTH:
                        directions[0] += constants[4];
                        directions[1] += constants[3];
                        directions[2] += constants[2];
                        directions[3] += constants[1];
                        directions[4] += constants[0];
                        directions[5] += constants[1];
                        directions[6] += constants[2];
                        directions[7] += constants[3];
                        break;
                    case SOUTH_WEST:
                        directions[1] += constants[4];
                        directions[2] += constants[3];
                        directions[3] += constants[2];
                        directions[4] += constants[1];
                        directions[5] += constants[0];
                        directions[6] += constants[1];
                        directions[7] += constants[2];
                        directions[0] += constants[3];
                        break;
                    case WEST:
                        directions[2] += constants[4];
                        directions[3] += constants[3];
                        directions[4] += constants[2];
                        directions[5] += constants[1];
                        directions[6] += constants[0];
                        directions[7] += constants[1];
                        directions[0] += constants[2];
                        directions[1] += constants[3];
                        break;
                    case NORTH_WEST:
                        directions[3] += constants[4];
                        directions[4] += constants[3];
                        directions[5] += constants[2];
                        directions[6] += constants[1];
                        directions[7] += constants[0];
                        directions[0] += constants[1];
                        directions[1] += constants[2];
                        directions[2] += constants[3];
                        break;
                }
            }
        }
        return directions;
    }
}
