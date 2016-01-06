package team037;

import battlecode.common.*;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team037.Navigator;

import java.lang.Override;

public class SlugNavigator extends Navigator
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

    public SlugNavigator(RobotController rc) {
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


    @Override
    public boolean takeNextStep() throws GameActionException {
        if (!rc.isCoreReady()) {
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
        } else if (tryMove(toMove.rotateLeft().rotateLeft(), currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight(), currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateLeft().rotateLeft().rotateLeft(), currentLocation)) {
            return true;
        } else if (tryMove(toMove.rotateRight().rotateRight().rotateRight(), currentLocation)) {
            return true;
        } else {
            // we couldn't move anywhere! reset the tail and try again
            resetTail();
            return false;
        }


    }

    private boolean tryMove(Direction toMove, MapLocation currentLocation) throws GameActionException {
        MapLocation nextLoc = currentLocation.add(toMove);
        if (rc.canMove(toMove) && !inTail(nextLoc)) {
            if(rc.senseRubble(nextLoc) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                move(toMove, currentLocation);
                return true;
            }
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