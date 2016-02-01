package team037;

import battlecode.common.*;

/**
 * Navigator used for flying units, which can ignore map topography entirely.
 */
public class FlyingNavigator extends Navigator
{
    private RobotController rc;
    private MapLocation target;

    public FlyingNavigator(RobotController rc) {
        super(rc);
        this.rc = rc;
    }

    @Override
    public MapLocation getTarget() {
        return target;
    }

    @Override
    public void setTarget(MapLocation dest) { target = dest; }

    @Override
    public boolean takeNextStep() throws GameActionException {
        if (!rc.isCoreReady() || target == null) {
            return false;
        }

        MapLocation currentLocation = rc.getLocation();
        if (currentLocation.equals(target)) {
            return false;
        }

        Direction toMove = currentLocation.directionTo(target);
        rc.setIndicatorString(1, toMove.name());
        if (tryMove(toMove)) {
            return true;
        } else if (tryMove(toMove.rotateLeft())) {
            return true;
        } else if (tryMove(toMove.rotateRight())) {
            return true;
        } else if (tryMove(toMove.rotateLeft().rotateLeft())) {
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight())) {
            return true;
        } else if (tryMove(toMove.rotateLeft().rotateLeft().rotateLeft())) {
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight().rotateRight())) {
            return true;
        } else if (tryMove(toMove.opposite())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean tryMove(Direction toMove) throws GameActionException {
        if (rc.canMove(toMove)) {
            rc.move(toMove);
            return true;
        }
        return false;
    }

}