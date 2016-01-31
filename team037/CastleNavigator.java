package team037;

import battlecode.common.*;

/**
 * Navigator specifically for the castle strategy, coordinating the movement of the castle.
 */
public class CastleNavigator extends Navigator {
    private static Unit unit;
    private static MapLocation target;
    private static Direction[] dirs;

    public CastleNavigator(Unit unit) {
        super(unit.rc);
        this.unit = unit;
        dirs = new Direction[]{Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
                Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    }

    @Override
    public void setTarget(MapLocation t) { target = t; }

    @Override
    public MapLocation getTarget()
    {
        return target;
    }


    public Direction getNextDirToTarget() throws GameActionException {
        if (target == null) {
            return Direction.NONE;
        }
        Direction toMove = unit.currentLocation.directionTo(target);

        if (toMove.isDiagonal()) {
            toMove = toMove.rotateLeft();
        }

        if (!canMoveOnMap(toMove)) {
            toMove = toMove.rotateRight().rotateRight();
            if (!canMoveOnMap(toMove)) {
                return Direction.NONE;
            }
        }

        return toMove;
    }

    public boolean canMove(Direction d) throws GameActionException {
        return unit.rc.canMove(d) && canMoveNoRobots(d) && canMoveNoRubble(d) && canMoveOnMap(d);
    }

    public boolean canDangerousMove(Direction d) throws GameActionException {
        return unit.rc.canMove(d) && canMoveNoRobots(d) && canMoveOnMap(d);
    }

    private boolean canMoveNoRubble(Direction d) throws GameActionException {
        Direction left = d.rotateLeft().rotateLeft();
        Direction right = d.rotateRight().rotateRight();
        MapLocation top = unit.currentLocation.add(d, 3);

        if (unit.rc.senseRubble(top) > 0) {
            return false;
        }
        if (unit.rc.senseRubble(top.add(left)) > 0) {
            return false;
        }
        if (unit.rc.senseRubble(top.add(left, 2)) > 0) {
            return false;
        }
        if (unit.rc.senseRubble(top.add(right)) > 0) {
            return false;
        }
        if (unit.rc.senseRubble(top.add(right, 2)) > 0) {
            return false;
        }
        return true;
    }


    private boolean canMoveNoRobots(Direction d) throws GameActionException {
        Direction left = d.rotateLeft().rotateLeft();
        Direction right = d.rotateRight().rotateRight();
        MapLocation top = unit.currentLocation.add(d, 3);

        if (unit.rc.senseRobotAtLocation(top) != null) {
            return false;
        }
        if (unit.rc.senseRobotAtLocation(top.add(left)) != null) {
            return false;
        }
        if (unit.rc.senseRobotAtLocation(top.add(left, 2)) != null) {
            return false;
        }
        if (unit.rc.senseRobotAtLocation(top.add(right)) != null) {
            return false;
        }
        if (unit.rc.senseRobotAtLocation(top.add(right, 2)) != null) {
            return false;
        }
        return true;
    }


    private boolean canMoveOnMap(Direction d) throws GameActionException {
        MapLocation top = unit.currentLocation.add(d, 3);
        if (!unit.rc.onTheMap(top)) {
            return false;
        }
        return true;
    }

}
