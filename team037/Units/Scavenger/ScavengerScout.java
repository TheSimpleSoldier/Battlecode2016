package team037.Units.Scavenger;

import battlecode.common.*;
import team037.Messages.Communication;
import team037.ScoutMapKnowledge;
import team037.Units.ScoutBomb.ScoutBombScout;
import team037.Utilites.MapUtils;
import team037.Utilites.MoveUtils;

/**
 * Created by davej on 1/20/2016.
 */
public class ScavengerScout extends ScoutBombScout {

    public static boolean BOMB;
    public static MapLocation mySpot, archonLastLoc, archonCurrentLoc, digTarget;
    public static RobotInfo myArchon;
    public static double[] lastRubble = new double[8];

    public ScavengerScout(RobotController rc) {
        super(rc);
        BOMB = false;
        try {
            digTarget = null;

            myArchon = null;
            allies = rc.senseNearbyRobots(8, us);
            int distance = 9999999;
            for (int i = allies.length; --i >= 0;) {
                if (allies[i].type.equals(RobotType.ARCHON)) {
                    int nextDistance = currentLocation.distanceSquaredTo(allies[i].location);
                    if (myArchon == null || distance > nextDistance) {
                        myArchon = allies[i];
                        distance = nextDistance;
                    }
                }
            }

            if (myArchon == null) {
                BOMB = true;
            } else {
                archonLastLoc = null;
                archonCurrentLoc = myArchon.location;
                switch (archonCurrentLoc.directionTo(currentLocation)) {
                    case NORTH:
                    case NORTH_EAST:
                    case NORTH_WEST:
                        mySpot = archonCurrentLoc.add(Direction.NORTH, 2);
                        break;
                    case WEST:
                    case SOUTH_WEST:
                        mySpot = archonCurrentLoc.add(Direction.SOUTH_WEST, 2);
                        break;
                    case EAST:
                    case SOUTH_EAST:
                        mySpot = archonCurrentLoc.add(Direction.SOUTH_EAST, 2);
                        break;
                    case SOUTH:
                        if (!rc.isLocationOccupied(archonCurrentLoc.add(Direction.SOUTH_WEST, 2))) {
                            mySpot = archonCurrentLoc.add(Direction.SOUTH_WEST, 2);
                        } else if (!rc.isLocationOccupied(archonCurrentLoc.add(Direction.SOUTH_EAST, 2))) {
                            mySpot = archonCurrentLoc.add(Direction.SOUTH_EAST, 2);
                        } else {
                            mySpot = archonCurrentLoc.add(Direction.SOUTH, 2);
                        }
                        break;
                    default:
                        mySpot = archonCurrentLoc.add(Direction.SOUTH, 2);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean act() throws GameActionException {
        if (BOMB || myArchon == null || !rc.canSenseRobot(myArchon.ID)) {
            myArchon = null;
            BOMB = true;
            return super.act();
        }

        if (currentLocation.equals(mySpot)) {
            sendArchonMessages();
        }

        return scavengerDig() || scavengerMove() || clearWhileWaiting();
    }

    public static boolean clearWhileWaiting() throws GameActionException {
        if (!(rc.isCoreReady() && currentLocation.equals(mySpot))) {
            return false;
        }

        return MoveUtils.tryClearAnywhere(currentLocation.directionTo(myArchon.location));
    }

    public static boolean scavengerDig() {
        if (!(rc.isCoreReady() && digTarget != null &&
                digTarget.isAdjacentTo(archonCurrentLoc) &&
                digTarget.isAdjacentTo(currentLocation))) {
            return false;
        }
        if (!rc.canSense(digTarget) || rc.senseRubble(digTarget) < GameConstants.RUBBLE_SLOW_THRESH) {
            return false;
        }

        try {
            rc.clearRubble(currentLocation.directionTo(digTarget));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean scavengerMove() throws GameActionException {

        if (!rc.isCoreReady())
            return false;

        MapLocation target = mySpot;

        if (digTarget != null &&
                digTarget.isAdjacentTo(archonCurrentLoc) &&
                rc.canSense(digTarget) &&
                rc.senseRubble(digTarget) > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
            target = digTarget;
        }

        Direction moveDirection = currentLocation.directionTo(target);
//        rc.setIndicatorString(2,"Round: " + round + ", Direction: " + moveDirection.toString() + ", mySpot: (" + mySpot.x + "," + mySpot.y + ")");
        if (moveDirection.equals(Direction.NONE) || moveDirection.equals(Direction.OMNI)) {
            return false;
        }
        if (rc.canMove(moveDirection)) {
            rc.move(moveDirection);
            return true;
        } else if (rc.canMove(moveDirection.rotateRight())) {
            rc.move(moveDirection.rotateRight());
            return true;
        } else if (rc.canMove(moveDirection.rotateLeft())) {
            rc.move(moveDirection.rotateLeft());
            return true;
        } else if (rc.canMove(moveDirection.rotateRight().rotateRight())) {
            rc.move(moveDirection.rotateRight().rotateRight());
            return true;
        } else if (rc.canMove(moveDirection.rotateLeft().rotateLeft())) {
            rc.move(moveDirection.rotateLeft().rotateLeft());
            return true;
        }

        if (currentLocation.distanceSquaredTo(archonCurrentLoc) < 3) {
            if (rc.canMove(currentLocation.directionTo(archonCurrentLoc).opposite())) {
                rc.move(currentLocation.directionTo(archonCurrentLoc).opposite());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonCurrentLoc).opposite().rotateRight())) {
                rc.move(currentLocation.directionTo(archonCurrentLoc).opposite().rotateRight());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonCurrentLoc).opposite().rotateLeft())) {
                rc.move(currentLocation.directionTo(archonCurrentLoc).opposite().rotateLeft());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonCurrentLoc).opposite().rotateRight().rotateRight())) {
                rc.move(currentLocation.directionTo(archonCurrentLoc).opposite().rotateRight().rotateRight());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonCurrentLoc).opposite().rotateLeft().rotateLeft())) {
                rc.move(currentLocation.directionTo(archonCurrentLoc).opposite().rotateLeft().rotateLeft());
                return true;
            }
        }
        return false;
    }

    public void sendArchonMessages() {
        try {
            msgArchons();
            msgParts();
            msgDens();
            msgRubble();
        } catch (GameActionException e) {
            e.printStackTrace();
        }
    }

    public void collectData() throws GameActionException {
        super.collectData();
        if (BOMB || myArchon == null || !rc.canSenseRobot(myArchon.ID)) {
            BOMB = true;
            return;
        }
        myArchon = rc.senseRobot(myArchon.ID);
        if (currentLocation.distanceSquaredTo(myArchon.location) > 24) {
            BOMB = true;
        } else {
            if (!myArchon.location.equals(archonCurrentLoc)) {
//                rc.setIndicatorString(1, "Update target at round " + round + ".");
                archonLastLoc = archonCurrentLoc;
                archonCurrentLoc = myArchon.location;
                mySpot = mySpot.add(archonLastLoc.directionTo(archonCurrentLoc));
                digTarget = null;
            }

            if (zombies != null) {
                for (int i = zombies.length; --i >= 0; ) {

                    if (zombies[i].type.equals(RobotType.ZOMBIEDEN)) {
                        i--;
                        if (i < 0)
                            break;
                    }

                    MapLocation zombie = zombies[i].location;
                    int myDistance = currentLocation.distanceSquaredTo(zombie);

                    for (int j = allies.length; --j >= 0; ) {
                        if (myDistance > allies[j].location.distanceSquaredTo(zombie)) {
                            myDistance = -1;
                            break;
                        }
                    }

                    if (myDistance > 0 && myDistance < 25) {
                        target = zombie;
                        navigator.setTarget(zombie);
                        BOMB = true;
                        break;
                    }
                }
            }

            double[] rubble = new double[8];
            try {
                double least = 9999999;
                int leastDir = -1;
                for (int i = 8; --i >= 0; ) {
                    MapLocation check = archonCurrentLoc.add(dirs[i]);
                    if (rc.canSenseLocation(check)) {
                        rubble[i] = rc.senseRubble(check);
                        if (lastRubble[i] > GameConstants.RUBBLE_OBSTRUCTION_THRESH &&
                                rubble[i] < lastRubble[i] &&
                                rubble[i] < least &&
                                rubble[i] > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                            leastDir = i;
                            least = rubble[i];
                        }
                        lastRubble[i] = rubble[i];
                    }
                }

                if (leastDir >= 0) {
                    digTarget = archonCurrentLoc.add(dirs[leastDir]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
