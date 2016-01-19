package team037.Units.Rushers;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Navigation;
import team037.Navigator;
import team037.Unit;
import team037.Units.PacMan.PacMan;
import team037.Units.Scouts.ScoutingScout;

/**
 * Created by davej on 1/19/2016.
 */
public class RushingScout extends ScoutingScout implements PacMan {

    private int retreatCall = 0;
    double[][] PACMAN_WEIGHTS = new double[][] {
            {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
            {1, .5, .5, .5, .5},
            {8, 4, 2, 1,0}
    };

    public RushingScout(RobotController rc) {
        super(rc);
    }

    public boolean fight() {
        if (enemies.length > 0) {
            return runAway(PACMAN_WEIGHTS);
        }
        return false;
    }

    public void sendMessages() throws GameActionException {
        int offensiveEnemies = 0;
        MapLocation foundArchon = null;
        for (int i = enemies.length; --i>=0;)
        {
            switch (enemies[i].type)
            {
                case TURRET:
                case GUARD:
                case SOLDIER:
                case VIPER:
                    offensiveEnemies++;
                    break;
                case ARCHON:
                    offensiveEnemies += 10;
                    foundArchon = enemies[i].location;
                    break;

            }
        }

        if (offensiveEnemies > 3 && (rc.getRoundNum() - retreatCall) > 25 && msgsSent < 20)
        {
            retreatCall = rc.getRoundNum();
            Communication distressCall = new BotInfoCommunication();
            if (foundArchon == null) {
                foundArchon = currentLocation;
            }
            distressCall.setValues(new int[]{CommunicationType.toInt(CommunicationType.ARCHON_DISTRESS), 0, 0, id, foundArchon.x, foundArchon.y});
            communicator.sendCommunication(mapKnowledge.getRange(), distressCall);
            msgsSent++;
        }
    }


    /**
     * This method assumes you have determined that we need to run away.
     *
     * @param weights
     * @return
     */
    public Direction getRunAwayDirection(double[][] weights) {
        try {

            if (weights == null) {
                weights = DEFAULT_WEIGHTS;
            }


        /* This is the array that will ultimately decide where we go.
        The direction with the smallest weight will be taken. */
            int[] directions = new int[8];

            // First: apply weights of nearby units
            directions = applyUnitWeights(directions, zombies, PACMAN_WEIGHTS[0]);
            directions = applyUnitWeights(directions, enemies, PACMAN_WEIGHTS[1]);

            if (zombies.length > 2) {
                directions = applyConstants(directions, alliedArchonStartLocs, PACMAN_WEIGHTS[2]);
            }
            // Third: apply constant modifiers to the weights

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

    public int[] applyUnitWeights(int[] directions, RobotInfo[] units, double[] scalars) {

        if (units == null) {
            return directions;
        }

        int length = units.length;

        for (int i = length; --i >= 0; ) {
            MapLocation nextUnit = units[i].location;
            double add = (54 - nextUnit.distanceSquaredTo(currentLocation)) * scalars[0];
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
    public int[] applyConstants(int[] directions, MapLocation[] locations, double[] constants) {
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
