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
            {-16, -8, -4, 0, 0},            // target constants (attract towards target)
    };

    /**
     * If you want to use PacMan navigation, you should use the default runAway() method. If you need to incorporate
     * additional factors, implement them in the applyAdditionalWeights and applyAdditionalConstants methods.
     *
     * Check out PacManArchon for its set of weights, and the comments above applyUnitWeights for a basic description.
     */
    default int[] applyAdditionalWeights(int[] directions) { return directions; }
    default int[] applyAdditionalConstants(int[] directions) { return directions; }
    default int[] applyAllWeights(int[] directions, double[][] weights) {
        directions = PacManUtils.applySimpleWeights(Unit.currentLocation, directions, Unit.zombies);
        directions = PacManUtils.applySimpleWeights(Unit.currentLocation, directions, Unit.enemies);

        directions = applyAdditionalWeights(directions);

        return directions;
    }
    default int[] applyAllConstants(int[] directions, double[][] weights) {
        MapLocation loc = Unit.navigator.getTarget();
        if (loc != null) {
            directions = PacManUtils.applyConstant(Unit.currentLocation, directions, loc, weights[TARGET]);
        }

        directions = applyAdditionalConstants(directions);

        return directions;
    }

    /**
     * This method assumes you have determined that we need to run away.
     *
     * @param weights
     * @return
     */
    default Direction getRunAwayDirection(double[][] weights) {
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
            return Unit.dirs[minDir];
        } catch (Exception e) {
            return Direction.NONE;
        }
    }

    default boolean runAway(double[][] weights)  {
        Navigator navigator = Unit.navigator;

        Direction direction = getRunAwayDirection(weights);

        MapLocation nextLoc = Unit.currentLocation.add(direction);

        MapLocation saveTarget = navigator.getTarget();
        navigator.setTarget(nextLoc);
        try {
            boolean out = navigator.takeNextStep();
            navigator.setTarget(saveTarget);
            return out;
        } catch (Exception e) {
            navigator.setTarget(saveTarget);
            return false;
        }
    }
}
