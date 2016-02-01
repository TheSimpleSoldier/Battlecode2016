package team037.Units.PacMan;

import battlecode.common.*;
import team037.Navigation;
import team037.Navigator;
import team037.Unit;
import team037.Utilites.TurretMemory;
import team037.Utilites.FightMicroUtilites;

/**
 * PacMan is an interface for using an algorithm inspired by the little yellow guy who runs away from ghosts and eats
 * everything on the map. It is used for running away from units in a variety of situations while simultaneously
 * gravitating towards target locations.
 *
 * It starts with an array of 8 integers initialized to 0. Each integer represents a direction of movement: north (0),
 * northeast (1), east (2), southeast(3), south(4), southwest(5), west(6), and northwest(7).
 *
 * The array, which is called directions, is passed through two methods multiple times. The first method takes an array
 * of RobotInfo, finds the distance squared from this unit to each unit in the array, and adds value to every integer
 * in the directions array based on the distance. Weights are applied to the distance, usually making the value added
 * decay as directions moves away from the immediate direction from this unit to the unit in the array.
 *
 * The second method adds constants to the directions. This serves to force this unit to gravitate towards or away from
 * a specified location while prioritizing movement away from significant locations in immediate sight.
 *
 * After calling the methods mentioned above, the distances between this unit and the nearest rubble in the north,
 * south, east, and west are used to scale every value, giving non-walkable rubble significant weight based on
 * present dangers.
 *
 * Finally, the minimum value is found and its index is used to determine an ideal direction of movement. The result is
 * a style of movement that rarely gets trapped by zombies and enemies when used properly.
 */
public interface PacMan {

    // DEFAULT_WEIGHTS indices
    int ENEMIES = 0,                // Avoid enemies and zombies
            TARGET = 1,             // Move towards target
            NEUTRALS_AND_PARTS = 2, // Move towards neutrals and parts
            TURRETS = 3;            // Avoid turrets

    // DEFAULT_CONSTANTS indices
    int COUNTERMEASURES = 0,        // Move away from CountermeasureGuards
            ALLIED_ARCHONS = 1,     // Move away from allied archons
            BAD_ARCHONS = 2;        // Move toward enemy archons


    double RUBBLE_OBSTRUCT = GameConstants.RUBBLE_OBSTRUCTION_THRESH;
    double RUBBLE_SLOW = GameConstants.RUBBLE_SLOW_THRESH;
    double RUBBLE_DIFF = RUBBLE_OBSTRUCT - RUBBLE_SLOW;

    double[][] DEFAULT_WEIGHTS = new double[][] {
            {1, .5, .5, .5, .5},    // enemy and zombie weights (enemies in sensor range)
            {-16, -8, -4, 0, 0}, // target constants (attract towards target)
            {-1,.5,.5,.5,.5},       // weights for neutrals and parts
            {8,.5,.25}              // weights for turrets
    };

    int[][] DEFAULT_CONSTANTS = new int[][] {
            {9999999, 64, 16},      // constants for countermeasures
            {16,8,4},               // constants for pushing away from allied archon starting locations
            {-8,-4,-2}              // constants to attract slightly towards enemy base
    } ;

    boolean[] flags = new boolean[1];

    /**
     * Incorporate additional factors in the applyAdditionalWeights and applyAdditionalConstants methods.
     */
    default int[] applyAdditionalWeights(int[] directions) { return directions; }
    default int[] applyAdditionalConstants(int[] directions) { return directions; }

