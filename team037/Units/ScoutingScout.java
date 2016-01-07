package team037.Units;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import team037.FlyingNavigator;
import team037.MapKnowledge;
import team037.Utilites.MapUtils;

public class ScoutingScout extends BaseScout {

    FlyingNavigator move;
    MapKnowledge mapKnowledge = new MapKnowledge();
    boolean fullScan = false;
    Direction scoutDirection;

    public ScoutingScout(RobotController rc)  {
        super(rc);
        move = new FlyingNavigator(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        if(discoverEdges()) {
            return true;
        }
        return false;
    }

    private boolean discoverEdges() throws GameActionException {
        // precondition:
        if (mapKnowledge.mapBoundaryComplete()) {
            return false;
        }

        if (!fullScan) {
            mapKnowledge.senseAndUpdateEdges(rc);
            fullScan = true;
        }

        if (scoutDirection == null) {
            if (!setNewScoutDirection()) {
                return false;
            } else {
                move.setTarget(currentLocation.add(scoutDirection, 100));
            }
        }

        if (locationLastTurn.equals(currentLocation)) {
            int edge = MapUtils.senseEdge(rc, scoutDirection);
            if (edge != Integer.MIN_VALUE) {
                mapKnowledge.setValueInDirection(edge, scoutDirection);
                // TODO: send out a message!
                scoutDirection = null;
                return true;
            }
        }

        if (move.takeNextStep()) {
            return true;
        }
        return false;
    }

    private boolean setNewScoutDirection() {
        if (mapKnowledge.minY == Integer.MIN_VALUE) {
            scoutDirection = Direction.NORTH;
            return true;
        } else if (mapKnowledge.minX == Integer.MIN_VALUE) {
            scoutDirection = Direction.WEST;
            return true;
        } else if (mapKnowledge.maxY == Integer.MIN_VALUE) {
            scoutDirection = Direction.SOUTH;
            return true;
        } else if (mapKnowledge.maxX == Integer.MIN_VALUE) {
            scoutDirection = Direction.EAST;
            return true;
        }
        return false;
    }
}