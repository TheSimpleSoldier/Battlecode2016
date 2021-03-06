package team037;

import battlecode.common.*;

public class FlyingSlugNavigator extends Navigator
{
    private RobotController rc;
    private MapLocation target;
    private int tailLength = 10;
    private int idx = 0;
    private MapLocation[] visited;
    private boolean full;


    private void resetTail() {
        full = false;
        visited = new MapLocation[tailLength];
        visited[0] = rc.getLocation();
        idx = 1;
    }

    public FlyingSlugNavigator(RobotController rc) {
        super(rc);
        this.rc = rc;
        resetTail();
    }

    @Override
    public MapLocation getTarget() {
        return target;
    }

    @Override
    public void setTarget(MapLocation dest) {
        target = dest;
        resetTail();
    }

    private boolean inTail(MapLocation m) {
        if (!full) {
            for (int i = 0; i < idx; i++) {
                if (visited[i].equals(m)) {
                    return true;
                }
            }
        } else {
            for (int i = visited.length - 1; i >= 0; i--) {
                if (visited[i].equals(m)) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean moveAndClear() throws GameActionException {
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

        Direction toMove = currentLocation.directionTo(target);
        if (tryMove(toMove, currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateLeft(), currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateRight(), currentLocation)) {
            return true;
        }

        if (tryClear(toMove, currentLocation)) {
            return true;
        } else if (tryClear(toMove.rotateLeft(), currentLocation)) {
            return true;
        } else if (tryClear(toMove.rotateRight(), currentLocation)) {
            return true;
        }

        if (tryMove(toMove.rotateLeft().rotateLeft(), currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight(), currentLocation)) {
            return true;
        }

        if (tryClear(toMove.rotateLeft().rotateLeft(), currentLocation)) {
            return true;
        } else if (tryClear(toMove.rotateRight().rotateRight(), currentLocation)) {
            return true;
        }

        if (tryMove(toMove.rotateLeft().rotateLeft().rotateLeft(), currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight().rotateRight(), currentLocation)) {
            return true;
        }

        // we couldn't move anywhere! reset the tail and try again
        resetTail();
        return false;

    }


    @Override
    public Direction getNextStep(MapLocation currentLocation) throws GameActionException {
        Direction toMove = currentLocation.directionTo(target);
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
            resetTail();
            return Direction.NONE;
        }
    }

    public Direction getNextStep(MapLocation currentLocation, boolean straight, boolean left) throws GameActionException {
        Direction toMove = currentLocation.directionTo(target);
        if (!straight && left) {
            toMove = toMove.rotateLeft();
        } else if (!straight && !left) {
            toMove = toMove.rotateRight();
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
            resetTail();
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

    public boolean takeNextStep(boolean straight, boolean left) throws GameActionException {
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

        Direction toMove = getNextStep(currentLocation, straight, left);

        if (toMove.equals(Direction.NONE)) {
            return false;
        } else {
            rc.move(toMove);
            return true;
        }
    }

    private boolean tryClear(Direction toMove, MapLocation currentLocation) throws GameActionException {
        MapLocation nextLoc = currentLocation.add(toMove);
        if(rc.senseRubble(nextLoc) > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
            rc.clearRubble(toMove);
            return true;
        }
        return false;
    }

    private boolean tryMove(Direction toMove, MapLocation currentLocation) throws GameActionException {
        MapLocation nextLoc = currentLocation.add(toMove);
        if (rc.canMove(toMove) && !inTail(nextLoc)) {
            return true;
        }
        return false;
    }


    private void move(Direction dir, MapLocation currentLocation) throws GameActionException {
        MapLocation next = currentLocation.add(dir);
        visited[idx] = next;
        idx += 1;
        if (idx >= visited.length) {
            full = true;
            idx = 0;
        }
        rc.move(dir);
    }

}