    /**
     * Apply weights to locations of enemies, zombies, and turrets. Archons also apply weights to locations of parts
     * and neutral units. Calls applyAdditionalWeights, which does nothing by default. Implement applyAdditionalWeights
     * to incorporate additional factors.
     *
     * @param directions int array for storing the sums of the distances from this unit to all significant locations
     *                   and units weighed based on their directions relative to this unit.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return directions array storing the sum of the factors described above.
     */
    default int[] applyAllWeights(int[] directions, double[][] weights) {
        directions = PacManUtils.applyWeights(Unit.currentLocation, directions, Unit.zombies, weights[ENEMIES]);
        directions = PacManUtils.applyWeights(Unit.currentLocation, directions, Unit.enemies, weights[ENEMIES]);
        directions = PacManUtils.applyTurretWeights(Unit.currentLocation, directions, TurretMemory.getBufferContents(),weights[TURRETS]);
        if (Unit.type.equals(RobotType.ARCHON)) {
            RobotInfo[] neutrals = Unit.rc.senseNearbyRobots(Unit.sightRange, Team.NEUTRAL);
            directions = PacManUtils.applyWeights(Unit.currentLocation, directions, neutrals, weights[NEUTRALS_AND_PARTS]);
            MapLocation[] parts = Unit.rc.sensePartLocations(3);
            directions = PacManUtils.applyPartsWeights(Unit.currentLocation, directions, parts, weights[NEUTRALS_AND_PARTS]);
        }
        directions = applyAdditionalWeights(directions);

        return directions;
    }

    /**
     * Lightweight version of applyAllWeights. Does not apply weights to opposite direction, or opposite direction
     * rotated right or left.
     *
     * @param directions int array for storing the sums of the distances from this unit to all significant locations
     *                   and units weighed based on their directions relative to this unit.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return directions array storing the sum of the factors described above.
     */
    default int[] applyAllSimpleWeights(int[] directions, double[][] weights) {
        directions = PacManUtils.applySimpleWeights(Unit.currentLocation, directions, Unit.zombies);
        directions = PacManUtils.applySimpleWeights(Unit.currentLocation, directions, Unit.enemies);
        directions = applyAdditionalWeights(directions);

        return directions;
    }

    /**
     * Apply constants to the target location. Archons apply another constant to CountermeasureGuards. Optionally
     * have units tend towards the enemy archon starting locations and away from allied archon starting locations by
     * calling the function runAway(double[][] weights, boolean spawnGuards, boolean tendTowardsEnemy) with
     * tendTowardsEnemy set to true. Calls applyAdditionalConstants, which does nothing by default. Implement
     * applyAdditionalConstants to incorporate additional factors.
     *
     * @param directions int array for storing the sums of the distances from this unit to all significant locations
     *                   and units weighed based on their directions relative to this unit.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return directions array storing the sum of the factors described above.
     */
    default int[] applyAllConstants(int[] directions, double[][] weights) {
        MapLocation loc = Unit.navigator.getTarget();
        if (loc != null) {
            directions = PacManUtils.applyConstant(Unit.currentLocation, directions, loc, weights[TARGET]);
        }
        if (PacManUtils.countermeasure != null) {
            directions = PacManUtils.applySimpleConstant(Unit.currentLocation,directions,PacManUtils.countermeasure.location,DEFAULT_CONSTANTS[COUNTERMEASURES]);
        }
        if (flags[0]) {
            directions = PacManUtils.applySimpleConstants(Unit.currentLocation,directions,Unit.alliedArchonStartLocs,DEFAULT_CONSTANTS[ALLIED_ARCHONS]);
            directions = PacManUtils.applySimpleConstants(Unit.currentLocation,directions,Unit.enemyArchonStartLocs,DEFAULT_CONSTANTS[BAD_ARCHONS]);
        }
        directions = applyAdditionalConstants(directions);

        return directions;
    }

