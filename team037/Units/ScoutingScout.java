package team037.Units;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.EdgeDiscovered;
import team037.Messages.ExploringMapEdge;
import team037.Messages.MapBoundsCommunication;
import team037.Utilites.MapUtils;

public class ScoutingScout extends BaseScout {

    boolean fullScan = false;
    Direction scoutDirection;
    int dir = -1;

    public ScoutingScout(RobotController rc)  {
        super(rc);
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
//        if (mapKnowledge.mapBoundaryComplete()) {
//            return false;
//        }

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
                Communication communication = new EdgeDiscovered();
                communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.EXPLORE_EDGE), 0, dir});
                communicator.sendCommunication(2500, communication);

                communication = new MapBoundsCommunication();
                int x = mapKnowledge.minX;
                int width = mapKnowledge.maxX - x;
                int y = mapKnowledge.minY;
                int height = mapKnowledge.minY;
                communication.setValues(new int[]{0, x, width, 0, y, height});
                communicator.sendCommunication(2500, communication);

                return true;
            }
        }

        if (move.takeNextStep()) {
            return true;
        }
        return false;
    }

    private boolean setNewScoutDirection() throws GameActionException {
        dir = mapKnowledge.getClosestDir(currentLocation);
        if (dir == 0) {
            scoutDirection = Direction.NORTH;
            return true;
        } else if (dir == 1) {
            scoutDirection = Direction.WEST;
            return true;
        } else if (dir == 2) {
            scoutDirection = Direction.SOUTH;
            return true;
        } else if (dir == 3) {
            scoutDirection = Direction.EAST;
            return true;
        }

        if (dir != -1)
        {
            Communication communication = new ExploringMapEdge();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.EXPLORE_EDGE), 0, dir});
            communicator.sendCommunication(Math.max(type.sensorRadiusSquared * 2, Math.min(type.sensorRadiusSquared * 7, rc.getLocation().distanceSquaredTo(start))), communication);
        }


        return false;
    }
}