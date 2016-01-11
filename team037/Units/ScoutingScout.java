package team037.Units;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.EdgeDiscovered;
import team037.Messages.ExploringMapEdge;
import team037.Messages.MapBoundsCommunication;
import team037.Utilites.MapUtils;

public class ScoutingScout extends BaseScout {

    public static boolean fullScan = false;
    public static Direction scoutDirection;
    public static int dir = -1;
    public static boolean sentEdge = false;

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

        rc.setIndicatorString(1, "Map Bounds minX: " + mapKnowledge.minX + " minY: " + mapKnowledge.minY + " maxX: " + mapKnowledge.maxX + " maxY: " + mapKnowledge.maxY);

        if (rc.getRoundNum() % 5 == 0)
        {
            mapKnowledge.senseAndUpdateEdges();
        }

        if (scoutDirection == null) {
            if (!setNewScoutDirection()) {
                return false;
            } else {
//                System.out.println("Setting new target");
                move.setTarget(currentLocation.add(scoutDirection, 100));
            }
        }

        int edge = MapUtils.senseEdge(rc, scoutDirection);
        if (edge != Integer.MIN_VALUE && !mapKnowledge.exploredEdges[dir]) {
            System.out.println("Exploring edge: " + rc.getRoundNum());
            mapKnowledge.setValueInDirection(edge, scoutDirection);

            if (dir >= 0)
                mapKnowledge.exploredEdges[dir] = true;

            scoutDirection = null;
            Communication communication = new EdgeDiscovered();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.EXPLORE_EDGE), id, dir});
            communicator.sendCommunication(2500, communication);

            communication = mapKnowledge.getMapBoundsCommunication(id);
            communicator.sendCommunication(2500, communication);

            return true;
        }


        if (move.takeNextStep()) {
            return true;
        }
        return false;
    }

    public static void updateScoutDirection()
    {
        scoutDirection = null;
    }

    public static int getScoutDir()
    {
        return dir;
    }

    private boolean setNewScoutDirection() throws GameActionException {
        dir = /*mapKnowledge.getDir(id); // */ mapKnowledge.getClosestDir(currentLocation);
        if (dir == 0) {
            rc.setIndicatorString(0, "Going north");
            scoutDirection = Direction.NORTH;
        } else if (dir == 1) {
            rc.setIndicatorString(0, "Going West");
            scoutDirection = Direction.WEST;
        } else if (dir == 2) {
            rc.setIndicatorString(0, "Going South");
            scoutDirection = Direction.SOUTH;
        } else if (dir == 3) {
            rc.setIndicatorString(0, "Going East");
            scoutDirection = Direction.EAST;
        }

        if (dir != -1)
        {
            Communication communication = new ExploringMapEdge();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.EXPLORE_EDGE), 0, dir});
            communicator.sendCommunication(2500, communication);
            return true;
        }


        return false;
    }
}