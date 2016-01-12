package team037;

import battlecode.common.*;

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
            rc.setIndicatorString(0, "1");
            return true;
        } else if (tryMove(toMove.rotateLeft())) {
            rc.setIndicatorString(0, "2");
            return true;
        } else if (tryMove(toMove.rotateRight())) {
            rc.setIndicatorString(0, "3");
            return true;
        } else if (tryMove(toMove.rotateLeft().rotateLeft())) {
            rc.setIndicatorString(0, "4");
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight())) {
            rc.setIndicatorString(0, "5");
            return true;
        } else if (tryMove(toMove.rotateLeft().rotateLeft().rotateLeft())) {
            rc.setIndicatorString(0, "6");
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight().rotateRight())) {
            rc.setIndicatorString(0, "7");
            return true;
        } else if (tryMove(toMove.opposite())) {
            rc.setIndicatorString(0, "8");
            return true;
        } else {
            rc.setIndicatorString(0, "9");
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