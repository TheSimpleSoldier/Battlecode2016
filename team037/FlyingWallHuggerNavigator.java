package team037;

import battlecode.common.*;

public class FlyingWallHuggerNavigator extends Navigator {
    private static RobotController rc;
    private static MapLocation target;
    private static int ordRadius;
    private static int diagRadius;
    private static Direction initialDirectionToTarget;
    private static Direction currentDirection;
    private static boolean left;
    private static boolean huggingTheWall = true;

    public FlyingWallHuggerNavigator(RobotController rc) {
        super(rc);
        this.rc = rc;
        RobotType type = rc.getType();
        ordRadius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared));
        diagRadius = (int)Math.floor(Math.sqrt(type.sensorRadiusSquared/2.0));
        left = rc.getID() % 2 == 0;

    }

    @Override
    public MapLocation getTarget() {
        return target;
    }

    @Override
    public void setTarget(MapLocation dest) {
        target = dest;
        initialDirectionToTarget = rc.getLocation().directionTo(dest);
        currentDirection = initialDirectionToTarget.opposite();
    }

    private boolean nextLocationOnMap(MapLocation currentLocation) throws GameActionException {
        if (currentDirection.isDiagonal()) {
            return rc.onTheMap(currentLocation.add(currentDirection, diagRadius));
        } else {
            return rc.onTheMap(currentLocation.add(currentDirection, ordRadius));
        }
    }

    private void updateDirection() {
        if (left) {
            currentDirection = currentDirection.rotateLeft();
        } else {
            currentDirection = currentDirection.rotateRight();
        }
    }


    @Override
    public Direction getNextStep(MapLocation currentLocation) throws GameActionException {
        Direction toMove;
        if (currentLocation.directionTo(target).opposite().equals(initialDirectionToTarget)) {
            huggingTheWall = false;
        }

        if (!huggingTheWall) {
            toMove = currentLocation.directionTo(target);
        } else {
            while (!nextLocationOnMap(currentLocation)) {
                updateDirection();
            }
            toMove = currentDirection;
        }

        if (tryMove(toMove, currentLocation)) {
            return toMove;
        } else if (tryMove(toMove.rotateLeft(), currentLocation)) {
            return toMove.rotateLeft();
        } else if (tryMove(toMove.rotateRight(), currentLocation)) {
            return toMove.rotateRight();
        } else if (tryMove(toMove.rotateLeft().rotateLeft(), currentLocation)) {
            return toMove.rotateLeft().rotateLeft();
        } else if (tryMove(toMove.rotateRight().rotateRight(), currentLocation)) {
            return toMove.rotateRight().rotateRight();
        } else if (tryMove(toMove.rotateLeft().rotateLeft().rotateLeft(), currentLocation)) {
            return toMove.opposite().rotateRight();
        } else if (tryMove(toMove.rotateRight().rotateRight().rotateRight(), currentLocation)) {
            return toMove.opposite().rotateLeft();
        } else {
            // we couldn't move anywhere! reset the tail and try again
            return Direction.NONE;
        }
    }


    @Override
    public boolean takeNextStep() throws GameActionException {
        if (!rc.isCoreReady()) {
            return false;
        }

        if (target == null) {
            return false;
        }

        MapLocation currentLocation = rc.getLocation();
        if (currentLocation.equals(target)) {
            return false;
        }

        Direction toMove = getNextStep(currentLocation);
        if (toMove.equals(Direction.NONE)) {
            return false;
        } else {
            rc.move(toMove);
            return true;
        }
    }

    private boolean tryMove(Direction toMove, MapLocation currentLocation) throws GameActionException {
        if (rc.canMove(toMove)) {
            return true;
        }
        return false;
    }


}