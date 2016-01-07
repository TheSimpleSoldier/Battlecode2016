package team037;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.DataStructures.Communication;
import team037.Utilites.MapUtils;

public class MapKnowledge {
    public static final int MAP_ADD = 16100;

    // robot's knowledge of the map where MapLocation(x, y)
    public int minX = Integer.MIN_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int minY = Integer.MIN_VALUE;
    public int maxY = Integer.MIN_VALUE;

    public AppendOnlyMapLocationSet denLocations;
    public AppendOnlyMapLocationSet archonStartPosition;

    public MapKnowledge() {
        denLocations = new AppendOnlyMapLocationSet();
        archonStartPosition = new AppendOnlyMapLocationSet();
    }

    public void setMinX(int x) { minX = x; }
    public void setMaxX(int x) { maxX = x; }
    public void setMinY(int y) { minY = y; }
    public void setMaxY(int y) { maxY = y; }

    public void addDenLocation(MapLocation m) { denLocations.add(m); }
    public void addStartingArchonLocation(MapLocation m) { archonStartPosition.add(m); }

    public MapLocation nearestCorner(MapLocation m) {
        // TODO: implement this
        return null;
    }

    /**
     * In addition to reading in info from the comm, this should be called regularly
     */
    public void senseAndUpdateEdges(RobotController rc) throws GameActionException {
        if (minY == Integer.MIN_VALUE) {
            minY = MapUtils.senseEdge(rc, Direction.NORTH);
        }
        if (maxY == Integer.MIN_VALUE) {
            maxY = MapUtils.senseEdge(rc, Direction.SOUTH);
        }
        if (minX == Integer.MIN_VALUE) {
            minX = MapUtils.senseEdge(rc, Direction.WEST);
        }
        if (maxX == Integer.MIN_VALUE) {
            maxX = MapUtils.senseEdge(rc, Direction.EAST);
        }
    }


    public Communication packForMessage() {
        Communication communication = new Communication();

        if (minX != Integer.MIN_VALUE && maxX != Integer.MIN_VALUE) {
            communication.val1 = 3;
            communication.loc1X = minX + MAP_ADD;
            communication.val2 = maxX - minX;
        } else if (minX != Integer.MIN_VALUE) {
            communication.val1 = 2;
            communication.loc1X = minX + MAP_ADD;
            communication.val2 = 0;
        } else if (maxX != Integer.MIN_VALUE) {
            communication.val1 = 1;
            communication.loc1X = maxX + MAP_ADD;
            communication.val2 = 0;
        } else {
            communication.val1 = 0;
            communication.loc1X = 0;
            communication.val2 = 0;
        }


        if (minY != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE) {
            communication.val2 = 3;
            communication.loc1Y = minY + MAP_ADD;
            communication.val4 = maxY - minY;
        } else if (minY != Integer.MIN_VALUE) {
            communication.val2 = 2;
            communication.loc1Y = minY + MAP_ADD;
            communication.val4 = 0;
        } else if (maxY != Integer.MIN_VALUE) {
            communication.val2 = 1;
            communication.loc1Y = maxY + MAP_ADD;
            communication.val4 = 0;
        } else {
            communication.val2 = 0;
            communication.loc1Y = 0;
            communication.val4 = 0;
        }

        return communication;
    }

    public void setValueInDirection(int val, Direction d) {
        switch (d) {
            case NORTH:
                minY = val;
                break;
            case SOUTH:
                maxY = val;
                break;
            case WEST:
                minX = val;
                break;
            case EAST:
                maxX = val;
                break;
        }
    }

    public void updateEdgesFromMessage(Communication communication) {
        int widthBit = communication.val1;
        int xCoord = communication.loc1X;
        int width = communication.val2;

        int heightBit = communication.val3;
        int yCoord = communication.loc1Y;
        int height = communication.val4;


        if (widthBit == 0) {
            // we know nothing! (jon snow)
        } else if (widthBit == 1) {
            // we only have a endcoord
            maxX = xCoord - MAP_ADD;
        } else if (widthBit == 2) {
            // we only have the startcoord
            minX = xCoord - MAP_ADD;
        } else if (widthBit == 3) {
            // we have both
            minX = xCoord - MAP_ADD;
            maxX = xCoord - MAP_ADD + width;
        } else {
            System.out.println("control bit borked! in MapKnowledge");
        }


        if (heightBit == 0) {
            // w know nothing! (jon snow)
        } else if (heightBit == 1) {
            // we only have the endcoord
            maxY = yCoord - MAP_ADD;
        } else if (heightBit == 2) {
            // we only have the start coord
            minY = yCoord - MAP_ADD;
        } else if (heightBit == 3) {
            minY = yCoord - MAP_ADD;
            maxY = yCoord - MAP_ADD + height;
        } else {
            System.out.println("control bit borked! in MapKnowledge");
        }
    }


    public boolean mapBoundaryComplete() {
        return minX != Integer.MIN_VALUE && minY != Integer.MIN_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE;
    }


}