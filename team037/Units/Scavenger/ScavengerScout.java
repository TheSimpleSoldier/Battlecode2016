package team037.Units.Scavenger;

import battlecode.common.*;
import team037.Messages.Communication;
import team037.ScoutMapKnowledge;
import team037.Units.ScoutBomb.ScoutBombScout;

/**
 * Created by davej on 1/20/2016.
 */
public class ScavengerScout extends ScoutBombScout {

    public static boolean BOMB;
    public static MapLocation mySpot, archonLastLoc, digTarget;
    public static RobotInfo myArchon;
    public static double[] lastRubble = new double[8];

    public ScavengerScout(RobotController rc) {
        super(rc);
        BOMB = false;
        try {
            myArchon = null;
            digTarget = null;
            for (int i = 8; --i >= 0; ) {
                MapLocation loc = currentLocation.add(dirs[i]);
                if (rc.isLocationOccupied(loc)) {
                    RobotInfo checkBot = rc.senseRobotAtLocation(loc);
                    if (checkBot.type.equals(RobotType.ARCHON)) {
                        if (myArchon == null)
                            myArchon = checkBot;
                        else {
                            myArchon = null;
                            break;
                        }
                    }
                }
            }

            if (myArchon == null) {
                BOMB = true;
            } else {
                archonLastLoc = myArchon.location;
                switch (archonLastLoc.directionTo(currentLocation)) {
                    case NORTH:
                    case NORTH_EAST:
                    case NORTH_WEST:
                        mySpot = archonLastLoc.add(Direction.NORTH, 2);
                        break;
                    case WEST:
                    case SOUTH_WEST:
                        mySpot = archonLastLoc.add(Direction.SOUTH_WEST, 2);
                        break;
                    case EAST:
                    case SOUTH_EAST:
                        mySpot = archonLastLoc.add(Direction.SOUTH_EAST, 2);
                        break;
                    case SOUTH:
                        if (!rc.isLocationOccupied(archonLastLoc.add(Direction.SOUTH_WEST, 2)))  {
                            mySpot = archonLastLoc.add(Direction.SOUTH_WEST, 2);
                        } else if (!rc.isLocationOccupied(archonLastLoc.add(Direction.SOUTH_EAST, 2)))  {
                            mySpot = archonLastLoc.add(Direction.SOUTH_EAST, 2);
                        } else {
                            mySpot = archonLastLoc.add(Direction.SOUTH, 2);
                        }
                        break;
                    default:
                        mySpot = archonLastLoc.add(Direction.SOUTH, 2);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean act() throws GameActionException {
        if (BOMB || myArchon == null || !rc.canSenseRobot(myArchon.ID)) {
            BOMB = true;
            return super.act();
        }

        if (currentLocation.equals(mySpot)) {
            sendArchonMessages();
        }

        return scavengerDig() || scavengerMove() || clearWhileWaiting();
    }

    public static boolean clearWhileWaiting() {
        if (!(rc.isCoreReady() && currentLocation.equals(mySpot))) {
            return false;
        }

        double maxRubble = -1;
        int maxDir = -1;
        for (int i = 8; --i >= 0; ) {
            if (rc.canSense(currentLocation.add(dirs[i]))) {
                double rubble = rc.senseRubble(currentLocation.add(dirs[i]));
                if (rubble > maxRubble) {
                    maxDir = i;
                    maxRubble = rubble;
                }
            }
        }

        if (maxDir >= 0) {
            try {
                rc.clearRubble(dirs[maxDir]);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean scavengerDig() {
        if (!rc.isCoreReady() ||
                digTarget == null ||
                !digTarget.isAdjacentTo(myArchon.location) ||
                !digTarget.isAdjacentTo(currentLocation) ||
                !(rc.canSense(digTarget) && rc.senseRubble(digTarget) < GameConstants.RUBBLE_SLOW_THRESH)) {
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

        int distance = currentLocation.distanceSquaredTo(myArchon.location);
        if (!rc.isCoreReady() || currentLocation.equals(mySpot) || digTarget != null && !digTarget.isAdjacentTo(currentLocation))
            return false;

        MapLocation target = mySpot;

        if (digTarget != null) {
            target = digTarget;
        }

        Direction moved = currentLocation.directionTo(target);
        if (rc.canMove(moved)) {
            rc.move(moved);
            return true;
        } else {
            MapLocation adjacent = currentLocation.add(moved.rotateRight());
            if (adjacent.distanceSquaredTo(myArchon.location) < 25 && rc.canMove(currentLocation.directionTo(adjacent))) {
                rc.move(currentLocation.directionTo(adjacent));
                return true;
            }
            adjacent = currentLocation.add(moved.rotateLeft());
            if (adjacent.distanceSquaredTo(myArchon.location) < 25 && rc.canMove(currentLocation.directionTo(adjacent))) {
                rc.move(currentLocation.directionTo(adjacent));
                return true;
            }
            adjacent = currentLocation.add(moved.rotateRight().rotateRight());
            if (adjacent.distanceSquaredTo(myArchon.location) < 25 && rc.canMove(currentLocation.directionTo(adjacent))) {
                rc.move(currentLocation.directionTo(adjacent));
                return true;
            }
            adjacent = currentLocation.add(moved.rotateLeft().rotateLeft());
            if (adjacent.distanceSquaredTo(myArchon.location) < 25 && rc.canMove(currentLocation.directionTo(adjacent))) {
                rc.move(currentLocation.directionTo(adjacent));
                return true;
            }
        }

        if (distance < 3) {
            if (rc.canMove(currentLocation.directionTo(archonLastLoc).opposite())) {
                rc.move(currentLocation.directionTo(archonLastLoc).opposite());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonLastLoc).opposite().rotateRight())) {
                rc.move(currentLocation.directionTo(archonLastLoc).opposite().rotateRight());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonLastLoc).opposite().rotateLeft())) {
                rc.move(currentLocation.directionTo(archonLastLoc).opposite().rotateLeft());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonLastLoc).opposite().rotateRight().rotateRight())) {
                rc.move(currentLocation.directionTo(archonLastLoc).opposite().rotateRight().rotateRight());
                return true;
            } else if (rc.canMove(currentLocation.directionTo(archonLastLoc).opposite().rotateLeft().rotateLeft())) {
                rc.move(currentLocation.directionTo(archonLastLoc).opposite().rotateLeft().rotateLeft());
                return true;
            }
        }
        return false;
    }

    public void sendArchonMessages() {
        try {
//            if (mKnowledge.firstFoundEdge && msgsSent < 20) {
//                Communication com = mKnowledge.getMapBoundsCommunication();
//                communicator.sendCommunication(ScoutMapKnowledge.getMaxRange(), com);
//                msgsSent++;
//                mKnowledge.firstFoundEdge = false;
//                mKnowledge.updated = false;
//            }
//            if (mKnowledge.updated && msgsSent < 20) {
//                Communication com = mKnowledge.getMapBoundsCommunication();
//                communicator.sendCommunication(ScoutMapKnowledge.getRange(), com);
//                msgsSent++;
//                mKnowledge.updated = false;
//            }
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
        } else {

            if (!archonLastLoc.equals(myArchon.location)) {
                mySpot = mySpot.add(archonLastLoc.directionTo(myArchon.location));
            }
            archonLastLoc = myArchon.location;

            if (zombies != null) {
                for (int i = zombies.length; --i >= 0; ) {

                    if (zombies[i].type.equals(RobotType.ZOMBIEDEN)) {
                        i++;
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

                    if (myDistance > 0) {
                        target = zombie;
                        navigator.setTarget(zombie);
                        BOMB = true;
                        break;
                    }
                }
            }

            double[] rubble = new double[8];
            MapLocation loc = myArchon.location;
            if (digTarget != null &&
                    myArchon.location.isAdjacentTo(digTarget) &&
                    rc.canSense(digTarget) &&
                    rc.senseRubble(digTarget) > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {

                for (int i = 8; --i >= 0; ) {
                    if (rc.canSense(loc.add(dirs[i])))
                        rubble[i] = rc.senseRubble(loc.add(dirs[i]));
                }
                lastRubble = rubble;

            } else if (myArchon.location.equals(archonLastLoc)) {

                digTarget = null;
                try {
                    double least = 9999999;
                    int leastDir = -1;
                    for (int i = 8; --i >= 0; ) {
                        MapLocation check = loc.add(dirs[i]);
                        if (rc.canSenseLocation(check)) {
                            rubble[i] = rc.senseRubble(check);
                            if (rubble[i] < lastRubble[i] && rubble[i] < least && rubble[i] > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                                leastDir = i;
                                least = rubble[i];
                            }
                            lastRubble[i] = rubble[i];
                        }
                    }

                    if (leastDir >= 0) {
                        digTarget = loc.add(dirs[leastDir]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                digTarget = null;

                for (int i = 8; --i >= 0; ) {
                    if (rc.canSense(loc.add(dirs[i])))
                        rubble[i] = rc.senseRubble(loc.add(dirs[i]));
                }
                lastRubble = rubble;
            }
        }
    }
}
