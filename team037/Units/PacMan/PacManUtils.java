package team037.Units.PacMan;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.Unit;

/**
 * Created by davej on 1/22/2016.
 */
public class PacManUtils {

    public static RobotInfo countermeasure = null;

    public static MapLocation centerOfMass(RobotInfo[] bots) {
        if (bots == null || bots.length == 0)
            return null;

        int x = 0, y = 0;
        for (int i = bots.length; --i >= 0;) {
            MapLocation location = bots[i].location;
            x += location.x;
            y += location.y;
        }

        x /= bots.length;
        y /= bots.length;

        return new MapLocation(x,y);
    }

    public static boolean canDeployCountermeasure() {
        if (countermeasure == null || !Unit.rc.canSenseRobot(countermeasure.ID)) {
            countermeasure = null;
            return true;
        }
        return false;
    }

    public static RobotInfo deployCountermeasure(Direction toEnemy) throws GameActionException {

        RobotController rc = Unit.rc;

        if (!rc.isCoreReady() || !rc.hasBuildRequirements(RobotType.GUARD)) {
            return null;
        }

        MapLocation currentLocation = Unit.currentLocation;
        Bots nextBot = Bots.COUNTERMEASUREGUARD;
        RobotType nextType = RobotType.GUARD;

        if (rc.canBuild(toEnemy, nextType)) {
            rc.build(toEnemy, nextType);
            countermeasure = rc.senseRobotAtLocation(currentLocation.add(toEnemy));
        } else if (rc.canBuild(toEnemy.rotateLeft(), nextType)) {
            rc.build(toEnemy.rotateLeft(), nextType);
            countermeasure = rc.senseRobotAtLocation(currentLocation.add(toEnemy.rotateLeft()));
        } else if (rc.canBuild(toEnemy.rotateRight(), nextType)) {
            rc.build(toEnemy.rotateRight(), nextType);
            countermeasure = rc.senseRobotAtLocation(currentLocation.add(toEnemy.rotateRight()));
        } else if (rc.canBuild(toEnemy.rotateLeft().rotateLeft(), nextType)) {
            rc.build(toEnemy.rotateLeft().rotateLeft(), nextType);
            countermeasure = rc.senseRobotAtLocation(currentLocation.add(toEnemy.rotateLeft().rotateLeft()));
        } else if (rc.canBuild(toEnemy.rotateRight().rotateRight(), nextType)) {
            rc.build(toEnemy.rotateRight().rotateRight(), nextType);
            countermeasure = rc.senseRobotAtLocation(currentLocation.add(toEnemy.rotateRight().rotateRight()));
        }
        return countermeasure;
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
    public static int[] applyWeights(MapLocation currentLocation, int[] directions, RobotInfo[] units, double[] scalars) {

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
     * @param units      RobotInfo array whose locations will be used to find distances to the currentLocation variable
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applySimpleWeights(MapLocation currentLocation, int[] directions, RobotInfo[] units) {

        if (units == null) {
            return directions;
        }

        int length = units.length;
        int range = Unit.sightRange + 1;

        for (int i = length; --i >= 0; ) {
            MapLocation nextUnit = units[i].location;
            double add = range - nextUnit.distanceSquaredTo(currentLocation);
            double addAdjacent = add / 2;
            double addPerp = addAdjacent / 2;
            switch (currentLocation.directionTo(nextUnit)) {
                case NORTH:
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    break;
                case NORTH_EAST:
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    break;
                case EAST:
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    break;
                case SOUTH_EAST:
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    break;
                case SOUTH:
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    break;
                case SOUTH_WEST:
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    break;
                case WEST:
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    break;
                case NORTH_WEST:
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
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
     * @param location   MapLocation to be used to find distance to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applyConstant(MapLocation currentLocation, int[] directions, MapLocation location, double[] constants) {
        if (location != null) {
            switch (currentLocation.directionTo(location)) {
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

        return directions;
    }

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param location   MapLocation to be used to find distance to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applySimpleConstant(MapLocation currentLocation, int[] directions, MapLocation location, int[] constants) {
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

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param locations  MapLocations to be used to find distance to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applySimpleConstants(MapLocation currentLocation, int[] directions, MapLocation[] locations, int[] constants) {
        if (locations == null) {
            return directions;
        }
        for (int i = locations.length; --i >= 0; ) {
            MapLocation location = locations[i];
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
        }
        return directions;
    }
}