    /**
     * This method assumes you have determined that we need to run away.
     *
     * @param weights weights to apply to the distances between this unit and significant locations and units.
     * @return Direction enum representing the advised direction of movement.
     */
    default Direction getRunAwayDirection(double[][] weights) {
        try {
            RobotController rc = Unit.rc;
            MapLocation currentLocation = Unit.currentLocation;
            Direction[] dirs = Unit.dirs;

            if (weights == null) {
                weights = DEFAULT_WEIGHTS;
            }


        /* This is the array that will ultimately decide where we go.
        The direction with the smallest weight will be taken. */
            int[] directions = new int[8];

            // Get the distances from this unit to the nearest rubble in the North (0), East (2), South (4) and West (6)
            int[] ping0 = Navigation.map.ping(currentLocation, 0, 3);
            int[] ping2 = Navigation.map.ping(currentLocation, 2, 3);
            int[] ping4 = Navigation.map.ping(currentLocation, 4, 3);
            int[] ping6 = Navigation.map.ping(currentLocation, 6, 3);

            // First: apply constant modifiers to the weights
            directions = applyAllConstants(directions, weights);

            // Second: apply weights of nearby units
            directions = applyAllWeights(directions, weights);

            // Third: ensure values are greater than or equal to by finding the smallest value in the directions array
            //        and adding its absolute value to all 8 integers stored in the array.
            int minValue = 99999999;
            for (int i = 8; --i >= 0;) {
                if (directions[i] < minValue)
                    minValue = directions[i];
            }
            if (minValue < 1) {
                minValue = Math.abs(minValue);
                for (int i = 8; --i >= 0; ) {
                    directions[i] += minValue;
                }
            }

            // Fourth: scale weights based on nearby rubble
//            rc.setIndicatorString(0, "ping[0]:" + ping0[0] + ", ping[1]:" + ping0[1] + ", ping[2]:" + ping0[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            int divide = 17;
            int left = divide / ++ping0[0], mid = divide / ++ping0[1], right = divide / ++ping0[2];
            directions[7] *= 1 + left / 2;
            directions[0] *= 1 + mid;
            directions[1] *= 1 + right / 2;

//            rc.setIndicatorString(1, "ping[0]:" + ping2[0] + ", ping[1]:" + ping2[1] + ", ping[2]:" + ping2[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            left = divide / ++ping2[0];
            mid = divide / ++ping2[1];
            right = divide / ++ping2[2];
            directions[1] *= 1 + left / 2;
            directions[2] *= 1 + mid;
            directions[3] *= 1 + right / 2;

//            rc.setIndicatorString(2, "ping[0]:" + ping4[0] + ", ping[1]:" + ping4[1] + ", ping[2]:" + ping4[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");

            left = divide / ++ping4[0];
            mid = divide / ++ping4[1];
            right = divide / ++ping4[2];
            directions[3] *= 1 + left / 2;
            directions[4] *= 1 + mid;
            directions[5] *= 1 + right / 2;

//            rc.setIndicatorString(2, "ping[0]:" + ping6[0] + ", ping[1]:" + ping6[1] + ", ping[2]:" + ping6[2] + ", (" + currentLocation.x + "," + currentLocation.y + ")");
            left = divide / ++ping6[0];
            mid = divide / ++ping6[1];
            right = divide / ++ping6[2];
            directions[5] *= 1 + left / 2;
            directions[6] *= 1 + mid;
            directions[7] *= 1 + right / 2;

//            // Fifth: add a fraction of the value of each direction based on adjacent walkable rubble.
//            double[] rubble = PacManUtils.rubble;
//            if (rubble != null) {
//
//                if (rubble[0] > RUBBLE_OBSTRUCT) {
//                    directions[0] += RUBBLE_DIFF;
//                } else if (rubble[0] < RUBBLE_SLOW) {
//                    rubble[0] = 0;
//                } else {
//                    directions[0] += (double)directions[0] * (rubble[0] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[1] > RUBBLE_OBSTRUCT) {
//                    directions[1] += RUBBLE_DIFF;
//                } else if (rubble[1] < RUBBLE_SLOW) {
//                    rubble[1] = 0;
//                } else {
//                    directions[1] += (double)directions[1] * (rubble[1] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[2] > RUBBLE_OBSTRUCT) {
//                    directions[2] += RUBBLE_DIFF;
//                } else if (rubble[2] < RUBBLE_SLOW) {
//                    rubble[2] = 0;
//                } else {
//                    directions[2] += (double)directions[2] * (rubble[2] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[3] > RUBBLE_OBSTRUCT) {
//                    directions[3] += RUBBLE_DIFF;
//                } else if (rubble[3] < RUBBLE_SLOW) {
//                    rubble[3] = 0;
//                } else {
//                    directions[3] += (double)directions[3] * (rubble[3] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[4] > RUBBLE_OBSTRUCT) {
//                    directions[4] += RUBBLE_DIFF;
//                } else if (rubble[4] < RUBBLE_SLOW) {
//                    rubble[4] = 0;
//                } else {
//                    directions[4] += (double)directions[4] * (rubble[4] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[5] > RUBBLE_OBSTRUCT) {
//                    directions[5] += RUBBLE_DIFF;
//                } else if (rubble[5] < RUBBLE_SLOW) {
//                    rubble[5] = 0;
//                } else {
//                    directions[5] += (double)directions[5] * (rubble[5] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[6] > RUBBLE_OBSTRUCT) {
//                    directions[6] += RUBBLE_DIFF;
//                } else if (rubble[6] < RUBBLE_SLOW) {
//                    rubble[6] = 0;
//                } else {
//                    directions[6] += (double)directions[6] * (rubble[6] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//
//                if (rubble[7] > RUBBLE_OBSTRUCT) {
//                    directions[7] += RUBBLE_DIFF;
//                } else if (rubble[7] < RUBBLE_SLOW) {
//                    rubble[7] = 0;
//                } else {
//                    directions[7] += (double)directions[7] * (rubble[7] - RUBBLE_SLOW) / RUBBLE_DIFF;
//                }
//            }
            
            // Last: find the smallest value whose direction leads to a valid direction of movement.
            MapLocation nextLoc;
            int min, minDir, firstDir = -1;
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

                if (firstDir == -1) {
                    firstDir = minDir;
                }
                directions[minDir] = Integer.MAX_VALUE;
                nextLoc = currentLocation.add(dirs[minDir], 1);
            }
            while (!(rc.canMove(dirs[minDir]) || rc.senseRubble(nextLoc) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH || min == Integer.MAX_VALUE));
            if (min == Integer.MAX_VALUE) {
                if (rc.senseRubble(currentLocation.add(dirs[firstDir])) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir];
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].rotateRight())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].rotateRight();
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].rotateLeft())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].rotateLeft();
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].rotateRight().rotateRight())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].rotateRight().rotateRight();
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].rotateLeft().rotateLeft())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].rotateLeft().rotateLeft();
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].rotateRight().rotateRight().rotateRight())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].rotateRight().rotateRight().rotateRight();
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].rotateLeft().rotateLeft().rotateLeft())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].rotateLeft().rotateLeft().rotateLeft();
                }
                if (rc.senseRubble(currentLocation.add(dirs[firstDir].opposite())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                    return dirs[firstDir].opposite();
                }
            }
            return Unit.dirs[minDir];
        } catch (Exception e) {
            return Direction.NONE;
        }
    }

    /**
     * Run away and deploy countermeasures if conditions for deploying countermeasures are good.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return true if we moved, false otherwise.
     */
    default boolean runAwayWithCountermeasures(double[][] weights)  {
        Navigator navigator = Unit.navigator;

        Direction direction = getRunAwayDirection(weights);
        MapLocation currentLocation = Unit.currentLocation;

        if (PacManUtils.canDeployCountermeasure()) {
            try {
                if (PacManUtils.deployCountermeasure(direction.opposite()) != null) return true;
            } catch (GameActionException e) {e.printStackTrace();}
        }
            MapLocation nextLoc = currentLocation.add(direction);

            MapLocation saveTarget = navigator.getTarget();
            navigator.setTarget(nextLoc);
            try {
                boolean out = navigator.takeNextStep();
                navigator.setTarget(saveTarget);
                return out;
            } catch (Exception e) {
                navigator.setTarget(saveTarget);
                e.printStackTrace();
                return false;
            }

    }

    /**
     * This method returns true if we see an allied turret in which case we shouldn't deploy counter measures
     *
     * @return true of near turret, false otherwise
     */
    default boolean nearTurrets() {
        if (Unit.allies == null || Unit.allies.length == 0) return false;

        for (int i = Unit.allies.length; --i>=0; ) {
            if (Unit.allies[i].type == RobotType.TURRET) return true;
        }

        return false;
    }

    /**
     * This method checks to see if there are any fast zombies chasing us
     *
     * @return true if fast zombies are present, false otherwise
     */
    default boolean fastZombie() {
        for (int i = Unit.zombies.length; --i>=0; ) {
            if (Unit.zombies[i].type == RobotType.FASTZOMBIE) return true;
        }
        return false;
    }

    /**
     * This checks if we are blocked in a lot of places
     *
     * @ true if we are, false otherwise
     * @throws GameActionException
     */
    default boolean inCorner() throws GameActionException {
        int offMapCount = 0;
        for (int i = Unit.dirs.length; --i>=0; ) {
            if (!Unit.rc.onTheMap(Unit.currentLocation.add(Unit.dirs[i]))) {
                offMapCount+= 2;
            } else if (Unit.rc.senseRubble(Unit.currentLocation.add(Unit.dirs[i])) > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                offMapCount++;
            }
        }

        if (offMapCount > 6) return true;
        return false;
    }

    /**
     * This method determines if we should spawn counter measures or not
     *
     * @return true if we should, false otherwise
     */
    default boolean spawnCounterMeasure() throws GameActionException {
        if (FightMicroUtilites.offensiveEnemies(Unit.enemies)) return false;
        if (inCorner()) return true;
        if (nearTurrets()) return false;
        if (!fastZombie()) return false;
        if (Unit.zombies.length > 8) return false;
        return true;
    }

    /**
     * Run away and deploy countermeasures if conditions for deploying countermeasures are good. Perform a scan of the
     * area surrounding this unit for rubble topography.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return true if we moved, false otherwise.
     */
    default boolean runAway(double[][] weights)  {
        if (!Navigation.lastScan.equals(Unit.currentLocation)) {
            try {
                if (Clock.getBytecodesLeft() > 15000) {
                    PacManUtils.rubble = Navigation.map.scan(Unit.currentLocation);
                } else {
                    PacManUtils.rubble = Navigation.map.scanImmediateVicinity(Unit.currentLocation);
                }
                Navigation.lastScan = Unit.currentLocation;

                if (Unit.type.equals(RobotType.ARCHON) && spawnCounterMeasure()) {
                    return runAwayWithCountermeasures(weights);
                }

            } catch (Exception e) {e.printStackTrace();}
        }

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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Run away and deploy countermeasures if conditions for deploying countermeasures are good.
     * @param spawnGuards set to true to spawn countermeasure guards. Still performs check on standard conditions.
     * @param tendTowardsEnemy set to true if you wish to have the unit tend towards the enemy archon starting
     *                         locations and away from allied archon starting locations.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return true if we moved, false otherwise.
     */
    default boolean runAway(double[][] weights, boolean spawnGuards, boolean tendTowardsEnemy) {

        flags[0] = tendTowardsEnemy;

        if (!Navigation.lastScan.equals(Unit.currentLocation)) {
            try {
                if (Clock.getBytecodesLeft() > 15000) {
                    PacManUtils.rubble = Navigation.map.scan(Unit.currentLocation);
                } else {
                    PacManUtils.rubble = Navigation.map.scanImmediateVicinity(Unit.currentLocation);
                }
                Navigation.lastScan = Unit.currentLocation;

                if (spawnGuards && Unit.type.equals(RobotType.ARCHON) && spawnCounterMeasure()) {
                    return runAwayWithCountermeasures(weights);
                }

            } catch (Exception e) {e.printStackTrace();}
        }

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
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Run away. Do not deploy countermeasures. Do not tend towards anything except this unit's current target.
     * @param weights weights to apply to the distance squared from this unit to all significant locations and units.
     * @return true if we moved, false otherwise.
     */
    default boolean runAwayPure(double[][] weights)  {
        if (!Navigation.lastScan.equals(Unit.currentLocation)) {
            try {
                if (Clock.getBytecodesLeft() > 15000) {
                    PacManUtils.rubble = Navigation.map.scan(Unit.currentLocation);
                } else {
                    PacManUtils.rubble = Navigation.map.scanImmediateVicinity(Unit.currentLocation);
                }
                Navigation.lastScan = Unit.currentLocation;
            } catch (Exception e) {e.printStackTrace();}
        }

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
            e.printStackTrace();
            return false;
        }
    }
}
